package de.unibi.citec.clf.bonsai.behavioral;


import de.unibi.citec.clf.bonsai.behavioral.config.BehaviorConfiguration;
import static de.unibi.citec.clf.bonsai.engine.SCXMLStarterRSB.DEFAULT_INCLUDE_MAPPINGS;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.representer.Representer;
import rsb.RSBException;

/**
 *
 * @author lruegeme
 */
public class BehaviorALStarter {

    public static void main(String[] args) throws RSBException {
        inst = new BehaviorALStarter(args);
    }

//    private final String DEFAULT_PUBLISHER_SCOPE = "/behaviors/info";
    public static final String DEFAULT_REMOTE_BONSAI_SCOPE = "/bonsai";
    public static final String DEFAULT_SERVER_SCOPE = "/behavioral/server";

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BehaviorALStarter.class);
    private final String PATH_TO_LOGGING_PROPERTIES = getClass().getResource("/bonsai-behavior-logging.properties")
            .getPath();

    @Option(name = "-l", aliases = {"--logging"}, metaVar = "PATH", usage = "path to a log4j properties file")
    private String pathToLoggingProperties = PATH_TO_LOGGING_PROPERTIES;

    @Option(name = "-c", aliases = {"--config"}, metaVar = "PATH", usage = "path to the bahaviorAL config file")
    private String pathToConfig = null;

    @Option(name = "-s", aliases = {"--server_scope"}, metaVar = "VALUE", usage = "scope for server, default is: "
            + DEFAULT_SERVER_SCOPE)
    private String serverScope = DEFAULT_SERVER_SCOPE;

    @Option(name = "-b", aliases = {"--bonsai_server"}, metaVar = "VALUE", usage = "scope for bonsai (the server that gets the xml files), default is: "
            + DEFAULT_REMOTE_BONSAI_SCOPE)
    private String bonsaiScope = DEFAULT_REMOTE_BONSAI_SCOPE;

    @Option(name = "-m", aliases = {"--mapping"}, handler = MapOptionHandler.class, usage = "Include Mappings")
    private Map<String, String> includeMappings = DEFAULT_INCLUDE_MAPPINGS;

//    @Option(name = "-p", aliases = {"--info_scope"}, metaVar = "VALUE", usage = "scope for infos: "
//            + DEFAULT_PUBLISHER_SCOPE)
//    private String infoScope = DEFAULT_PUBLISHER_SCOPE;
    @Option(name = "--help", usage = "show help output")
    private boolean help = false;

    private BehaviorALStarter(String[] args) throws RSBException {

        // Check arguments and set paths
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.setUsageWidth(80);
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("BehaviorAL [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            return;
        }

        if (help) {
            System.out.println("BehaviorAL [options...] arguments...");
            parser.printUsage(System.out);
            System.out.println();
            return;
        }

        initLogging();
        BehaviorConfiguration c = initConfig();
        if (c == null) {
            logger.error("configuration failed");
            System.exit(1);
        }

        logger.info("Config:" + c);

        initServer(c);

        logger.info("behaviorAL RUNNING!");
    }

    private static BehaviorALStarter inst;

    public static BehaviorALStarter getInstance() {
        return inst;
    }

    private void initLogging() {
        if (pathToLoggingProperties == null) {
            BasicConfigurator.configure();
            logger.error("Did not find logging properties file.");
        } else {
            try {
                PropertyConfigurator.configure(pathToLoggingProperties);
            } catch (Exception e) {
                BasicConfigurator.configure();
            }
            logger.debug("Found logging properties file. - " + pathToLoggingProperties);
        }
    }

    private BehaviorConfiguration initConfig() {
        if (pathToConfig == null) {
            logger.error("no config given, using /tmp/test.yaml");
            pathToConfig = "/tmp/test.yaml";
        }

        LoaderOptions l = new LoaderOptions();
        l.setAllowDuplicateKeys(false);
        Yaml yaml = new Yaml(new Constructor(BehaviorConfiguration.class), new Representer(), new DumperOptions(), l);

        try (InputStream in = Files.newInputStream(Paths.get(pathToConfig))) {
            BehaviorConfiguration beh = yaml.loadAs(in, BehaviorConfiguration.class);
            return beh;
        } catch (IOException | ConstructorException ex) {
            logger.error("\n################## duplicate keys or error in configuration\n"
                    + "################## Check bottom of stack trace");
            logger.error("", ex);
            System.exit(1);
            return null;
        }

    }

    private void initServer(BehaviorConfiguration c) {

        try {
            BehaviorALControl bhc = new BehaviorALControl(bonsaiScope, c, includeMappings);
            RSBBehaviorALServer server = new RSBBehaviorALServer(bhc);
            server.init(serverScope);

            if (!c.autoload.isEmpty()) {
                logger.info("autoloading scxml:" + c.autoload);
                if (!bhc.load(c.autoload)) {
                    logger.error("autoload failed");
                }
            }

            logger.info("server initialized");

        } catch (Exception ex) {
            logger.fatal(ex);
            System.exit(1);
        }

    }
}

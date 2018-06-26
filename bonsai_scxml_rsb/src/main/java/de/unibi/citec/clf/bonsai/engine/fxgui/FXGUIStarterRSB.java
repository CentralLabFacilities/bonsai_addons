package de.unibi.citec.clf.bonsai.engine.fxgui;

import static de.unibi.citec.clf.bonsai.engine.SCXMLStarterRSB.DEFAULT_INCLUDE_MAPPINGS;
import de.unibi.citec.clf.bonsai.engine.fxgui.communication.FXGUISCXMLRemote;
import de.unibi.citec.clf.bonsai.engine.fxgui.communication.RemoteRSBController;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;

/**
 *
 * @author lruegeme
 */
public class FXGUIStarterRSB extends FXGUIStarter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FXGUIStarterRSB(args);
    }

    private static final String DEFAULT_REMOTE_BONSAI_SCOPE = "/bonsai";


    @Option(name = "-b", aliases = {"--bonsai_server"}, metaVar = "VALUE", usage = "scope for bonsai (the server that gets the xml files), default is: "
            + DEFAULT_REMOTE_BONSAI_SCOPE)
    public static String bonsaiScope = DEFAULT_REMOTE_BONSAI_SCOPE;
    
    private FXGUIStarterRSB(String[] args) {
        super(args);
    }

    @Override
    protected FXGUISCXMLRemote createRemote() {
        try {
            return new RemoteRSBController(bonsaiScope);
        } catch (Exception ex) {
            logger.fatal(ex);
            System.exit(0);
        }
        return null;
    }

}

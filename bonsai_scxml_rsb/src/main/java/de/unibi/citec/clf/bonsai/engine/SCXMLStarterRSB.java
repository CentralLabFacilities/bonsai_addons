package de.unibi.citec.clf.bonsai.engine;

import de.unibi.citec.clf.bonsai.engine.communication.LogPublisherRSB;
import de.unibi.citec.clf.bonsai.engine.communication.RSBServer;
import de.unibi.citec.clf.bonsai.engine.communication.SCXMLServer;
import de.unibi.citec.clf.bonsai.engine.communication.StateChangePublisherRSB;
import org.kohsuke.args4j.Option;
import rsb.InitializeException;
import rsb.RSBException;

/**
 * Starts the state machine.
 *
 * @author lkettenb
 */
public class SCXMLStarterRSB extends SCXMLStarter {

    public SCXMLStarterRSB() {
    }

    private final String DEFAULT_PUBLISHER_SCOPE = "/bonsai/info";
    private final String SCOPE_STATECHANGE = "/transitions";
    private final String SCOPE_LOG = "/log";
    private final String DEFAULT_SERVER_SCOPE = "/bonsai/server";

    @Option(name = "-p", aliases = {"--publisher_scope"}, metaVar = "VALUE", usage = "scope for publisher type")
    private String publisherScope = DEFAULT_PUBLISHER_SCOPE;
    @Option(name = "-s", aliases = {"--server_scope"}, metaVar = "VALUE", usage = "scope for publisher type")
    private String serverScope = DEFAULT_SERVER_SCOPE;

    @Override
    public SCXMLServer createServer() {
        //LOG.info("RSB server scope: " + serverScope);
        RSBServer srv = new RSBServer(stateMachineController);
        srv.init(serverScope);
        
        //LOG.info("RSB publisher scope: " + publisherScope);
            try {
                StateChangePublisherRSB scp = new StateChangePublisherRSB(publisherScope + SCOPE_STATECHANGE);
                //skillStateMachine.addListener(scp);
            } catch (InitializeException ex) {
                LOG.error("Could not initialize StateChangePublisherRSB: " + ex.toString());
            }
            try {
                LogPublisherRSB lp = new LogPublisherRSB(publisherScope + SCOPE_LOG);
            } catch (InitializeException ex) {
                LOG.error("Could not initialize LogPublisherRSB: " + ex.toString());
            }
        return srv;
    }

    /**
     * Starts the application.
     *
     * @param args Arguments
     * @throws RSBException
     */
    public static void main(String[] args) throws RSBException {
        SCXMLStarterRSB scxmlStarterRSB = new SCXMLStarterRSB();
        scxmlStarterRSB.startup(args);
    }
}

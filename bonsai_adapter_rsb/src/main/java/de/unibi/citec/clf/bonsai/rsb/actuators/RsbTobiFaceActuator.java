package de.unibi.citec.clf.bonsai.rsb.actuators;


import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import rsb.Factory;
import rsb.InitializeException;
import rsb.RSBException;
import de.unibi.citec.clf.bonsai.actuators.TobiFaceActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import java.util.concurrent.ExecutionException;
import rsb.patterns.RemoteServer;

/**
 * RSB implementation of TobiFace.
 *
 * @author lruegeme
 */
public class RsbTobiFaceActuator extends RsbNode implements TobiFaceActuator {
    
    public static final String OPTION_TIMEOUT = "timeout";
    private static String METHOD_SETWINDOWTEXT = "setWindowText";
    
    
    private Logger logger = Logger.getLogger(getClass());

    private long timeout = -1;
    private RemoteServer remoteServer;

    /**
     * Constructs a new {@link RsbFaceIdentificationHumavipsActuator}.
     *
     * @param scope Scope of the server.
     */
    public RsbTobiFaceActuator() throws InitializeException {
    }

    @Override
    public void setWindowText(String txt) {
       try {
            logger.debug("Call setWindowText(" + txt + ")");
            remoteServer.call(METHOD_SETWINDOWTEXT, txt, 0.2);
        } catch (RSBException | ExecutionException ex) {
            logger.error("Unable to setWindowText.", ex);
        } catch (TimeoutException | InterruptedException ex) {
            logger.error("Unable to setWindowText.");    
        }
    }


    @Override
    public void startNode() throws InitializeException {
        try {
            final Factory factory = Factory.getInstance();
            remoteServer = factory.createRemoteServer(scope, 1);
            remoteServer.activate();
        } catch (RSBException e) { 
            throw new InitializeException("Can not activate rsb server for scope: " + scope, e);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
    }
    
}

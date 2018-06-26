package de.unibi.citec.clf.bonsai.rsb.actuators;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import rsb.InitializeException;
import rsb.RSBException;
import rsb.patterns.RemoteServer;
import de.unibi.citec.clf.bonsai.actuators.StartStopActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;

/**
 * RSB implementation of th {@link StartStopActuator}.
 *
 * @author lziegler
 */
public class RsbStartStopActuator extends RsbNode implements StartStopActuator {
    
    public static final String OPTION_START_METHOD = "startMethod";
    public static final String OPTION_STOP_METHOD = "stopMethod";
    public static final String OPTION_TIMEOUT = "timeout";

    /**
     * The log.
     */
    private Logger logger = Logger.getLogger(getClass());
    private RemoteServer server;
    private static Object serverLock = new Object();

    private String startMethod = "start";
    private String stopMethod = "stop";
    private long timeout = 1000;
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        startMethod = conf.requestOptionalValue(OPTION_START_METHOD, startMethod);
        stopMethod = conf.requestOptionalValue(OPTION_STOP_METHOD, stopMethod);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }

    /**
     * Constructs a new {@link RsbStartStopActuator}.
     *
     * @param scope Scope of the server.
     * @param method Method that will be called by this actuator.
     * @param timeout The amount of seconds methods calls should wait for their
     * replies to arrive before failing.
     */
    public RsbStartStopActuator()
            throws InitializeException {
        this.startMethod = startMethod;
        this.stopMethod = stopMethod;
        this.timeout = timeout;
            
    }


    @Override
    public void startProcessing() throws IOException {
        call(startMethod);
    }

    @Override
    public void stopProcessing() throws IOException {
        call(stopMethod);
    }
    
    

    private void call(String method) throws IOException {
        try {
            synchronized (serverLock) {
                server.call(method, (double) timeout / 1000.0);
            }
        } catch (RSBException ex) {
            String error = "Exception while executing server call \""
                    + method + "\" on scope " + server.getScope() + ex.toString();
            logger.fatal(error);
            throw new IOException(error, ex);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            String error = "Timeout while executing server call \""
                    + method + "\" on scope " + server.getScope();
            logger.fatal(error);
            throw new IOException(error, e);
        }
    }
    

    @Override
    public void startNode() throws InitializeException {
        try {
        	server = RsbRemoteServerRepository.getInstance().requestRemoteServer(scope, timeout);
        } catch (RSBException e) {
            throw new InitializeException(
                    "Can not activate rsb server for scope: " + scope, e);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }
}

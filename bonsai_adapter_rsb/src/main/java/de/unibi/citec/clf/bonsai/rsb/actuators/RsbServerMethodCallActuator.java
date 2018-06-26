package de.unibi.citec.clf.bonsai.rsb.actuators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;

import org.apache.log4j.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.patterns.RemoteServer;
import de.unibi.citec.clf.bonsai.actuators.RPCActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;

/**
 * RSB implementation of th {@link RPCActuator}.
 *
 * @author lkettenb
 */
public class RsbServerMethodCallActuator extends RsbNode implements RPCActuator {
    
    public static final String OPTION_TIMEOUT = "timeout";
    public static final String OPTION_METHOD = "methodName";

    /**
     * The log.
     */
    private Logger logger = Logger.getLogger(getClass());
    private RemoteServer server;
    private static Object serverLock = new Object();
    private String methodName;
    private long timeout = -1;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        methodName = conf.requestValue(OPTION_METHOD);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }

    public RsbServerMethodCallActuator() throws InitializeException {

    }

    @Override
    public <U, T> U call(T data) throws ExecutionException {
        U result = null;
        try {
            synchronized (serverLock) {
                result = server.call(methodName, data);
            }
        } catch (RSBException | ExecutionException ex) {
            String error = "Unable to execute server call \""
                    + methodName + "\" on scope " + server.getScope();
            logger.fatal(error);
            logger.debug(error, ex);
            throw new ExecutionException(error, ex);
        } catch (TimeoutException | InterruptedException e) {
            String error = "Timeout while executing server call \""
                    + methodName + "\" on scope " + server.getScope();
            logger.fatal(error);
            logger.debug(error, e);
            throw new ExecutionException(error, e);
        }
        return result;
    }

    @Override
    public void call() throws ExecutionException {
        try {
            synchronized (serverLock) {
                server.call(methodName, new Event());
            }
        } catch (RSBException | InterruptedException | TimeoutException ex) {
            String error = "Timeout while executing server call \""
                    + methodName + "\" on scope " + server.getScope();
            logger.fatal(error);
            logger.debug(error, ex);
            throw new ExecutionException(error, ex);
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

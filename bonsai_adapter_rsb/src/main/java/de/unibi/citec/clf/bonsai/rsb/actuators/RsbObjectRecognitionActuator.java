package de.unibi.citec.clf.bonsai.rsb.actuators;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.patterns.RemoteServer;
import de.unibi.citec.clf.bonsai.actuators.ObjectRecognitionActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.imageio.IIOException;

/**
 *
 * @author lruegeme
 */
public class RsbObjectRecognitionActuator extends RsbNode implements ObjectRecognitionActuator {

    private RemoteServer server;
    private long timeout = 1000;

    public static final String OPTION_TIMEOUT = "timeout";

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }

    /**
     * Constructs a new {@link RsbStartStopActuator}.
     *
     * @param scope Scope of the server.
     * @param timeout The amount of seconds methods calls should wait for their replies to arrive before failing.
     * @throws rsb.InitializeException
     */
    public RsbObjectRecognitionActuator()
            throws InitializeException {
    }

    @Override
    public Future<Boolean> recognize() throws IOException {
        try {
            final Future<Event> ret = server.callAsync("runOnce");
            return new Future<Boolean>() {

                @Override
                public boolean cancel(boolean bln) {
                    return ret.cancel(bln);
                }

                @Override
                public boolean isCancelled() {
                    return ret.isCancelled();
                }

                @Override
                public boolean isDone() {
                    return ret.isDone();
                }

                @Override
                public Boolean get() throws InterruptedException, ExecutionException {
                    ret.get();
                    return true;
                }

                @Override
                public Boolean get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                    ret.get(l, tu);
                    return true;
                }
            };
        } catch (RSBException ex) {
            java.util.logging.Logger.getLogger(RsbObjectRecognitionActuator.class.getName()).log(Level.SEVERE, null, ex);
            throw new IIOException("blub");
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

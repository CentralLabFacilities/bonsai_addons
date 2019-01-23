package de.unibi.citec.clf.bonsai.rsb.actuators;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import de.unibi.citec.clf.bonsai.core.time.Time;
import org.apache.log4j.Logger;

import rsb.Event;
import rsb.Factory;
import rsb.Handler;
import rsb.Informer;
import rsb.InitializeException;
import rsb.Listener;
import rsb.RSBException;
import rst.vision.HeadObjectsType.HeadObjects;
import de.unibi.citec.clf.bonsai.actuators.FaceIdentificationHumavipsActuator;
import de.unibi.citec.clf.bonsai.actuators.StartStopActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;

/**
 * RSB implementation of th {@link StartStopActuator}.
 *
 * @author lziegler
 */
public class RsbFaceIdentificationHumavipsActuator extends RsbNode implements FaceIdentificationHumavipsActuator {

    public static final String OPTION_TIMEOUT = "timeout";
    private long timeout = 1000;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }

    /**
     * The log.
     */
    private Logger logger = Logger.getLogger(getClass());
    private Informer<HeadObjects> informer;
    private Listener listener;

    /**
     * Constructs a new {@link RsbFaceIdentificationHumavipsActuator}.
     *
     * @param scope Scope of the server.
     * @param method Method that will be called by this actuator.
     * @param timeout The amount of seconds methods calls should wait for their replies to arrive before failing.
     */
    public RsbFaceIdentificationHumavipsActuator() {

    }

    private static Event eventIn;

    @Override
    public void assignNewId() throws IOException, TimeoutException {

        Handler handler = arg0 -> eventIn = arg0;

        try {

            listener.addHandler(handler, true);

            long timeStart = Time.currentTimeMillis();
            while (eventIn == null && Time.currentTimeMillis() - timeStart < timeout) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            if (eventIn == null) {
                throw new TimeoutException();
            }

            // switch tracking id
            HeadObjects.Builder builder = HeadObjects.newBuilder((HeadObjects) eventIn.getData());
            builder.getHeadObjectsBuilder(0).getTrackingInfoBuilder().setId(9999999);

            // set altered data
            eventIn.setData(builder.build());

            // forward altered event
            informer.send(eventIn);

            listener.removeHandler(handler, true);

        } catch (InterruptedException | RSBException e) {

            logger.error(e.getMessage());
            logger.debug(e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public void startNode() throws InitializeException {
        try {
            final Factory factory = Factory.getInstance();
            listener = factory.createListener(scope);
            listener.activate();
            informer = factory.createInformer(scope);
            informer.activate();
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

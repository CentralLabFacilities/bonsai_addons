package de.unibi.citec.clf.bonsai.rsb.sensors;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


import org.apache.log4j.Logger;

import rsb.AbstractDataHandler;
import rsb.InitializeException;
import rsb.Listener;
import rsb.RSBException;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import de.unibi.citec.clf.bonsai.core.SensorListener;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbListenerRepository;
import de.unibi.citec.clf.bonsai.rsb.RsbSensor;
import de.unibi.citec.clf.bonsai.util.BoundSynchronizedQueue;
import rsb.Event;

/**
 * A simple {@link Sensor} for BTL based types that uses their static
 * <code>fromDocument</code> method to extract the data. This allows this sensor
 * to be used for every RSB-publisher that provides a BTL parseable result.
 * 
 * @author lkettenb
 * @author jwienke
 */
public class RsbStringSensor extends RsbSensor<String, String> {

    private final Logger logger = Logger.getLogger(RsbStringSensor.class);
    private BoundSynchronizedQueue<String> queue;
    private final Set<SensorListener<String>> listeners = new HashSet<>();
    private Listener myRsbListener;
    

    /**
     * Constructs a new instance of this sensor for the given BTL type based on
     * a publisher.
     * 
     * @param typeClass
     *            type to return by this sensor.
     * @param rstType
     * @throws IllegalArgumentException
     *             the given type does not contain a static fromDocument method.
     */
    public RsbStringSensor(Class<String> typeClass, Class<String> rstType) throws IllegalArgumentException {
        super(String.class, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSensorListener(SensorListener<String> listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSensorListener(SensorListener<String> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllSensorListeners() {
        listeners.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readLast(long timeout) throws IOException, InterruptedException {
        return queue.nextCached(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public void handleEvent(Event data) {
        if(!(data.getType().equals(String.class))) {
            logger.trace("got non string data: " + data.getType());
            return;
        }
        String string = (String) data.getData();
        logger.trace("received data");

        queue.push(string);

        for (SensorListener<String> l : listeners) {
            l.newDataAvailable(string);
        }
    }

    @Override
    public void startNode() {
        queue = new BoundSynchronizedQueue<>(bufferSize);

        try {
        	myRsbListener = RsbListenerRepository.getInstance().requestListener(String.class, this.getClass(), scope);
        	logger.trace("add THIS as handler for listener");
        	myRsbListener.addHandler(this, true);

        } catch (InterruptedException | RSBException ex) {
            throw new IllegalArgumentException("Unable to activate listener for scope: " + scope, ex);
        }
    }

    @Override
    public void destroyNode() {
        logger.fatal("CLEANUP CALLED");
    	try {
    		myRsbListener.removeHandler(this, false);
		} catch (InterruptedException e) {
			logger.warn("Interrupted while sensor cleanup");
		}
    }

    
    
}

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
import de.unibi.citec.clf.bonsai.rsb.RsbListenerRepository;
import de.unibi.citec.clf.bonsai.rsb.RsbSensor;
import de.unibi.citec.clf.bonsai.util.BoundSynchronizedQueue;
import java.util.LinkedList;
import java.util.List;
import rsb.Event;
import rsb.transport.XOP;

/**
 * A simple {@link Sensor} for BTL based types that uses their static <code>fromDocument</code> method to extract the
 * data. This allows this sensor to be used for every RSB-publisher that provides a BTL parseable result.
 *
 * @author lkettenb
 * @author jwienke
 */
public class RsbDoubleCacheSensor extends RsbSensor<Double, String>{

    private Logger logger = Logger.getLogger(RsbDoubleCacheSensor.class);
    private BoundSynchronizedQueue<Double> queue;
    private Set<SensorListener<Double>> listeners = new HashSet<>();
    private Listener myRsbListener;

    /**
     * Constructs a new instance of this sensor for the given BTL type based on a publisher.
     *
     * @param typeClass type to return by this sensor.
     * @param bufferSize
     * @param scope name of the subscriber this sensor will work on
     * @throws IllegalArgumentException the given type does not contain a static fromDocument method.
     */
    public RsbDoubleCacheSensor(Class<Double> typeClass, Class<String> rstType) throws IllegalArgumentException {
        super(Double.class, String.class);
    }

    public List<Double> getDataSamples(int numSamples) {
        LinkedList<Double> l = queue.getAllElements();

        if(numSamples < 0) {
            return null;
        }
        
        while (l.size() > numSamples) {
            l.removeFirst();
        }

        return l;
    }

    public double getAverage(int numSamples) {
        if (numSamples > queue.getCapacity() || numSamples > queue.getSize()) {
            logger.warn("not enougth samples");
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSensorListener(SensorListener<Double> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAllSensorListeners() {
        listeners.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double readLast(long timeout) throws IOException, InterruptedException {
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
    public void removeSensorListener(SensorListener<Double> listener) {
        listeners.remove(listener);
    }

    @Override
    public void handleEvent(Event data) {
        if(!(data.getType().equals(String.class))) {
            logger.trace("got non string data: " + data.getType());
            return;
        }
        String in = (String) data.getData();
        logger.trace("received data");
        
        Double d = Double.parseDouble(in);

        queue.push(d);

        listeners.forEach((li) -> {
            li.newDataAvailable(d);
        });
    }

    @Override
    public void startNode() {
        queue = new BoundSynchronizedQueue<>(bufferSize);

        try {
            myRsbListener = RsbListenerRepository.getInstance().requestListener(String.class, this.getClass(), scope);
            logger.trace("add THIS as handler for listener");
            myRsbListener.addHandler(this, true);

        } catch (RSBException | InterruptedException e) {
            throw new IllegalArgumentException("Unable to activate listener for scope: " + scope, e);
        }
    }

    @Override
    public void destroyNode() {
        try {
            myRsbListener.removeHandler(this, false);
        } catch (InterruptedException e) {
            logger.warn("Interrupted while sensor cleanup");
        }
    }

}

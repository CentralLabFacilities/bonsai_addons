package de.unibi.citec.clf.bonsai.rsb.sensors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import rsb.Event;
import rsb.Listener;
import rsb.RSBException;

import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.bonsai.core.SensorListener;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbListenerRepository;
import de.unibi.citec.clf.bonsai.rsb.RsbSensor;
import de.unibi.citec.clf.bonsai.util.BoundSynchronizedQueue;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.rst.RstSerializer.DeserializationException;
import de.unibi.citec.clf.btl.rst.RstSerializerRepository;
import de.unibi.citec.clf.btl.rst.RstTypeFactory;
import java.util.List;
import java.util.logging.Level;

/**
 * A simple {@link ScopeSensor} for RST types.
 * 
 * @author lkettenb, lziegler
 * @param <DataType>
 *            RST type returned by this sensor
 * @param <RstType>
 */
public class RsbBtlRstSensor<DataType extends Type, RstType extends GeneratedMessage> extends RsbSensor<DataType, RstType> {

    private BoundSynchronizedQueue<DataType> queue;
    private final Set<SensorListener<DataType>> listeners = new HashSet<>();
    private Listener listener;
    private final Logger logger = Logger.getLogger(getClass());
    
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
    }

    /**
     * Constructs a new instance of this sensor.
     *
     * @param typeClass type to return by this sensor.
     * @param rstType
     * @throws IllegalArgumentException the given type does not contain a static fromDocument method.
     */
    public RsbBtlRstSensor(Class<DataType> typeClass, Class<RstType> rstType) {
        super(typeClass, rstType);
    }
    
    @Override
    public void handleEvent(Event t) {

        DataType data;

        try {
            data = RstTypeFactory.getInstance().createType(t, dataTypeClass);
        } catch (DeserializationException e) {
            logger.error("Error converting rsb event to btl. " + listener.getScope() + " " + listener.getConfig());
            logger.debug("Error converting rsb event to btl", e);
            return;
        }

        queue.push(data);

        listeners.forEach((l) -> {
            l.newDataAvailable(data);
        });
    }

    @Override
    public void startNode() {
        this.queue = new BoundSynchronizedQueue<>(bufferSize);

        try {
        	listener = RsbListenerRepository.getInstance().requestListener(dataTypeClass, this.getClass(), scope);
            listener.addHandler(this, true);
        } catch (InterruptedException | RSBException ex) {
            throw new IllegalArgumentException("Unable to activate listener for scope: " + scope, ex);
        }

        if (RstSerializerRepository.getRstSerializer(dataTypeClass) == null) {
            throw new IllegalArgumentException("No btl rst serializer for type " + dataTypeClass.getSimpleName());
        }

    }

    @Override
    public void destroyNode() {
        try {
			listener.removeHandler(this, false);
            listener.deactivate();
		} catch (InterruptedException e) {
			logger.warn("Interrupted while sensor cleanup");
		} catch (RSBException ex) {
            logger.warn("broken sensor destroy", ex);
        }
        
    }

    ///////////
    // SENSOR
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addSensorListener(SensorListener<DataType> listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSensorListener(SensorListener<DataType> listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType readLast(long timeout) throws IOException, InterruptedException {
        DataType data;
        if (timeout == -1) {
            data = queue.nextCached(500);
        } else {
            data = queue.next(timeout);
        }
        
        if (data == null) {
            if (dataTypeClass.isAssignableFrom(List.class)) {
                try {
                    return dataTypeClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(RsbBtlRstSensor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }       
                
        return  data;//nextCached(timeout);
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
    public void removeAllSensorListeners() {
        listeners.clear();
    }
}

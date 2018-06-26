package de.unibi.citec.clf.bonsai.rsb.sensors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Document;

import org.apache.log4j.Logger;

import rsb.Listener;
import rsb.RSBException;
import rsb.transport.XOMXOP;
import rsb.transport.XOP;
import de.unibi.citec.clf.bonsai.core.exception.CommunicationException;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import de.unibi.citec.clf.bonsai.core.SensorListener;
import de.unibi.citec.clf.bonsai.rsb.RsbListenerRepository;
import de.unibi.citec.clf.bonsai.rsb.RsbSensor;
import de.unibi.citec.clf.bonsai.util.BoundSynchronizedQueue;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import rsb.Event;
import rst.xml.XOPType;

/**
 * A simple {@link Sensor} for BTL based types that uses their static
 * <code>fromDocument</code> method to extract the data. This allows this sensor
 * to be used for every RSB-publisher that provides a BTL parseable result.
 * 
 * @author lkettenb
 * @author jwienke
 */
public class RsbBtlXmlSensor<DataType extends Type> extends RsbSensor<DataType, XOPType.XOP> {

    private Logger logger = Logger.getLogger(RsbBtlXmlSensor.class);
    private BoundSynchronizedQueue<DataType> queue;
    private Set<SensorListener<DataType>> listeners = new HashSet<>();
    private Listener myRsbListener;

    public RsbBtlXmlSensor(Class<DataType> typeClass, Class<XOPType.XOP> rstType) {
        super(typeClass, rstType);
    }

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

    @Override
    public void removeAllSensorListeners() {
        listeners.clear();
    }

    /**
     * Safely extracts the BTL type from the document found in the
     * {@link XOPData}.
     * 
     * @param data
     *            data containing the document
     * @return extracted BTL type
     * @throws IOException
     *             error extracting BTL type instance
     */
    private DataType safeExtractType(XOMXOP data) throws CommunicationException {

        Document doc = new Document(data.getDocument());

        DataType type = null;

        try {
            type = XomTypeFactory.getInstance().createType(doc, dataTypeClass);
        } catch (DeserializationException e) {
            logger.error("Error converting rsb event to btl");
            logger.debug(e);
        }
        return type;

    }

    /**
     * Hook method that may be implemented in subclasses to post process the
     * simple BTL based de-serialization of the data type.
     * 
     * @param data
     *            {@link XOPData} for the event
     * @param extractedType
     *            type already extract with BTL <code>fromDocument</code>
     * @throws IOException
     *             the post processing failed and returning the type to callers
     *             would be illegal
     */
    protected void postProcessType(XOP data, DataType extractedType) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType readLast(long timeout) throws IOException, InterruptedException {
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
        if(!(data.getType().equals(XOP.class))) {
            logger.trace("got non xop data: " + data.getType());
            return;
        }
        XOP xop = (XOP) data.getData();
        
        DataType btlData;
        try {
            btlData = safeExtractType(new XOMXOP(xop));
            queue.push(btlData);

            for (SensorListener<DataType> l : listeners) {
                l.newDataAvailable(btlData);
            }
        } catch (CommunicationException ex) {
            logger.error("Error extracting BTL type from publisher event " + "data of type: "
                    + dataTypeClass.getSimpleName());
            logger.debug(xop.getDocumentAsText(), ex);
        }
    }

    @Override
    public void startNode() {
        queue = new BoundSynchronizedQueue<>(bufferSize);

        try {
        	myRsbListener = RsbListenerRepository.getInstance().requestListener(dataTypeClass, this.getClass(), scope);
        	logger.trace("add THIS as handler for listener");
        	myRsbListener.addHandler(this, true);

        } catch (InterruptedException | RSBException ex) {
            throw new IllegalArgumentException("Unable to activate listener for scope: " + scope, ex);
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

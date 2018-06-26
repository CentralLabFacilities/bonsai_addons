package de.unibi.citec.clf.bonsai.rsb.slots;



import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import java.io.IOException;

import nu.xom.Document;

import org.apache.log4j.Logger;

import rsb.transport.XOMXOP;
import de.unibi.citec.clf.bonsai.core.exception.CommunicationException;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbamWorkingMemory;
import de.unibi.citec.clf.bonsai.util.XPathUtilities;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
import de.unibi.citec.clf.btl.xml.XomSerializerRepository;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import de.unibi.citec.clf.rsbam.model.MemoryRecord;
import java.io.ByteArrayInputStream;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.ParsingException;

public class RsbBtlMemorySlot implements RsbWorkingMemorySlot<Type> {

    private Logger logger = Logger.getLogger(getClass());
    private RsbamWorkingMemory memory;
    private Class<? extends Type> slotType;
    private String slot;
    private String baseTag;

    public RsbBtlMemorySlot() {
    }

    @Override
    public void initialize(RsbamWorkingMemory memory, String slot, Class<? extends Type> dataType) {
        this.memory = memory;
        this.slot = slot;
        this.slotType = dataType;
        this.baseTag = getBaseTag();
    }

    @Override
    public void memorize(Type object) throws CommunicationException {
        logger.trace("Memorizing");
        XOMXOP xopData;
        Document rawDoc;
        try {
            rawDoc = XomTypeFactory.getInstance().createDocument(object);
            xopData = new XOMXOP(XPathUtilities.addParentXPath(rawDoc, slot));
            logger.trace("XOP: " + xopData.getDocumentAsText());
            memory.memorize(slot, baseTag, xopData);
        } catch (SerializationException e) {
            throw new CommunicationException(e.getMessage(), e);
        }

    }

    @Override
    public void forget() throws CommunicationException {
        memory.forget(slot, baseTag);
    }

    @Override
    public Type recall() throws CommunicationException {

        logger.trace("Recalling");
        String xmap = slot + "/" + baseTag;
        String xml = null;
        
        
        Set<MemoryRecord> data = memory.recall(xmap.replaceAll("//", "/"));
        for(MemoryRecord rec : data) {
            //logger.fatal("records:" + data.size() +"\nmemrecord0:"+rec);
            xml = rec.getDataAsXml();
            break;
        }
        
        

        if (xml != null) {
            logger.trace("XOP: " + xml);
            Builder parser = new Builder();
            Document doc = null;
             try {
                doc = parser.build(new ByteArrayInputStream(xml.getBytes()));
            } catch (ParsingException | IOException ex) {
                logger.fatal("PARSE FATAL"+ex.getMessage(),ex);
                throw new CommunicationException("whatever error: " + data);
            }
            
            if (doc == null) {
                throw new CommunicationException("Document empty in XOP: " + data);
            }

            doc = XPathUtilities.removeParentXPath(doc, slot);
            if (doc == null) {
                throw new CommunicationException("Document empty in XOP: " + data);
            }

            // create BTL type
            Type obj = safeExtractType(doc);

            return obj;
        } else {
            logger.debug("XOP: null");
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Type> getDataType() {
        return Type.class;
    }

    private String getBaseTag() {
        XomSerializer<?> serializer = XomSerializerRepository.getSerializer(slotType);
        if (serializer == null) {
            throw new IllegalArgumentException("no serializer found for data type: " + slotType.getSimpleName());
        }
        return serializer.getBaseTag();
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
    private Type safeExtractType(Document doc) throws CommunicationException {

        Type data = null;

        try {
            data = XomTypeFactory.getInstance().createType(doc, slotType);
        } catch (DeserializationException e) {
            logger.error("Error converting rsb event to btl");
            logger.debug(e);
        }
        return data;

    }
    
    @Override
    public void cleanUp() {
    //todo
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
    }
}

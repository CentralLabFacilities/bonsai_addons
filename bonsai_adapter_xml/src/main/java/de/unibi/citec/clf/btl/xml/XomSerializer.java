package de.unibi.citec.clf.btl.xml;



import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;
//import org.dom4j.Attribute;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.xml.serializers.common.TimestampSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

public abstract class XomSerializer<T extends Type> {

    private static final String TIMESTAMP_NODE = "TIMESTAMP";
    private static final String GENERATOR_NODE = "GENERATOR";
    private static final String FRAME_NODE = "FRAME_ID";
    private Logger logger = Logger.getLogger(getClass());
    private boolean legacyParsing = false;

    public Document serialize(T data) throws SerializationException {

        Element parent = new Element(getBaseTag());

        sanitizeElement(parent);

        fillInto(data, parent);

        Document doc = new Document(parent);
        return doc;

    }

    public T deserialize(Document doc) throws DeserializationException {

        try {
            Element element = doc.getRootElement();
            T t = fromElement(element);

            t.setSourceDocument(doc.toXML());
            return t;

        } catch (ParsingException e) {
            String msg = "Parsing Exception for type " + getDataType().getSimpleName() + ": " + e.getMessage();
            logger.fatal(msg);
            logger.debug(e.getMessage(), e);
            throw new DeserializationException(msg, e);
        }
    }

    /**
     * Parses the contents of the given element into the type instance.
     * Therefore element must already be parsed into the base tag of the type.
     * Subclasses must also provide a method with this name and can use the base
     * class' implementation.
     * 
     * @param element
     *            element to get the contents of this type from
     * @param type
     *            type to fill with the contents
     * @throws DeserializationException
     */
    public T fromElement(Element element) throws ParsingException, DeserializationException {

        logger.info(element);
        T t = doFromElement(element);

        // timestamp
        try {

            Element timestampNode = null;
            TimestampSerializer s = new TimestampSerializer();
            try {
                timestampNode = ElementParser.getFirstChildElement(element, s.getBaseTag());
            } catch (IllegalArgumentException e) {
                // ignore this exception if legacy parsing is enabled
                // because it
                // could also be TIMESTAMP
                if (!isLegacyParsing()) {
                    throw e;
                }
            }

            if (timestampNode == null && isLegacyParsing()) {
                timestampNode = ElementParser.getFirstChildElement(element, "TIMESTAMPS");
            }

            Timestamp timestamp = s.fromElement(timestampNode);
            t.setTimestamp(timestamp);

        } catch (IllegalArgumentException e) {
            if (isLegacyParsing()) {
                logger.warn("No timestamp found in the document, " + "but ignoring this for legacy parsing");
            } else {
                logger.warn("I droped one exception sry ;-)");
                //throw e;
            }
        }

        // generator
        try {
            Element generatorNode = ElementParser.getFirstChildElement(element, GENERATOR_NODE);
            t.setGenerator(ElementParser.getTagValue(generatorNode));
        } catch (IllegalArgumentException e) {
            if (isLegacyParsing()) {
                logger.warn("No GENERATOR element found in the document[" + element.getLocalName() + "], "
                        + "but ignoring this for legacy parsing.");
                logger.debug(element.toXML(), e);
            } else {
                throw e;
            }
        }
        
        // frame
        try {
            Element frameNode = ElementParser.getFirstChildElement(element, FRAME_NODE);
            t.setFrameId(ElementParser.getTagValue(frameNode));
        } catch (IllegalArgumentException e) {
            logger.warn("No FRAME_ID element found in the document[" + element.getLocalName() + "], "
                    + "but ignoring this for legacy parsing.");
            logger.debug(element.toXML(), e);
        } catch(UnexpectedElementFormatException e) {
        	logger.warn("FRAME_ID element is empty in the document[" + element.getLocalName() + "], "
                    + "but ignoring this for legacy parsing.");
            logger.debug(element.toXML(), e);
        }

        return t;
    }

    /**
     * Sanitizes the given xml parent element, so that { T extends Type type =
     * new T extends Type();@link Type#fillInto(Element)} is prevented from
     * generating duplicate {@link Element}s and {@link Attribute}s. This method
     * will be called automatically when invoking {@link Type#fillInto(Element)}
     * .
     * 
     * @param parent
     *            parent node that will be sanitized.
     */
    public void sanitizeElement(Element parent) {
        Nodes nodesG = parent.query(GENERATOR_NODE);
        for (int i = 0; i < nodesG.size(); i++) {
            if (nodesG.get(i) instanceof Element) {
                parent.removeChild(nodesG.get(i));
            }
        }
        Nodes nodesF = parent.query(FRAME_NODE);
        for (int i = 0; i < nodesF.size(); i++) {
            if (nodesF.get(i) instanceof Element) {
                parent.removeChild(nodesF.get(i));
            }
        }
        Nodes nodesT = parent.query(TIMESTAMP_NODE);
        for (int i = 0; i < nodesT.size(); i++) {
            if (nodesT.get(i) instanceof Element) {
                parent.removeChild(nodesT.get(i));
            }
        }
        doSanitizeElement(parent);
    }

    /**
     * Fills the given parent node with the contents of this type. Subclasses
     * really should override this method but can use the base class'
     * implementation to fill in the values of the base class without having to
     * duplicate the serialization code.
     * 
     * @param parent
     *            parent node that will be modified to contain the information
     *            stored in this type
     * @throws SerializationException
     */
    public void fillInto(T data, Element parent) throws SerializationException {
        doFillInto(data, parent);
        
        // timestamp
        TimestampSerializer s = new TimestampSerializer();
        Element timestampElement = new Element(s.getBaseTag());
        Timestamp timestamp = new Timestamp(data.getTimestamp());
        s.fillInto(timestamp, timestampElement);
        Nodes tNodes = parent.query(TIMESTAMP_NODE);
        if (tNodes.size() > 0) {
            parent.replaceChild(tNodes.get(0), timestampElement);
        } else {
            parent.appendChild(timestampElement);
        }

        // generator
        Element generatorElement = new Element(GENERATOR_NODE);
        generatorElement.appendChild(data.getGenerator());
        Nodes gNodes = parent.query(GENERATOR_NODE);
        if (gNodes.size() > 0) {
            parent.replaceChild(gNodes.get(0), generatorElement);
        } else {
            parent.appendChild(generatorElement);
        }
        
        // frame
        Element frameElement = new Element(FRAME_NODE);
        frameElement.appendChild(data.getFrameId());
        Nodes fNodes = parent.query(FRAME_NODE);
        if (fNodes.size() > 0) {
            parent.replaceChild(fNodes.get(0), frameElement);
        } else {
            parent.appendChild(frameElement);
        }
    }

    /**
     * Parses the contents of the given element into the type instance.
     * Therefore element must already be parsed into the base tag of the type.
     * Subclasses must also provide a method with this name and can use the base
     * class' implementation.
     * 
     * @param element
     *            element to get the contents of this type from
     * @param type
     *            type to fill with the contents
     * @throws DeserializationException
     */
    public abstract T doFromElement(Element element) throws ParsingException, DeserializationException;

    /**
     * Sanitizes the given xml parent element, so that { T extends Type type =
     * new T extends Type();@link Type#fillInto(Element)} is prevented from
     * generating duplicate {@link Element}s and {@link Attribute}s. This method
     * will be called automatically when invoking {@link Type#fillInto(Element)}
     * .
     * 
     * @param parent
     *            parent node that will be sanitized.
     */
    public abstract void doSanitizeElement(Element parent);

    /**
     * Fills the given parent node with the contents of this type. Subclasses
     * really should override this method but can use the base class'
     * implementation to fill in the values of the base class without having to
     * duplicate the serialization code.
     * 
     * @param parent
     *            parent node that will be modified to contain the information
     *            stored in this type
     * @throws SerializationException
     */
    public abstract void doFillInto(T data, Element parent) throws SerializationException;

    public abstract Class<T> getDataType();

    public abstract String getBaseTag();

    /**
     * Sets the parsing mode for older xml representations that may contain
     * errors in the generator or timestamp tags.
     * 
     * @param legacyParsing
     *            if <code>true</code>, the parsing will not fail directly if
     *            there is an error in the mentioned tag and instead try to
     *            guess good values for them. If <code>false</code> every xml
     *            error will result in a parsing error.
     */
    public void setLegacyParsing(boolean legacyParsing) {
        this.legacyParsing = legacyParsing;
    }

    /**
     * Tells whether a legacy parsing mechanism should be used to fill this
     * element or not. See {@link #setLegacyParsing(boolean)} for details.
     * 
     * 
     * @return <code>true</code> if legacy parsing shall be enabled, else
     *         <code>false</code>
     */
    public boolean isLegacyParsing() {
        return legacyParsing;
    }

    public static class SerializationException extends Exception {
        private static final long serialVersionUID = -132201105128523928L;

        public SerializationException() {
            super();
        }

        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public SerializationException(String message) {
            super(message);
        }

        public SerializationException(Throwable cause) {
            super(cause);
        }
    }

    public static class DeserializationException extends Exception {
        private static final long serialVersionUID = 7350863188660012099L;

        public DeserializationException() {
            super();
        }

        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DeserializationException(String message) {
            super(message);
        }

        public DeserializationException(Throwable cause) {
            super(cause);
        }
    }
}

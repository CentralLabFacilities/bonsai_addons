package de.unibi.citec.clf.btl.xml.serializers.object;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.data.object.ObjectOrder;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.map.ViewpointSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * This class is used to store an objectName with the orderer of it. *
 *
 * @author vlosing
 */
public class ObjectOrderSerializer extends XomSerializer<ObjectOrder> {

    private static Logger logger = Logger.getLogger(ObjectOrder.class);

    private static String ATTR_OBJECT_NAME = "OBJECTNAME";
    private static String ATTR_OBJECT_CATEGORY = "OBJECTCATEGORY";
    private static String ATTR_ORDERER_NAME = "ORDERERNAME";
    private static String ATTR_FACEID = "FACEID";

    ViewpointSerializer viewSerializer = new ViewpointSerializer();

    @Override
    public String getBaseTag() {
        return "OBJECTORDER";
    }

    @Override
    public ObjectOrder doFromElement(Element element) throws ParsingException,
            DeserializationException {

        ObjectOrder type = new ObjectOrder();

        logger.debug("fromElement called");

        type.setObjectName(ElementParser.getAttributeValue(element, ATTR_OBJECT_NAME));
        type.setCategory(ElementParser.getAttributeValue(element, ATTR_OBJECT_CATEGORY));
        type.setOrdererName(ElementParser.getAttributeValue(element,ATTR_ORDERER_NAME));
        type.setOrdererFaceClassId(ElementParser.getIntAttributeValue(element, ATTR_FACEID));
        try {
            Element viewElem = ElementParser.getFirstChildElement(element,
                    viewSerializer.getBaseTag());
            type.setTargetLocation(viewSerializer.fromElement(viewElem));
        } catch (IllegalArgumentException e) {
            logger.warn("tag for target location missing in data!");
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(ObjectOrder type, Element parent)
            throws SerializationException {

        parent.addAttribute(new Attribute(ATTR_OBJECT_NAME, type.getObjectName()));
        parent.addAttribute(new Attribute(ATTR_ORDERER_NAME, type.getOrdererName()));
        parent.addAttribute(new Attribute(ATTR_OBJECT_CATEGORY, type.getCategory()));
        parent.addAttribute(new Attribute(ATTR_FACEID, String.valueOf(type.getOrdererFaceClassId())));

        Element targetElem = new Element(viewSerializer.getBaseTag());
        if (type.getTargetLocation() != null) {
            viewSerializer.fillInto(type.getTargetLocation(), targetElem);
            parent.appendChild(targetElem);
        }
    }

    @Override
    public Class<ObjectOrder> getDataType() {
        return ObjectOrder.class;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }
}

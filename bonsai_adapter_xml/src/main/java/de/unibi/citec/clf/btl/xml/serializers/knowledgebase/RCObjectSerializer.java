package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.RCObject;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class RCObjectSerializer extends XomSerializer<RCObject> {

    @Override
    public RCObject doFromElement(Element element) throws ParsingException, DeserializationException {
        RCObject obj = new RCObject();
        
        obj.setName(ElementParser.getAttributeValue(element, "name"));
        obj.setLocation(ElementParser.getAttributeValue(element, "location"));
        obj.setCategory(ElementParser.getAttributeValue(element, "category"));
        obj.setShape(ElementParser.getAttributeValue(element, "shape"));
        obj.setColor(ElementParser.getAttributeValue(element, "color"));
        obj.setSize(ElementParser.getIntAttributeValue(element, "size"));
        obj.setType(ElementParser.getAttributeValue(element, "type"));
        obj.setWeight(ElementParser.getIntAttributeValue(element, "weight"));
        
        return obj;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(RCObject data, Element parent) throws SerializationException {
        parent.addAttribute(new Attribute("name", data.getName()));
        parent.addAttribute(new Attribute("location", data.getLocation()));
        parent.addAttribute(new Attribute("category", data.getCategory()));
        parent.addAttribute(new Attribute("shape", data.getShape()));
        parent.addAttribute(new Attribute("color", data.getColor()));
        parent.addAttribute(new Attribute("type", data.getType()));
        parent.addAttribute(new Attribute("size", String.valueOf(data.getSize())));
        parent.addAttribute(new Attribute("weight", String.valueOf(data.getWeight())));
    }

    @Override
    public Class<RCObject> getDataType() {
        return RCObject.class;
    }

    @Override
    public String getBaseTag() {
        return "RCOBJECT";
    }
    
}

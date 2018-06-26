package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.Door;
import de.unibi.citec.clf.btl.data.map.Annotation;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.map.AnnotationSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class DoorSerializer extends XomSerializer<Door> {

    AnnotationSerializer as = new AnnotationSerializer();

    @Override
    public Door doFromElement(Element element) throws ParsingException, XomSerializer.DeserializationException {
        Door door = new Door();

        door.setRoomOne(ElementParser.getAttributeValue(element, "roomone"));
        door.setRoomTwo(ElementParser.getAttributeValue(element, "roomtwo"));

        Elements subElements = element.getChildElements();
        for (int i = 0; i < subElements.size(); i++) {
            if (as.getBaseTag().equals(subElements.get(i).getLocalName())) {
                Annotation a = as.doFromElement(subElements.get(i));
                door.setAnnotation(a);
            }
        }

        return door;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(Door data, Element parent) throws XomSerializer.SerializationException {
        parent.addAttribute(new Attribute("roomone", data.getRoomOne()));
        parent.addAttribute(new Attribute("roomtwo", data.getRoomTwo()));

        Element anotNode = new Element(as.getBaseTag());
        as.fillInto(data.getAnnotation(), anotNode);
        parent.appendChild(anotNode);

    }

    @Override
    public Class<Door> getDataType() {
        return Door.class;
    }

    @Override
    public String getBaseTag() {
        return "DOOR";
    }

}

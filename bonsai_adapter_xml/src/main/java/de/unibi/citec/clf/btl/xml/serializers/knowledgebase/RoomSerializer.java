package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.Room;
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
public class RoomSerializer extends XomSerializer<Room> {
    
    AnnotationSerializer as = new AnnotationSerializer();

    @Override
    public Room doFromElement(Element element) throws ParsingException, DeserializationException {
        Room room = new Room();
        
        room.setName(ElementParser.getAttributeValue(element, "name"));
        
        room.setNumberOfDoors(ElementParser.getIntAttributeValue(element, "numberofdoors"));
        
        Elements subElements = element.getChildElements();
        for(int i = 0; i < subElements.size(); i++){
            if(as.getBaseTag().equals(subElements.get(i).getLocalName())){
                Annotation a = as.doFromElement(subElements.get(i));
                room.setAnnotation(a);
            }
        }
        
        return room;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(Room data, Element parent) throws SerializationException {
        parent.addAttribute(new Attribute("name", data.getName()));
        
        parent.addAttribute(new Attribute("numberofdoors", String.valueOf(data.getNumberOfDoors())));
        
        Element anotNode = new Element(as.getBaseTag());
        as.fillInto(data.getAnnotation(), anotNode);
        parent.appendChild(anotNode);
        
    }

    @Override
    public Class<Room> getDataType() {
        return Room.class;
    }

    @Override
    public String getBaseTag() {
        return "ROOM";
    }
    
}

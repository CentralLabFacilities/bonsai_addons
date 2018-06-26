package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.Location;
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
public class LocationSerializer extends XomSerializer<Location> {

    AnnotationSerializer as = new AnnotationSerializer();

    @Override
    public Location doFromElement(Element element) throws ParsingException, DeserializationException {
        Location loc = new Location();

        loc.setIsBeacon(ElementParser.getBooleanAttributeValue(element, "isbeacon"));
        loc.setIsPlacement(ElementParser.getBooleanAttributeValue(element, "isplacement"));
        loc.setName(ElementParser.getAttributeValue(element, "name"));
        loc.setRoom(ElementParser.getAttributeValue(element, "room"));

        Elements subElements = element.getChildElements();
        for (int i = 0; i < subElements.size(); i++) {
            if (as.getBaseTag().equals(subElements.get(i).getLocalName())) {
                Annotation a = as.doFromElement(subElements.get(i));
                loc.setAnnotation(a);
            }
        }
        return loc;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(Location data, Element parent) throws SerializationException {
        parent.addAttribute(new Attribute("name", data.getName()));
        parent.addAttribute(new Attribute("room", data.getRoom()));
        parent.addAttribute(new Attribute("isbeacon", String.valueOf(data.isBeacon())));
        parent.addAttribute(new Attribute("isplacement", String.valueOf(data.isPlacement())));
        
        Element anotNode = new Element(as.getBaseTag());
        as.fillInto(data.getAnnotation(), anotNode);
        parent.appendChild(anotNode);
    }

    @Override
    public Class<Location> getDataType() {
        return Location.class;
    }

    @Override
    public String getBaseTag() {
        return "LOCATION";
    }

}

package de.unibi.citec.clf.btl.xml.serializers.person;

import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Elements;
import nu.xom.ParsingException;
import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

import java.util.LinkedList;


public class PersonAttributeSerializer extends XomSerializer<PersonAttribute>  {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public String getBaseTag() {
        return "PERSONATTRIBUTE";
    }

    @Override
    public PersonAttribute doFromElement(Element element) throws ParsingException, DeserializationException {
        PersonAttribute pers = new PersonAttribute();
        LinkedList<PersonAttribute.Gesture> gestures = new LinkedList();
        Elements sub = element.getChildElements();
        for(int i=0; i < sub.size(); i++){
            if(sub.get(i).getLocalName().equals("GESTURE")){
                PersonAttribute.Gesture g = PersonAttribute.Gesture.fromString(ElementParser.getAttributeValue(sub.get(i), "gesture"));
                gestures.add(g);
            }
        }

        pers.setGestures(gestures);
        pers.setAge(ElementParser.getAttributeValue(element, "age"));
        pers.setPosture(PersonAttribute.Posture.fromString(ElementParser.getAttributeValue(element, "posture")));
        pers.setGender(PersonAttribute.Gender.fromString(ElementParser.getAttributeValue(element, "gender")));
        pers.setShirtcolor(PersonAttribute.Shirtcolor.fromString(ElementParser.getAttributeValue(element, "shirtcolor")));

        return pers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(PersonAttribute data, Element parent) throws SerializationException {
        for (PersonAttribute.Gesture gesture : data.getGestures()) {
            Element subNode = new Element("GESTURE");
            subNode.addAttribute(new Attribute("gesture", gesture.getGestureName()));
            parent.appendChild(subNode);
        }
        parent.addAttribute(new Attribute("posture", data.getPosture().getPostureName()));
        parent.addAttribute(new Attribute("gender", data.getGender().getGenderName()));
        parent.addAttribute(new Attribute("age", data.getAge()));
        parent.addAttribute(new Attribute("shirtcolor", data.getShirtcolor().getColorName()));

    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<PersonAttribute> getDataType() {
        return PersonAttribute.class;
    }

}

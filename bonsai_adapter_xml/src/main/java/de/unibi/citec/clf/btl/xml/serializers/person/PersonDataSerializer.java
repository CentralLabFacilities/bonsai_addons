package de.unibi.citec.clf.btl.xml.serializers.person;



import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.navigation.PositionDataSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

public class PersonDataSerializer extends XomSerializer<PersonData> {

    private Logger logger = Logger.getLogger(getClass());

    private PositionDataSerializer pds = new PositionDataSerializer();
    private PersonAttributeSerializer pas = new PersonAttributeSerializer();

    @Override
    public String getBaseTag() {
        return "PERSONDATA";
    }

    @Override
    public PersonData doFromElement(Element element) throws ParsingException, DeserializationException {
        PersonData pers = new PersonData();

        //rebuild persondata stuff
        pers.setUuid(ElementParser.getAttributeValue(element, "uuid"));
        pers.setName(ElementParser.getAttributeValue(element, "name"));

        //rebuild personAttributes
        PersonAttribute pa = new PersonAttribute();
        pa.setAge(ElementParser.getAttributeValue(element, "age"));
        pa.setGender(PersonAttribute.Gender.fromString(ElementParser.getAttributeValue(element, "gender")));
        pa.setPosture(PersonAttribute.Posture.fromString(ElementParser.getAttributeValue(element, "posture")));
        pa.setShirtcolor(PersonAttribute.Shirtcolor.fromString(ElementParser.getAttributeValue(element, "shirtcolor")));
        //pa.setGesture(PersonAttribute.Gesture.fromString(ElementParser.getAttributeValue(element, "gesture")));
        pers.setPersonAttribute(pa);

        //rebuild position
        Nodes nodes = element.query(pds.getBaseTag());
        int nrNodes = nodes.size();
        for (int i = 0; i < nrNodes; i++) {

            Node node = nodes.get(i);
            if (node instanceof Element) {

                Element positionElement = (Element) node;

                try {
                    PositionData parsed = pds.fromElement(positionElement);
                    pers.setPosition(parsed);
                } catch (ParsingException e) {
                    pers.setPosition(null);
                    e.printStackTrace();
                }

            }
        }
        return pers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(PersonData data, Element parent) throws SerializationException {

        //fill stuff from personattributes
        PersonAttribute pa = data.getPersonAttribute();
        parent.addAttribute(new Attribute("gender", pa.getGender().getGenderName()));
        parent.addAttribute(new Attribute("posture", pa.getPosture().getPostureName()));
        parent.addAttribute(new Attribute("shirtcolor", pa.getShirtcolor().getColorName()));
        parent.addAttribute(new Attribute("age", pa.getAge()));
        //parent.addAttribute(new Attribute("gesture", pa.getGesture().getGestureName()));

        // fill stuff from persondata itself
        parent.addAttribute(new Attribute("name", data.getName()));
        parent.addAttribute(new Attribute("uuid", data.getUuid()));

        Element pointNode = new Element(pds.getBaseTag());
        pds.fillInto(data.getPosition(), pointNode);
        parent.appendChild(pointNode);

    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }
}

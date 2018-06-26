package de.unibi.citec.clf.btl.xml.serializers.vision1d;



import nu.xom.Attribute;
import nu.xom.Element;
import de.unibi.citec.clf.btl.data.vision1d.SonarData;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * This class is used to store data from the sonar sensors from nao. It holds
 * the distances to objects in front of the left or right sensor.
 * 
 * @author sebschne
 */
public class SonarDataSerializer extends XomSerializer<SonarData> {


    public SonarDataSerializer() {
    }


    @Override
    public String getBaseTag() {
        return "SONARDATA";
    }

    @Override
    public SonarData doFromElement(Element element) {
        SonarData type = new SonarData();

        Element leftElement = element.getFirstChildElement("DISTLEFT");
        type.setDistanceLeft(
                ElementParser.getDoubleAttributeValue(leftElement, "value"),
                LengthUnit.METER);
        Element rightElement = element.getFirstChildElement("DISTRIGHT");
        type.setDistanceRight(
                ElementParser.getDoubleAttributeValue(rightElement, "value"),
                LengthUnit.METER);

        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(SonarData data, Element parent) throws SerializationException {

        Element rightElement = new Element("DISTRIGHT");
        parent.appendChild(rightElement);
        rightElement.addAttribute(new Attribute("value", String
                .valueOf(data.getDistanceRight(LengthUnit.METER))));
        Element leftElement = new Element("DISTLEFT");
        parent.appendChild(leftElement);
        leftElement.addAttribute(new Attribute("value", String
                .valueOf(data.getDistanceLeft(LengthUnit.METER))));

    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<SonarData> getDataType() {
        return SonarData.class;
    }

}

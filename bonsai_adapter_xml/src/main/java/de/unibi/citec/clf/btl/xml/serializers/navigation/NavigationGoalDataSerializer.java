package de.unibi.citec.clf.btl.xml.serializers.navigation;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * This class represents a location in world Coordinates (X[m], Y[m], Yaw[rad]),
 * which is sent as goal to the robot. It is also possible to adjust the
 * tolerance of the short-range navigation using coordinateTolerance and
 * yawTolerance.
 * 
 * @author unknown
 * @author jwienke
 */
public class NavigationGoalDataSerializer extends XomSerializer<NavigationGoalData> {

    PositionDataSerializer pds = new PositionDataSerializer();

    @Override
    public String getBaseTag() {
        return "NAVIGATIONGOAL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(NavigationGoalData data, Element parent) throws SerializationException {


        // add parent class as a subelement in tree
        Element parentClass = new Element(pds.getBaseTag());
        pds.doFillInto(data, parentClass);
        parent.appendChild(parentClass);
        // set coordinatetolerance and yawlolerance from navigationgoaldata
        parent.addAttribute(new Attribute("coordinatetolerance",
                String.valueOf(
                        data.getCoordinateTolerance(NavigationGoalData.iLU))));
        parent.addAttribute(new Attribute("yawtolerance",
                String.valueOf(
                        data.getYawTolerance(NavigationGoalData.iAU))));
    }

    @Override
    public NavigationGoalData doFromElement(Element element) throws ParsingException, DeserializationException {


        // read attributes of parent class from positiondataserializer
        Element elementPositionData = element.getFirstChildElement(pds.getBaseTag());
        NavigationGoalData ngd = (NavigationGoalData) pds.doFromElement(elementPositionData);
        // read coordinatetolerance and yawlolerance from element
        ngd.setCoordinateTolerance(
                ElementParser.getDoubleAttributeValue(element,"coordinatetolerance"),
                NavigationGoalData.iLU);
        ngd.setYawTolerance(
                ElementParser.getDoubleAttributeValue(element,"yawtolerance"),
                NavigationGoalData.iAU);


        return ngd;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<NavigationGoalData> getDataType() {
        return NavigationGoalData.class;
    }

}

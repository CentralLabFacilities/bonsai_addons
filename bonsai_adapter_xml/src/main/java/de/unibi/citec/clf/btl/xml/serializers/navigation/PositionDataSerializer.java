package de.unibi.citec.clf.btl.xml.serializers.navigation;



import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point2DSerializer;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;


/**
 * Base class for all sensor results containing (robot/world) positions (X, Y,
 * theta).
 */
public class PositionDataSerializer extends XomSerializer<PositionData> {

    Point2DSerializer p2ds = new Point2DSerializer();
    /**
     * Creates a position data object initialized with x, y and theta set to 0.0
     * and the timestamp set to the current time.
     */
    public PositionDataSerializer() {
        setLegacyParsing(true);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PositionDataSerializer)) {
            return false;
        }

        PositionDataSerializer other = (PositionDataSerializer) obj;

        return other.equals(this);

    }

    /**
     * Constructs a {@link PositionDataSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param element
     *            The XOM {@link Element} to construct an object from.
     * @return The {@link PositionDataSerializer} object containing all the
     *         information given by the {@link Element} object.
     * @throws ParsingException
     */
    @Override
    public PositionData doFromElement(Element element) throws ParsingException, DeserializationException {

         // read attributes of parent class from point2dserializer
        Element elementCoordinates = element.getFirstChildElement(p2ds.getBaseTag());
        Point2D p2d = p2ds.doFromElement(elementCoordinates);
        PositionData pd = new PositionData();
        pd.setY(p2d.getY(LengthUnit.METER), LengthUnit.METER);
        pd.setX(p2d.getX(LengthUnit.METER), LengthUnit.METER);
        // read yaw from element
        pd.setYaw(
                ElementParser.getDoubleAttributeValue(element, "theta"),
                AngleUnit.RADIAN);
        pd.setFrameId(ElementParser.getAttributeValue(element, "frameid"));
        return pd;
    }

    @Override
    public void doSanitizeElement(Element parent) {
        // TODO Auto-generated method stub

    }

    /**
     * Serializes the {@link PositionDataSerializer} object into a given XOM
     * {@link Element}.
     * 
     * @param parent
     *            The {@link Element} to serialize the object into. The given
     *            {@link Element} object should have the base tag defined by
     *            this class. (see {@link #getClass().getSimpleName()})
     * @see #getClass().getSimpleName()
     */
    @Override
    public void doFillInto(PositionData data, Element parent) throws SerializationException {

        // add parent class as a subelement in tree
        Element parentClass = new Element(p2ds.getBaseTag());
        p2ds.doFillInto(data, parentClass);
        parent.appendChild(parentClass);

        parent.addAttribute(new Attribute("theta", String.valueOf(data
                .getYaw(AngleUnit.RADIAN))));
        parent.addAttribute(new Attribute("frameid", data.getFrameId()));

    }

    @Override
    public Class<PositionData> getDataType() {
        return PositionData.class;
    }

    @Override
    public String getBaseTag() {
        return "POSITIONDATA";
    }

}

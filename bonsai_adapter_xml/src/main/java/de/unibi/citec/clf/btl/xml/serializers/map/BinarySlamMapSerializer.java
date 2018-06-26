package de.unibi.citec.clf.btl.xml.serializers.map;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.map.BinarySlamMap;
import de.unibi.citec.clf.btl.data.map.DynamicGridMap;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.navigation.PositionDataSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * A container for both the position and the map obtained from the SLAM
 * algorithm. The representation of the map is in the form of a probabilistic
 * occupancy grid: values of 0.0 means certainly occupied, 1.0 means a certainly
 * empty cell. Initially 0.5 means uncertainty.
 * 
 * Keep in mind that the occupancy grid is ordered like the first quadrant of a
 * mathematical plot (x to the right, y upwards, origin in the bottom left), NOT
 * like an image in computer graphics (x right, y down, origin at the the top
 * left).
 * 
 * @author dklotz
 * @author others (nkoester?, sebsche?)
 * @author jwienke
 * @author lziegler
 */
public class BinarySlamMapSerializer extends XomSerializer<BinarySlamMap> {

    private PositionDataSerializer posSerializer = new PositionDataSerializer();
    private DynamicGridMapSerializer mapSerializer = new DynamicGridMapSerializer();

    @Override
    public String getBaseTag() {
        return "BINARYSLAMMAP";
    }

    @Override
    public BinarySlamMap doFromElement(Element element) throws ParsingException, DeserializationException {
        BinarySlamMap data = new BinarySlamMap();

        Element posElement = ElementParser.getFirstChildElement(element, "POSITION");
        PositionData pos = posSerializer.fromElement(posElement);
        data.setX(pos.getX(LengthUnit.METER), LengthUnit.METER);
        data.setY(pos.getY(LengthUnit.METER), LengthUnit.METER);
        data.setYaw(pos.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);

        // parse rest of metadata
        DynamicGridMap map = mapSerializer.fromElement(element);
        data.setDynamicGridMap(map);
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(BinarySlamMap type, Element parent) throws SerializationException {
        Element posElem = new Element("POSITION");
        posSerializer.fillInto(type, posElem);
        parent.appendChild(posElem);

        mapSerializer.fillInto(type.getDynamicGridMap(), parent);
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<BinarySlamMap> getDataType() {
        return BinarySlamMap.class;
    }
}

package de.unibi.citec.clf.btl.xml.serializers.map;



import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.navigation.PositionDataSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.Type;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
/**
 * Representation of a viewpoint. Each viewpoint has coordinates (x, y, yaw), a
 * prescribed category and a unique label.
 *
 * @author rfeldhans
 */

public class ViewpointSerializer extends XomSerializer<Viewpoint> {

    PositionDataSerializer pds = new PositionDataSerializer();

	/**
     * Default constructor as expected by {@link Type}. You should not use it!
     */
	public ViewpointSerializer() {
		
	}
	
    /**
     * Getter for the XML base tag used for (de-)serialization.
     *
     * @return XML base tag.
     */
    @Override
	public Class<Viewpoint> getDataType() {
		return Viewpoint.class;
	}

	@Override
	public String getBaseTag() {
        return "VIEWPOINT";
    }
       
    @Override
    public boolean equals(Object obj) {
        try {
            if (!(obj instanceof ViewpointSerializer)) {
                return false;
            }

            ViewpointSerializer other = (ViewpointSerializer) obj;

            return other.equals(this);
            
        } catch (Exception e) {
            return false;
        }

    }

	@Override
	public Viewpoint doFromElement(Element element)
			throws ParsingException, DeserializationException {

        // read attributes of parent class from positiondataserializer
        Element elementPositionData = element.getFirstChildElement(pds.getBaseTag());
        PositionData pd = pds.doFromElement(elementPositionData);
        Viewpoint vp = new Viewpoint();
        vp.setY(pd.getY(LengthUnit.METER), LengthUnit.METER);
        vp.setX(pd.getX(LengthUnit.METER), LengthUnit.METER);
        vp.setYaw(pd.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        vp.setFrameId(pd.getFrameId());
        // read label and category from element
        vp.setLabel(ElementParser.getAttributeValue(element, "label"));

        return vp;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFillInto(Viewpoint data, Element parent) throws SerializationException {
        // add parent class as a subelement in tree
        Element positionDataElement = new Element(pds.getBaseTag());
        pds.doFillInto(data, positionDataElement);
        parent.appendChild(positionDataElement);

        parent.addAttribute(new Attribute("label",
                data.getLabel()));
		
	}
}

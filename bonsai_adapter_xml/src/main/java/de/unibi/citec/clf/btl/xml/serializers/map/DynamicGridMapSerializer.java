package de.unibi.citec.clf.btl.xml.serializers.map;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.map.DynamicGridMap;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * A container for the map e.g. obtained from the SLAM algorithm. The
 * representation of the map is in the form of a probabilistic occupancy grid:
 * values of 0.0 means certainly occupied, 1.0 means a certainly empty cell.
 * Initially 0.5 means uncertainty.
 * 
 * Keep in mind that the occupancy grid is ordered like the first quadrant of a
 * mathematical plot (x to the right, y upwards, origin in the bottom left), NOT
 * like an image in computer graphics (x right, y down, origin at the the top
 * left).
 * 
 * @author dklotz
 * @author jwienke
 * @author lziegler
 */
public class DynamicGridMapSerializer extends XomSerializer<DynamicGridMap> {

	private static final String ATTACHMENT_URI_ATTRIBUTE_NAME = "uri";
	private static final String PROPERTY_ELEMENT_NAME = "MAPPROPERTIES";
	private static final String WIDTH_ATTRIBUTE_NAME = "width";
	private static final String HEIGHT_ATTRIBUTE_NAME = "height";
	private static final String ORIGINX_ATTRIBUTE_NAME = "xorigin";
	private static final String ORIGINY_ATTRIBUTE_NAME = "yorigin";
	private static final String RESOLUTION_ATTRIBUTE_NAME = "resolution";

	private static LengthUnit iLU = LengthUnit.METER;

	@Override
	public String getBaseTag() {
		return "DYNAMICGRIDMAP";
	}

	@Override
    public DynamicGridMap doFromElement(Element element)
			throws ParsingException, DeserializationException {
	    
	    DynamicGridMap type = new DynamicGridMap();

		// parse rest of metadata

		type.setUri(element.getAttributeValue(ATTACHMENT_URI_ATTRIBUTE_NAME));

		Element props = element.getFirstChildElement(PROPERTY_ELEMENT_NAME);
		type.setWidth(ElementParser.getIntAttributeValue(props,
				WIDTH_ATTRIBUTE_NAME));
		type.setHeight(ElementParser.getIntAttributeValue(props,
				HEIGHT_ATTRIBUTE_NAME));
		type.setOriginX(ElementParser.getIntAttributeValue(props,
				ORIGINX_ATTRIBUTE_NAME));
		type.setOriginY(ElementParser.getIntAttributeValue(props,
				ORIGINY_ATTRIBUTE_NAME));
		type.setResolution(ElementParser.getDoubleAttributeValue(props,
				RESOLUTION_ATTRIBUTE_NAME),iLU);
		
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSanitizeElement(Element parent) {
		Elements elements = parent.getChildElements(PROPERTY_ELEMENT_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFillInto(DynamicGridMap type, Element parent) throws SerializationException {

		sanitizeElement(parent);

		parent.addAttribute(new Attribute(ATTACHMENT_URI_ATTRIBUTE_NAME,
		        type.getUri()));

		Element propertiesElement = new Element(PROPERTY_ELEMENT_NAME);
		parent.appendChild(propertiesElement);
		propertiesElement.addAttribute(new Attribute(WIDTH_ATTRIBUTE_NAME,
				String.valueOf(type.getWidth())));
		propertiesElement.addAttribute(new Attribute(HEIGHT_ATTRIBUTE_NAME,
				String.valueOf(type.getHeight())));
		propertiesElement.addAttribute(new Attribute(ORIGINX_ATTRIBUTE_NAME,
				String.valueOf(type.getOriginX())));
		propertiesElement.addAttribute(new Attribute(ORIGINY_ATTRIBUTE_NAME,
				String.valueOf(type.getOriginY())));
		propertiesElement.addAttribute(new Attribute(RESOLUTION_ATTRIBUTE_NAME,
				String.valueOf(type.getResolution(iLU))));

	}

	@Override
    public Class<DynamicGridMap> getDataType() {
        return DynamicGridMap.class;
    }
}

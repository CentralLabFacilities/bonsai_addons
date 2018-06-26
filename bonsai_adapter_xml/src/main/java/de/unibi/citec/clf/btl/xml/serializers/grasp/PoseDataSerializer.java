package de.unibi.citec.clf.btl.xml.serializers.grasp;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.grasp.PoseData;
import de.unibi.citec.clf.btl.units.LengthUnit;

public class PoseDataSerializer extends XomSerializer<PoseData>{

	/**
	 * Constructor.
	 */
	public PoseDataSerializer() {
		super();
	}

	@Override
	public Class<PoseData> getDataType() {
		return PoseData.class;
	}

	@Override
	public String getBaseTag() {
		return "currentarmcoord";
	}
	
	/**
	 * Constructs a {@link PoseDataSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PoseDataSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */	
	@Override
	public PoseData doFromElement(Element element)
			throws ParsingException, DeserializationException {
		PoseData pd = new PoseData();
		
		Element poseElement = element.getFirstChildElement("currentxyz");
		pd.setX(ElementParser.getDoubleAttributeValue(poseElement, "x"),LengthUnit.MILLIMETER);
		pd.setY(ElementParser.getDoubleAttributeValue(poseElement, "y"),LengthUnit.MILLIMETER);
		pd.setZ(ElementParser.getDoubleAttributeValue(poseElement, "z"),LengthUnit.MILLIMETER);
		return pd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}
	
	 /**
	 * Serializes the {@link PoseDataSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(PoseData data, Element parent) throws SerializationException {
		Element poseElement = new Element("currentxyz");
		parent.appendChild(poseElement);
		poseElement.addAttribute(new Attribute("x", String.valueOf(data.getX(LengthUnit.MILLIMETER))));
		poseElement.addAttribute(new Attribute("y", String.valueOf(data.getY(LengthUnit.MILLIMETER))));
		poseElement.addAttribute(new Attribute("z", String.valueOf(data.getZ(LengthUnit.MILLIMETER))));	
	}

}

package de.unibi.citec.clf.btl.xml.serializers.grasp;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.grasp.KatanaPoseData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;

public class KatanaPoseDataSerializer extends XomSerializer<KatanaPoseData> {

	/**
	 * Constructor.
	 */
	public KatanaPoseDataSerializer() {
		super();
	}

	@Override
	public Class<KatanaPoseData> getDataType() {
		return KatanaPoseData.class;
	}

	@Override
	public String getBaseTag() {
		return "POSITION";
	}

	/**
	 * Constructs a {@link KatanaPoseDataSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link KatanaPoseDataSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */	
	@Override
	public KatanaPoseData doFromElement(Element element)
			throws ParsingException, DeserializationException {
		
		KatanaPoseData kpd = new KatanaPoseData();
		
		Element poseElement = element.getFirstChildElement("P");
		kpd.setX(ElementParser.getDoubleAttributeValue(poseElement, "x"),LengthUnit.MILLIMETER);
		kpd.setY(ElementParser.getDoubleAttributeValue(poseElement, "y"),LengthUnit.MILLIMETER);
		kpd.setZ(ElementParser.getDoubleAttributeValue(poseElement, "z"),LengthUnit.MILLIMETER);

		Element angleElement = element.getFirstChildElement("ANGLE");
		kpd.setPhi(ElementParser.getDoubleAttributeValue(angleElement, "phi"),AngleUnit.RADIAN);
		kpd.setPsi(ElementParser.getDoubleAttributeValue(angleElement, "psi"),AngleUnit.RADIAN);
		kpd.setTheta(ElementParser.getDoubleAttributeValue(angleElement,
				"theta"),AngleUnit.RADIAN);
		return kpd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}
	 
	 /**
	 * Serializes the {@link KatanaPoseDataSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(KatanaPoseData data, Element parent) throws SerializationException {
	
		Element poseElement = new Element("P");
		parent.appendChild(poseElement);
		poseElement.addAttribute(new Attribute("x", String.valueOf(data.getX(LengthUnit.MILLIMETER))));
		poseElement.addAttribute(new Attribute("y", String.valueOf(data.getY(LengthUnit.MILLIMETER))));
		poseElement.addAttribute(new Attribute("z", String.valueOf(data.getZ(LengthUnit.MILLIMETER))));

		Element angleElement = new Element("ANGLE");
		parent.appendChild(angleElement);
		angleElement
				.addAttribute(new Attribute("phi", String.valueOf(data.getPhi(AngleUnit.RADIAN))));
		angleElement
				.addAttribute(new Attribute("psi", String.valueOf(data.getPsi(AngleUnit.RADIAN))));
		angleElement.addAttribute(new Attribute("theta", String
				.valueOf(data.getTheta(AngleUnit.RADIAN))));
		
	}

}

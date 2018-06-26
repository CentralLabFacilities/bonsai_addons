package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point3DSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.data.vision3d.KinectData;

public class KinectDataSerializer extends XomSerializer<KinectData> {

	private static final Logger logger = Logger.getLogger(KinectDataSerializer.class);
	
	public KinectDataSerializer() {
		super();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<KinectData> getDataType() {
		return KinectData.class;
	}

	@Override
	public String getBaseTag() {
		return "KINECTDATA";
	}
	
	/**
	 * Serializes the {@link KinectDataSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(KinectData data, Element parent) throws SerializationException {
		// cleanup
		Elements elements = parent.getChildElements(KinectData.TILT_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		elements = parent.getChildElements(KinectData.ACCELERATION_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}

		Element transElement = new Element(KinectData.TILT_TAG_NAME);
		transElement.addAttribute(new Attribute("value", String
				.valueOf(data.getTilt(AngleUnit.RADIAN))));

		Element rotElement = new Element(KinectData.ACCELERATION_TAG_NAME);
		rotElement.addAttribute(new Attribute("x", String
				.valueOf(data.getAccelerationX())));
		rotElement.addAttribute(new Attribute("y", String
				.valueOf(data.getAccelerationY())));
		rotElement.addAttribute(new Attribute("z", String
				.valueOf(data.getAccelerationZ())));

		parent.appendChild(transElement);
		parent.appendChild(rotElement);
		
	}

	/**
	 * Constructs a {@link KinectDataSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link KinectDataSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 */
	@Override
	public KinectData doFromElement(Element element)
			throws ParsingException, DeserializationException {
	
		KinectData kd = new KinectData();
		
		Element transElement = element.getFirstChildElement(KinectData.TILT_TAG_NAME);
		double tilt = ElementParser.getDoubleAttributeValue(transElement,
				"value");
		kd.setTilt(tilt, AngleUnit.RADIAN);

		Element rotElement = element
				.getFirstChildElement(KinectData.ACCELERATION_TAG_NAME);
		double accX = ElementParser.getDoubleAttributeValue(rotElement, "x");
		double accY = ElementParser.getDoubleAttributeValue(rotElement, "y");
		double accZ = ElementParser.getDoubleAttributeValue(rotElement, "z");
		kd.setAcceleration(accX, accY, accZ);
		
		return kd;
	}
	
	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof Point3DSerializer))
				return false;

			Point3DSerializer other = (Point3DSerializer) obj;

			super.equals(other);

		} catch (Exception e) {
			logger.error("equals() Exception: " + e.getMessage());
			return false;
		}
		return true;
	}

}

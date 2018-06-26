package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;

public class Pose3DSerializer extends XomSerializer<Pose3D> {
	
	public static final String TRANSLATION_TAG_NAME = "TRANSLATION";
	public static final String ROTATION_TAG_NAME = "ROTATION";
	
	private Point3DSerializer translation = new Point3DSerializer();
	private Rotation3DSerializer rotation = new Rotation3DSerializer();
	
	/**
	 * Creates a new instance.
	 */
	public Pose3DSerializer() {
		super();
		setLegacyParsing(true);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pose3DSerializer) {
			Pose3DSerializer other = (Pose3DSerializer) obj;
			return super.equals(other);
		}
		return false;
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<Pose3D> getDataType() {
		return Pose3D.class;
	}

	@Override
	public String getBaseTag() {
		return "POSE3D";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFillInto(Pose3D data, Element parent) throws SerializationException {

		// cleanup
		Elements elements = parent.getChildElements(TRANSLATION_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		elements = parent.getChildElements(ROTATION_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}

		// fill
		Element originElement0 = new Element(TRANSLATION_TAG_NAME);
		Element originElement1 = new Element(translation.getBaseTag());
		translation.fillInto(data.getTranslation(), originElement1);
		originElement0.appendChild(originElement1);
		parent.appendChild(originElement0);

		Element rotElement = new Element(ROTATION_TAG_NAME);
		Element rotElement1 = new Element(rotation.getBaseTag());
		rotation.fillInto(data.getRotation(), rotElement1);
		rotElement.appendChild(rotElement1);
		parent.appendChild(rotElement);

	}

	/**
	 * Fills an {@link BoundingBox3DSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param hypothesisElement
	 *            The XOM {@link Element} to fill an object from.
	 * @param type
	 *            The {@link BoundingBox3DSerializer} object to fill with all the
	 *            information given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public Pose3D doFromElement(Element dataElement)
			throws ParsingException, DeserializationException {

		Pose3D pose = new Pose3D();
		
		try {
			Element originElement = dataElement
					.getFirstChildElement(TRANSLATION_TAG_NAME);
			Point3DSerializer translation = new Point3DSerializer();
			Element originElement1 = originElement
					.getFirstChildElement(translation.getBaseTag());
			pose.setTranslation(translation.fromElement(originElement1));

			Element rotElement = dataElement
					.getFirstChildElement(ROTATION_TAG_NAME);
			Rotation3DSerializer rotation = new Rotation3DSerializer();
			Element rotElement1 = rotElement
					.getFirstChildElement(rotation.getBaseTag());
			if (rotElement1 == null) {
				throw new ParsingException("Missing tag " + ROTATION_TAG_NAME);
			}
			
			pose.setRotation(rotation.fromElement(rotElement1));

		} catch (IllegalArgumentException e) {
			throw new ParsingException(e.getMessage(), e);
		}
		return pose;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		
	}
}

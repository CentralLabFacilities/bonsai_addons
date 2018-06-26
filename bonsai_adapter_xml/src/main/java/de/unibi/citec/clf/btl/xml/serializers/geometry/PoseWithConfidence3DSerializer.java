package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.geometry.PoseWithConfidence3D;
import de.unibi.citec.clf.btl.xml.XomSerializer;

public class PoseWithConfidence3DSerializer extends XomSerializer<PoseWithConfidence3D> {

	/**
	 * Creates a new instance.
	 */
	public PoseWithConfidence3DSerializer() {
		super();
	}
	
	@Override
	public Class<PoseWithConfidence3D> getDataType() {
		return PoseWithConfidence3D.class;
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public String getBaseTag() {
		return "POSEWITHCONFIDENCE3D";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFillInto(PoseWithConfidence3D data, Element parent) throws SerializationException {

		// fill
		Element transElement = new Element(Pose3DSerializer.TRANSLATION_TAG_NAME);
		transElement.addAttribute(new Attribute("confidence", String
				.valueOf(data.getTranslationConfidence())));

		Element rotElement = new Element(Pose3DSerializer.ROTATION_TAG_NAME);
		rotElement.addAttribute(new Attribute("confidence", String
				.valueOf(data.getRotationConfidence())));

	}

	/**
	 * Fills an {@link BoundingBox3DSerializer} object from a given XOM {@link Element}
	 * .
	 * 
	 * @param hypothesisElement
	 *            The XOM {@link Element} to fill an object from.
	 * @param type
	 *            The {@link BoundingBox3DSerializer} object to fill with all the
	 *            information given by the {@link Element} object.
	 * @throws ParsingException
	 */
	public PoseWithConfidence3D doFromElement(Element dataElement)
			throws ParsingException, DeserializationException {

		PoseWithConfidence3D pose = new PoseWithConfidence3D();
		
		try {

			if (!dataElement.getLocalName().equals(getBaseTag())) {
				throw new ParsingException("Parsing Error: Base tag "
						+ "must be '" + getClass().getSimpleName() + "'");
			}

			Element transElement = dataElement
					.getFirstChildElement(Pose3DSerializer.TRANSLATION_TAG_NAME);
			double trans = Double.parseDouble(transElement.getAttribute(
					"confidence").getValue());

			Element rotElement = dataElement
					.getFirstChildElement(Pose3DSerializer.ROTATION_TAG_NAME);
			double rot = Double.parseDouble(rotElement.getAttribute(
					"confidence").getValue());

			pose.setTranslationConfidence(trans);
			pose.setTranslationConfidence(rot);

		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present

			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse coordinate values. ", e);
		}
		return pose;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PoseWithConfidence3DSerializer) {
			PoseWithConfidence3DSerializer other = (PoseWithConfidence3DSerializer) obj;
			return super.equals(other);
		}
		return false;
	}
	
	@Override
	public void doSanitizeElement(Element parent) {
	}
}

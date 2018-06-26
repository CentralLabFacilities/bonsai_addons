package de.unibi.citec.clf.btl.xml.serializers.geometry;



import javax.vecmath.Matrix3d;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;

/**
 * This is a generic rotation type. It can be set and read using several
 * representation formats.
 * 
 * @author lziegler
 */
public class Rotation3DSerializer extends XomSerializer<Rotation3D> {

	private static final String BASE_TAG = "ROTATION3D";
	private static final String MATRIX_TAG = "MATRIX";

	/**
	 * Creates a default rotation object (no rotation).
	 */
	public Rotation3DSerializer() {
		super();
	}
	
	/**
	 * Creates a default rotation object (no rotation).
	 */
	public Rotation3DSerializer(Rotation3D o) {
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<Rotation3D> getDataType() {
		return Rotation3D.class;
	}

	@Override
	public String getBaseTag() {
		return BASE_TAG;
	}
	
	/**
	 * Serializes the {@link Rotation3DSerializer} object into a given XOM {@link Element}
	 * .
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(Rotation3D data, Element parent) throws SerializationException {
		Elements oldPositions = parent.getChildElements(BASE_TAG);
		for (int i = 0; i < oldPositions.size(); i++) {
			parent.removeChild(oldPositions.get(i));
		}
		Element pos = new Element(MATRIX_TAG);
		pos.addAttribute(new Attribute("a00", String.valueOf(data.getMatrix().m00)));
		pos.addAttribute(new Attribute("a01", String.valueOf(data.getMatrix().m01)));
		pos.addAttribute(new Attribute("a02", String.valueOf(data.getMatrix().m02)));
		pos.addAttribute(new Attribute("a10", String.valueOf(data.getMatrix().m10)));
		pos.addAttribute(new Attribute("a11", String.valueOf(data.getMatrix().m11)));
		pos.addAttribute(new Attribute("a12", String.valueOf(data.getMatrix().m12)));
		pos.addAttribute(new Attribute("a20", String.valueOf(data.getMatrix().m20)));
		pos.addAttribute(new Attribute("a21", String.valueOf(data.getMatrix().m21)));
		pos.addAttribute(new Attribute("a22", String.valueOf(data.getMatrix().m22)));
		parent.appendChild(pos);
	}

	/**
	 * Constructs a {@link Rotation3DSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link Rotation3DSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public Rotation3D doFromElement(Element objectElement)
			throws ParsingException, DeserializationException {

		try {
			if (objectElement == null) {
				throw new ParsingException(
						"Parsing Error: Empty element when parsing Rotation3D");
			}
			Matrix3d mat = new Matrix3d();

			Element pos = objectElement.getFirstChildElement(MATRIX_TAG);
			mat.m00 = Double.parseDouble(pos.getAttribute("a00").getValue());
			mat.m01 = Double.parseDouble(pos.getAttribute("a01").getValue());
			mat.m02 = Double.parseDouble(pos.getAttribute("a02").getValue());
			mat.m10 = Double.parseDouble(pos.getAttribute("a10").getValue());
			mat.m11 = Double.parseDouble(pos.getAttribute("a11").getValue());
			mat.m12 = Double.parseDouble(pos.getAttribute("a12").getValue());
			mat.m20 = Double.parseDouble(pos.getAttribute("a20").getValue());
			mat.m21 = Double.parseDouble(pos.getAttribute("a21").getValue());
			mat.m22 = Double.parseDouble(pos.getAttribute("a22").getValue());

			Rotation3D rot = new Rotation3D();
			rot.setMatrix(mat);
			return rot;
		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present
			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse matrix values");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof Rotation3DSerializer))
				return false;

			Rotation3DSerializer other = (Rotation3DSerializer) obj;

			return super.equals(other);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void doSanitizeElement(Element parent) {
	}
}

package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.units.LengthUnit;

/**
 * This class represents a point in space by all 3 dimensions.
 * 
 * @author lziegler
 */
public class Point3DSerializer extends XomSerializer<Point3D> {

	private static final Logger logger = Logger.getLogger(Point3DSerializer.class);
    private final static String TAG_FRAMEID = "frame";

	public Point3DSerializer() {
        setLegacyParsing(true);
    }
	
	@Override
	public Class<Point3D> getDataType() {
		return Point3D.class;
	}

	@Override
	public String getBaseTag() {
		return "POINT3D";
	}

	/**
	 * Serializes the {@link Point3DSerializer} object into a given XOM {@link Element}
	 * .
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(Point3D data, Element parent) throws SerializationException {

		parent.addAttribute(new Attribute("x", String
				.valueOf(data.getX(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute("y", String
				.valueOf(data.getY(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute("z", String
				.valueOf(data.getZ(LengthUnit.MILLIMETER))));
		parent.addAttribute(new Attribute(TAG_FRAMEID, data.getFrameId()));
	}
	
	/**
	 * Constructs a {@link Point3DSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link Point3DSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 */
	@Override
	public Point3D doFromElement(Element objectElement)
			throws ParsingException, DeserializationException {

		try {

			double x0 = Double.parseDouble(objectElement.getAttribute("x")
					.getValue());
			double y0 = Double.parseDouble(objectElement.getAttribute("y")
					.getValue());
			double z0 = Double.parseDouble(objectElement.getAttribute("z")
					.getValue());

			String s0;
			try {
				s0 = (objectElement.getAttribute(TAG_FRAMEID)
						.getValue());
			} catch (IllegalArgumentException | NullPointerException e) {
				s0 = "";
			}

            Point3D p = new Point3D();
			p.setFrameId(TAG_FRAMEID);
			p.setX(x0, LengthUnit.MILLIMETER);
			p.setY(y0, LengthUnit.MILLIMETER);
			p.setZ(z0, LengthUnit.MILLIMETER);
			
			return p;

		} catch (NullPointerException ex) {

			logger.error("NullPointer: " + ex.getMessage());

			// this happens when an element or attribute that is required is
			// not present

			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse coordinate values.", e);
		}
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

	@Override
	public void doSanitizeElement(Element parent) {

	}
}

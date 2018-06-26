package de.unibi.citec.clf.btl.xml.serializers.geometry;



import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.units.LengthUnit;

/**
 * This class represents a point in 2 dimensions.
 * 
 * @author lziegler
 */
public class Point2DSerializer extends XomSerializer<Point2D> {
	
	private static Logger logger = Logger.getLogger(Point2DSerializer.class);
    
    private final static String TAG_FRAMEID = "frame";

	public Point2DSerializer() {
	    setLegacyParsing(true);
	}

	@Override
	public Class<Point2D> getDataType() {
		return Point2D.class;
	}

	@Override
	public String getBaseTag() {
		return "POINT2D";
	}

	/**
	 * Serializes the {@link Point2D} object into a given XOM {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(Point2D data, Element parent) throws SerializationException {

		parent.addAttribute(new Attribute("x", String.valueOf(data.getX(LengthUnit.METER))));
		parent.addAttribute(new Attribute("y", String.valueOf(data.getY(LengthUnit.METER))));
		parent.addAttribute(new Attribute(TAG_FRAMEID, String.valueOf(data.getFrameId())));
	}
	
	/**
	 * Constructs a {@link Point2D} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link Point2D} object containing all the information given
	 *         by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public Point2D doFromElement(Element element) throws ParsingException, DeserializationException {

		try {

			if (!element.getLocalName().equals(getBaseTag())) {
				throw new ParsingException("Parsing Error: Base tag "
						+ "for point2d object must be '"
						+ getClass().getSimpleName() + "'");
			}

			double x0 = Double
					.parseDouble(element.getAttribute("x").getValue());
			double y0 = Double
					.parseDouble(element.getAttribute("y").getValue());

			String frameID = "";
			try {
				frameID = (element.getAttribute(TAG_FRAMEID).getValue());
			} catch (IllegalArgumentException | NullPointerException e) {
				frameID = "";
			}

			Point2D p = new Point2D();
			p.setX(x0, LengthUnit.METER);
			p.setY(y0, LengthUnit.METER);
			p.setFrameId(frameID);
			return p;

		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present

			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse coordinate values. ", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null)
			return false;

		if (!Point2D.class.isAssignableFrom(obj.getClass())) {
			logger.debug("equals: not assignable from Point2D");
            return false;
		}

		Point2D other = (Point2D) obj;
		return super.equals(other);
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

	}

}

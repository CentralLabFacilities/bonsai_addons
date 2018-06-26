package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;

/**
 * This is a serializable and iterable polygon type. It is similar to
 * nothing, but has a double precision.
 * 
 * TODO: implement utility functions like those in {@link java.awt.Polygon}.
 * 
 * @author lziegler
 */
public class PrecisePolygonSerializer extends XomSerializer<PrecisePolygon> {

	public PrecisePolygonSerializer(PrecisePolygon poly) {
	}

	public PrecisePolygonSerializer() {
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<PrecisePolygon> getDataType() {
		return PrecisePolygon.class;
	}

	@Override
	public String getBaseTag() {
		return "PRECISEPOLYGON";
	}

	/**
	 * Serializes the {@link PrecisePolygonSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	public void doFillInto(PrecisePolygon data, Element parent) throws SerializationException {
		for (Point2D p : data) {
			Point2DSerializer xp = new Point2DSerializer();
			Element item = new Element(xp.getBaseTag());
			xp.fillInto(p, item);
			parent.appendChild(item);
		}
	}
	
	/**
	 * Constructs a {@link PrecisePolygonSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PrecisePolygonSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */
	public PrecisePolygon doFromElement(Element objectElement)
			throws ParsingException, DeserializationException {

		PrecisePolygon poly = new PrecisePolygon();
		if (objectElement == null) {
			return poly;
		}
		
		try {

			Point2DSerializer s = new Point2DSerializer();
			Elements points = objectElement.getChildElements(s.getBaseTag());
			for (int i = 0; i < points.size(); i++) {
				Element point = points.get(i);
				Point2D p = new Point2DSerializer().fromElement(point);
				poly.addPoint(p);
			}

		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present
			throw new IllegalArgumentException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"could not parse coordinate values");
		}
		return poly;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof PrecisePolygonSerializer))
				return false;

			PrecisePolygonSerializer other = (PrecisePolygonSerializer) obj;

			return super.equals(other);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void doSanitizeElement(Element parent) {
		
	}
}

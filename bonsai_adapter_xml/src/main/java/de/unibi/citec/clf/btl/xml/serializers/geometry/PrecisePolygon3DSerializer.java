package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon3D;

/**
 * This is a serializable and iterable polygon type. It is similar to
 * nothing, but has a double precision and 3D coordinates.
 * 
 * TODO: implement utility functions like those in {@link java.awt.Polygon}.
 * 
 * @author lziegler
 */
public class PrecisePolygon3DSerializer extends XomSerializer<PrecisePolygon3D> {

	public PrecisePolygon3DSerializer() {
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public String getBaseTag() {
		return "PRECISEPOLYGON3D";
	}

	/**
	 * Serializes the {@link PrecisePolygon3DSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(PrecisePolygon3D data, Element parent) throws SerializationException {
		for (Point3D p : data) {
			Point3DSerializer xp = new Point3DSerializer();
			Element item = new Element(xp.getBaseTag());
			xp.fillInto(p, item);
			parent.appendChild(item);
		}
	}

	/**
	 * Constructs a {@link PrecisePolygon3DSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PrecisePolygon3DSerializer} object containing all the
	 *         information given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public PrecisePolygon3D doFromElement(Element objectElement)
			throws ParsingException, DeserializationException {

		PrecisePolygon3D poly = new PrecisePolygon3D();

		try {

			Point3DSerializer s = new Point3DSerializer();
			Elements points = objectElement.getChildElements(s.getBaseTag());
			for (int i = 0; i < points.size(); i++) {
				Element point = points.get(i);
				Point3D p = new Point3DSerializer().fromElement(point);
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

	@Override
	public void doSanitizeElement(Element parent) {
		
	}

	@Override
	public Class<PrecisePolygon3D> getDataType() {
		return PrecisePolygon3D.class;
	}

}

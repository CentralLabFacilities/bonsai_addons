package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.geometry.Cylinder;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.units.LengthUnit;

/**
 * This is a serializable cylinder type.
 * 
 * @author lziegler
 */
public class CylinderSerializer extends XomSerializer<Cylinder> {

	private static LengthUnit internalLengthUnit = LengthUnit.MILLIMETER;

	public static String POSITION_TAG = "POSITION";
	public static String RADIUS_TAG = "RADIUS";
	public static String HEIGHT_TAG = "HEIGHT";

	/**
	 * Constructor.
	 * 
	 * @param position
	 *            Center of the cylinder.
	 * @param rotationX
	 *            Rotation of the cylinder's axis around x-axis.
	 * @param rotationY
	 *            Rotation of the cylinder's axis around y-axis.
	 * @param rotationZ
	 *            Rotation of the cylinder's axis around z-axis.
	 * @param angleUnit
	 *            Unit of the given rotation angles.
	 * @param height
	 *            Height of the cylinder.
	 * @param radius
	 *            Radius of the cylinder.
	 * @param lengthUnit
	 *            Unit of the height and radius values.
	 */
	public CylinderSerializer(Point3DSerializer position, Rotation3DSerializer direction,
			double height, double radius, LengthUnit lengthUnit) {
	}

	/**
	 * Default constructor.
	 */
	public CylinderSerializer() {
		super();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<Cylinder> getDataType() {
		return Cylinder.class;
	}

	@Override
	public String getBaseTag() {
		return "CYLINDER";
	}

	/**
	 * Serializes the {@link CylinderSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	public void doFillInto(Cylinder data, Element parent) throws SerializationException {

		Elements oldPositions = parent.getChildElements(POSITION_TAG);
		for (int i = 0; i < oldPositions.size(); i++) {
			parent.removeChild(oldPositions.get(i));
		}
		Element pos = new Element(POSITION_TAG);
		pos.addAttribute(new Attribute("x", String.valueOf(data.getPosition()
				.getX(internalLengthUnit))));
		pos.addAttribute(new Attribute("y", String.valueOf(data.getPosition()
				.getY(internalLengthUnit))));
		pos.addAttribute(new Attribute("z", String.valueOf(data.getPosition()
				.getZ(internalLengthUnit))));
		parent.appendChild(pos);

		Rotation3DSerializer r = new Rotation3DSerializer();
		Elements oldAxes = parent.getChildElements(r.getBaseTag());
		for (int i = 0; i < oldAxes.size(); i++) {
			parent.removeChild(oldAxes.get(i));
		}

		Element dir = new Element(r.getBaseTag());
		r.fillInto(data.getOrientation(), dir);
		parent.appendChild(dir);

		Elements oldRadius = parent.getChildElements(RADIUS_TAG);
		for (int i = 0; i < oldRadius.size(); i++) {
			parent.removeChild(oldRadius.get(i));
		}
		Element radius = new Element(RADIUS_TAG);
		radius.addAttribute(new Attribute("r", String
				.valueOf(data.getRadius(internalLengthUnit))));
		parent.appendChild(radius);

		Elements oldHeight = parent.getChildElements(HEIGHT_TAG);
		for (int i = 0; i < oldHeight.size(); i++) {
			parent.removeChild(oldHeight.get(i));
		}
		Element height = new Element(HEIGHT_TAG);
		height.addAttribute(new Attribute("h", String
				.valueOf(data.getHeight(internalLengthUnit))));
		parent.appendChild(height);
	}
	
	/**
	 * Constructs a {@link CylinderSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link CylinderSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public Cylinder doFromElement(Element objectElement)
			throws ParsingException, DeserializationException {

		Cylinder cyl = new Cylinder();
		
		try {

			Element pos = objectElement.getFirstChildElement(POSITION_TAG);
			double px = Double.parseDouble(pos.getAttribute("x").getValue());
			double py = Double.parseDouble(pos.getAttribute("y").getValue());
			double pz = Double.parseDouble(pos.getAttribute("z").getValue());
			Element radius = objectElement.getFirstChildElement(RADIUS_TAG);
			double r = Double.parseDouble(radius.getAttribute("r").getValue());
			Element height = objectElement.getFirstChildElement(HEIGHT_TAG);
			double h = Double.parseDouble(height.getAttribute("h").getValue());

			Rotation3DSerializer orientation = new Rotation3DSerializer();

			Element rotElem = objectElement.getFirstChildElement(orientation.getBaseTag());
			if (rotElem == null) {
				throw new ParsingException("Missing element "
						+ orientation.getBaseTag());
			}
			
			cyl.setPosition(new Point3D(px, py, pz, internalLengthUnit));
			cyl.setOrientation(orientation.fromElement(rotElem));
			cyl.setHeight(h, internalLengthUnit);
			cyl.setRadius(r, internalLengthUnit);

		} catch (NullPointerException ex) {

			// this happens when an element or attribute that is required is
			// not present
			throw new ParsingException("Missing element or attribute "
					+ "in document.", ex);
		} catch (NumberFormatException e) {
			throw new ParsingException("could not parse coordinate values");
		}
		return cyl;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		
	}
}

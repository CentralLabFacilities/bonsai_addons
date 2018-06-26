package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import org.apache.log4j.Logger;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point3DSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Rotation3DSerializer;
import de.unibi.citec.clf.btl.xml.serializers.vision2d.RegionDataSerializer;
import de.unibi.citec.clf.btl.data.vision3d.PlaneData;

/**
 * This type represents a plane in 3D space. It may be an infinite plane (if no
 * borders are specified) or a bordered plane if a polygon describing the
 * borders is specified.
 * 
 * @author lziegler
 */
public class PlaneDataSerializer extends XomSerializer<PlaneData> {

	private final static Logger logger = Logger.getLogger(PlaneDataSerializer.class);
	
	private Point3DSerializer origin;
	private Rotation3DSerializer rotation;
	private RegionDataSerializer parentSerializer;

	/**
	 * Creates a new instance.
	 */
	public PlaneDataSerializer() {
		super();
		origin = new Point3DSerializer();
		rotation = new Rotation3DSerializer();
		parentSerializer = new RegionDataSerializer();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<PlaneData> getDataType() {
		return PlaneData.class;
	}

	@Override
	public String getBaseTag() {
		return "PLANE";
	}
	
	/**
	 * Serializes the {@link PlaneDataSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(PlaneData data, Element parent) throws SerializationException {
		// cleanup
		Elements elements = parent.getChildElements(PlaneData.ORIGIN_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		elements = parent.getChildElements(PlaneData.ROTATION_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		
		parentSerializer.fillInto(data, parent);

		// fill
		Element originElement = new Element(PlaneData.ORIGIN_TAG_NAME);
		origin.fillInto(data.getOrigin(), originElement);
		parent.appendChild(originElement);

		Element normalElement = new Element(PlaneData.ROTATION_TAG_NAME);
		rotation.fillInto(data.getRotation(), normalElement);
		parent.appendChild(normalElement);
		
	}

	/**
	 * Constructs a {@link PlaneDataSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PlaneDataSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */		
	@Override
	public PlaneData doFromElement(Element element)
			throws ParsingException, DeserializationException {
    
		PlaneData pd = new PlaneData(parentSerializer.fromElement(element));
		try {
		    
			Element originElement = element
					.getFirstChildElement(PlaneData.ORIGIN_TAG_NAME);
			origin = new Point3DSerializer();
			pd.setOrigin(origin.fromElement(originElement));

			Element normalElement = element
					.getFirstChildElement(PlaneData.ROTATION_TAG_NAME);
			if (normalElement == null) {
				throw new ParsingException("Missing tag " + PlaneData.ROTATION_TAG_NAME);
			}
			rotation = new Rotation3DSerializer();
			pd.setRotation(rotation.fromElement(normalElement));
			
		} catch (IllegalArgumentException e) {
			throw new ParsingException(e.getMessage(), e);
		}
		return pd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof PlaneDataSerializer))
				return false;

			PlaneDataSerializer other = (PlaneDataSerializer) obj;

			super.equals(other);

		} catch (Exception e) {
			logger.error("equals() Exception: " + e.getMessage());
			return false;
		}
		return true;
	}
}

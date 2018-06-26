package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.geometry.PolygonalPatch3D;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * This is a serializable and iterable wrapper for {@link java.awt.Polygon}.
 * 
 * @author lziegler
 */
public class PolygonalPatchSerializer extends XomSerializer<PolygonalPatch3D> {

	public static final String BASE_TAG_NAME = "BASE";
	public static final String BORDER_TAG_NAME = "BORDER";
	
	private Pose3DSerializer poseSerializer;
	private PrecisePolygonSerializer polySerializer;
	
	public PolygonalPatchSerializer() {
	    setLegacyParsing(true);
	    
	    poseSerializer = new Pose3DSerializer();
	    polySerializer = new PrecisePolygonSerializer();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<PolygonalPatch3D> getDataType() {
		return PolygonalPatch3D.class;
	}

	@Override
	public String getBaseTag() {
		return "POLYGONALPATCH";
	}

	/**
	 * Serializes the {@link PolygonalPatchSerializer} object into a given XOM {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(PolygonalPatch3D data, Element parent) throws SerializationException {
        
        if (data == null) {
            return;
        }
        
        // cleanup
		Elements elements = parent.getChildElements(BASE_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		elements = parent.getChildElements(BORDER_TAG_NAME);
		for (int i = 0; i < elements.size(); i++) {
			parent.removeChild(elements.get(i));
		}
		
		// fill
		Element baseElement0 = new Element(BASE_TAG_NAME);
		Element baseElement1 = new Element(poseSerializer.getBaseTag());
		poseSerializer.fillInto(data.getBase(), baseElement1);
		baseElement0.appendChild(baseElement1);
		parent.appendChild(baseElement0);
		
		Element borderElement = new Element(BORDER_TAG_NAME);
		Element borderElement1 = new Element(polySerializer.getBaseTag());
		polySerializer.fillInto(data.getBorder(), borderElement1);
		borderElement.appendChild(borderElement1);
		parent.appendChild(borderElement);
		
	}
	
	/**
	 * Constructs a {@link PolygonalPatchSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PolygonalPatchSerializer} object containing all the information given
	 *         by the {@link Element} object.
	 * @throws ParsingException 
	 * @throws DeserializationException 
	 */
	public PolygonalPatch3D doFromElement(Element dataElement) throws ParsingException, DeserializationException {
		PolygonalPatch3D p = new PolygonalPatch3D();
		try {
			Element originElement = dataElement
					.getFirstChildElement(BASE_TAG_NAME);
			Element originElement1 = originElement
					.getFirstChildElement(poseSerializer.getBaseTag());
			p.setBase(poseSerializer.fromElement(originElement1));

			Element rotElement = dataElement
					.getFirstChildElement(BORDER_TAG_NAME);
			Element rotElement1 = rotElement
					.getFirstChildElement(polySerializer.getBaseTag());
			if (rotElement1 == null) {
				throw new ParsingException("Missing tag " + BORDER_TAG_NAME);
			}
			
			p.setBorder(polySerializer.fromElement(rotElement1));

		} catch (IllegalArgumentException e) {
			throw new ParsingException(e.getMessage(), e);
		}
		return p;
	}

	@Override
	public void doSanitizeElement(Element parent) {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((polySerializer == null) ? 0 : polySerializer.hashCode());
		result = prime * result + ((poseSerializer == null) ? 0 : poseSerializer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolygonalPatchSerializer other = (PolygonalPatchSerializer) obj;
		if (polySerializer == null) {
			if (other.polySerializer != null)
				return false;
		} else if (!polySerializer.equals(other.polySerializer))
			return false;
		if (poseSerializer == null) {
			if (other.poseSerializer != null)
				return false;
		} else if (!poseSerializer.equals(other.poseSerializer))
			return false;
		return true;
	}
	
}

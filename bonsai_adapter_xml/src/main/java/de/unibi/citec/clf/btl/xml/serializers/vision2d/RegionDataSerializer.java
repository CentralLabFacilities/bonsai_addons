package de.unibi.citec.clf.btl.xml.serializers.vision2d;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.PrecisePolygonSerializer;
import de.unibi.citec.clf.btl.data.vision2d.RegionData;
import de.unibi.citec.clf.btl.data.vision2d.RegionData.Scope;

public class RegionDataSerializer extends XomSerializer<RegionData> {

	private static final String SCOPE_TAG_NAME = "SCOPE";
	
	protected PrecisePolygonSerializer polygon = new PrecisePolygonSerializer();

	public RegionDataSerializer() {
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<RegionData> getDataType() {
		return RegionData.class;
	}

	@Override
	public String getBaseTag() {
		return "REGION";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof RegionDataSerializer))
				return false;

			RegionDataSerializer other = (RegionDataSerializer) obj;

			return super.equals(other);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Serializes the {@link RegionDataSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(RegionData data, Element parent) throws SerializationException {

		Element polygonElem = new Element(polygon.getBaseTag());
		polygon.fillInto(data.getPolygon(), polygonElem);

		Element scopeElem = new Element(SCOPE_TAG_NAME);
		scopeElem.appendChild(data.getScope().name());

		parent.appendChild(polygonElem);
		parent.appendChild(scopeElem);
		
	}
	
	/**
	 * Constructs a {@link RegionDataSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link RegionDataSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public RegionData doFromElement(Element element)
			throws ParsingException, DeserializationException {

		RegionData rd = new RegionData();
		
		rd.setPolygon(polygon.fromElement(element
				.getFirstChildElement(polygon.getBaseTag())));
		
		rd.setScope(Scope.valueOf(element.getFirstChildElement(
				SCOPE_TAG_NAME).getValue()));
		return rd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub
		
	}
}

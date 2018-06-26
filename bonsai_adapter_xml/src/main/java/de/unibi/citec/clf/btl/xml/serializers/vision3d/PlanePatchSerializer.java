package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatch;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.PolygonalPatchSerializer;

/**
 * This type represents a plane in 3D space. It may be an infinite plane (if no
 * borders are specified) or a bordered plane if a polygon describing the
 * borders is specified.
 * 
 * @author lziegler
 */
public class PlanePatchSerializer extends XomSerializer<PlanePatch> {

	private PolygonalPatchSerializer parentSerializer;

	/**
	 * Creates a new instance.
	 */
	public PlanePatchSerializer() {
		super();

		parentSerializer = new PolygonalPatchSerializer();
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<PlanePatch> getDataType() {
		return PlanePatch.class;
	}

	@Override
	public String getBaseTag() {
		return "PLANEPATCH";
	}

	/**
	 * Serializes the {@link PlanePatchSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(PlanePatch data, Element parent) throws SerializationException {
		parentSerializer.fillInto(data, parent);
	}

	/**
	 * Constructs a {@link PlanePatchSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link PlanePatchSerializer} object containing all the
	 *         information given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public PlanePatch doFromElement(Element element) throws ParsingException,
			DeserializationException {

		PlanePatch pd = new PlanePatch(parentSerializer.fromElement(element));
		return pd;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentSerializer == null) ? 0 : parentSerializer.hashCode());
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
		PlanePatchSerializer other = (PlanePatchSerializer) obj;
		if (parentSerializer == null) {
			if (other.parentSerializer != null)
				return false;
		} else if (!parentSerializer.equals(other.parentSerializer))
			return false;
		return true;
	}
}

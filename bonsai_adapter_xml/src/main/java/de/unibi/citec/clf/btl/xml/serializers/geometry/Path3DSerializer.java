package de.unibi.citec.clf.btl.xml.serializers.geometry;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.object.ObjectLocationSerializer;
import de.unibi.citec.clf.btl.data.geometry.Path3D;
import de.unibi.citec.clf.btl.data.geometry.Path3D.Scope;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon3D;

public class Path3DSerializer extends XomSerializer<Path3D> {

	public static final String SCOPE_TAG_NAME = "SCOPE";

	public Path3DSerializer() {
	}

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public Class<Path3D> getDataType() {
		return Path3D.class;
	}

	@Override
	public String getBaseTag() {
		return "PATH3D";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFillInto(Path3D data, Element parent) throws SerializationException {

		// polygon
		PrecisePolygon3DSerializer p = new PrecisePolygon3DSerializer();
		Element polygonElem = new Element(p.getBaseTag());
		p.fillInto(data.getPolygon(), polygonElem);

		Element scopeElem = new Element(SCOPE_TAG_NAME);
		scopeElem.appendChild(data.getScope().name());

		parent.appendChild(polygonElem);
		parent.appendChild(scopeElem);
	}

	/**
	 * Fills an {@link ObjectLocationSerializer.Hypothesis} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param hypothesisElement
	 *            The XOM {@link Element} to fill an object from.
	 * @param type
	 *            The {@link ObjectLocationSerializer.Hypothesis} object to fill with
	 *            all the information given by the {@link Element} object.
	 * @throws ParsingException
	 */
	@Override
	public Path3D doFromElement(Element dataElement) throws ParsingException, DeserializationException {

		PrecisePolygon3DSerializer s = new PrecisePolygon3DSerializer();
		Element e = dataElement.getFirstChildElement(s.getBaseTag());
		PrecisePolygon3D p = s.fromElement(e);
		
		Path3D path = new Path3D(p);
		path.setScope(Scope.valueOf(dataElement.getFirstChildElement(SCOPE_TAG_NAME)
				.getValue()));

		return path;
	}

	@Override
	public void doSanitizeElement(Element parent) {

	}
}

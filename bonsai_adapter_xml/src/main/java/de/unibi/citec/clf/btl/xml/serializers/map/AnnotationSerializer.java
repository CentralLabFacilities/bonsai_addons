package de.unibi.citec.clf.btl.xml.serializers.map;



import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.common.TimestampSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.PrecisePolygonSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.map.Annotation;
import de.unibi.citec.clf.btl.data.map.Viewpoint;

import java.util.LinkedList;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 * This type describes an annotation of a map, e.g., rooms or no-go areas. Each
 * annotation is represented by an area (polygon) and has several viewpoints of
 * different categories.
 * 
 * <code>
 * <ANNOTATION label="kitchen">
 *      <TIMESTAMP>
 *          <INSERTED value="1334754163505" />
 *          <UPDATED value="1334754163505" />
 *      </TIMESTAMP>
 *      <GENERATOR>unknown</GENERATOR>
 *      <VIEWPOINT label="table1" category="VIEW">
 *          <ROBOTPOSITION>
 *              <TIMESTAMP>
 *                  <INSERTED value="1334589211533" />
 *                  <UPDATED value="1334589211533" />
 *              </TIMESTAMP>
 *              <GENERATOR>unknown</GENERATOR>
 *              <POSITION x="0.0" y="1.0" theta="2.0" ref="world" kind="absolute" />
 *              </ROBOTPOSITION>
 *      </VIEWPOINT>
 *      <VIEWPOINT label="table2" category="VIEW">
 *          <ROBOTPOSITION>
 *              <TIMESTAMP>
 *                  <INSERTED value="1334589211533" />
 *                  <UPDATED value="1334589211533" />
 *              </TIMESTAMP>
 *              <GENERATOR>unknown</GENERATOR>
 *              <POSITION x="0.0" y="1.0" theta="2.0" ref="world" kind="absolute" />
 *          </ROBOTPOSITION>
 *      </VIEWPOINT>
 *      <PRECISEPOLYGON>
 *          <POINT2D x="0.0" y="0.0" scope="GLOBAL" />
 *          <POINT2D x="0.0" y="2000.0" scope="GLOBAL" />
 *          <POINT2D x="2000.0" y="2000.0" scope="GLOBAL" />
 *          <POINT2D x="2000.0" y="0.0" scope="GLOBAL" />
 *      </PRECISEPOLYGON>
 * </ANNOTATION>
 * </code>
 * 
 * @author lkettenb
 */

public class AnnotationSerializer extends XomSerializer<Annotation> {

	/**
	 * Default constructor as expected by {@link Type}. You should not use it!
	 */
	public AnnotationSerializer() {
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof AnnotationSerializer)) {
				return false;
			}

			AnnotationSerializer other = (AnnotationSerializer) obj;

			return other.equals(this);

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Constructs a {@link AnnotationSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link AnnotationSerializer} object containing all the
	 *         information given by the {@link Element} object.
	 * @throws ParsingException
	 */

	@Override
	public Annotation doFromElement(Element element) throws ParsingException, DeserializationException {
		String tmpLabel = "";
		PrecisePolygonSerializer tmpPolygon = new PrecisePolygonSerializer();
		LinkedList<Viewpoint> tmpViewpoints = new LinkedList<>();

		Annotation anno = new Annotation();

		try {

			tmpLabel = ElementParser.getAttributeValue(element, "label");

			ViewpointSerializer vp = new ViewpointSerializer();
			Elements viewpoints = element.getChildElements(vp.getBaseTag());
			for (int i = 0; i < viewpoints.size(); i++) {
				tmpViewpoints.add(vp.fromElement(viewpoints.get(i)));
			}

			Element elementPolygon = element.getFirstChildElement(tmpPolygon
					.getBaseTag());

			anno.setLabel(tmpLabel);
			anno.setPolygon(tmpPolygon.fromElement(elementPolygon));
			anno.setViewpoints(tmpViewpoints);
		} catch (ParsingException ex) {
			// This exception is never thrown by PrecisePolygon.fromElement...
		} catch (NullPointerException ex) {
			// this happens when an element or attribute that is required is
			// not present
			throw new IllegalArgumentException("Missing element or attribute "
					+ "in document.", ex);
		}

		return anno;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFillInto(Annotation data, Element parent) throws SerializationException {
		sanitizeElement(parent);

		parent.addAttribute(new Attribute("label", data.getLabel()));

		// timestamp
		TimestampSerializer tmpTs = new TimestampSerializer();
		Element timestampElement = new Element(tmpTs.getBaseTag());
		tmpTs.fillInto(data.getTimestamp(), timestampElement);
		parent.appendChild(timestampElement);

		// generator
		Element generatorElement = new Element("GENERATOR");
		generatorElement.appendChild(data.getGenerator());
		parent.appendChild(generatorElement);

		for (Viewpoint viewpoint : data.getViewpoints()) {
			ViewpointSerializer tmpVp = new ViewpointSerializer();
			Element item = new Element(tmpVp.getBaseTag());
			tmpVp.fillInto(viewpoint, item);
			parent.appendChild(item);
		}

		PrecisePolygonSerializer tmpPoly = new PrecisePolygonSerializer();
		Element item = new Element(tmpPoly.getBaseTag());
		tmpPoly.fillInto(data.getPolygon(), item);
		parent.appendChild(item);

	}

	@Override
	public Class<Annotation> getDataType() {
		return Annotation.class;
	}

	@Override
	public String getBaseTag() {
		return "ANNOTATION";
	}
}

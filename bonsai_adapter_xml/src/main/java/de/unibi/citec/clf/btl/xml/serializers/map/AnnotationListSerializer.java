

package de.unibi.citec.clf.btl.xml.serializers.map;



import de.unibi.citec.clf.btl.data.map.Annotation;
import de.unibi.citec.clf.btl.data.map.AnnotationList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.common.TimestampSerializer;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 *
 * @author cwitte
 */
public class AnnotationListSerializer extends XomListSerializer<Annotation, AnnotationList> {
	/**
	 * Default constructor as expected by {@link Type}. You should not use it!
	 */
	public AnnotationListSerializer() {
            setLegacyParsing(true);
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof AnnotationListSerializer)) {
				return false;
			}

			AnnotationListSerializer other = (AnnotationListSerializer) obj;

			return other.equals(this);

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Constructs a {@link AnnotationListSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link AnnotationListSerializer} object containing all the
	 *         information given by the {@link Element} object.
	 * @throws ParsingException
	 */

	@Override
	public AnnotationList doFromElement(Element element) throws ParsingException, XomSerializer.DeserializationException {
                AnnotationList annotationList = new AnnotationList();
                AnnotationSerializer annotationSerializer = new AnnotationSerializer();
                
                Elements annotationElements = element.getChildElements(annotationSerializer.getBaseTag());
                for(int i=0; i<annotationElements.size(); i++) {
                    annotationList.add(annotationSerializer.fromElement(annotationElements.get(i)));
                }

                
                return annotationList;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFillInto(AnnotationList annotationList, Element parent) throws XomSerializer.SerializationException {
		sanitizeElement(parent);

		AnnotationSerializer annotationSerializer = new AnnotationSerializer();
                Element annotationElement;
                
                // timestamp
                TimestampSerializer tmpTs = new TimestampSerializer();
		Element timestampElement = new Element(tmpTs.getBaseTag());
		tmpTs.fillInto(annotationList.getTimestamp(), timestampElement);
		parent.appendChild(timestampElement);

		// generator
		Element generatorElement = new Element("GENERATOR");
		generatorElement.appendChild(annotationList.getGenerator());
		parent.appendChild(generatorElement);
                
                parent.addAttribute(new Attribute("type", "annotation"));
                for(Annotation annotation : annotationList) {
                    annotationElement = new Element(annotationSerializer.getBaseTag());
                    annotationSerializer.fillInto(annotation, annotationElement);
                    parent.appendChild(annotationElement);
                }
	}

	@Override
	public Class<AnnotationList> getDataType() {
		return AnnotationList.class;
	}

	@Override
	public String getBaseTag() {
		return "LIST";
	}

    @Override
    public XomSerializer<Annotation> getItemSerializer() {
        return new AnnotationSerializer();
    }

    @Override
    public AnnotationList getDefaultInstance() {
        return new AnnotationList();
    }
}
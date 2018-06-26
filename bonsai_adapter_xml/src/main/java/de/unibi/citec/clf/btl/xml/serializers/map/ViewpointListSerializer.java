

package de.unibi.citec.clf.btl.xml.serializers.map;



import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.data.map.ViewpointList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.common.TimestampSerializer;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 *
 * @author kharmening
 */
public class ViewpointListSerializer extends XomListSerializer<Viewpoint, ViewpointList> {
	/**
	 * Default constructor as expected by {@link Type}. You should not use it!
	 */
	public ViewpointListSerializer() {
            setLegacyParsing(true);
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof ViewpointListSerializer)) {
				return false;
			}

			ViewpointListSerializer other = (ViewpointListSerializer) obj;

			return other.equals(this);

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Constructs a {@link ViewpointListSerializer} object from a given XOM
	 * {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link ViewpointListSerializer} object containing all the
	 *         information given by the {@link Element} object.
	 * @throws ParsingException
	 */

	@Override
	public ViewpointList doFromElement(Element element) throws ParsingException, XomSerializer.DeserializationException {
                ViewpointList viewpointList = new ViewpointList();
                ViewpointSerializer viewpointSerializer = new ViewpointSerializer();
                
                Elements viewpointElements = element.getChildElements(viewpointSerializer.getBaseTag());
                for(int i=0; i<viewpointElements.size(); i++) {
                    viewpointList.add(viewpointSerializer.fromElement(viewpointElements.get(i)));
                }
                
                return viewpointList;
	}

	@Override
	public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFillInto(ViewpointList viewpointList, Element parent) throws XomSerializer.SerializationException {
		sanitizeElement(parent);

		ViewpointSerializer viewpointSerializer = new ViewpointSerializer();
                Element viewpointElement;
                
                // timestamp
                TimestampSerializer tmpTs = new TimestampSerializer();
		Element timestampElement = new Element(tmpTs.getBaseTag());
		tmpTs.fillInto(viewpointList.getTimestamp(), timestampElement);
		parent.appendChild(timestampElement);

		// generator
		Element generatorElement = new Element("GENERATOR");
		generatorElement.appendChild(viewpointList.getGenerator());
		parent.appendChild(generatorElement);
                
                parent.addAttribute(new Attribute("type", "viewpoint"));
                for(Viewpoint viewpoint : viewpointList) {
                    viewpointElement = new Element(viewpointSerializer.getBaseTag());
                    viewpointSerializer.fillInto(viewpoint, viewpointElement);
                    parent.appendChild(viewpointElement);
                }
	}

	@Override
	public Class<ViewpointList> getDataType() {
		return ViewpointList.class;
	}

	@Override
	public String getBaseTag() {
		return "LIST";
	}

    @Override
    public XomSerializer<Viewpoint> getItemSerializer() {
        return new ViewpointSerializer();
    }

    @Override
    public ViewpointList getDefaultInstance() {
        return new ViewpointList();
    }
}
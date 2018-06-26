package de.unibi.citec.clf.btl.xml.serializers.object;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.object.ObjectData;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * Results of the object recognition. This is a very general base class.
 * 
 * @author sebschne
 * @author jwienke
 */
public class ObjectDataSerializer extends XomSerializer<ObjectData> {

	protected final static String CLASS_ELEMENT = "CLASS";
	protected final static String RATING_ELEMENT = "RATING";
	protected final static String RELIABILITY_ATTRIBUTE = "RELIABILITY";

	public static class HypothesisSerializer extends XomSerializer<ObjectData.Hypothesis> {

		/**
		 * Getter for the xml base tag used for this (de-)serialization.
		 * 
		 * @return xml base tag
		 */
		@Override
		public String getBaseTag() {
			return "HYPOTHESIS";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void doFillInto(ObjectData.Hypothesis type, Element parent) throws SerializationException {

			// reliability
			Element rating = new Element(RATING_ELEMENT);
			Element reliability = new Element(RELIABILITY_ATTRIBUTE);
			reliability.addAttribute(new Attribute("value", String
					.valueOf(type.getReliability())));
			rating.appendChild(reliability);

			parent.appendChild(rating);

			// class label
			Element className = new Element(CLASS_ELEMENT);
			className.appendChild(type.getClassLabel());

			parent.appendChild(className);
		}

		/**
		 * Fills an {@link ObjectData.Hypothesis} object from a given XOM
		 * {@link Element}.
		 * 
		 * @param hypothesisElement
		 *            The XOM {@link Element} to fill an object from.
		 * @param type
		 *            The {@link ObjectData.Hypothesis} object to fill with all
		 *            the information given by the {@link Element} object.
		 */
		public ObjectData.Hypothesis doFromElement(Element hypothesisElement) {
		    ObjectData.Hypothesis type = new ObjectData.Hypothesis();
			Element rating = hypothesisElement
					.getFirstChildElement(RATING_ELEMENT);

			String relString = rating
					.getFirstChildElement(RELIABILITY_ATTRIBUTE)
					.getAttribute("value").getValue();
			if (relString != null && !relString.equals("")) {
				type.setReliability(Double.parseDouble(relString));
			} else {
				type.setReliability(0d);
			}

			Element classes = hypothesisElement
					.getFirstChildElement(CLASS_ELEMENT);

			final String classLabel = classes.getValue();
			if (classLabel == null) {
				throw new IllegalArgumentException("Empty class text.");
			}
			type.setClassLabel(classLabel);
			return type;
		}

        @Override
        public void doSanitizeElement(Element parent) {
        }

        @Override
        public Class<ObjectData.Hypothesis> getDataType() {
            return ObjectData.Hypothesis.class;
        }
	}
	
	private HypothesisSerializer hypSerializer = new HypothesisSerializer(); 

	/**
	 * Getter for the xml base tag used for this (de-)serialization.
	 * 
	 * @return xml base tag
	 */
	@Override
	public String getBaseTag() {
		return "OBJECT";
	}

	/**
	 * Fills an {@link ObjectData} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to fill an object from.
	 * @param type
	 *            The {@link ObjectData} object to fill with all the information
	 *            given by the {@link Element} object.
	 * @throws DeserializationException 
	 * @throws ParsingException 
	 */
	@Override
    public ObjectData doFromElement(Element objectElement) throws ParsingException, DeserializationException {
	    ObjectData type = new ObjectData();
		try {

			// check hypotheses
			Nodes nodes = objectElement.query(hypSerializer.getBaseTag());
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				if (node instanceof Element) {

					ObjectData.Hypothesis hyp = hypSerializer.fromElement((Element) node);
					type.addHypothesis(hyp);
				}
			}

		} catch (NullPointerException ex) {
			// this happens when an element or attribute that is required is
			// not present
			throw new IllegalArgumentException("Missing element or attribute "
					+ "in document.", ex);
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSanitizeElement(Element parent) {

		Nodes nodes = parent.query(CLASS_ELEMENT);
		for (int i = 0; i < nodes.size(); i++) {
			parent.removeChild(nodes.get(i));
		}

		nodes = parent.query(hypSerializer.getBaseTag());
		for (int i = 0; i < nodes.size(); i++) {
			parent.removeChild(nodes.get(i));
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws SerializationException 
	 */
	@Override
	public void doFillInto(ObjectData type, Element parent) throws SerializationException {

		sanitizeElement(parent);

		for (ObjectData.Hypothesis hyp : type.getHypotheses()) {
			Element hypothesis = new Element(hypSerializer.getBaseTag());
			hypSerializer.fillInto(hyp, hypothesis);
			parent.appendChild(hypothesis);
		}

	}

    @Override
    public Class<ObjectData> getDataType() {
        return ObjectData.class;
    }
}

package de.unibi.citec.clf.btl.xml.serializers.object;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.object.ObjectData;
import de.unibi.citec.clf.btl.data.object.ObjectPositionData;
import de.unibi.citec.clf.btl.data.object.ObjectPositionData.Hypothesis;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.PrecisePolygonSerializer;

/**
 * Results of the object recognition. This class is meant so define the position
 * of the object in the world! The given polygon describes the objects's
 * position in world coordinates! If you want to define the location in image
 * coordinates use {@link ObjectLocationSerializer}.
 * 
 * @author lziegler
 */
public class ObjectPositionSerializer extends XomSerializer<ObjectPositionData> {

    public static class HypothesisSerializer extends
            XomSerializer<ObjectPositionData.Hypothesis> {

        ObjectDataSerializer.HypothesisSerializer hypSerializer = new ObjectDataSerializer.HypothesisSerializer();

        @Override
        public String getBaseTag() {
            return hypSerializer.getBaseTag();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void doFillInto(ObjectPositionData.Hypothesis hyp, Element parent) throws SerializationException {
            hypSerializer.fillInto(hyp, parent);
        }

        /**
         * Fills an {@link ObjectLocationSerializer.Hypothesis} object from a
         * given XOM {@link Element}.
         * 
         * @param hypothesisElement
         *            The XOM {@link Element} to fill an object from.
         * @param type
         *            The {@link ObjectLocationSerializer.Hypothesis} object to
         *            fill with all the information given by the {@link Element}
         *            object.
         * @throws DeserializationException 
         * @throws ParsingException 
         */
        public ObjectPositionData.Hypothesis doFromElement(
                Element hypothesisElement) throws ParsingException, DeserializationException {

            ObjectData.Hypothesis h = hypSerializer
                    .fromElement(hypothesisElement);
            ObjectPositionData.Hypothesis h1 = new ObjectPositionData.Hypothesis(
                    h);
            return h1;
        }

        @Override
        public void doSanitizeElement(Element parent) {
        }

        @Override
        public Class<ObjectPositionData.Hypothesis> getDataType() {
            return ObjectPositionData.Hypothesis.class;
        }
    }

    /**
     * Position of the object in the sensor's image.
     */
    private PrecisePolygonSerializer psPolygonSerializer = new PrecisePolygonSerializer();
    private ObjectDataSerializer oSerializer = new ObjectDataSerializer();
    private HypothesisSerializer hSerializer = new HypothesisSerializer();

    @Override
    public String getBaseTag() {
        return "OBJECTPOSITION";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(ObjectPositionData type, Element parent) throws SerializationException {

        oSerializer.fillInto(type, parent);

        // polygon
        Element polygonElem = new Element(psPolygonSerializer.getBaseTag());
        psPolygonSerializer.fillInto(type.getPolygon(), polygonElem);
        polygonElem
                .addAttribute(new Attribute("kind", type.getCoordinateKind()));
        polygonElem.addAttribute(new Attribute("ref", type.getReference()));

        parent.appendChild(polygonElem);
    }

    /**
     * Fills an {@link ObjectPositionSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to fill an object from.
     * @param type
     *            The {@link ObjectPositionSerializer} object to fill with all
     *            the information given by the {@link Element} object.
     * @throws ParsingException
     */
    @Override
    public ObjectPositionData doFromElement(Element objectElement)
            throws ParsingException, DeserializationException {

        ObjectPositionData type = new ObjectPositionData(
                oSerializer.fromElement(objectElement));
        try {

            Element polygonElem = objectElement
                    .getFirstChildElement(psPolygonSerializer.getBaseTag());

            type.setPolygon(psPolygonSerializer.fromElement(polygonElem));
            type.setCoordinateKind(polygonElem.getAttributeValue("kind"));
            type.setReference(polygonElem.getAttributeValue("ref"));

            // clean up
            type.clearHypotheses();

            // check hypotheses
            Nodes nodes = objectElement.query(hSerializer.getBaseTag());
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node instanceof Element) {

                    Hypothesis hyp = hSerializer.fromElement((Element) node);
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

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<ObjectPositionData> getDataType() {
        return ObjectPositionData.class;
    }

}

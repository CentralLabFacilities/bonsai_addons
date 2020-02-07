package de.unibi.citec.clf.btl.xml.serializers.object;



import de.unibi.citec.clf.btl.xml.serializers.geometry.PrecisePolygonSerializer;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.object.ObjectData;
import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.data.object.ObjectData.Hypothesis;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * Results of the object recognition. This class is meat so define the location
 * of the object in the detector's camera image! The given polygon describes the
 * objects's location in pixel coordinates! If you want to define an object in
 * world coordinates use {@link ObjectPositionSerializer}.
 * 
 * @author lziegler
 */
public class ObjectLocationSerializer extends XomSerializer<ObjectLocationData> {

    protected final static String SENSORTYPE_ATTRIBUTE = "SENSORTYPE";

    public static class HypothesisSerializer extends
            XomSerializer<ObjectLocationData.Hypothesis> {

        ObjectDataSerializer.HypothesisSerializer hypSerializer = new ObjectDataSerializer.HypothesisSerializer();

        @Override
        public String getBaseTag() {
            return hypSerializer.getBaseTag();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void doFillInto(ObjectLocationData.Hypothesis hyp, Element parent) throws SerializationException {
            hypSerializer.fillInto(hyp, parent);
        }

        /**
         * Fills an {@link ObjectData.Hypothesis} object from a
         * given XOM {@link Element}.
         * 
         * @param hypothesisElement
         *            The XOM {@link Element} to fill an object from.
         * @throws DeserializationException 
         * @throws ParsingException 
         */
        public ObjectLocationData.Hypothesis doFromElement(
                Element hypothesisElement) throws ParsingException, DeserializationException {

            ObjectData.Hypothesis h = hypSerializer
                    .fromElement(hypothesisElement);
            ObjectLocationData.Hypothesis h1 = new ObjectLocationData.Hypothesis(
                    h);
            return h1;
        }

        @Override
        public void doSanitizeElement(Element parent) {
        }

        @Override
        public Class<Hypothesis> getDataType() {
            return Hypothesis.class;
        }
    }

    ObjectDataSerializer oSerializer = new ObjectDataSerializer();
    HypothesisSerializer hSerializer = new HypothesisSerializer();
    PrecisePolygonSerializer pSerializer = new PrecisePolygonSerializer();

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     * 
     * @return xml base tag
     */
    @Override
    public String getBaseTag() {
        return "OBJECTLOCATION";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(ObjectLocationData type, Element parent) throws SerializationException {

        oSerializer.fillInto(type, parent);

        // polygon
        Element polygonElem = new Element(pSerializer.getBaseTag());
        if (type.getPolygon() != null) {
            pSerializer.fillInto(type.getPolygon(), polygonElem);
        }
        
        // sensor type
        Element sensorElem = new Element(SENSORTYPE_ATTRIBUTE);

        parent.appendChild(polygonElem);
        parent.appendChild(sensorElem);
    }

    /**
     * Fills an {@link ObjectLocationSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to fill an object from.
     * @throws DeserializationException 
     * @throws ParsingException 
     */
    @Override
    public ObjectLocationData doFromElement(Element objectElement) throws ParsingException, DeserializationException {
        /*
        ObjectLocationData type = new ObjectLocationData(
                oSerializer.fromElement(objectElement));
        try {

            type.setPolygon(pSerializer.fromElement(objectElement
                    .getFirstChildElement(pSerializer.getBaseTag())));

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
        }*/
        return null;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<ObjectLocationData> getDataType() {
        return ObjectLocationData.class;
    }
}

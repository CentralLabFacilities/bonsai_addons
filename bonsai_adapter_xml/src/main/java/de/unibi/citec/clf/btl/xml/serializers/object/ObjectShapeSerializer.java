package de.unibi.citec.clf.btl.xml.serializers.object;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.object.ObjectData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.geometry.Point3DSerializer;

/**
 * Results of the object recognition. This class is meat so define the location
 * of the object in the detector's camera image and contain shape information in
 * 3D! The given polygon describes the objects's location in pixel coordinates!
 * 
 * @author lziegler
 */
public class ObjectShapeSerializer extends XomSerializer<ObjectShapeData> {

    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_DEPTH = "depth";
    private static final String TAG_SHAPE = "SHAPE";
    private static final String ATTR_ID = "ID";

    public static class HypothesisSerializer extends
            XomSerializer<ObjectShapeData.Hypothesis> {

        ObjectLocationSerializer.HypothesisSerializer hypSerializer = new ObjectLocationSerializer.HypothesisSerializer();

        @Override
        public String getBaseTag() {
            return hypSerializer.getBaseTag();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void doFillInto(ObjectShapeData.Hypothesis hyp, Element parent) throws SerializationException {
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
        public ObjectShapeData.Hypothesis doFromElement(Element hypothesisElement) throws ParsingException, DeserializationException {

            ObjectData.Hypothesis h = hypSerializer
                    .fromElement(hypothesisElement);
            ObjectShapeData.Hypothesis h1 = new ObjectShapeData.Hypothesis(h);
            return h1;
        }

        @Override
        public void doSanitizeElement(Element parent) {
        }

        @Override
        public Class<ObjectShapeData.Hypothesis> getDataType() {
            return ObjectShapeData.Hypothesis.class;
        }
    }

    private Point3DSerializer pSerializer = new Point3DSerializer();
    private ObjectDataSerializer oSerializer = new ObjectDataSerializer();
    private HypothesisSerializer hSerializer = new HypothesisSerializer();

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     * 
     * @return xml base tag
     */
    @Override
    public String getBaseTag() {
        return "OBJECTSHAPE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(ObjectShapeData type, Element parent) throws SerializationException {

        oSerializer.fillInto(type, parent);
/*
        Element shapeElem = new Element(TAG_SHAPE);
        shapeElem.addAttribute(new Attribute(ATTR_DEPTH, String.valueOf(type
                .getDepth(ObjectShapeData.internalUnit))));
        shapeElem.addAttribute(new Attribute(ATTR_HEIGHT, String.valueOf(type
                .getHeight(ObjectShapeData.internalUnit))));
        shapeElem.addAttribute(new Attribute(ATTR_WIDTH, String.valueOf(type
                .getWidth(ObjectShapeData.internalUnit))));
        shapeElem.addAttribute(new Attribute(ATTR_ID, type.getId()));
        parent.appendChild(shapeElem);
        

        Element centerElem = new Element(pSerializer.getBaseTag());
        pSerializer.fillInto(type.getCenter(), centerElem);
        parent.appendChild(centerElem);*/
    }

    /**
     * Fills an {@link ObjectShapeSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to fill an object from.
     * @throws ParsingException
     */
    @Override
    public ObjectShapeData doFromElement(Element objectElement)
            throws ParsingException, DeserializationException {

        ObjectShapeData type = new ObjectShapeData(
                oSerializer.fromElement(objectElement));
/*
        try {
            Element shapeElem = objectElement.getFirstChildElement(TAG_SHAPE);
            type.setHeight(Double.parseDouble(shapeElem.getAttribute(
                    ATTR_HEIGHT).getValue()), ObjectShapeData.internalUnit);
            type.setWidth(Double.parseDouble(shapeElem.getAttribute(ATTR_WIDTH)
                    .getValue()), ObjectShapeData.internalUnit);
            type.setDepth(Double.parseDouble(shapeElem.getAttribute(ATTR_DEPTH)
                    .getValue()), ObjectShapeData.internalUnit);
            type.setId(ElementParser.getAttributeValue(shapeElem, ATTR_ID));
            type.setCenter(pSerializer.fromElement(objectElement
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
        return type;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<ObjectShapeData> getDataType() {
        return ObjectShapeData.class;
    }
}

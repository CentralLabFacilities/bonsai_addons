package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.vision3d.KinectImageData;

/**
 * Representation for the image metadata format used by Kinectserver.
 * 
 * @author vrichter
 */
public class KinectImageDataSerializer extends XomSerializer<KinectImageData> {

    private static final String IMAGE_URI_ATTRIBUTE = "uri";
    private static final String IMAGE_ELEMENT = "IMAGE";
    private static final String PROPERTIES_ELEMENT = "PROPERTIES";

    private static final String WIDTH_ATTRIBUTE = "width";
    private static final String HEIGHT_ATTRIBUTE = "height";
    private static final String COLORSPACE_ATTRIBUTE = "colorspace";
    private static final String CHANNELS_ATTRIBUTE = "channels";
    private static final String DEPTH_ATTRIBUTE = "depth";
    private static final String SUBSAMPLING_ATTRIBUTE = "subsampling";

    @Override
    public Class<KinectImageData> getDataType() {
        return KinectImageData.class;
    }

    @Override
    public String getBaseTag() {
        return "IMAGESET";
    }

    @Override
    public void doFillInto(KinectImageData data, Element parent) throws SerializationException {

    }

    /**
     * Constructs a {@link KinectImageDataSerializer} object from a given XOM
     * {@link Element}.
     * 
     * @param objectElement
     *            The XOM {@link Element} to construct an object from.
     * @return The {@link KinectImageDataSerializer} object containing all the
     *         information given by the {@link Element} object.
     */
    @Override
    public KinectImageData doFromElement(Element element) throws ParsingException, DeserializationException {

        KinectImageData kid = new KinectImageData();

        try {
            Element imageElement = element.getFirstChildElement(IMAGE_ELEMENT);

            // TODO: what does this do in ImageData? Here it is causing
            // exceptions.
            // Type.fromElement(imageElement, imageData);

            kid.setUri(imageElement.getAttributeValue(IMAGE_URI_ATTRIBUTE));

            // parse rest of metadata
            Element props = imageElement
                    .getFirstChildElement(PROPERTIES_ELEMENT);
            kid.setWidth(Integer.parseInt(props
                    .getAttributeValue(WIDTH_ATTRIBUTE)));
            kid.setHeight(Integer.parseInt(props
                    .getAttributeValue(HEIGHT_ATTRIBUTE)));
            kid.setChannels(Integer.parseInt(props
                    .getAttributeValue(CHANNELS_ATTRIBUTE)));
            kid.setColorspace(props.getAttributeValue(COLORSPACE_ATTRIBUTE)
                    .trim());
            kid.setDepth(props.getAttributeValue(DEPTH_ATTRIBUTE).trim());
            kid.setSubsampling(Integer.parseInt(props
                    .getAttributeValue(SUBSAMPLING_ATTRIBUTE)));

        } catch (NullPointerException ex) {
            // this happens when an element or attribute that is required is
            // not present
            throw new IllegalArgumentException("Missing element or attribute "
                    + "in document.", ex);
        }
        return kid;
    }

    @Override
    public void doSanitizeElement(Element parent) {

    }
}

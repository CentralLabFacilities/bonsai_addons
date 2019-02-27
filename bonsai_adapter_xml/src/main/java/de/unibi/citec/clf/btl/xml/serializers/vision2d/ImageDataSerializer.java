package de.unibi.citec.clf.btl.xml.serializers.vision2d;


import java.util.Base64;
import de.unibi.citec.clf.btl.data.vision2d.ImageData;
import de.unibi.citec.clf.btl.data.vision2d.ImageData.ColorMode;
import nu.xom.Element;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

import nu.xom.Attribute;
import nu.xom.Elements;

public class ImageDataSerializer extends XomSerializer<ImageData> {

    private static final String SCOPE_TAG_NAME = "SCOPE";

    public ImageDataSerializer() {
    }

    /**
     * Getter for the xml base tag used for this (de-)serialization.
     *
     * @return xml base tag
     */
    @Override
    public Class<ImageData> getDataType() {
        return ImageData.class;
    }

    @Override
    public String getBaseTag() {
        return "IMAGE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        try {
            if (!(obj instanceof ImageDataSerializer)) {
                return false;
            }

            ImageDataSerializer other = (ImageDataSerializer) obj;

            return super.equals(other);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Serializes the {@link RegionDataSerializer} object into a given XOM
     * {@link Element}.
     *
     * @param img
     * @param parent The {@link Element} to serialize the object into. The given
     * {@link Element} object should have the base tag defined by this class.
     * (see {@link #getClass().getSimpleName()})
     * @throws de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException
     * @see #getClass().getSimpleName()
     */
    @Override
    public void doFillInto(ImageData img, Element parent) throws SerializationException {
        
        Element id = new Element("Info");
        id.addAttribute(new Attribute("width", String.valueOf(img.getWidth())));
        id.addAttribute(new Attribute("height", String.valueOf(img.getHeight())));
        id.addAttribute(new Attribute("depth", String.valueOf(img.getDepth())));
        id.addAttribute(new Attribute("color", String.valueOf(img.getColorMode())));
        
        
    /*    String test = new String(img.getData(),StandardCharsets.UTF_8);
        System.err.println("original: " + test);
        test = cleanString(test);
        System.err.println("modified: " + test); */
                
        Element data = new Element("Data");   

        //data.appendChild(new String(img.getData(),StandardCharsets.UTF_8));
        //data.appendChild(test);
        

        String encodedData = Base64.getEncoder().encodeToString(img.getData());
        data.appendChild(encodedData);


        parent.appendChild(id);
        parent.appendChild(data);

    }

    /**
     * Constructs a {@link RegionDataSerializer} object from a given XOM
     * {@link Element}.
     *
     * @param element The XOM {@link Element} to construct an object from.
     * @return The {@link RegionDataSerializer} object containing all the
     * information given by the {@link Element} object.
     * @throws ParsingException
     * @throws de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException
     */
    @Override
    public ImageData doFromElement(Element element)
            throws ParsingException, DeserializationException {

        ImageData result = new ImageData();
        Elements childs = element.getChildElements();
        for (int i = 0 ; i < childs.size(); i++) {
            Element e = childs.get(i);
            switch (e.getLocalName()) {
                case "Info":
                    result.setWidth(ElementParser.getIntAttributeValue(e, "width"));
                    result.setHeight(ElementParser.getIntAttributeValue(e, "height"));
                    result.setDepth(ElementParser.getIntAttributeValue(e, "depth"));
                    result.setColorMode(ColorMode.valueOf(ElementParser.getAttributeValue(e, "color"))); 
                    break;
                case "Data":
                    byte[] decodedData = Base64.getDecoder().decode(e.getValue());
                    result.setData(decodedData);
                    break;
            }
            
        }
        return result;
    }

    @Override
    public void doSanitizeElement(Element parent) {
		// TODO Auto-generated method stub

    }
    
    
    
    

}



package de.unibi.citec.clf.btl.rst.serializers.vision;



import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.btl.data.vision2d.ImageData;
import de.unibi.citec.clf.btl.rst.RstSerializer;

import rst.vision.ImageType;


/**
 *
 * @author hneumann
 */
public class ImageDataSerializer extends RstSerializer<ImageData, ImageType.Image>{

    @Override
    public ImageData deserialize(ImageType.Image msg) throws DeserializationException {
        ImageData img = new ImageData();
        //msg.getData().forEach(a->System.out.println(a));
        System.out.println("data order: " + msg.getDataOrder());
        System.out.println("channels: " + msg.getChannels());
        img.setData(msg.getData().toByteArray());
        img.setDepth(msg.getDepth().getNumber());
        switch (msg.getColorMode()) {
            case COLOR_BGR:
                    img.setColorMode(ImageData.ColorMode.BGR);
                    break;
            default: img.setColorMode(ImageData.ColorMode.RGB);
                System.out.println(msg.getColorMode());
        }
        
        img.setHeight(msg.getHeight());
        img.setWidth(msg.getWidth());
       
        return img;
    }

    @Override
    public void serialize(ImageData data, GeneratedMessage.Builder<?> abstractBuilder) throws SerializationException {
        ImageType.Image.Builder builder = (ImageType.Image.Builder)abstractBuilder;
        
//        TODO do it!
        
        
    }

    @Override
    public Class<ImageType.Image> getMessageType() {
        return ImageType.Image.class;
    }

    @Override
    public Class<ImageData> getDataType() {
        return ImageData.class;
    }
    
}


package de.unibi.citec.clf.btl.rst.serializers.navigation;


import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.btl.data.map.BinarySlamMap;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import java.nio.ByteBuffer;
import rst.geometry.PoseType;
import rst.geometry.RotationType;
import rst.geometry.TranslationType;
import rst.navigation.OccupancyGrid2DIntType;

/**
 *
 * @author alangfel
 */
public class SlamMapSerializer extends RstSerializer<BinarySlamMap, OccupancyGrid2DIntType.OccupancyGrid2DInt> {

    @Override
    public BinarySlamMap deserialize(OccupancyGrid2DIntType.OccupancyGrid2DInt msg) throws DeserializationException {

        //Origin
        float xMeter = 0;
        float yMeter = 0;
        float phi = 0;
        String theUri = "slammap";

        BinarySlamMap map = new BinarySlamMap();
        map.setGenerator("ros4rsb");
        map.setUri(theUri);
        map.setX(xMeter, LengthUnit.METER);
        map.setY(yMeter, LengthUnit.METER);
        map.setYaw(phi, AngleUnit.RADIAN);
        map.setWidth(msg.getWidth());
        map.setHeight(msg.getHeight());
        map.setOriginX((int) msg.getOrigin().getTranslation().getX());
        map.setOriginY((int) msg.getOrigin().getTranslation().getY());

        map.setResolution(msg.getResolution(), LengthUnit.METER);

        //float[] linearMap;
        //map.setSlamMap(linearMap);
        int height = msg.getHeight();
        int width = msg.getWidth();

        float[] slammap = new float[height * width];

        ByteString map_data = msg.getMap();

//        System.out.println("width: " + width + " height: " + height + " groesse: " + msg.getMap().size());
        if (map_data != null) {
            for (int i = 0; i < height * width; i++) {
                byte test = map_data.byteAt(i);
                int zahl = (int) test;
                float val = 0.0f;
                if (zahl == -1) {
                    val = 0.5f;
                } else {
//                    val = 1.0f - (((float) zahl) / 100.0f);
//                    System.out.println("Zahl: " + zahl);
                    val = ((float) zahl) / 100.0f;
                }
                slammap[i] = val;
            }
        }
        map.setSlamMap(slammap);

        return map;
    }

    @Override
    public void serialize(BinarySlamMap data, GeneratedMessage.Builder<?> abstractBuilder) throws SerializationException {
        OccupancyGrid2DIntType.OccupancyGrid2DInt.Builder builder = (OccupancyGrid2DIntType.OccupancyGrid2DInt.Builder) abstractBuilder;
        
        builder.setHeight(data.getHeight());
        builder.setWidth(data.getWidth());
        
        //Create origin rst.geometry.pose
        PoseType.Pose.Builder poseb = PoseType.Pose.newBuilder();
        
        TranslationType.Translation.Builder translationb = TranslationType.Translation.newBuilder();
        translationb.setX(data.getOriginX());
        translationb.setY(data.getOriginY());
        translationb.setZ(0.0);

        TranslationType.Translation translation = translationb.build();
        poseb.setTranslation(translation);
        
        RotationType.Rotation.Builder rotationb = RotationType.Rotation.newBuilder();
        rotationb.setQw(0.0);
        rotationb.setQx(0.0);
        rotationb.setQy(0.0);
        rotationb.setQz(0.0);
        
        RotationType.Rotation rotation = rotationb.build();
        poseb.setRotation(rotation);
        
        PoseType.Pose pose = poseb.build();

        builder.setOrigin(pose);
        builder.setResolution((float) data.getResolution(LengthUnit.METER));
        
        float[] slamMap = data.getDynamicGridMap().getLinearMap();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[data.getHeight()*data.getWidth()]);

        for (int i = 0; i < data.getHeight()*data.getWidth(); i++) {
            float val = slamMap[i];
            int rosVal = 0;
            if(val == 0.5f) {
                rosVal = -1;
            } else {
                rosVal = (int)val*100;
            }
//            System.out.println("rosVal: " + rosVal);
            byte valB = (byte)rosVal;
            buffer.put(valB);
        }
        
        ByteString slammap = ByteString.copyFrom(buffer.array());
        builder.setMap(slammap);
    }

    @Override
    public Class<OccupancyGrid2DIntType.OccupancyGrid2DInt> getMessageType() {
        return OccupancyGrid2DIntType.OccupancyGrid2DInt.class;
    }

    @Override
    public Class<BinarySlamMap> getDataType() {
        return BinarySlamMap.class;
    }

}



package de.unibi.citec.clf.btl.rst.serializers.geometry;



import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;

import javax.vecmath.Vector3d;

import rst.geometry.PoseType;
import rst.geometry.RotationType;
import rst.geometry.TranslationType;

/**
 *
 * @author alangfel
 */
public class PositionSerializer extends RstSerializer<PositionData, PoseType.Pose> {

    public PositionSerializer() {
//        final ProtocolBufferConverter<PoseType.Pose> converter0 = new ProtocolBufferConverter<PoseType.Pose>(
//                PoseType.Pose.getDefaultInstance());
//
//        // register data types
//        ConverterRepository<ByteBuffer> repo = DefaultConverterRepository
//                .getDefaultConverterRepository();
//
//        repo.addConverter(converter0);
//        
//        System.out.println("Registered Converter!");
        
    }
    
    @Override
    public PositionData deserialize(PoseType.Pose msg) throws DeserializationException {
        double x, y, yaw;
        x = msg.getTranslation().getX();
        y = msg.getTranslation().getY();
        //maybe the wrong value from rotation
        
        Rotation3D rot = new Rotation3D();
        rot.setQuaternion(msg.getRotation().getQx(), msg.getRotation().getQy(), msg.getRotation().getQz(), msg.getRotation().getQw());
        yaw = rot.getYaw(AngleUnit.RADIAN);
        
        PositionData position = new PositionData(x, y, yaw, 0, LengthUnit.METER, AngleUnit.RADIAN, TimeUnit.MILLISECONDS);
        position.setFrameId(msg.getTranslation().getFrameId());
        return position;
    }

    @Override
    public void serialize(PositionData data, GeneratedMessage.Builder<?> abstractBuilder) throws SerializationException {
        PoseType.Pose.Builder builder = (PoseType.Pose.Builder) abstractBuilder;
        
        TranslationType.Translation translation; 
        TranslationType.Translation.Builder translationb = TranslationType.Translation.newBuilder();
        
        translationb.setX(data.getX(LengthUnit.METER));
        translationb.setY(data.getY(LengthUnit.METER));
        translationb.setZ(0.0);
        translationb.setFrameId(data.getFrameId());
        
        translation = translationb.build();
        
        RotationType.Rotation rotation;
        RotationType.Rotation.Builder rotationb = RotationType.Rotation.newBuilder();
        
        Rotation3D rot = new Rotation3D(new Vector3d(0,0,1), data.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        rotationb.setQw(rot.getQuaternion().w);
        rotationb.setQx(rot.getQuaternion().x);
        rotationb.setQy(rot.getQuaternion().y);
        rotationb.setQz(rot.getQuaternion().z);
        rotationb.setFrameId(data.getFrameId());
        rotation = rotationb.build();
        
        builder.setTranslation(translation);
        builder.setRotation(rotation);
    }

    @Override
    public Class<PoseType.Pose> getMessageType() {
        return PoseType.Pose.class;
    }

    @Override
    public Class<PositionData> getDataType() {
		return PositionData.class;
    }

    
    
}
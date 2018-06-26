package de.unibi.citec.clf.btl.ros.serializers.person;


import clf_perception_vision_msgs.ExtendedPersonStamped;
import de.unibi.citec.clf.bonsai.core.exception.TransformException;
import de.unibi.citec.clf.bonsai.ros.TFTransformer;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.navigation.PositionData.ReferenceFrame;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import org.ros.message.MessageFactory;

public class ExtendedPersonStampedSerializer extends RosSerializer<PersonData, clf_perception_vision_msgs.ExtendedPersonStamped> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExtendedPersonStampedSerializer.class);
    @Override
    public PersonData deserialize(ExtendedPersonStamped msg) throws DeserializationException {

        //logger.fatal("\n\n\n\n\n\n\n\nFail!!!!!!!!!!!!!!!!!!!!!\n\n\n\n\n\n\n\n");
        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Pose3D pose = MsgTypeFactory.getInstance().createType(msg.getPose().getPose(), Pose3D.class);
        //Point3D globalLocation = MsgTypeFactory.getInstance().createType(msg.getPosition(), Point3D.class);
        //Point3D globalVelocity = MsgTypeFactory.getInstance().createType(msg.getVelocity(), Point3D.class);

        PositionData positionData = new PositionData();
        positionData.setFrameId(ReferenceFrame.GLOBAL);



        positionData.setX(pose.getTranslation().getX(iLU), iLU);
        positionData.setY(pose.getTranslation().getY(iLU), iLU);
        //TODO: velocity to yaw
        positionData.setYaw(pose.getRotation().getYaw(iAU), iAU);
        positionData.setFrameId(msg.getPose().getHeader().getFrameId());

        PersonData personData = new PersonData();
        //id is written in name attr
        String id = msg.getTransformid();
        personData.setUuid(id);
        
        personData.setPosition(positionData);
        personData.setFrameId(positionData.getFrameId());

        return personData;
    }

    @Override
    public ExtendedPersonStamped serialize(PersonData data, MessageFactory fact) throws SerializationException {
        ExtendedPersonStamped person = fact.newFromType(ExtendedPersonStamped._TYPE);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

//        LengthUnit iLU = LengthUnit.METER;
//        AngleUnit iAU = AngleUnit.RADIAN;
//
//        person.setPosition((geometry_msgs.Point)MsgTypeFactory.getInstance().createMsg(data.getPosition()));
//        Body.Builder bodyBuilder = builder.getBodyBuilder();
//
//        PositionData position = data.getPosition();
//
//        Rotation3D globalOrientation = new Rotation3D(new Vector3d(0, 0, 1), position.getYaw(iAU), iAU);
//        globalOrientation.setFrameId(position.getFrameId());
//        Point3D globalLocation = new Point3D(position.getX(iLU), position.getY(iLU), 0.0, iLU);
//        globalLocation.setFrameId(position.getFrameId());
//
//        Rotation3DSerializer rot = new Rotation3DSerializer();
//        rot.serialize(globalOrientation, bodyBuilder.getOrientationBuilder());
//
//        Point3DSerializer p = new Point3DSerializer();
//        p.serialize(globalLocation, bodyBuilder.getLocationBuilder());
//
//        builder.getTrackingInfoBuilder().setId(data.getId());
    }

    @Override
    public Class<ExtendedPersonStamped> getMessageType() {
        return ExtendedPersonStamped.class;
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }

}

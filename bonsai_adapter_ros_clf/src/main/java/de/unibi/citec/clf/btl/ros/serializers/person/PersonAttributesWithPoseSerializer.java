package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.navigation.PositionData.ReferenceFrame;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import org.ros.message.MessageFactory;

public class PersonAttributesWithPoseSerializer extends RosSerializer<PersonData, openpose_ros_msgs.PersonAttributesWithPose> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PersonAttributesWithPoseSerializer.class);
    @Override
    public PersonData deserialize(openpose_ros_msgs.PersonAttributesWithPose msg) throws DeserializationException {

        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Point3D localLocation = MsgTypeFactory.getInstance().createType( msg.getPoseStamped().getPose().getPosition(), Point3D.class);

        PositionData positionData = new PositionData();
        positionData.setFrameId(ReferenceFrame.LOCAL);
        positionData.setX(localLocation.getX(iLU), iLU);
        positionData.setY(localLocation.getY(iLU), iLU);
        positionData.setYaw(0.0, iAU);

        PersonData personData = new PersonData();

        personData.setPosition(positionData);
        personData.setFrameId(positionData.getFrameId());
        
        Point3D localHeadLocation = MsgTypeFactory.getInstance().createType( msg.getHeadPoseStamped().getPose().getPosition(), Point3D.class);
        personData.setHeadPosition(localHeadLocation);

        PersonAttribute attribute = MsgTypeFactory.getInstance().createType(msg.getAttributes(),PersonAttribute.class);

        personData.setName(msg.getAttributes().getName());
        personData.setPersonAttribute(attribute);

        return personData;
    }

    @Override
    public openpose_ros_msgs.PersonAttributesWithPose serialize(PersonData data, MessageFactory fact) throws SerializationException {
        openpose_ros_msgs.PersonAttributesWithPose person = fact.newFromType(openpose_ros_msgs.PersonAttributesWithPose._TYPE);
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
//        Point3D localLocation = new Point3D(position.getX(iLU), position.getY(iLU), 0.0, iLU);
//        localLocation.setFrameId(position.getFrameId());
//
//        Rotation3DSerializer rot = new Rotation3DSerializer();
//        rot.serialize(globalOrientation, bodyBuilder.getOrientationBuilder());
//
//        Point3DSerializer p = new Point3DSerializer();
//        p.serialize(localLocation, bodyBuilder.getLocationBuilder());
//
//        builder.getTrackingInfoBuilder().setId(data.getId());
    }

    @Override
    public Class<openpose_ros_msgs.PersonAttributesWithPose> getMessageType() {
        return openpose_ros_msgs.PersonAttributesWithPose.class;
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }

}

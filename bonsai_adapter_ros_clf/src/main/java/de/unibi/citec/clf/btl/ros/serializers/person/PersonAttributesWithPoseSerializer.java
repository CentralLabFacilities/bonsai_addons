package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose2D;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import org.ros.message.MessageFactory;

public class PersonAttributesWithPoseSerializer extends RosSerializer<PersonData, clf_person_recognition_msgs.PersonAttributesWithPose> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PersonAttributesWithPoseSerializer.class);
    @Override
    public PersonData deserialize(clf_person_recognition_msgs.PersonAttributesWithPose msg) throws DeserializationException {

        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Point3D localLocation = MsgTypeFactory.getInstance().createType( msg.getPoseStamped().getPose().getPosition(), Point3D.class);

        Pose2D pose2D = new Pose2D();
        pose2D.setFrameId(msg.getPoseStamped().getHeader().getFrameId());
        pose2D.setX(localLocation.getX(iLU), iLU);
        pose2D.setY(localLocation.getY(iLU), iLU);
        pose2D.setYaw(0.0, iAU);
        MsgTypeFactory.setHeader(pose2D, msg.getPoseStamped().getHeader());

        PersonData personData = new PersonData();
        MsgTypeFactory.setHeader(personData, msg.getPoseStamped().getHeader());

        personData.setPosition(pose2D);
        personData.setFrameId(pose2D.getFrameId());

        Point3D localHeadLocation = MsgTypeFactory.getInstance().createType( msg.getHeadPoseStamped().getPose().getPosition(), Point3D.class);

        personData.setHeadPosition(localHeadLocation);

        Point3D leftHandLocation = MsgTypeFactory.getInstance().createType( msg.getLeftHand().getPose().getPosition(), Point3D.class);

        personData.setLeftHandPosition(leftHandLocation);

        Point3D rightHandLocation = MsgTypeFactory.getInstance().createType( msg.getRightHand().getPose().getPosition(), Point3D.class);

        personData.setRightHandPosition(rightHandLocation);

        Point3D leftEyeLocation = MsgTypeFactory.getInstance().createType( msg.getLeftEyeStamped().getPose().getPosition(), Point3D.class);

        personData.setLeftEyePosition(leftEyeLocation);

        Point3D rightEyeLocation = MsgTypeFactory.getInstance().createType( msg.getRightEyeStamped().getPose().getPosition(), Point3D.class);

        personData.setRightEyePosition(rightEyeLocation);

        Point3D noseLocation = MsgTypeFactory.getInstance().createType( msg.getNoseStamped().getPose().getPosition(), Point3D.class);

        personData.setNosePosition(noseLocation);

        PersonAttribute attribute = MsgTypeFactory.getInstance().createType(msg.getAttributes(),PersonAttribute.class);

        personData.setEstimateAngle(((double) msg.getEstimateAngle()));

        personData.setName(msg.getAttributes().getName());
        personData.setPersonAttribute(attribute);

        return personData;
    }

    @Override
    public clf_person_recognition_msgs.PersonAttributesWithPose serialize(PersonData data, MessageFactory fact) throws SerializationException {
        clf_person_recognition_msgs.PersonAttributesWithPose person = fact.newFromType(clf_person_recognition_msgs.PersonAttributesWithPose._TYPE);
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
    public Class<clf_person_recognition_msgs.PersonAttributesWithPose> getMessageType() {
        return clf_person_recognition_msgs.PersonAttributesWithPose.class;
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }

}

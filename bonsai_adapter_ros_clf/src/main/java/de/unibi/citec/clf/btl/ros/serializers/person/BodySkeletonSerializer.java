package de.unibi.citec.clf.btl.ros.serializers.person;



import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.person.BodySkeleton;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;

import openpose_ros_msgs.PersonDetection;

public class BodySkeletonSerializer extends RosSerializer<BodySkeleton, openpose_ros_msgs.PersonDetection> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BodySkeletonSerializer.class);
    @Override
    public BodySkeleton deserialize(PersonDetection skel) throws DeserializationException {
        BodySkeleton retSkel = new BodySkeleton();
        
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.CHEST, MsgTypeFactory.getInstance().createType(skel.getChest().getPos(), Point3D.class), 
                skel.getChest().getConfidence(), skel.getChest().getU(), skel.getChest().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LANKLE, MsgTypeFactory.getInstance().createType(skel.getLAnkle().getPos(), Point3D.class), 
                skel.getLAnkle().getConfidence(), skel.getLAnkle().getU(), skel.getLAnkle().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LEAR, MsgTypeFactory.getInstance().createType(skel.getLEar().getPos(), Point3D.class), 
                skel.getLEar().getConfidence(), skel.getLEar().getU(), skel.getLEar().getV());  
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LELBOW, MsgTypeFactory.getInstance().createType(skel.getLElbow().getPos(), Point3D.class), 
                skel.getLElbow().getConfidence(), skel.getLElbow().getU(), skel.getLElbow().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LEYE, MsgTypeFactory.getInstance().createType(skel.getLEye().getPos(), Point3D.class), 
                skel.getLEye().getConfidence(), skel.getLEye().getU(), skel.getLEye().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LHIP, MsgTypeFactory.getInstance().createType(skel.getLHip().getPos(), Point3D.class), 
                skel.getLHip().getConfidence(), skel.getLHip().getU(), skel.getLHip().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LKNEE, MsgTypeFactory.getInstance().createType(skel.getLKnee().getPos(), Point3D.class), 
                skel.getLKnee().getConfidence(), skel.getLKnee().getU(), skel.getLKnee().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LSHOULDER, MsgTypeFactory.getInstance().createType(skel.getLShoulder().getPos(), Point3D.class), 
                skel.getLShoulder().getConfidence(), skel.getLShoulder().getU(), skel.getLShoulder().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.LWRIST, MsgTypeFactory.getInstance().createType(skel.getLWrist().getPos(), Point3D.class), 
                skel.getLWrist().getConfidence(), skel.getLWrist().getU(), skel.getLWrist().getV());  
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.NECK, MsgTypeFactory.getInstance().createType(skel.getNeck().getPos(), Point3D.class), 
                skel.getNeck().getConfidence(), skel.getNeck().getU(), skel.getNeck().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.NOSE, MsgTypeFactory.getInstance().createType(skel.getNose().getPos(), Point3D.class), 
                skel.getNose().getConfidence(), skel.getNose().getU(), skel.getNose().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RANKLE, MsgTypeFactory.getInstance().createType(skel.getRAnkle().getPos(), Point3D.class), 
                skel.getRAnkle().getConfidence(), skel.getRAnkle().getU(), skel.getRAnkle().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.REAR, MsgTypeFactory.getInstance().createType(skel.getREar().getPos(), Point3D.class), 
                skel.getREar().getConfidence(), skel.getREar().getU(), skel.getREar().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RELBOW, MsgTypeFactory.getInstance().createType(skel.getRElbow().getPos(), Point3D.class), 
                skel.getRElbow().getConfidence(), skel.getRElbow().getU(), skel.getRElbow().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.REYE, MsgTypeFactory.getInstance().createType(skel.getREye().getPos(), Point3D.class), 
                skel.getREye().getConfidence(), skel.getREye().getU(), skel.getREye().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RHIP, MsgTypeFactory.getInstance().createType(skel.getRHip().getPos(), Point3D.class), 
                skel.getRHip().getConfidence(), skel.getRHip().getU(), skel.getRHip().getV());  
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RKNEE, MsgTypeFactory.getInstance().createType(skel.getRKnee().getPos(), Point3D.class), 
                skel.getRKnee().getConfidence(), skel.getRKnee().getU(), skel.getRKnee().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RSHOULDER, MsgTypeFactory.getInstance().createType(skel.getRShoulder().getPos(), Point3D.class),
                skel.getRShoulder().getConfidence(), skel.getRShoulder().getU(), skel.getRShoulder().getV());
        retSkel.addJoint(BodySkeleton.SkeletonJoint.JointType.RWRIST, MsgTypeFactory.getInstance().createType(skel.getRWrist().getPos(), Point3D.class), 
                skel.getRWrist().getConfidence(), skel.getRWrist().getU(), skel.getRWrist().getV());

        /*
        switch(skel.getGenderHyp().getGender()) {
            case "M": retSkel.setGender(PersonAttribute.Gender.MALE); break;
            case "F": retSkel.setGender(PersonAttribute.Gender.FEMALE); break;
            default: retSkel.setGender(PersonAttribute.Gender.MALE); break;
        }
        retSkel.setAge(skel.getAgeHyp().getAge());
        
        
        switch(skel.getShirtcolor()) {
            case "red": retSkel.setShirtcolor(PersonAttribute.Shirtcolor.RED); break;
            case "blue": retSkel.setShirtcolor(PersonAttribute.Shirtcolor.BLUE); break;
            case "black": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.BLACK); break;
            case "green": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.GREEN); break;
            case "grey": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.GREY); break;
            case "orange": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.ORANGE); break;
            case "purple": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.PURPLE); break;
            case "white": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.WHITE); break;
            case "yellow": retSkel.setShirtcolor(BodySkeleton.Shirtcolor.YELLOW); break;
            default: retSkel.setShirtcolor(BodySkeleton.Shirtcolor.WHITE); break;
        }
        */
        return retSkel;
    }
    
    @Override
    public PersonDetection serialize(BodySkeleton data, MessageFactory fact) throws SerializationException {
        PersonDetection skeleton = fact.newFromType(PersonDetection._TYPE);
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
    public Class<PersonDetection> getMessageType() {
        return PersonDetection.class;
    }

    @Override
    public Class<BodySkeleton> getDataType() {
        return BodySkeleton.class;
    }
}

package de.unibi.citec.clf.btl.ros.serializers.person;



import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.navigation.PositionData.ReferenceFrame;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import org.ros.message.MessageFactory;

import people_msgs.Person;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class PersonSerializer extends RosSerializer<PersonData, people_msgs.Person> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PersonSerializer.class);
    @Override
    public PersonData deserialize(people_msgs.Person msg) throws DeserializationException {

        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Point3D globalLocation = MsgTypeFactory.getInstance().createType(msg.getPosition(), Point3D.class);
        Point3D globalVelocity = MsgTypeFactory.getInstance().createType(msg.getVelocity(), Point3D.class);

        PositionData positionData = new PositionData();
        positionData.setFrameId(ReferenceFrame.GLOBAL);
        positionData.setX(globalLocation.getX(iLU), iLU);
        positionData.setY(globalLocation.getY(iLU), iLU);
        //TODO: velocity to yaw
        positionData.setYaw(0.0, iAU);

        PersonData personData = new PersonData();
        //id is written in name attr        
        String id = msg.getName();
        personData.setUuid(id);

        personData.setPosition(positionData);
        personData.setFrameId(positionData.getFrameId());

        return personData;
    }

    @Override
    public people_msgs.Person serialize(PersonData data, MessageFactory fact) throws SerializationException {
        people_msgs.Person person = fact.newFromType(people_msgs.Person._TYPE);
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
    public Class<people_msgs.Person> getMessageType() {
        return people_msgs.Person.class;
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }

}

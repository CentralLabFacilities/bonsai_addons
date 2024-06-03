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

import java.util.ArrayList;
import java.util.List;

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
        personData.setReliability(msg.getReliability());

        return personData;
    }

    @Override
    public people_msgs.Person serialize(PersonData data, MessageFactory fact) throws SerializationException {

        people_msgs.Person person = fact.newFromType(people_msgs.Person._TYPE);

        person.setName(data.getUuid());
        person.setPosition(MsgTypeFactory.getInstance().createMsg(data.getPosition(), geometry_msgs.Point.class));
        person.setReliability(data.getReliability());

        return person;
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

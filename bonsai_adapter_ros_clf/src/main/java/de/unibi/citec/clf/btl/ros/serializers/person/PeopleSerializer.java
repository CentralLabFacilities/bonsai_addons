package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.person.PersonDataList;
import people_msgs.People;
import people_msgs.Person;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;

@Deprecated //Use PeoplewithHeadSerializer instead
public class PeopleSerializer extends RosSerializer<PersonDataList, People> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PeopleSerializer.class);
    @Override
    public PersonDataList deserialize(People msg) throws DeserializationException {
        PersonDataList persons = new PersonDataList();

        for(Person p: msg.getPeople()){
            PersonData person = MsgTypeFactory.getInstance().createType(p, PersonData.class);
            person.getPosition().setFrameId(msg.getHeader().getFrameId());
            persons.add(person);
        }

        return persons;
    }

    @Override
    public People serialize(PersonDataList data, MessageFactory fact) throws SerializationException {
        People people = fact.newFromType(People._TYPE);
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
    public Class<People> getMessageType() {
        return People.class;
    }

    @Override
    public Class<PersonDataList> getDataType() {
        return PersonDataList.class;
    }

}

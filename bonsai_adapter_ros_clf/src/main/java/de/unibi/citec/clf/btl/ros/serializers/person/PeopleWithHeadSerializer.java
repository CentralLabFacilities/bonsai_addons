package de.unibi.citec.clf.btl.ros.serializers.person;

import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;
import people_msgs.Person;
import geometry_msgs.Point;
import bayes_people_tracker_msgs.PeopleWithHead;

public class PeopleWithHeadSerializer extends RosSerializer<PersonDataList, PeopleWithHead> {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PeopleWithHeadSerializer.class);

    @Override
    public PeopleWithHead serialize(PersonDataList data, MessageFactory fact) throws RosSerializer.SerializationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PersonDataList deserialize(PeopleWithHead msg) throws DeserializationException {
        PersonDataList persons = new PersonDataList();

        for(int i = 0; i < msg.getPeople().size(); i++){
            Person pers = msg.getPeople().get(i);
            Point point = msg.getHeadPositions().get(i);

            PersonData person = MsgTypeFactory.getInstance().createType(pers, PersonData.class);
            person.getPosition().setFrameId(msg.getHeader().getFrameId());
            Point3D headposi = MsgTypeFactory.getInstance().createType(point, Point3D.class);
            person.setHeadPosition(headposi);

            persons.add(person);
        }

        return persons;
    }

    @Override
    public Class<PeopleWithHead> getMessageType() {
        return PeopleWithHead.class;
    }

    @Override
    public Class<PersonDataList> getDataType() {
        return PersonDataList.class;
    }
}

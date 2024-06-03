package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.units.TimeUnit;
import people_msgs.People;
import people_msgs.Person;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;
import std_msgs.Header;

import java.util.ArrayList;
import java.util.List;

public class PeopleSerializer extends RosSerializer<PersonDataList, People> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PeopleSerializer.class);

    @Override
    public PersonDataList deserialize(People msg) throws DeserializationException {
        PersonDataList persons = new PersonDataList();

        for (Person p : msg.getPeople()) {
            PersonData person = MsgTypeFactory.getInstance().createType(p, PersonData.class);
            String frame_id = msg.getHeader().getFrameId().startsWith("/") ? msg.getHeader().getFrameId().substring(1) : msg.getHeader().getFrameId();
//            logger.trace("Frame id of deserialized person: "+frame_id);
            person.getPosition().setFrameId(frame_id);
            person.getTimestamp().setCreated(msg.getHeader().getStamp().secs, TimeUnit.SECONDS);
            persons.add(person);
        }

        return persons;
    }

    @Override
    public People serialize(PersonDataList data, MessageFactory fact) throws SerializationException {

        People people = fact.newFromType(People._TYPE);

        List<Person> personList = new ArrayList<>();

        for (PersonData personData : data) {
            personList.add(MsgTypeFactory.getInstance().createMsg(personData, Person.class));
        }

        people.setPeople(personList);
        people.setHeader(MsgTypeFactory.getInstance().makeHeader(data));

        return people;
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

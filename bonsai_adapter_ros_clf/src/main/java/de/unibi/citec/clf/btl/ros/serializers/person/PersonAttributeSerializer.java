package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import openpose_ros_msgs.PersonAttributes;
import org.ros.message.MessageFactory;

import java.util.Arrays;
import java.util.LinkedList;


public class PersonAttributeSerializer extends RosSerializer<PersonAttribute, openpose_ros_msgs.PersonAttributes> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PersonAttributeSerializer.class);

    @Override
    public openpose_ros_msgs.PersonAttributes serialize(PersonAttribute data, MessageFactory fact) throws SerializationException {
        logger.fatal("We don't care, because we don't need this!");
        return null;
    }

    @Override
    public PersonAttribute deserialize(openpose_ros_msgs.PersonAttributes msg) throws DeserializationException {
        PersonAttribute ret = new PersonAttribute();

        ret.setAge(msg.getAgeHyp().getAge());
        switch (msg.getGenderHyp().getGender()) {
            default:
            case "male": ret.setGender(PersonAttribute.Gender.MALE); break;
            case "female": ret.setGender(PersonAttribute.Gender.FEMALE); break;
        }
        switch(msg.getShirtcolor()) {
            default:
            case "no color":
            case "white": ret.setShirtcolor(PersonAttribute.Shirtcolor.WHITE); break;
            case "red": ret.setShirtcolor(PersonAttribute.Shirtcolor.RED); break;
            case "orange": ret.setShirtcolor(PersonAttribute.Shirtcolor.ORANGE); break;
            case "yellow": ret.setShirtcolor(PersonAttribute.Shirtcolor.YELLOW); break;
            case "green": ret.setShirtcolor(PersonAttribute.Shirtcolor.GREEN); break;
            case "blue": ret.setShirtcolor(PersonAttribute.Shirtcolor.BLUE); break;
            case "purple": ret.setShirtcolor(PersonAttribute.Shirtcolor.PURPLE); break;
            case "black": ret.setShirtcolor(PersonAttribute.Shirtcolor.BLACK); break;
            case "grey": ret.setShirtcolor(PersonAttribute.Shirtcolor.GREY); break;
        }

        switch(msg.getPosture().getPosture()) {
            case 1: ret.setPosture(PersonAttribute.Posture.SITTING); break;
            default:
            case 2: ret.setPosture(PersonAttribute.Posture.STANDING); break;
            case 3: ret.setPosture(PersonAttribute.Posture.LYING); break;
        }
        LinkedList<PersonAttribute.Gesture> gestures = new LinkedList<>();
        for(openpose_ros_msgs.Gesture gesture: msg.getGestures()){
            int g = gesture.getGesture();
            switch(g) {
                case 1: gestures.add(PersonAttribute.Gesture.POINTING_LEFT); break;
                case 2: gestures.add(PersonAttribute.Gesture.POINTING_RIGHT); break;
                case 3: gestures.add(PersonAttribute.Gesture.RAISING_LEFT_ARM); break;
                case 4: gestures.add(PersonAttribute.Gesture.RAISING_RIGHT_ARM); break;
                case 5: gestures.add(PersonAttribute.Gesture.WAVING); break;
                default:
                case 6: gestures.add(PersonAttribute.Gesture.NEUTRAL); break;
            }
        }

        ret.setGestures(gestures);

        return ret;
    }

    @Override
    public Class<PersonAttributes> getMessageType() {
        return PersonAttributes.class;
    }

    @Override
    public Class<PersonAttribute> getDataType() {
        return PersonAttribute.class;
    }
}

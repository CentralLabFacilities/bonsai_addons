package de.unibi.citec.clf.btl.ros.serializers.person;


import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import clf_person_recognition_msgs.Gesture;
import clf_person_recognition_msgs.PersonAttributes;
import clf_person_recognition_msgs.Posture;
import org.ros.message.MessageFactory;

import java.util.LinkedList;


public class PersonAttributeSerializer extends RosSerializer<PersonAttribute, clf_person_recognition_msgs.PersonAttributes> {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PersonAttributeSerializer.class);

    @Override
    public clf_person_recognition_msgs.PersonAttributes serialize(PersonAttribute data, MessageFactory fact) throws SerializationException {
        logger.fatal("We don't care, because we don't need this!");
        return null;
    }

    @Override
    public PersonAttribute deserialize(clf_person_recognition_msgs.PersonAttributes msg) throws DeserializationException {
        PersonAttribute ret = new PersonAttribute();

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
            case Posture.SITTING: ret.setPosture(PersonAttribute.Posture.SITTING); break;
            case Posture.STANDING: ret.setPosture(PersonAttribute.Posture.STANDING); break;
            case Posture.LYING: ret.setPosture(PersonAttribute.Posture.LYING); break;
            default: throw new DeserializationException("unhandled Gesture: " + msg.getPosture().getPosture());
        }
        LinkedList<PersonAttribute.Gesture> gestures = new LinkedList<>();
        for(clf_person_recognition_msgs.Gesture gesture: msg.getGestures()){
            int g = gesture.getGesture();
            switch(g) {
                case Gesture.POINTING_LEFT: gestures.add(PersonAttribute.Gesture.POINTING_LEFT); break;
                case Gesture.POINTING_RIGHT: gestures.add(PersonAttribute.Gesture.POINTING_RIGHT); break;
                case Gesture.RAISING_LEFT_ARM: gestures.add(PersonAttribute.Gesture.RAISING_LEFT_ARM); break;
                case Gesture.RAISING_RIGHT_ARM: gestures.add(PersonAttribute.Gesture.RAISING_RIGHT_ARM); break;
                case Gesture.WAVING: gestures.add(PersonAttribute.Gesture.WAVING); break;
                case Gesture.NEUTRAL: gestures.add(PersonAttribute.Gesture.NEUTRAL); break;
                case Gesture.POINTING_LEFT_DOWN: gestures.add(PersonAttribute.Gesture.POINTING_LEFT_DOWN); break;
                case Gesture.POINTING_RIGHT_DOWN: gestures.add(PersonAttribute.Gesture.POINTING_RIGHT_DOWN); break;
                default: throw new DeserializationException("unhandled Gesture: " + g);
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

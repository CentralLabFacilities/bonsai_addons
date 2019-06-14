package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.DisplayActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import pepper_clf_msgs.DisplayChoice;
import pepper_clf_msgs.ChoiceGroup;
import pepper_clf_msgs.Choice;

import java.util.*;

public class PepperDisplayActuator extends RosNode implements DisplayActuator  {


    String choiceTopic;
    private GraphName nodeName;
    private Publisher<DisplayChoice> choicePublisher;
    private MessageFactory msgFactory;

    public PepperDisplayActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {

        this.choiceTopic = conf.requestValue("choiceTopic");
    }


    @Override
    public void setWindowText(String txt) {

    }

    @Override
    public void displayChoice(String layout,
                              String caption,
                              Byte caption_mode,
                              List<String> choices,
                              Map<String, String> choice_captions,
                              Map<String, String> choice_submit_texts,
                              Map<String, Byte> choice_types,
                              Map<String, List<String>> choice_group_names,
                              Map<String, List<String>> choice_group_items,
                              Map<String, String> templates) {

        DisplayChoice dc = choicePublisher.newMessage();
        dc.setLayout(layout);
        dc.setCaption(caption);
        dc.setCaptionMode(caption_mode);


        for(String choice_name: choices){
            Choice choice =  msgFactory.newFromType(Choice._TYPE);
            choice.setChoiceId(choice_name);
            choice.setType(choice_types.get(choice_name));
            choice.setCaption(choice_captions.get(choice_name));
            try{
                choice.setSubmitText(choice_submit_texts.get(choice_name));
            }catch(NullPointerException ignored){

            }
            for(String choice_group_name: choice_group_names.get(choice_name)){
                ChoiceGroup choice_group = msgFactory.newFromType(ChoiceGroup._TYPE);

                choice_group.setId(choice_group_name);
                for(String item: choice_group_items.get(choice_group_name)){
                    choice_group.getValues().add(item.toLowerCase());
                    //choice_group.getDisplayNames().add(WordUtils.capitalize(item));
                    choice_group.getDisplayNames().add(item);
                }
                choice.getChoiceGroups().add(choice_group);
            }
            choice.setTemplate(templates.get(choice_name));
            dc.getChoices().add(choice);
        }

        choicePublisher.publish(dc);

    }


    @Override
    public void onStart(ConnectedNode connectedNode) {

        msgFactory = connectedNode.getTopicMessageFactory();
        choicePublisher = connectedNode.newPublisher(choiceTopic, DisplayChoice._TYPE);

        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(choicePublisher!=null){choicePublisher.shutdown();}

    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }
}

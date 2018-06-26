package de.unibi.citec.clf.bonsai.rsb;



import de.unibi.citec.clf.bonsai.actuators.SpeechActuator;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import de.unibi.citec.clf.bonsai.core.BonsaiManager;
import de.unibi.citec.clf.bonsai.core.configuration.XmlConfigurationParser;

public class SaySrvTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        
        BasicConfigurator.configure();

        BonsaiManager.getInstance().configure("src/test/resources/saysrvtest.xml", new XmlConfigurationParser());

        SpeechActuator speechActuator = BonsaiManager.getInstance().createActuator(
                "SpeechActuator", SpeechActuator.class);

        //Thread.sleep(4000);

        System.out.println("Talk first string");
        speechActuator.say("my name is tobi");
        System.out.println("Done Talk first string");
        speechActuator.sayAsync("my name is tobi");
        System.out.println("Done Talk second string");
    }

}

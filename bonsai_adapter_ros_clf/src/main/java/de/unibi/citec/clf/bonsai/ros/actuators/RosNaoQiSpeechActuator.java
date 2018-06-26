package de.unibi.citec.clf.bonsai.ros.actuators;

import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;

import naoqi_bridge_msgs.SpeechWithFeedbackActionFeedback;
import naoqi_bridge_msgs.SpeechWithFeedbackActionGoal;
import naoqi_bridge_msgs.SpeechWithFeedbackActionResult;
import org.ros.message.Duration;

/**
 *
 * @author llach
 */
public class RosNaoQiSpeechActuator extends RosNode implements SpeechActuator {

    String topic;
    private GraphName nodeName;
    private ActionClient<SpeechWithFeedbackActionGoal, SpeechWithFeedbackActionFeedback, SpeechWithFeedbackActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosNaoQiSpeechActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    private ActionFuture sendToTTS(String data) throws IOException {

        logger.debug(">>> waiting for TTS-Action server...");
        if (!ac.waitForActionServerToStart(new Duration(10))) {
            logger.error("action server not started on " + this.topic);
        }
        logger.debug(">>> TTS-Action server found! sending action goal");
        SpeechWithFeedbackActionGoal goalMessage = ac.newGoalMessage();
        goalMessage.getGoal().setSay(data);

        return ac.sendGoal(goalMessage);

    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.topic, SpeechWithFeedbackActionGoal._TYPE, SpeechWithFeedbackActionFeedback._TYPE, SpeechWithFeedbackActionResult._TYPE);
        initialized = true;
        logger.debug("on start, RosNaoQiSpeechActuator done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }

    @Override
    public Future<Void> sayAsync(String text) throws IOException {
        logger.info("NaoQi TTS: " + text);

        Future<Void> ret = sendToTTS(text).toVoidFuture();

        return ret;
    }

    @Override
    public void say(String text) throws IOException {
        logger.info("NaoQi TTS: " + text);
        try {
            sendToTTS(text).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IOException("get failed");
        }
    }

    @Override
    public void sayAccentuated(String accented_text) throws IOException {
        logger.info("sayAccentuated not supported atm, saying" + accented_text + "it anyway.");
        sendToTTS(accented_text);
    }

    @Override
    public void sayAccentuated(String accented_text, String prosodyConfig) throws IOException {
        logger.info("sayAccentuated not supported atm, saying" + accented_text + "it anyway.");
        sendToTTS(accented_text);
    }

    @Override
    public void sayAccentuated(String accented_text, boolean async) throws IOException {
        logger.info("sayAccentuated not supported atm, saying" + accented_text + "it anyway.");
        sendToTTS(accented_text);
    }

    @Override
    public void sayAccentuated(String accented_text, boolean async, String prosodyConfig) throws IOException {
        logger.info("sayAccentuated not supported atm, saying" + accented_text + "it anyway.");
        sendToTTS(accented_text);
    }
    
}

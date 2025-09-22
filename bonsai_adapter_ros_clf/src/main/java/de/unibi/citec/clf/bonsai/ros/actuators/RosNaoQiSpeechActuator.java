package de.unibi.citec.clf.bonsai.ros.actuators;

import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.Future;

import de.unibi.citec.clf.btl.data.speech.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        logger.debug(">>> waiting for TTS-Action server...");
        if (!ac.waitForActionServerToStart(new Duration(10))) {
            logger.error("action server not started on " + this.topic);
        }
        logger.debug(">>> TTS-Action server found! sending action goal");
        initialized = true;
        logger.debug("on start, RosNaoQiSpeechActuator done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }

    @Nullable
    @Override
    public Future<String> sayTranslated(@NotNull String text, @NotNull Language language, @NotNull Language lang) throws IOException {
        return null;
    }

    @Nullable
    @Override
    public Future<Boolean> enableASR(boolean enable) throws IOException {
        return null;
    }

    @NotNull
    @Override
    public Future<Void> sayAsync(@NotNull String text, @NotNull Language language) throws IOException {
        logger.info("NaoQi TTS: " + text);

        Future<Void> ret = sendToTTS(text).toVoidFuture();

        return ret;
    }
}

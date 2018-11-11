package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import com.github.rosjava_actionlib.ActionFuture
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import org.ros.exception.RosException
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.util.concurrent.Future

import pal_interaction_msgs.TtsActionFeedback
import pal_interaction_msgs.TtsActionGoal
import pal_interaction_msgs.TtsActionResult
import java.io.IOException

/**
 *
 * @author lruegeme
 */
class PalSpeech(private val nodeName: GraphName) : RosNode(), SpeechActuator {

    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var ac: ActionClient<TtsActionGoal, TtsActionFeedback, TtsActionResult>? = null
    private lateinit var topic: String

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic")
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(connectedNode, this.topic, TtsActionGoal._TYPE, TtsActionFeedback._TYPE, TtsActionResult._TYPE)

        if(ac?.waitForActionServerToStart(Duration(2.0)) ==  true) {
            logger.info("PalSpeech server connected $topic")
            initialized = true
        } else {
            logger.error("PalSpeech server timeout after 2sec $topic")
        }

    }

    override fun destroyNode() {
        ac?.finish()
    }

    private fun sendToTTS(data: String, langId : String = "en_GB"): ActionFuture<TtsActionGoal, TtsActionFeedback, TtsActionResult> {

        ac?.let { client ->
            val goal = client.newGoalMessage()
            goal.goal.rawtext.text = data
            goal.goal.rawtext.langId = langId

            val sendGoal = client.sendGoal(goal)

            logger.info("PAL TTS: $data")

            return sendGoal

        }

        throw RosException("action server failure ${this.topic}")

    }

    @Throws(IOException::class)
    override fun say(text: String) {
        sendToTTS(text).get()
    }

    @Throws(IOException::class)
    override fun sayAsync(text: String): Future<Void> {
        return sendToTTS(text).toVoidFuture()
    }

    override fun sayAccentuated(accented_text: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, prosodyConfig: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, async: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, async: Boolean, prosodyConfig: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
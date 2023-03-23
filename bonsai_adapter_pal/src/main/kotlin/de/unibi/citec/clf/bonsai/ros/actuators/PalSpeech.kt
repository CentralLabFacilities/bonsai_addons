package de.unibi.citec.clf.bonsai.ros.actuators

import actionlib_msgs.GoalStatusArray
import com.github.rosjava_actionlib.ActionClient
import com.github.rosjava_actionlib.ActionClientListener
import com.github.rosjava_actionlib.ActionFuture
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import org.ros.exception.RosException
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import pal_interaction_msgs.TtsActionFeedback
import pal_interaction_msgs.TtsActionGoal
import pal_interaction_msgs.TtsActionResult
import std_srvs.*
import java.io.IOException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lruegeme
 */
class PalSpeech(private val nodeName: GraphName) : RosNode(), SpeechActuator, ActionClientListener<TtsActionFeedback, TtsActionResult> {
    override fun statusReceived(status: GoalStatusArray) {
        for(state in status.statusList) {
            if(state.status !in 2..5 && state.status < 8) return
        }
        enableSpeech(true)
    }

    override fun feedbackReceived(feedback: TtsActionFeedback) {
        //NOP
    }

    override fun resultReceived(result: TtsActionResult) {
        //NOP
    }

    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var ac: ActionClient<TtsActionGoal, TtsActionFeedback, TtsActionResult>? = null
    private var clientDisableSpeech: ServiceClient<SetBoolRequest, SetBoolResponse>? = null
    private lateinit var topic: String
    private var disableSpeechTopic: String = ""
    private var speechEnabled: Boolean? = null;

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic")
        disableSpeechTopic = conf.requestOptionalValue("disableSpeechTopic", disableSpeechTopic)
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(connectedNode, this.topic, TtsActionGoal._TYPE, TtsActionFeedback._TYPE, TtsActionResult._TYPE)
        if(disableSpeechTopic.isNotEmpty()) {
            try {
                clientDisableSpeech = connectedNode.newServiceClient(disableSpeechTopic,SetBool._TYPE)
            } catch (e: ServiceNotFoundException) {
                throw RosRuntimeException(e)
            }
        }
        if(ac?.waitForActionServerToStart(Duration(20.0)) ==  true) {
            logger.info("PalSpeech server connected $topic")
            ac?.attachListener(this)
            initialized = true
        } else {
            logger.error("PalSpeech server timeout after 20sec $topic")
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

    private fun enableSpeech(enabled: Boolean) {
        if (speechEnabled == enabled) return

        speechEnabled = enabled;
        clientDisableSpeech?.let {
            logger.debug("enabling speech $enabled")
            val a = it.newMessage()
            a.data = enabled
            val res = ResponseFuture<SetBoolResponse>()
            it.call(a, res)
            try {
                res.get(500,TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                logger.warn("enableSpeech Timeout")
            }

        } ?: logger.warn("SpeechRec not connected")

    }

    @Deprecated("Deprecated in Java")
    @Throws(IOException::class)
    override fun say(text: String) {
        sayAsync(text).get()
    }

    @Throws(IOException::class)
    override fun sayAsync(text: String): Future<Void> {
        enableSpeech(false)
        val ret = sendToTTS(text)
        return object : Future<Void> {
            override fun isDone(): Boolean {
                val done = ret.isDone()
                if(done) enableSpeech(true)
                return done
            }

            override fun get(): Void? {
                val a = ret.toVoidFuture().get()
                enableSpeech(true)
                return null
            }

            override fun get(p0: Long, p1: TimeUnit?): Void? {
                val a = ret.toVoidFuture().get(p0,p1)
                enableSpeech(true)
                return null
            }

            override fun cancel(p0: Boolean): Boolean {
                val b = ret.cancel(p0)
                enableSpeech(true)
                return b
            }

            override fun isCancelled(): Boolean {
                return ret.isCancelled()
            }

        }

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
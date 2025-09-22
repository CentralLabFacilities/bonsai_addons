package de.unibi.citec.clf.bonsai.ros.actuators

import actionlib_msgs.GoalID
import actionlib_msgs.GoalStatus
import actionlib_msgs.GoalStatusArray
import com.github.rosjava_actionlib.ActionClient
import com.github.rosjava_actionlib.ActionClientListener
import com.github.rosjava_actionlib.ActionFuture
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.speech.Language
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
import java.util.concurrent.*

/**
 *
 * @author lruegeme
 */
class PalSpeech(private val nodeName: GraphName) : RosNode(), SpeechActuator, ActionClientListener<TtsActionFeedback, TtsActionResult> {

    private val statesFinished = setOf(GoalStatus.SUCCEEDED)
    override fun statusReceived(status: GoalStatusArray) {
        for (state in status.statusList) {
            if(state.goalId.id != lastGoalId?.id) continue
            if(statesFinished.contains(state.status)) {
                enableSpeech(true)
                lastGoalId = null
                return
            }
        }
    }

    override fun feedbackReceived(feedback: TtsActionFeedback) {
        //NOP
    }

    override fun resultReceived(result: TtsActionResult) {
        //NOP
    }

    private var lastGoalId: GoalID? = null

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
            lastGoalId = sendGoal.goalId
            logger.info("PAL TTS: $data")

            return sendGoal

        }

        throw RosException("action server failure ${this.topic}")

    }

    private fun enableSpeech(enabled: Boolean) {
        if (speechEnabled == enabled) return
        speechEnabled = enabled;

        clientDisableSpeech?.run {
            enableASR(enabled).get()
        } ?: logger.warn("SpeechRec not connected")

    }

    override fun sayAsync(text: String, language: Language): Future<Void> {
        enableSpeech(false)
        val ret = sendToTTS(text)
        return ret.toVoidFuture()
    }

    override fun sayTranslated(text: String, speakLanguage: Language, textLanguage: Language): Future<String?> {
        enableSpeech(false)
        val ret = sendToTTS(text)
        return object : Future<String?> {
            override fun cancel(p0: Boolean): Boolean {
               return ret.cancel(p0)
            }

            override fun isCancelled(): Boolean {
                return ret.isCancelled
            }

            override fun isDone(): Boolean {
                return ret.isDone
            }

            override fun get(): String {
                ret.get()
                return ""
            }

            override fun get(p0: Long, p1: TimeUnit): String? {
                ret.get(p0,p1)
                return ""
            }

        }
    }

    override fun enableASR(enable: Boolean): Future<Boolean> {
        clientDisableSpeech?.let {
            logger.debug("enabling speech $enable")
            val a = it.newMessage()
            a.data = enable
            val res = ResponseFuture<SetBoolResponse>()
            it.call(a, res)
            return res.toBooleanFuture()

        } ?: throw NotImplementedError("SpeechRec not connected")
    }

}
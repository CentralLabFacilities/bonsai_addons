package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.PostureActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import org.ros.exception.RosException
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import play_motion_msgs.*
import std_srvs.SetBoolResponse
import std_srvs.Trigger
import std_srvs.TriggerRequest
import std_srvs.TriggerResponse
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class PlayMotion(private val nodeName: GraphName) : RosNode(), PostureActuator {
    override fun assumePose(pose: String, group: String?): Future<Boolean> {
        TODO("not implemented")
    }

    override fun isInPose(pose: String, group: String?): Future<Boolean?> {
        client_iat?.let { client ->
            val req = client.newMessage()
            req.motionName = pose
            req.tolerance = 0.05f
            val res = ResponseFuture<IsAlreadyThereResponse>()
            client.call(req, res)
            return res.toTypeFuture { it.alreadyThere }
        }
        throw RosException("is_already_there client not connected on topic $topic_iat")
    }

    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var ac: ActionClient<PlayMotionActionGoal, PlayMotionActionFeedback, PlayMotionActionResult>? = null
    private var client_iat: ServiceClient<IsAlreadyThereRequest, IsAlreadyThereResponse>? = null
    private lateinit var topic: String
    private var topic_iat: String? = null

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic")
        topic_iat = conf.requestOptionalValue("topic_is_already_there", "")
        if(topic_iat?.isEmpty() == true){
            topic_iat = null
        }
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(connectedNode, this.topic, PlayMotionActionGoal._TYPE, PlayMotionActionFeedback._TYPE, PlayMotionActionResult._TYPE)

        topic_iat?.let {
            try {
                client_iat = connectedNode.newServiceClient(topic_iat, IsAlreadyThere._TYPE)
            } catch (e: ServiceNotFoundException) {
                logger.error("PlayMotion: is_already_there not provided on $topic_iat")
                throw e
            }
        }

        if(ac?.waitForActionServerToStart(Duration(20.0)) ==  true) {
            logger.info("PlayMotion server connected $topic")
            initialized = true
        } else {
            logger.info("PlayMotion server timeout after 20sec $topic")
        }

    }

    override fun destroyNode() {
        ac?.finish()
    }

    override fun executeMotion(
        motion: String,
        group: String?
    ): Future<Boolean>? {

        ac?.let { client ->
            val goal = client.newGoalMessage()
            goal.goal.motionName = motion
            goal.goal.skipPlanning = false

            val sendGoal = client.sendGoal(goal)

            logger.info("PAL Play Motion: $motion")

            return object : Future<Boolean> {
                override fun cancel(p0: Boolean): Boolean = sendGoal.cancel(p0)
                override fun isCancelled(): Boolean = sendGoal.isCancelled
                override fun isDone(): Boolean = sendGoal.isDone
                override fun get(): Boolean = get(1000, TimeUnit.DAYS)
                override fun get(p0: Long, p1: TimeUnit): Boolean {
                    val code = sendGoal.get(p0, p1).result
                    logger.fatal("PlayMotion code:${code.errorCode} ${code.errorString}")
                    return code.errorCode == PlayMotionResult.SUCCEEDED
                }
            }

        }

        throw RosException("action server failure ${this.topic}")
    }

    override fun listMotions(group: String?): MutableList<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
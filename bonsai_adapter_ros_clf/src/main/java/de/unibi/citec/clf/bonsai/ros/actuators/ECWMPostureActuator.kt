package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.ManipulationActuator.MoveitResult
import de.unibi.citec.clf.bonsai.actuators.PostureActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import ecwm_msgs.NamedPoseActionFeedback
import ecwm_msgs.NamedPoseActionGoal
import ecwm_msgs.NamedPoseActionResult
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ECWMPostureActuator(private val nodeName: GraphName) : RosNode(), PostureActuator {
    private val logger = Logger.getLogger(javaClass)
    private var clientMoveTo:  ActionClient<NamedPoseActionGoal, NamedPoseActionFeedback, NamedPoseActionResult>? = null

    private var topic = "/ecwm/GraspServerMTC/move_to"

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName;
    }

    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestOptionalValue("topic", topic)
    }

    override fun executeMotion(motion: String, group: String?): Future<Boolean?> {
        return moveTo(motion,group, false)
    }

    override fun moveTo(pose: String, group: String?, upright: Boolean): Future<Boolean?> {
        clientMoveTo?.let {
            val req = it.newGoalMessage()
            req.goal.pose = pose
            req.goal.group = group
            req.goal.uprightMovement = upright
            val res = it.sendGoal(req)
            return object : Future<Boolean?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): Boolean = MoveitResult.getById(res.get().result.code.`val`) == MoveitResult.SUCCESS
                override fun get(p0: Long, p1: TimeUnit?): Boolean = MoveitResult.getById(res.get(p0,p1).result.code.`val`) == MoveitResult.SUCCESS
            }
        }
        throw RosException("service server failure ${this.topic}")
    }

    override fun moveTo(pose: String, group: String?): Future<Boolean?> {
        return moveTo(pose, group, false)
    }

    override fun isInPose(pose: String, group: String?): Future<Boolean> {
        TODO("Not yet implemented")
    }

    override fun listMotions(group: String?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientMoveTo = ActionClient(
            connectedNode,
            this.topic,
            NamedPoseActionGoal._TYPE,
            NamedPoseActionFeedback._TYPE,
            NamedPoseActionResult._TYPE )
        initialized = true
    }

    override fun destroyNode() {
        clientMoveTo?.let { it.finish() }
    }

}

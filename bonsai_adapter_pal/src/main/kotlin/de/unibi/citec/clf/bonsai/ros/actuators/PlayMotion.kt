package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.PostureActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import org.ros.exception.RosException
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.util.concurrent.Future

import play_motion_msgs.PlayMotionActionGoal
import play_motion_msgs.PlayMotionActionFeedback
import play_motion_msgs.PlayMotionActionResult


class PlayMotion(private val nodeName: GraphName) : RosNode(), PostureActuator {
    override fun assumePose(pose: String, group: String?): Future<Boolean> {
        TODO("not implemented")
    }

    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var ac: ActionClient<PlayMotionActionGoal, PlayMotionActionFeedback, PlayMotionActionResult>? = null
    private lateinit var topic: String

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic")
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(connectedNode, this.topic, PlayMotionActionGoal._TYPE, PlayMotionActionFeedback._TYPE, PlayMotionActionResult._TYPE)

        if(ac?.waitForActionServerToStart(Duration(4.0)) ==  true) {
            logger.info("PlayMotion server connected $topic")
            initialized = true
        } else {
            logger.info("PlayMotion server timeout after 4sec $topic")
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

            return sendGoal.toBooleanFuture()

        }

        throw RosException("action server failure ${this.topic}")
    }

    override fun listMotions(group: String?): MutableList<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
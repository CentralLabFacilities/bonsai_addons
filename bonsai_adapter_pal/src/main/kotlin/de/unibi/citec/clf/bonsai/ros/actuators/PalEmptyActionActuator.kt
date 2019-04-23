package de.unibi.citec.clf.bonsai.ros.actuators

import actionlib_msgs.GoalID
import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.ExecuteUntilCancelActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import pal_common_msgs.EmptyActionGoal
import pal_common_msgs.EmptyActionFeedback
import pal_common_msgs.EmptyActionResult
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.io.IOException
import java.util.concurrent.Future

/**
 *
 * @author lruegeme
 */
class PalEmptyActionActuator(private val nodeName: GraphName) : RosNode(), ExecuteUntilCancelActuator {

    private var ac: ActionClient<EmptyActionGoal, EmptyActionFeedback, EmptyActionResult>? = null
    private lateinit var topic: String
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var lastGoalId: GoalID? = null

    init {
        initialized = false
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(
            connectedNode,
            this.topic,
            EmptyActionGoal._TYPE,
            EmptyActionFeedback._TYPE,
            EmptyActionResult._TYPE
        )

        if (ac?.waitForActionServerToStart(Duration(20.0)) == true) {
            logger.info("PalEmptyActionActuator connected to $topic")
            initialized = true
        }

    }

    override fun destroyNode() {
        ac?.finish()
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    @Throws(ConfigurationException::class)
    override fun configure(ioc: IObjectConfigurator) {
        this.topic = ioc.requestValue("topic")
    }

    @Throws(IOException::class)
    override fun executeAction(): Future<Boolean> {
        ac?.let { client ->
            val goal = client.newGoalMessage()
            val sendGoal = client.sendGoal(goal)

            lastGoalId = goal.goalId

            return sendGoal.toBooleanFuture()

        }

        throw IOException("action server failure ${this.topic}")

    }

}

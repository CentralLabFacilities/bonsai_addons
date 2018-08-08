package de.unibi.citec.clf.bonsai.ros.actuators

import actionlib_msgs.GoalID
import com.github.rosjava_actionlib.ActionClient
import control_msgs.PointHeadAction
import control_msgs.PointHeadActionFeedback
import control_msgs.PointHeadActionGoal
import control_msgs.PointHeadActionResult
import de.unibi.citec.clf.bonsai.actuators.GazeActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import org.ros.exception.RosException
import org.ros.message.Duration
import org.ros.message.Time
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import pal_interaction_msgs.TtsActionFeedback
import pal_interaction_msgs.TtsActionGoal
import pal_interaction_msgs.TtsActionResult
import java.io.IOException

import java.util.concurrent.Future

/**
 *
 * @author lruegeme
 */
class PalHeadActionGazeActuator(private val nodeName: GraphName) : RosNode(), GazeActuator {

    private var ac: ActionClient<PointHeadActionGoal, PointHeadActionFeedback, PointHeadActionResult>? = null
    private lateinit var topic: String
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)
    private var lastGoalId: GoalID? = null

    init {
        initialized = false
    }

    override fun onStart(connectedNode: ConnectedNode) {
        ac = ActionClient(connectedNode, this.topic, PointHeadActionGoal._TYPE, PointHeadActionFeedback._TYPE, PointHeadActionResult._TYPE)

        if(ac?.waitForActionServerToStart(Duration(2.0)) ==  true) {
            logger.info("Gaze Actuator connected to $topic")
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
    override fun lookAt(pose: Pose3D): Future<Void> {
        return lookAt(pose,1000)
    }

    @Throws(IOException::class)
    override fun lookAt(pose: Pose3D, duration: Long ): Future<Void> {
        ac?.let { client ->
            val goal = client.newGoalMessage()
            goal.header = MsgTypeFactory.getInstance().makeHeader(pose);

            goal.goal.target.header = goal.header
            goal.goal.target.point = MsgTypeFactory.getInstance().createMsg(pose.translation, geometry_msgs.Point::class.java)
            goal.goal.pointingAxis.x = 0.0
            goal.goal.pointingAxis.y = 0.0
            goal.goal.pointingAxis.z = 1.0

            goal.goal.minDuration = Duration.fromMillis(duration)

            val sendGoal = client.sendGoal(goal)

            lastGoalId = goal.goalId

            logger.info("PAL Point Head Action: $goal")

            return sendGoal.toVoidFuture()

        }

        throw IOException("action server failure ${this.topic}")
    }

    override fun setGazeTargetPitchAsync(pitch: Float, duration: Float): Future<Boolean> {
        TODO("not implemented")
    }

    override fun setGazeTargetYawAsync(yaw: Float, duration: Float): Future<Boolean> {
        TODO("not implemented")
    }

    override fun setGazeTargetPitch(pitch: Float) {
        TODO("not implemented")
    }

    override fun setGazeTargetYaw(yaw: Float) {
        TODO("not implemented")
    }

    override fun manualStop() {
        ac?.let { client ->
            lastGoalId?.let { client.sendCancel(lastGoalId) }
        }
    }

    override fun setGazeTarget(pitch: Float, yaw: Float, speed: Float) {
        TODO("not implemented")
    }

    @Throws(IOException::class)
    override fun setGazeTargetAsync(pitch: Float, yaw: Float): Future<Boolean> {
        TODO("not implemented")
    }

    @Throws(IOException::class)
    override fun setGazeTargetAsync(pitch: Float, yaw: Float, duration: Float): Future<Boolean> {
        TODO("not implemented")
    }

    @Throws(IOException::class)
    override fun setGazeTarget(azimuth: Float, elevation: Float) {
        TODO("not implemented")
    }

}

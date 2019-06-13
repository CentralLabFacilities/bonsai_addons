package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.HandOverActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import hand_over_msgs.*
import org.ros.message.Duration
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.io.IOException
import java.util.concurrent.Future

/**
 * Created by lruegeme
 */
class RosHandOverActuator(private val nodeName: GraphName) : RosNode(), HandOverActuator {

    private lateinit var handoverTopic: String
    private lateinit var measureTopic: String

    private var acHandover: ActionClient<HandOverActionGoal, HandOverActionFeedback, HandOverActionResult>? = null
    private var acMeasure: ActionClient<MeasureForceActionGoal, MeasureForceActionFeedback, MeasureForceActionResult>? =
        null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.handoverTopic = conf.requestValue("handover_topic")
        this.measureTopic = conf.requestValue("measure_topic")
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {
        acHandover = ActionClient(
            connectedNode,
            this.handoverTopic,
            HandOverActionGoal._TYPE,
            HandOverActionFeedback._TYPE,
            HandOverActionResult._TYPE
        )
        acMeasure = ActionClient(
            connectedNode,
            this.measureTopic,
            MeasureForceActionGoal._TYPE,
            MeasureForceActionFeedback._TYPE,
            MeasureForceActionResult._TYPE
        )

        var c = 0;

        if (acHandover?.waitForActionServerToStart(Duration(20.0)) == true) {
            c++
        } else {
            logger.error("could not connect to $handoverTopic")
        }

        if (acMeasure?.waitForActionServerToStart(Duration(20.0)) == true) {
            c++
        } else {
            logger.error("could not connect to $measureTopic")
        }

        if(c == 2) {
            initialized = true
            logger.info("${javaClass.simpleName} started")
        }

    }

    override fun destroyNode() {
        acHandover?.let { it.finish() }
        acMeasure?.let { it.finish() }
    }


    @Throws(IOException::class)
    override fun handOver(group_name: String, type: Byte): Future<Boolean> {
        acHandover?.let { client ->
            val goalMessage = client.newGoalMessage()
            goalMessage.goal.groupName = group_name
            goalMessage.goal.type = type
            logger.info("sending hand_over goal for group $group_name with type $type and id: ${goalMessage.goalId}")
            return client.sendGoal(goalMessage).toBooleanFuture()
        }

        throw IOException("action server failure ${this.handoverTopic}")
    }

    @Throws(IOException::class)
    override fun checkForce(group_name: String, threshold: Float): Future<Boolean>? {
        acMeasure?.let { client ->
            val goalMessage = client.newGoalMessage()
            //goalMessage.goal.groupName = group_name
            goalMessage.goal.threshold = threshold
            logger.info("sending measure goal with threshold $threshold and id: ${goalMessage.goalId}")
            return client.sendGoal(goalMessage).toBooleanFuture()
        }

        throw IOException("action server failure ${this.measureTopic}")
    }


}

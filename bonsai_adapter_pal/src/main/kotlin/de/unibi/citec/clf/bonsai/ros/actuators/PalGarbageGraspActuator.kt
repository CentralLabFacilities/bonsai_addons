package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.GarbageGraspActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.GarbageFuture
import de.unibi.citec.clf.btl.util.GarbageGraspResult
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.util.concurrent.Future
import garbage_grasping_msgs.*
import org.ros.exception.RosException
import org.ros.message.Duration
import java.io.IOException


class PalGarbageGraspActuator(private val nodeName: GraphName) : RosNode(), GarbageGraspActuator {

    private lateinit var garbageGraspTopic: String

    private var acGarbageGrasp: ActionClient<garbageActionGoal, garbageActionFeedback, garbageActionResult>? = null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.garbageGraspTopic = conf.requestValue("topic")

    }

    @Throws(IOException::class)
    override fun getResult(): Future<GarbageGraspResult> {

        acGarbageGrasp?.let { client ->
            val goal = client.newGoalMessage()
            return GarbageFuture(client.sendGoal(goal))
        }

        throw RosException("action server failure ${this.garbageGraspTopic}")
    }

    override fun onStart(connectedNode: ConnectedNode) {
        acGarbageGrasp = ActionClient(
                connectedNode,
                this.garbageGraspTopic,
                garbageActionGoal._TYPE,
                garbageActionFeedback._TYPE,
                garbageActionResult._TYPE )


        if (acGarbageGrasp?.waitForActionServerToStart(Duration(2.0)) == true) {
            initialized = true
            logger.info("${javaClass.simpleName} started")
        } else {
            logger.error("could not connect to $garbageGraspTopic")
        }
    }

    override fun destroyNode() {
        acGarbageGrasp?.let { it.finish() }
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }
}
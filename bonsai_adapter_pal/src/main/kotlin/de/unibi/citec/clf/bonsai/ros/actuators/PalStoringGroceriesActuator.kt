package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import de.unibi.citec.clf.bonsai.actuators.StoringGroceriesActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.GroceriesFuture
import de.unibi.citec.clf.btl.util.StoringGroceriesResult
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import java.util.concurrent.Future
import storing_groceries_msgs.*
import org.ros.exception.RosException
import org.ros.message.Duration
import java.io.IOException


class PalStoringGroceriesActuator(private val nodeName: GraphName) : RosNode(), StoringGroceriesActuator {

    private lateinit var pickTopic: String
    private lateinit var placeTopic: String

    private var pick_ac: ActionClient<GroceriesActionGoal, GroceriesActionFeedback, GroceriesActionResult>? = null
    private var place_ac: ActionClient<GroceriesActionGoal, GroceriesActionFeedback, GroceriesActionResult>? = null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.pickTopic = conf.requestValue("pickTopic")
        this.placeTopic = conf.requestValue("placeTopic")

    }

    @Throws(IOException::class)
    override fun getResult(action:String): Future<StoringGroceriesResult> {
        if(action=="pick") {
            pick_ac?.let { client ->
                val goal = client.newGoalMessage()
                return GroceriesFuture(client.sendGoal(goal))
            }
        } else if(action=="place") {
            place_ac?.let { client ->
                val goal = client.newGoalMessage()
                return GroceriesFuture(client.sendGoal(goal))
            }
        } else {
            throw RosException("Unknown action")
        }

        throw RosException("action server failure")
    }

    override fun onStart(connectedNode: ConnectedNode) {
        pick_ac= ActionClient(
                connectedNode,
                this.pickTopic,
                GroceriesActionGoal._TYPE,
                GroceriesActionFeedback._TYPE,
                GroceriesActionResult._TYPE )

        if (pick_ac?.waitForActionServerToStart(Duration(2.0)) == true) {
            initialized = true
            logger.info("${javaClass.simpleName} started")
        } else {
            logger.error("could not connect to $pickTopic")
        }
        place_ac= ActionClient(
                connectedNode,
                this.placeTopic,
                GroceriesActionGoal._TYPE,
                GroceriesActionFeedback._TYPE,
                GroceriesActionResult._TYPE )

        if (place_ac?.waitForActionServerToStart(Duration(2.0)) == true) {
            initialized = true
            logger.info("${javaClass.simpleName} started")
        } else {
            logger.error("could not connect to $placeTopic")
        }
    }

    override fun destroyNode() {
        pick_ac?.let { it.finish() }
        place_ac?.let { it.finish() }
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }
}

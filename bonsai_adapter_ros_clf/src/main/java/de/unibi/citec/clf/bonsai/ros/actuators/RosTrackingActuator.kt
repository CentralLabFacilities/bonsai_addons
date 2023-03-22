package de.unibi.citec.clf.bonsai.ros.actuators

import clf_perception_vision_msgs.ToggleCFtldTrackingWithBB
import clf_perception_vision_msgs.ToggleCFtldTrackingWithBBRequest
import clf_perception_vision_msgs.ToggleCFtldTrackingWithBBResponse
import de.unibi.citec.clf.bonsai.actuators.TrackingActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import org.apache.log4j.Logger
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future

/**
 *
 * @author jkummert
 */
class RosTrackingActuator(gn: GraphName) : RosNode(), TrackingActuator {
    private lateinit var topic: String
    private val nodeName: GraphName
    private val logger = Logger.getLogger(javaClass)
    private var clientTrigger: ServiceClient<ToggleCFtldTrackingWithBBRequest, ToggleCFtldTrackingWithBBResponse>? =
        null

    init {
        initialized = false
        nodeName = gn
    }

    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic")
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientTrigger = try {
            connectedNode.newServiceClient(topic, ToggleCFtldTrackingWithBB._TYPE)
        } catch (e: ServiceNotFoundException) {
            throw RosRuntimeException(e)
        }
        initialized = true
    }

    override fun destroyNode() {
        if (clientTrigger != null) clientTrigger!!.shutdown()
    }

    override fun startTracking(boundingbox: List<Int>): Future<Boolean> {
        if (!clientTrigger!!.isConnected) {
            initialized = false
            throw RosRuntimeException("node not connected")
        }

        val req = clientTrigger!!.newMessage()
        //set data
        req.roi.xOffset = boundingbox[0]
        req.roi.yOffset = boundingbox[1]
        req.roi.height = boundingbox[2]
        req.roi.width = boundingbox[3]
        val res = ResponseFuture<ToggleCFtldTrackingWithBBResponse>()
        clientTrigger!!.call(req, res)
        return res.toBooleanFuture()
    }

    override fun stopTracking() {
        val req = clientTrigger!!.newMessage()
        //dont set data
        val res = ResponseFuture<ToggleCFtldTrackingWithBBResponse>()
        clientTrigger!!.call(req, res)
    }
}
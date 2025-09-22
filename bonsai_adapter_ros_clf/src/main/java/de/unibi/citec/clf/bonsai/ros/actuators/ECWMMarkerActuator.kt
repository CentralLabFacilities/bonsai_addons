package de.unibi.citec.clf.bonsai.ros.actuators

import de.unibi.citec.clf.bonsai.actuators.ECWMMarker
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import ecwm_msgs.FindMarker
import ecwm_msgs.FindMarkerRequest
import ecwm_msgs.FindMarkerResponse
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future

class ECWMMarkerActuator(private val nodeName: GraphName) : RosNode(), ECWMMarker {

    private val logger = Logger.getLogger(javaClass)

    private var clientFindMarker: ServiceClient<FindMarkerRequest, FindMarkerResponse>? = null

    private var markerTopic: String = "/ecwm/MarkerFind/find_marker"


    override fun getDefaultNodeName(): GraphName {
        return nodeName;
    }

    override fun configure(conf: IObjectConfigurator) {
        markerTopic = conf.requestOptionalValue("topic_find_marker", markerTopic)
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientFindMarker = connectedNode.newServiceClient(markerTopic, FindMarker._TYPE)
        initialized = true
    }

    override fun destroyNode() {
        clientFindMarker?.shutdown()
    }

    override fun findMarker(id: UInt, align: Boolean, max_age: Double): Future<Boolean?> {
        clientFindMarker?.let {
            var req = it.newMessage()
            req.id = id.toInt()
            req.align = align
            req.maxAge = max_age.toInt()
            var res = ResponseFuture<FindMarkerResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                it.found
            }
        }
        throw RosException("service server failure ${this.markerTopic}")
    }

}
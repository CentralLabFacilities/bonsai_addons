package de.unibi.citec.clf.bonsai.ros.actuators


import clf_person_recognition_msgs.TrackPerson
import clf_person_recognition_msgs.TrackPersonRequest
import clf_person_recognition_msgs.TrackPersonResponse
import de.unibi.citec.clf.bonsai.actuators.TrackingActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.geometry.Point3D
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import org.apache.log4j.Logger
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future

/**
 *
 * @author lruegeme
 */
class ClfTrackingActuator(gn: GraphName) : RosNode(), TrackingActuator {
    private lateinit var topic: String
    private val nodeName: GraphName
    private val logger = Logger.getLogger(javaClass)
    private var clientTrigger: ServiceClient<TrackPersonRequest, TrackPersonResponse>? =
        null

    init {
        initialized = false
        nodeName = gn
    }

    override fun configure(conf: IObjectConfigurator) {
        topic = conf.requestValue("topic") // /person_rec_lw/track_person
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }


    override fun connectionsAlive(): Boolean {
        return clientTrigger?.isConnected ?: false;
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientTrigger = try {
            connectedNode.newServiceClient(topic, TrackPerson._TYPE)
        } catch (e: ServiceNotFoundException) {
            throw RosRuntimeException(e)
        }
        initialized = true
    }

    override fun destroyNode() {
        if (clientTrigger != null) clientTrigger!!.shutdown()
    }

    override fun startTracking(boundingbox: List<Int>): Future<Boolean> {
        TODO("not implemented")
    }

    override fun startTracking(lastPose: Point3D?, threshold: Double?): Future<Boolean> {
        clientTrigger?.let { client ->
            val req = client.newMessage()
            req.lastKnownPosition = MsgTypeFactory.getInstance().createMsg(lastPose, geometry_msgs.PointStamped._TYPE)
            req.lastKnownPosition.header.frameId = lastPose?.frameId
            val res = ResponseFuture<TrackPersonResponse>()
            client.call(req, res)
            return res.toBooleanFuture()
        }
        throw RosRuntimeException("client error")
    }

    override fun stopTracking() {
        TODO("not implemented")
    }
}
package de.unibi.citec.clf.bonsai.ros.actuators

import de.unibi.citec.clf.bonsai.actuators.StringActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.`object`.Actuator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import openpose_ros_msgs.GetFollowRoiRequest
import openpose_ros_msgs.GetFollowRoiResponse
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.topic.Publisher
import org.ros.node.service.ServiceClient

import load_config_msgs.*

import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * @author lruegeme
 */
class DynamicReconfigureConfigLoader(private val nodeName: GraphName) : RosNode(), Actuator {

    lateinit var topic: String
    private var clientConfig: ServiceClient<LoadConfigRequest, LoadConfigResponse>? = null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    enum class options private constructor(val value: Int) {
        MOVE_BASE_NORMAL(1),
        MOVE_BASE_FOLLOWING(2)
    }

    init {
        this.initialized = false
    }

    override fun configure(conf: IObjectConfigurator) {
        this.topic = conf.requestValue("topic")
    }

    @Throws(IOException::class)
    fun setConfiguration(data: options) {
        clientConfig?.let { client ->
            val req = client.newMessage()
            val a = req.config
            a.code = data.value.toByte()
            req.config = a
            val res = ResponseFuture<LoadConfigResponse>()
            client.call(req, res)

            var tmp: LoadConfigResponse? = null
            try {
                tmp = res.get()
                logger.fatal(tmp)
            } catch (e: InterruptedException) {
                throw IOException(e)
            } catch (e: ExecutionException) {
                throw IOException(e)
            }

        }

    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {
        try {
            clientConfig = connectedNode.newServiceClient(topic, LoadConfig._TYPE)
            initialized = true
            logger.fatal("on start " + javaClass.simpleName)
        } catch (e: ServiceNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun destroyNode() {
        if (clientConfig != null) clientConfig!!.shutdown()
    }

}

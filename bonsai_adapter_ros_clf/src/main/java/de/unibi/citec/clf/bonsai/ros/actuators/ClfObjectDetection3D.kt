package de.unibi.citec.clf.bonsai.ros.actuators

import clf_object_recognition_msgs.Detect3D
import clf_object_recognition_msgs.Detect3DRequest
import clf_object_recognition_msgs.Detect3DResponse
import de.unibi.citec.clf.bonsai.actuators.ObjectDetectionActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.List
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D
import de.unibi.citec.clf.btl.data.`object`.ObjectShapeData
import de.unibi.citec.clf.btl.data.`object`.ObjectShapeList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import org.apache.commons.lang.NotImplementedException
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.io.IOException

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lruegeme
 */
class ClfObjectDetection3D(private val nodeName: GraphName) : RosNode(), ObjectDetectionActuator {

    private lateinit var topicObject: String
    private lateinit var topicTable: String
    private lateinit var objectIdParam : String
    private lateinit var objectIdMap : Map<String, String>

    private var serviceObjects: ServiceClient<Detect3DRequest, Detect3DResponse>? = null
    //private var serviceTable: ServiceClient<FindTableRequest, FindTableResponse>? = null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.topicObject = conf.requestValue("topic_detect_objects")
        //this.topicTable = conf.requestValue("topic_detect_surface")
        this.objectIdParam = conf.requestValue("object_id_param_tree")
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {

        try {
            serviceObjects = connectedNode.newServiceClient(this.topicObject, Detect3D._TYPE)
            //serviceTable = connectedNode.newServiceClient(this.topicTable, FindTable._TYPE)
            objectIdMap = connectedNode.parameterTree.getMap(objectIdParam) as Map<String, String>
        } catch (ex: ServiceNotFoundException) {
            throw RosRuntimeException(ex.message)
        }

        initialized = true
    }

    override fun destroyNode() {
        serviceObjects?.shutdown()
        //serviceTable?.shutdown()
    }


    override fun detectObjects(minConf: Double, roi: BoundingBox3D?): Future<List<ObjectShapeData>> {
        val let = serviceObjects?.let { client ->
            var goal = client.newMessage()
            goal.minConf = minConf.toFloat()
            goal.skipIcp = true
            roi?.let {
                logger.warn("roi not supported")
                throw IOException("${ClfObjectDetection3D::class.java} roi not supported")
            }

            val res = ResponseFuture<Detect3DResponse>()
            client.call(goal, res)

            return object : Future<List<ObjectShapeData>> {
                override fun isDone(): Boolean = res.isDone
                override fun cancel(b: Boolean): Boolean = res.cancel(b)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun get(): List<ObjectShapeData> = get(0,TimeUnit.MINUTES)

                override fun get(timeout: Long, unit: TimeUnit): List<ObjectShapeData> {
                    var msg = res.get(timeout,unit)
                    //logger.debug("Message: " + msg)
                    val data = ObjectShapeList()
                    for (i in 0 until msg?.detections?.size!!) {
                        val detection3d = msg.detections[i]
                        //logger.debug("Number of hypothesis: " + detection3d.results.size)
                        val osd = MsgTypeFactory.getInstance().createType(detection3d, ObjectShapeData::class.java)
                        osd.id = "${detection3d.header.stamp}_$i"
                        for(hyp in osd.hypotheses) {
                            //logger.debug("Class label: " + hyp.classLabel + " is: " + objectIdMap[hyp.classLabel] + " with probability: " + hyp.reliability)
                            hyp.classLabel = objectIdMap[hyp.classLabel] ?: "unknown"
                        }
                        data.add(osd)
                    }
                    return data
                }
            }
        }
        throw IOException("server not connected to topic $topicObject")
    }

    override fun detectSurface(): Future<BoundingBox3D> {
        throw NotImplementedException()
    }
}

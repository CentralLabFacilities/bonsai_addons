package de.unibi.citec.clf.bonsai.ros.actuators

import clf_grasping_msgs.*
import de.unibi.citec.clf.bonsai.actuators.ObjectDetectionActuator
import de.unibi.citec.clf.bonsai.actuators.PlanningSceneActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.List
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D
import de.unibi.citec.clf.btl.data.`object`.ObjectShapeData
import de.unibi.citec.clf.btl.data.`object`.ObjectShapeList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import knowledge_base_msgs.QueryResponse
import org.apache.commons.lang.NotImplementedException
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import vision_msgs.Detection3D
import java.io.IOException
import java.util.HashMap

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 * @author lruegeme
 */
class ClfGraspingObjectDetection(private val nodeName: GraphName) : RosNode(), ObjectDetectionActuator {

    private lateinit var topicObject: String
    private lateinit var topicTable: String
    private lateinit var objectIdParam : String
    private lateinit var objectIdMap : Map<String, String>

    private var serviceObjects: ServiceClient<FindObjectsInROIRequest, FindObjectsInROIResponse>? = null
    private var serviceTable: ServiceClient<FindTableRequest, FindTableResponse>? = null
    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.topicObject = conf.requestValue("topic_detect_objects")
        this.topicTable = conf.requestValue("topic_detect_surface")
        this.objectIdParam = conf.requestValue("object_id_param_tree")
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {

        try {
            serviceObjects = connectedNode.newServiceClient(this.topicObject, FindObjectsInROI._TYPE)
            serviceTable = connectedNode.newServiceClient(this.topicTable, FindTable._TYPE)
            objectIdMap = connectedNode.parameterTree.getMap(objectIdParam) as Map<String, String>
        } catch (ex: ServiceNotFoundException) {
            throw RosRuntimeException(ex.message)
        }

        initialized = true
    }

    override fun destroyNode() {
        serviceObjects?.shutdown()
        serviceTable?.shutdown()
    }


    override fun detectObjects(roi: BoundingBox3D?): Future<List<ObjectShapeData>> {
        val let = serviceObjects?.let { client ->
            var goal = client.newMessage()
            goal.addToPlanningScene = true
            roi?.let { roi ->
                val boxmsg = MsgTypeFactory.getInstance().createMsg(roi, vision_msgs.BoundingBox3D::class.java)
                goal.roi = boxmsg
            }

            val res = ResponseFuture<FindObjectsInROIResponse>()
            client.call(goal, res);

            return object : Future<List<ObjectShapeData>> {
                override fun isDone(): Boolean = res.isDone
                override fun cancel(b: Boolean): Boolean = res.cancel(b)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun get(): List<ObjectShapeData> = get(0,TimeUnit.MINUTES);

                override fun get(timeout: Long, unit: TimeUnit?): List<ObjectShapeData> {
                    var msg = res.get(timeout,unit)
                    val data = ObjectShapeList()
                    for (i in 0 until msg.detections.size) {
                        val detection3d = msg.detections[i]
                        val osd = MsgTypeFactory.getInstance().createType(detection3d, ObjectShapeData::class.java)
                        osd.id = msg.objectIds[i]
                        for(hyp in osd.hypotheses) {
                            hyp.classLabel = objectIdMap[hyp.classLabel]
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
        val let = serviceTable?.let { client ->
            var goal = client.newMessage()
            goal.addToPlanningScene = true

            val res = ResponseFuture<FindTableResponse>()
            client.call(goal, res);

            return object : Future<BoundingBox3D> {
                override fun isDone(): Boolean = res.isDone
                override fun cancel(b: Boolean): Boolean = res.cancel(b)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun get(): BoundingBox3D = get(0,TimeUnit.MINUTES);

                override fun get(timeout: Long, unit: TimeUnit?): BoundingBox3D {
                    var msg = res.get(timeout,unit)
                    return MsgTypeFactory.getInstance().createType(msg.bbox, BoundingBox3D::class.java)
                }
            }
        }
        throw IOException("server not connected to topic $topicTable")
    }
}

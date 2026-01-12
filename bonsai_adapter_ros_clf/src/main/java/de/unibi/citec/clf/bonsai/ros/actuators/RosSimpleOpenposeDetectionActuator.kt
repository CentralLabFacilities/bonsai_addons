package de.unibi.citec.clf.bonsai.ros.actuators

import clf_person_recognition_msgs.GetCrowdAttributesWithPose
import clf_person_recognition_msgs.GetCrowdAttributesWithPoseRequest
import clf_person_recognition_msgs.GetCrowdAttributesWithPoseResponse
import clf_person_recognition_msgs.PersonAttributesWithPose
import de.unibi.citec.clf.bonsai.actuators.DetectPeopleActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.person.PersonData
import de.unibi.citec.clf.btl.data.person.PersonDataList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import de.unibi.citec.clf.btl.ros.RosSerializer
import org.apache.log4j.Logger
import org.ros.exception.RosRuntimeException
import org.ros.exception.ServiceNotFoundException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RosSimpleOpenposeDetectionActuator(gn: GraphName) : RosNode(), DetectPeopleActuator {

    private lateinit var topic: String
    private val nodeName: GraphName
    private var clientTrigger: ServiceClient<GetCrowdAttributesWithPoseRequest, GetCrowdAttributesWithPoseResponse>? =
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
            connectedNode.newServiceClient(topic, GetCrowdAttributesWithPose._TYPE)
        } catch (e: ServiceNotFoundException) {
            throw RosRuntimeException(e)
        }
        initialized = true
    }

    override fun destroyNode() {
        clientTrigger?.shutdown()
    }

    override fun getPeople(): Future<PersonDataList> {
        clientTrigger?.let { client ->
            val req = client.newMessage()
            val res = ResponseFuture<GetCrowdAttributesWithPoseResponse>()
            client.call(req, res)
            return PersonDataListFuture(res)
        }
        throw RosRuntimeException("client error")
    }

    companion object {
        private val logger = Logger.getLogger(this::class.java)
        private fun getListFromResponse(attributes: List<PersonAttributesWithPose>): PersonDataList {
            return PersonDataList().apply {
                attributes.forEach {
                    try {
                        add(MsgTypeFactory.getInstance().createType(it, PersonData::class.java))
                    } catch (_: RosSerializer.DeserializationException) {
                        logger.error("Could not deserialize PersonAttributesWithPose Response")
                    }
                }
            }
        }
    }


    private class PersonDataListFuture(val res: ResponseFuture<GetCrowdAttributesWithPoseResponse>) :
        Future<PersonDataList> {
        override fun cancel(b: Boolean): Boolean {
            return res.cancel(b)
        }

        override fun isCancelled(): Boolean {
            return res.isCancelled
        }

        override fun isDone(): Boolean {
            return res.isDone
        }

        @Throws(InterruptedException::class, ExecutionException::class)
        override fun get(): PersonDataList {
            return res.get()?.let {
                getListFromResponse(it.attributes)
            } ?: PersonDataList()
        }

        @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
        override fun get(
            l: Long,
            timeUnit: TimeUnit
        ): PersonDataList {
            return res.get(l, timeUnit)?.let {
                getListFromResponse(it.attributes)
            } ?: PersonDataList()
        }

    }


}
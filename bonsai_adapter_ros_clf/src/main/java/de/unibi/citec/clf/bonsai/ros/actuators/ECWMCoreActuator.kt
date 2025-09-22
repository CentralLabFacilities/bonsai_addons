package de.unibi.citec.clf.bonsai.ros.actuators

import de.unibi.citec.clf.bonsai.actuators.WorldModel
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.data.world.Entity
import de.unibi.citec.clf.btl.data.world.EntityList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import ecwm_msgs.*
import geometry_msgs.Pose
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future

class ECWMCoreActuator(private val nodeName: GraphName) : RosNode(), WorldModel {

    private val logger = Logger.getLogger(javaClass)

    private var clientFetch: ServiceClient<FetchEntitiesRequest,FetchEntitiesResponse>? = null
    private var clientFetchName: ServiceClient<FetchEntitiesRequest,FetchEntitiesResponse>? = null
    private var clientMoveEntity: ServiceClient<MoveEntityRequest, MoveEntityResponse>? = null
    private var clientAddEntities: ServiceClient<AddEntitiesRequest, AddEntitiesResponse>? = null
     private var clientRemoveEntities: ServiceClient<AddEntitiesRequest, AddEntitiesResponse>? = null

    private var topicFetch: String = "/ecwm/WorldServices/get_entities_by_model"
    private var topicFetchName: String = "/ecwm/WorldServices/get_entity_by_name"
    private var topicMove: String = "/ecwm/WorldServices/move_entity"
    private var topicAdd: String = "/ecwm/WorldServices/add_entities"
    private var topicRemove: String = "/ecwm/WorldServices/remove_entities"

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName;
    }

    override fun configure(conf: IObjectConfigurator) {
        topicFetch = conf.requestOptionalValue("topic_fetch", topicFetch)
        topicFetchName = conf.requestOptionalValue("topic_fetch_name", topicFetchName)
        topicMove = conf.requestOptionalValue("topic_move", topicMove)
        topicAdd = conf.requestOptionalValue("topic_add", topicAdd)
        topicRemove = conf.requestOptionalValue("topic_remove", topicRemove)
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientFetch = connectedNode.newServiceClient(topicFetch,FetchEntities._TYPE)
        clientFetchName = connectedNode.newServiceClient(topicFetchName,FetchEntities._TYPE)
        clientMoveEntity = connectedNode.newServiceClient(topicMove, MoveEntity._TYPE)
        clientAddEntities = connectedNode.newServiceClient(topicAdd, AddEntities._TYPE)
        clientRemoveEntities = connectedNode.newServiceClient(topicRemove, AddEntities._TYPE)
        initialized = true
    }

    override fun destroyNode() {
        clientFetch?.shutdown()
        clientFetchName?.shutdown()
        clientMoveEntity?.shutdown()
        clientAddEntities?.shutdown()
        clientRemoveEntities?.shutdown()
    }

    override fun fetchEntitiesByType(expression: String): Future<EntityList?> {
        clientFetch?.let { client ->
            var req = client.newMessage()
            req.expression = expression
            val res = ResponseFuture<FetchEntitiesResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                val list: EntityList = EntityList()
                for (e in it.entities) {
                    val t = MsgTypeFactory.getInstance().createType(e, Entity::class.java)
                    list.add(t)
                }
                list
            }
        }
        throw RosException("service server failure ${this.topicFetch}")
    }

    override fun getEntity(expression: String): Future<Entity?> {
        clientFetchName?.let { client ->
            var req = client.newMessage()
            req.expression = expression
            val res = ResponseFuture<FetchEntitiesResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                val list: EntityList = EntityList()
                for (e in it.entities) {
                    val t = MsgTypeFactory.getInstance().createType(e, Entity::class.java)
                    list.add(t)
                }
                list.first()
            }
        }
        throw RosException("service server failure ${this.topicFetch}")
    }

    override fun moveEntity(enitiy: Entity, pose: Pose3D): Future<Entity?> {
        clientMoveEntity?.let {
            var req = it.newMessage()
            req.name = enitiy.id
            req.pose = MsgTypeFactory.getInstance().createMsg(pose, Pose._TYPE)
            var res = ResponseFuture<MoveEntityResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                MsgTypeFactory.getInstance().createType(it.entity, Entity::class.java)
            }
        }
        throw RosException("service server failure ${this.topicMove}")
    }


    override fun addEntities(entities: List<Entity>): Future<Boolean?> {
        clientAddEntities?.let {
            var req = it.newMessage()
            req.entities.addAll(entities.map { entity ->
                MsgTypeFactory.getInstance().createMsg(entity, ecwm_msgs.Entity::class.java)
            })
            var res = ResponseFuture<AddEntitiesResponse>()
            it.call(req, res)
            return res.toTypeFuture { rf ->
                    rf.success ?: false
                }
            }
        throw RosException("service server failure ${this.topicAdd}")
        }


    override fun removeEntities(entities: List<Entity>): Future<Boolean?> {
        clientRemoveEntities?.let {
            var req = it.newMessage()

            req.entities.addAll(entities.map { entity ->
                MsgTypeFactory.getInstance().createMsg(entity, ecwm_msgs.Entity::class.java)
            })
            var res = ResponseFuture<AddEntitiesResponse>()
            it.call(req, res)
            return res.toTypeFuture { rf ->
                rf.success ?: false
            }
        }
        throw RosException("service server failure ${this.topicRemove}")

    }


}
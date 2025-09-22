package de.unibi.citec.clf.bonsai.ros.actuators

import de.unibi.citec.clf.bonsai.actuators.ECWMSpirit
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.ecwm.Spirit
import de.unibi.citec.clf.btl.data.ecwm.SpiritGoal
import de.unibi.citec.clf.btl.data.ecwm.StorageArea
import de.unibi.citec.clf.btl.data.ecwm.StorageList
import de.unibi.citec.clf.btl.data.geometry.Point2D
import de.unibi.citec.clf.btl.data.geometry.Pose2D
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon
import de.unibi.citec.clf.btl.data.world.Entity
import de.unibi.citec.clf.btl.data.world.EntityList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import ecwm_msgs.*
import geometry_msgs.PoseStamped
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import shape_msgs.SolidPrimitive
import java.util.concurrent.Future

class ECWMSpiritActuator(private val nodeName: GraphName) : RosNode(), ECWMSpirit {

    private val logger = Logger.getLogger(javaClass)

    private var clientNavigation: ServiceClient<NavigateToRequest, NavigateToResponse>? = null
    private var clientSpirit: ServiceClient<GetSpiritGoalRequest, GetSpiritGoalResponse>? = null
    private var clientRoomsFromPose: ServiceClient<GetRoomsFromPoseRequest, GetRoomsFromPoseResponse>? = null
    private var clientGetRoomPolygon: ServiceClient<GetRoomPolygonRequest, GetRoomPolygonResponse>? = null
    private var clientGetEntitiesInRoom: ServiceClient<GetEntitiesInRoomRequest, GetEntitiesInRoomResponse>? = null
    private var clientListEntityStorage: ServiceClient<ListStoragesRequest, ListStoragesResponse>? = null


    private var navigationTopic: String = "/ecwm/SpiritNavigation/plan"
    private var spiritTopic: String = "/ecwm/SpiritNavigation/get_sg"
    private var topicListStorages: String = "/ecwm/AffordanceServices/get_entity_storages"
    private var topicGetRoomsFromPose = "/ecwm/LocationServices/from_pose"
    private var topicGetRoomPolygon = "/ecwm/LocationServices/room_polygon"
    private var topicGetEntitiesInRoom = "/ecwm/LocationServices/entities"

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun configure(conf: IObjectConfigurator) {
        navigationTopic = conf.requestOptionalValue("topic_nav", navigationTopic)
        topicListStorages = conf.requestOptionalValue("topic_list_storage", topicListStorages)

    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientNavigation = connectedNode.newServiceClient(navigationTopic, NavigateTo._TYPE)
        clientSpirit = connectedNode.newServiceClient(spiritTopic, GetSpiritGoal._TYPE)
        clientRoomsFromPose = connectedNode.newServiceClient(topicGetRoomsFromPose, GetRoomsFromPose._TYPE)
        clientGetRoomPolygon = connectedNode.newServiceClient(topicGetRoomPolygon, GetRoomPolygon._TYPE)
        clientGetEntitiesInRoom = connectedNode.newServiceClient(topicGetEntitiesInRoom, GetEntitiesInRoom._TYPE)
        clientListEntityStorage = connectedNode.newServiceClient(topicListStorages, ListStorages._TYPE)
        initialized = true
    }

    override fun destroyNode() {
        clientNavigation?.shutdown()
        clientListEntityStorage?.shutdown()
    }

    override fun getSpiritGoal(
        entity: Entity,
        spirit: String?,
        storage: String?,
        forceMoveThreshold: Int,
        onBlocked: ECWMSpirit.BlockageHandling,
        considerRoom: Boolean
    ): Future<ECWMSpirit.SpiritGoalResult?> {
        clientNavigation?.let { client ->
            val req = client.newMessage()
            req.entity = entity.id
            req.spirit = spirit ?: ""
            req.storage = storage ?: ""
            if (forceMoveThreshold < 255) {
                req.useBest = true
                req.useBestThreshold = forceMoveThreshold.toByte()
            }
            req.allBlocked = when (onBlocked) {
                ECWMSpirit.BlockageHandling.USE_NEAREST -> NavigateToRequest.FORCE_NEAREST
                ECWMSpirit.BlockageHandling.USE_BEST -> NavigateToRequest.FORCE_BEST
                else -> NavigateToRequest.NOTHING
            }
            req.considerLocation = considerRoom
            val res = ResponseFuture<NavigateToResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                val targetPose = MsgTypeFactory.getInstance().createType(it.goal.target, Pose2D::class.java)
                val viewTarget = MsgTypeFactory.getInstance().createType(it.goal.viewTarget, Pose3D::class.java)
                val status = ECWMSpirit.SpiritGoalStatus.valueOf(res.response!!.result)
                val goal = SpiritGoal(it.goal.camHeigth, viewTarget, targetPose, it.wasBlocked)
                ECWMSpirit.SpiritGoalResult(status, goal)
            }
        }
        throw RosException("service server failure ${this.navigationTopic}")
    }

    override fun getSpiritGoal(
        spirit: Spirit,
        forceMoveThreshold: Int,
        onBlocked: ECWMSpirit.BlockageHandling,
        considerRoom: Boolean
    ): Future<ECWMSpirit.SpiritGoalResult?> {
        return getSpiritGoal(spirit.entity, spirit.affordance, spirit.storage, forceMoveThreshold, onBlocked, considerRoom)
    }

    override fun getSpiritGoalCurrent(spirit: Spirit): Future<SpiritGoal?> {
        clientSpirit?.let { client ->
            val req = client.newMessage()
            req.entity = spirit.entity.id
            req.spirit = spirit.affordance
            req.storage = spirit.storage
            val res = ResponseFuture<GetSpiritGoalResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                val targetPose = MsgTypeFactory.getInstance().createType(it.goal.target, Pose2D::class.java)
                val viewTarget = MsgTypeFactory.getInstance().createType(it.goal.viewTarget, Pose3D::class.java)
                SpiritGoal(it.goal.camHeigth, viewTarget, targetPose, false)
            }
        }
        throw RosException("service server failure ${this.spiritTopic}")
    }

    override fun getRoomsOf(pose: Pose3D): Future<EntityList?> {
        clientRoomsFromPose?.let { client ->
            var req = client.newMessage()
            req.pose = MsgTypeFactory.getInstance().createMsg(pose, PoseStamped::class.java)
            val res = ResponseFuture<GetRoomsFromPoseResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                EntityList().also { list ->
                    list.addAll(it.rooms.map { MsgTypeFactory.getInstance().createType(it, Entity::class.java) })
                }
            }
        }
        throw RosException("service server failure ${this.topicGetRoomsFromPose}")
    }

    override fun getRoomPolygon(entity: Entity): Future<PrecisePolygon?> {
        clientGetRoomPolygon?.let { client ->
            var req = client.newMessage()
            req.room = MsgTypeFactory.getInstance().createMsg(entity, ecwm_msgs.Entity::class.java)
            val res = ResponseFuture<GetRoomPolygonResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                var p = PrecisePolygon()
                for (point in it.location.polygon) {
                    p.addPoint(Point2D(point.x, point.y))
                }
                p
            }
        }
        throw RosException("service server failure ${this.topicGetRoomPolygon}")
    }

    override fun getEntitiesInRoom(room: Entity, includeObjects: Boolean, includeRooms: Boolean): Future<EntityList?> {
        clientGetEntitiesInRoom?.let { client ->
            var req = client.newMessage()
            req.includeObjects = includeObjects
            req.includeRooms = includeRooms
            req.room = MsgTypeFactory.getInstance().createMsg(room, ecwm_msgs.Entity::class.java)
            val res = ResponseFuture<GetEntitiesInRoomResponse>()
            client.call(req, res)
            return res.toTypeFuture {
                EntityList().also { list ->
                    list.addAll(it.entities.map { MsgTypeFactory.getInstance().createType(it, Entity::class.java) })
                }
            }
        }
        throw RosException("service server failure ${this.topicGetEntitiesInRoom}")
    }

    override fun fetchEntityStorages(enitiy: Entity): Future<StorageList?> {
        clientListEntityStorage?.let {
            var req = it.newMessage()
            req.expression = enitiy.id
            var res = ResponseFuture<ListStoragesResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                val list = StorageList()
                for (s in it.storages) {
                    assert(s.shape.type == SolidPrimitive.BOX)
                    val sizeX = s.shape.dimensions[SolidPrimitive.BOX_X.toInt()]
                    val sizeY = s.shape.dimensions[SolidPrimitive.BOX_Y.toInt()]
                    val sizeZ = s.shape.dimensions[SolidPrimitive.BOX_Z.toInt()]
                    val storageArea = StorageArea(s.name, sizeX, sizeY, sizeZ)
                    list.add(storageArea)
                }
                list
            }
        }
        throw RosException("service server failure ${this.topicListStorages}")
    }

}
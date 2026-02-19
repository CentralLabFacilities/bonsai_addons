package de.unibi.citec.clf.bonsai.ros.actuators

import com.github.rosjava_actionlib.ActionClient
import com.github.rosjava_actionlib.ActionFuture
import de.unibi.citec.clf.bonsai.actuators.ECWMGrasping
import de.unibi.citec.clf.bonsai.actuators.ManipulationActuator.MoveitResult
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.world.Entity
import de.unibi.citec.clf.btl.data.world.EntityList
import de.unibi.citec.clf.btl.data.world.Model
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D
import de.unibi.citec.clf.btl.data.geometry.Point3D
import de.unibi.citec.clf.btl.data.geometry.Point3DStamped
import de.unibi.citec.clf.btl.data.geometry.Pose3D
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import de.unibi.citec.clf.btl.units.LengthUnit
import ecwm_msgs.*
import geometry_msgs.Point
import geometry_msgs.Pose
import geometry_msgs.PoseStamped
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


fun ActionFuture<GraspEntityActionGoal, GraspEntityActionFeedback, GraspEntityActionResult>.toResultFuture() : Future<MoveitResult> {
    val res = this
    return object : Future<MoveitResult> {
        override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
        override fun isCancelled(): Boolean = res.isCancelled
        override fun isDone(): Boolean = res.isDone
        override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
        override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
    }
}



class ECWMGraspingActuator(private val nodeName: GraphName) : RosNode(), ECWMGrasping {
    private val logger = Logger.getLogger(javaClass)
    private var clientGraspEntity: ActionClient<GraspEntityActionGoal, GraspEntityActionFeedback, GraspEntityActionResult>? = null
    private var clientPlaceEntity: ActionClient<PlaceEntityActionGoal, PlaceEntityActionFeedback, PlaceEntityActionResult>? = null
    private var clientHoldEntity: ActionClient<PlaceEntityActionGoal, PlaceEntityActionFeedback, PlaceEntityActionResult>? = null
    private var clientRetract: ActionClient<PlaceEntityActionGoal, PlaceEntityActionFeedback, PlaceEntityActionResult>? = null
    private var clientPour: ActionClient<PourEntityActionGoal, PourEntityActionFeedback, PourEntityActionResult>? = null
    private var clientOpen: ActionClient<OpenDoorActionGoal, OpenDoorActionFeedback, OpenDoorActionResult>? = null
    private var clientClose: ActionClient<OpenDoorActionGoal, OpenDoorActionFeedback, OpenDoorActionResult>? = null

    private var clientWipe: ActionClient<WipeActionGoal, WipeActionFeedback, WipeActionResult>? = null

    private var clientRecognizeObjects: ServiceClient<RecognizeObjectsRequest, RecognizeObjectsResponse>? = null
    private var clientRecognizeEntities: ServiceClient<RecognizeEntitiesRequest, RecognizeEntitiesResponse>? = null

    private var clientSetup: ServiceClient<SetupPlanningSceneRequest, SetupPlanningSceneResponse>? = null
    private var clientSetupArea: ServiceClient<SetupPlanningSceneAreaRequest, SetupPlanningSceneAreaResponse>? = null
    private var clientAttach: ServiceClient<AttachEntityRequest, AttachEntityResponse>? = null
    private var clientBox: ServiceClient<GetBoundingBoxRequest, GetBoundingBoxResponse>? = null

    private var topicRecognizeObjects: String = "/ecwm/ClfRecognition/recognize_objects"
    private var topicRecognizeEntities: String = "/ecwm/ClfRecognition/recognize_entities"

    private var topicGrasp: String = "/ecwm/GraspServerMTC/grasp_entity"
    private var topicPlace: String = "/ecwm/GraspServerMTC/place_entity"
    private var topicHold: String = "/ecwm/GraspServerMTC/hold_entity"
    private var topicRetract: String = "/ecwm/GraspServerMTC/retract"
    private var topicPour: String = "/ecwm/GraspServerMTC/pour"
    private var topicOpen: String = "/ecwm/GraspServerMTC/open_door"
    private var topicClose: String = "/ecwm/GraspServerMTC/close_door"

    private var topicWipeArea: String = "/ecwm/GraspServerMTC/wipe"

    private var topicBox: String = "/ecwm/CollisionViz/get_box"

    private var topicPS: String = "/ecwm/PlanningScene/setup"
    private var topicPSA: String = "/ecwm/PlanningScene/setup_area"
    private var topicAttach: String = "/ecwm/PlanningScene/attach"

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName;
    }

    override fun configure(conf: IObjectConfigurator) {
        topicRecognizeObjects = conf.requestOptionalValue("topicRecognize", topicRecognizeObjects)
        topicRecognizeEntities = conf.requestOptionalValue("topicRecognizeEntities", topicRecognizeEntities)
        topicGrasp = conf.requestOptionalValue("topicGrasp", topicGrasp)
        topicPlace = conf.requestOptionalValue("topicPlace", topicPlace)
        topicPour = conf.requestOptionalValue("topicPour", topicPour)
        topicPS = conf.requestOptionalValue("topicPS", topicPS)
        topicPSA = conf.requestOptionalValue("topicPSA", topicPSA)
        topicAttach = conf.requestOptionalValue("topicAttach", topicAttach)
        topicOpen = conf.requestOptionalValue("topicOpen", topicOpen)
        topicClose = conf.requestOptionalValue("topicClose", topicClose)
        topicWipeArea = conf.requestOptionalValue("topicWipeArea", topicWipeArea)
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientRecognizeObjects = connectedNode.newServiceClient(topicRecognizeObjects, RecognizeObjects._TYPE)
        clientRecognizeEntities = connectedNode.newServiceClient(topicRecognizeEntities, RecognizeEntities._TYPE)

        clientSetup = connectedNode.newServiceClient(topicPS, SetupPlanningScene._TYPE)
        clientSetupArea = connectedNode.newServiceClient(topicPSA, SetupPlanningSceneArea._TYPE)
        clientAttach = connectedNode.newServiceClient(topicAttach, AttachEntity._TYPE)
        clientBox = connectedNode.newServiceClient(topicBox, GetBoundingBox._TYPE)

        //clientGraspEntity = connectedNode.newServiceClient(topicGrasp, GraspEntity._TYPE)
        clientGraspEntity = ActionClient(
            connectedNode,
            this.topicGrasp,
            GraspEntityActionGoal._TYPE,
            GraspEntityActionFeedback._TYPE,
            GraspEntityActionResult._TYPE )
        clientPlaceEntity = ActionClient(
            connectedNode,
            this.topicPlace,
            PlaceEntityActionGoal._TYPE,
            PlaceEntityActionFeedback._TYPE,
            PlaceEntityActionResult._TYPE )
        clientHoldEntity = ActionClient(
            connectedNode,
            this.topicHold,
            PlaceEntityActionGoal._TYPE,
            PlaceEntityActionFeedback._TYPE,
            PlaceEntityActionResult._TYPE )
        clientPour = ActionClient(
            connectedNode,
            this.topicPour,
            PourEntityActionGoal._TYPE,
            PourEntityActionFeedback._TYPE,
            PourEntityActionResult._TYPE )
        clientOpen = ActionClient(
            connectedNode,
            this.topicOpen,
            OpenDoorActionGoal._TYPE,
            OpenDoorActionFeedback._TYPE,
            OpenDoorActionResult._TYPE )
        clientClose = ActionClient(
            connectedNode,
            this.topicClose,
            OpenDoorActionGoal._TYPE,
            OpenDoorActionFeedback._TYPE,
            OpenDoorActionResult._TYPE )
        clientWipe = ActionClient(
            connectedNode,
            this.topicWipeArea,
            WipeActionGoal._TYPE,
            WipeActionFeedback._TYPE,
            WipeActionResult._TYPE )
        clientRetract =  ActionClient(
            connectedNode,
            this.topicRetract,
            PlaceEntityActionGoal._TYPE,
            PlaceEntityActionFeedback._TYPE,
            PlaceEntityActionResult._TYPE )
        initialized = true
    }

    override fun destroyNode() {
        clientSetup?.shutdown()
        clientSetupArea?.shutdown()
        clientAttach?.shutdown()
        clientRecognizeObjects?.shutdown()
        clientRecognizeEntities?.shutdown()

        clientGraspEntity?.let { it.finish() }
        clientPlaceEntity?.let { it.finish() }
        clientHoldEntity?.let { it.finish() }
        clientPour?.let { it.finish() }
        clientOpen?.let { it.finish() }
        clientClose?.let { it.finish() }
        clientWipe?.let { it.finish() }
        clientRetract?.let { it.finish() }
    }


    override fun recognizeObjects(
        entity: Entity,
        storage: String,
        minProb: Double,
        fastPose: Boolean,
        addEntities: Boolean,
        clearStorage: Boolean,
        padding: Float
    ): Future<EntityList?> {
        clientRecognizeObjects?.let {
            var req = it.newMessage()
            req.entity = entity.id
            req.storage = storage
            req.addEntities = addEntities
            req.clearStorage = clearStorage
            req.minProb = minProb.toFloat()
            req.fast6dof = fastPose
            req.padding = padding
            //req.useEntity = false
            //req.addGroundPlane = false
            var res = ResponseFuture<RecognizeObjectsResponse>()
            it.call(req,res)
            return res.toTypeFuture { rf ->
                val list = EntityList()
                for (e in rf.entities) {
                    val e = MsgTypeFactory.getInstance().createType(e, Entity::class.java)
                    list.add(e)
                }
                list
            }
        }
        throw RosException("service server failure ${this.topicRecognizeObjects}")
    }

    override fun recognizeEntities(
        minProb: Double,
        fastPose: Boolean,
        addEntities: Boolean,
        safetyHeight: Double
    ): Future<EntityList?> {
        clientRecognizeEntities?.let {
            val req = it.newMessage()
            req.minProb = minProb.toFloat()
            req.fast6dof = fastPose
            req.addEntities = addEntities
            if (safetyHeight > 0.0) {
                req.addSafetyPlane = true
                req.safetyHeight = safetyHeight.toFloat()
            }
            var res = ResponseFuture<RecognizeEntitiesResponse>()
            it.call(req,res)
            return res.toTypeFuture { rf ->
                val list = EntityList()
                for (e in rf.entities) {
                    val e = MsgTypeFactory.getInstance().createType(e, Entity::class.java)
                    list.add(e)
                }
                list
            }
        }
        throw RosException("service server failure ${this.topicRecognizeObjects}")
    }

    override fun graspEntity(
        entity: Entity,
        upright: Boolean,
        carryPose: String?,
        unknownEntity: Boolean,
        keepScene: Boolean
    ): Future<MoveitResult?> {
        clientGraspEntity?.let {
            val goal = it.newGoalMessage()
            goal.goal.entityName = entity.id
            goal.goal.keepScene = keepScene
            goal.goal.onSuccess = GraspEntityGoal.NOTHING
            goal.goal.uprightGrasping = upright
            goal.goal.addGroundPlane = true
            if (unknownEntity) {
                goal.goal.useEntity = true
                goal.goal.entity = MsgTypeFactory.getInstance().createMsg(entity, ecwm_msgs.Entity::class.java)
            }
            if (carryPose != null && carryPose != "null") goal.goal.carryPose = carryPose
            val res: ActionFuture<GraspEntityActionGoal, GraspEntityActionFeedback, GraspEntityActionResult> = it.sendGoal(goal)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicGrasp}")
    }

    override fun placeEntity(
        target_entity: Entity,
        targetStorage: String,
        attachedEntity: Entity?,
        upright: Boolean,
    ): Future<MoveitResult?> {
        clientPlaceEntity?.let {
            var req = it.newGoalMessage()
            req.goal.attachedObjectName = attachedEntity?.id ?: ""
            req.goal.attachedObjectType = attachedEntity?.modelName ?: ""
            req.goal.usePose = false
            req.goal.targetEntityName = target_entity.id
            req.goal.targetStorage = targetStorage ?: ""
            req.goal.keepScene = false
            req.goal.uprightGrasping = upright
            var res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicPlace}")
    }

    override fun placeEntity(
        attached_entity: Entity?,
        pose: Pose3D,
        flip: Boolean,
        min_dist: Point3D?,
        max_dist: Point3D?,
        upright: Boolean,
        topdown: Boolean
    ): Future<MoveitResult?> {
        clientPlaceEntity?.let {
            var req = it.newGoalMessage()
            req.goal.attachedObjectName = attached_entity?.id ?: ""
            req.goal.attachedObjectType = attached_entity?.modelName ?: ""
            req.goal.usePose = true
            req.goal.upsideDown = flip
            req.goal.pose =  MsgTypeFactory.getInstance().createMsg(pose, PoseStamped::class.java)
            req.goal.minPoseDist = MsgTypeFactory.getInstance().createMsg(min_dist, Point::class.java)
            req.goal.maxPoseDist = MsgTypeFactory.getInstance().createMsg(max_dist, Point::class.java)
            req.goal.keepScene = false
            req.goal.topDown = topdown
            req.goal.uprightGrasping = upright
            var res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicPlace}")
    }

    override fun placeEntity(attached_entity: Entity?, pose: Pose3D, flip: Boolean, upright: Boolean): Future<MoveitResult?> {
        val min_dist = Point3D(-0.15, -0.15, -0.05, LengthUnit.METER)
        val max_dist = Point3D(0.15, 0.15, 0.2, LengthUnit.METER)
        return placeEntity(attached_entity, pose, flip, min_dist, max_dist, upright)
    }

    override fun wipeArea(
        target_entity: Entity,
        point: Point3DStamped,
        area: Point3D,
        max_height_offset: Double,
    ): Future<MoveitResult?> {
        clientWipe?.let {
            var req = it.newGoalMessage()
            req.goal.targetEntityName = target_entity.id
            logger.debug("set target_entity_name to ${target_entity.id}")
            req.goal.targetStorage = point.frameId
            logger.debug("set targetStorage to ${point.frameId}")
            req.goal.point =  MsgTypeFactory.getInstance().createMsg(point, Point::class.java)
            req.goal.area =  MsgTypeFactory.getInstance().createMsg(area, Point::class.java)
            req.goal.area.z = max_height_offset
            var res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicWipeArea}")
    }

    override fun wipeArea(
        target_entity: Entity,
        pose: Pose3D,
        area_width: Double,
        area_length: Double,
        max_height_offset: Double,
    ): Future<MoveitResult?> {
        val area = Point3D(area_width, area_length, 0.0, LengthUnit.METER)
        val p = Point3DStamped(pose.translation.getX(LengthUnit.METER), pose.translation.getY(LengthUnit.METER), 0.0, LengthUnit.METER, pose.frameId)
        return wipeArea(target_entity, p, area, max_height_offset)
    }

    override fun wipeArea(
        target_entity_name: String,
        pose: Pose3D,
        area_width: Double,
        area_length: Double,
        max_height_offset: Double,
    ): Future<MoveitResult?> {
        val area = Point3DStamped(area_width, area_length, 0.0, LengthUnit.METER, pose.frameId)
        clientWipe?.let {
            var req = it.newGoalMessage()
            req.goal.targetEntityName = target_entity_name
            logger.debug("set target_entity_name to ${target_entity_name}")
            req.goal.targetStorage = pose.frameId
            logger.debug("set targetStorage to ${pose.frameId}")
            req.goal.point =  MsgTypeFactory.getInstance().createMsg(pose, Point::class.java)
            req.goal.area =  MsgTypeFactory.getInstance().createMsg(area, Point::class.java)
            req.goal.area.z = max_height_offset
            var res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicWipeArea}")
    }

    override fun approachPose(pose: Pose3D, flip: Boolean, min_dist: Point3D, max_dist: Point3D, upright: Boolean): Future<MoveitResult?> {
        clientHoldEntity?.let {
            val req = it.newGoalMessage()
            req.goal.usePose = true
            req.goal.upsideDown = flip
            req.goal.pose =  MsgTypeFactory.getInstance().createMsg(pose, PoseStamped::class.java)
            req.goal.minPoseDist = MsgTypeFactory.getInstance().createMsg(min_dist, Point::class.java)
            req.goal.maxPoseDist = MsgTypeFactory.getInstance().createMsg(max_dist, Point::class.java)
            req.goal.keepScene = false
            req.goal.uprightGrasping = upright
            val res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicHold}")
    }

    override fun pourInto(target_entity: Entity, attachedEntity: Entity?): Future<MoveitResult?> {
        clientPour?.let {
            var req = it.newGoalMessage()
            req.goal.targetEntityName = target_entity.id
            var res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicPour}")

    }

    override fun setupPlanningScene(
        entities: List<Entity>,
        clear_scene: Boolean,
        clear_attached: Boolean
    ): Future<Boolean> {
        clientSetup?.let {
            var req = it.newMessage();
            req.entitiyNames.addAll(entities.map { it.id })
            req.clearAttached = clear_attached
            req.clearScene = clear_scene
            var res = ResponseFuture<SetupPlanningSceneResponse>()
            it.call(req, res)
            return res.toBooleanFuture()
        }
        throw RosException("service server failure ${this.topicPS}")
    }

    override fun setupPlanningSceneArea(
        size: Float,
        clear_scene: Boolean,
        clear_attached: Boolean,
        no_graspable: Boolean
    ): Future<Boolean> {
        clientSetupArea?.let {
            var req = it.newMessage();
            req.size = size
            req.clearAttached = clear_attached
            req.clearScene = clear_scene
            req.target.header.frameId = "base_link"
            req.target.pose.position.x = 1.0
            req.noGraspable = no_graspable
            var res = ResponseFuture<SetupPlanningSceneAreaResponse>()
            it.call(req, res)
            return res.toBooleanFuture()
        }
        throw RosException("service server failure ${this.topicPS}")
    }

    override fun attachEntity(entity: Entity, pose: Pose3D?, create: Boolean): Future<Boolean> {
        clientAttach?.let {
            var req = it.newMessage();
            req.name = entity.id
            req.type = entity.modelName
            req.create = create
            if(pose != null) {
                req.usePose = true;
                req.attachPose = MsgTypeFactory.getInstance().createMsg(pose, Pose::class.java)
            }
            var res = ResponseFuture<AttachEntityResponse>()
            it.call(req, res)
            return res.toBooleanFuture()
        }
        throw RosException("service server failure ${this.topicAttach}")
    }

    override fun openDoor(door: Entity): Future<MoveitResult?> {
        clientOpen?.let {
            val req = it.newGoalMessage();
            req.goal.door = door.id
            val res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicOpen}")
    }

    override fun closeDoor(door: Entity): Future<MoveitResult?> {
        clientClose?.let {
            val req = it.newGoalMessage();
            req.goal.door = door.id
            val res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicClose}")
    }

    override fun getBoundingBox(type: Model): Future<BoundingBox3D?> {
        clientBox?.let {
            var req = it.newMessage();
            req.type = type.typeName
            var res = ResponseFuture<GetBoundingBoxResponse>()
            it.call(req, res)
            return res.toTypeFuture { rf ->
                MsgTypeFactory.getInstance().createType(rf.box, BoundingBox3D::class.java)
            }
        }
        throw RosException("service server failure ${this.topicBox}")
    }

    override fun retract(): Future<MoveitResult?> {
        clientRetract?.let {
            val req = it.newGoalMessage()
            req.goal.usePose = true
            req.goal.keepScene = false
            val res = it.sendGoal(req)
            return object : Future<MoveitResult?> {
                override fun cancel(p0: Boolean): Boolean = res.cancel(p0)
                override fun isCancelled(): Boolean = res.isCancelled
                override fun isDone(): Boolean = res.isDone
                override fun get(): MoveitResult = MoveitResult.getById(res.get().result.code.`val`)
                override fun get(p0: Long, p1: TimeUnit?): MoveitResult =  MoveitResult.getById(res.get(p0,p1).result.code.`val`)
            }
        }
        throw RosException("service server failure ${this.topicRetract}")
    }


}

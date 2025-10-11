package de.unibi.citec.clf.bonsai.ros.actuators

import de.unibi.citec.clf.bonsai.actuators.ECWMRobocup
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture
import de.unibi.citec.clf.btl.data.ecwm.robocup.EntityStorage
import de.unibi.citec.clf.btl.data.ecwm.robocup.EntityWithAttributes
import de.unibi.citec.clf.btl.data.ecwm.robocup.ModelWithAttributes
import de.unibi.citec.clf.btl.data.ecwm.robocup.ModelWithAttributesList
import de.unibi.citec.clf.btl.data.knowledge.Attributes
import de.unibi.citec.clf.btl.data.world.Entity
import de.unibi.citec.clf.btl.data.world.EntityList
import de.unibi.citec.clf.btl.data.world.Model
import de.unibi.citec.clf.btl.data.world.ModelList
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import ecwm_msgs.GetAllSpirits
import ecwm_msgs.GetAllSpiritsRequest
import ecwm_msgs.GetAllSpiritsResponse
import ecwm_robocup_msgs.*
import org.apache.log4j.Logger
import org.ros.exception.RosException
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.service.ServiceClient
import java.util.concurrent.Future

class ECWMRobocupActuator(private val nodeName: GraphName) : RosNode(), ECWMRobocup {

    private val logger = Logger.getLogger(javaClass)

    private var clientTypes: ServiceClient<GetAllTypesWithAttributesRequest, GetAllTypesWithAttributesResponse>? = null
    private var clientModelAttributes: ServiceClient<GetTypeAttributesRequest, GetTypeAttributesResponse>? = null
    private var clientEntityAttributes: ServiceClient<GetEntityAttributesRequest, GetEntityAttributesResponse>? = null
    private var clientStorage: ServiceClient<GetCategoryStorageRequest, GetCategoryStorageResponse>? = null
    private var clientSpirit: ServiceClient<GetAllSpiritsRequest, GetAllSpiritsResponse>? = null
    private var clientAllModelAttributes: ServiceClient<GetAllAttributesRequest, GetAllAttributesResponse>? = null
    private var clientAllEntitieAttributes: ServiceClient<GetAllEntitiesWithAttributesRequest, GetAllEntitiesWithAttributesResponse>? = null

    private var topicEntities: String = "/ecwm/RobocupServices/get_entities"
    private var topicTypes: String = "/ecwm/RobocupServices/get_types"
    private var topicAttributes: String = "/ecwm/RobocupServices/get_attributes"
    private var topicStorage: String = "/ecwm/RobocupServices/get_storage"
    private var topicSpirit: String = "/ecwm/RobocupServices/get_spirits"
    private var topicAllAttributes: String = "/ecwm/RobocupServices/get_all_attributes"
    private var topicEntityAttributes: String = "/ecwm/RobocupServices/get_entity_attributes"

    init {
        initialized = false
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName;
    }

    override fun configure(conf: IObjectConfigurator) {
        topicTypes = conf.requestOptionalValue("topicTypes", topicTypes)
        topicAttributes = conf.requestOptionalValue("topicAttributes", topicAttributes)
        topicStorage = conf.requestOptionalValue("topicStorage", topicStorage)
        topicSpirit = conf.requestOptionalValue("topicSpirit", topicSpirit)
        topicAllAttributes = conf.requestOptionalValue("topicAllAttributes", topicAllAttributes)
    }

    override fun onStart(connectedNode: ConnectedNode) {
        clientTypes = connectedNode.newServiceClient(topicTypes, GetAllTypesWithAttributes._TYPE)
        clientModelAttributes = connectedNode.newServiceClient(topicAttributes, GetTypeAttributes._TYPE)
        clientStorage = connectedNode.newServiceClient(topicStorage, GetCategoryStorage._TYPE)
        clientSpirit = connectedNode.newServiceClient(topicSpirit, GetAllSpirits._TYPE)
        clientAllModelAttributes = connectedNode.newServiceClient(topicAllAttributes, GetAllAttributes._TYPE)
        clientAllEntitieAttributes = connectedNode.newServiceClient(topicEntities, GetAllEntitiesWithAttributes._TYPE)
        clientEntityAttributes = connectedNode.newServiceClient(topicEntityAttributes, GetEntityAttributes._TYPE)
        initialized = true
    }

    override fun getModelAttributes(type: String): Future<Attributes?> {
        clientModelAttributes?.let { client ->
            var req = client.newMessage()
            req.type = type
            val res = ResponseFuture<GetTypeAttributesResponse>()
            client.call(req,res)
            return res.toTypeFuture {
                Attributes("type:$type", HashMap()).also { attr ->
                    for (a in it.attributes) {
                        attr.addAttributes(a.key, a.values)
                    }
                }
            }
        }
        throw RosException("service server failure ${this.topicAttributes}")
    }

    override fun getModelAttributes(type: ModelWithAttributes): Future<ModelWithAttributes?> {
        if (type.typeName.isEmpty()) {
            throw RosException("empty entity type given")
        }
        clientModelAttributes?.let {
            var req = it.newMessage()
            req.type = type.typeName
            val res = ResponseFuture<GetTypeAttributesResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                ModelWithAttributes(type.typeName, HashMap()).also { e ->
                    for (a in it.attributes) {
                        e.addAttributes(a.key, a.values)
                    }
                }
            }
        }
        throw RosException("service server failure ${this.topicAttributes}")
    }

    override fun getEntityAttributes(entity: Entity): Future<Attributes?> {
        clientEntityAttributes?.let { client ->
            val req = client.newMessage()
            req.overwrite = false
            req.entity = entity.id
            val res = ResponseFuture<GetEntityAttributesResponse>()
            client.call(req,res)
            return res.toTypeFuture {
                Attributes("entity:${entity.id}", HashMap()).also { attr ->
                    for (a in it.attributes) {
                        attr.addAttributes(a.key, a.values)
                    }
                }
            }
        }
        throw RosException("service server failure ${this.topicEntityAttributes}")
    }

    override fun getEntityAttributes(entity: EntityWithAttributes): Future<EntityWithAttributes?> {
        clientEntityAttributes?.let { client ->
            val req = client.newMessage()
            req.overwrite = false
            req.entity = entity.id
            val res = ResponseFuture<GetEntityAttributesResponse>()
            client.call(req,res)
            return res.toTypeFuture {
                EntityWithAttributes(entity, HashMap()).also { e ->
                    for (a in it.attributes) {
                        e.addAttributes(a.key, a.values)
                    }
                }
            }
        }
        throw RosException("service server failure ${this.topicEntityAttributes}")
    }

    override fun getAllModels(): Future<ModelWithAttributesList?> {
        clientAllModelAttributes?.let {
            var req = it.newMessage()
            val res = ResponseFuture<GetAllAttributesResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                ModelWithAttributesList().also { list ->
                    list.addAll(it.models.map { ma ->
                        ModelWithAttributes(ma.name, HashMap()).also { e ->
                            for (att in ma.attributes) {
                                e.addAttributes(att.key, att.values)
                            }
                        }
                    })
                }
            }
        }
        throw RosException("service server failure ${this.topicTypes}")
    }

    override fun getTypesWithAttributes(attributes: Map<String, String>): Future<ModelList?> {
        clientTypes?.let {
            var req = it.newMessage()
            req.attributes.addAll(attributes.map { kv ->
                val msg : ecwm_robocup_msgs.Attribute = MsgTypeFactory.getInstance().newMessage(Attribute._TYPE)
                msg.key = kv.key
                msg.values.add(kv.value)
                msg
            })
            val res = ResponseFuture<GetAllTypesWithAttributesResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                ModelList().also { a ->
                    a.addAll(it.types.map { type ->
                        Model(type)
                    })
                }
            }
        }
        throw RosException("service server failure ${this.topicTypes}")
    }

    override fun getEntitiesWithAttributes(attributes: Map<String, String>): Future<EntityList?> {
        clientAllEntitieAttributes?.let { client ->
            val req = client.newMessage()
            req.attributes.addAll(attributes.map { kv ->
                val msg : ecwm_robocup_msgs.Attribute = MsgTypeFactory.getInstance().newMessage(Attribute._TYPE)
                msg.key = kv.key
                msg.values.add(kv.value)
                msg
            })
            val res = ResponseFuture<GetAllEntitiesWithAttributesResponse>()
            client.call(req,res)
            return res.toTypeFuture {
                EntityList().also { a ->
                    a.addAll(it.entities.map { id ->
                        Entity(id)
                    })
                }
            }
        }
        throw RosException("service server failure ${this.topicTypes}")
    }

    override fun getCategoryStorage(category: String): Future<EntityStorage?> {
        clientStorage?.let {
            var req = it.newMessage()
            req.category = category
            val res = ResponseFuture<GetCategoryStorageResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                EntityStorage(MsgTypeFactory.getInstance().createType(it.entity,Entity::class.java),it.storage)
            }
        }
        throw RosException("service server failure ${this.topicStorage}")
    }

    override fun getEntitySpirits(entity: Entity): Future<Map<String, Set<String>>?> {
        clientSpirit?.let {
            var req = it.newMessage()
            req.entityName = entity.id
            val res = ResponseFuture<GetAllSpiritsResponse>()
            it.call(req,res)
            return res.toTypeFuture {
                var map : MutableMap<String, Set<String>> = HashMap()
                for (s in it.storageSpirits) {
                    map[s.storageName] = HashSet<String>().apply { addAll(s.spiritNames) }
                }
                map
            }
        }
        throw RosException("service server failure ${this.topicSpirit}")
    }

    override fun destroyNode() {
        clientTypes?.shutdown();
        clientModelAttributes?.shutdown();
        clientStorage?.shutdown();

    }


}
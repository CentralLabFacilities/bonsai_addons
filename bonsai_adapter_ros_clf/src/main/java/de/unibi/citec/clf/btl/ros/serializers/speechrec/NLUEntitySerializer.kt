package de.unibi.citec.clf.btl.ros.serializers.speechrec

import de.unibi.citec.clf.btl.data.speechrec.NLUEntity
import de.unibi.citec.clf.btl.ros.RosSerializer
import org.apache.log4j.Logger
import org.ros.message.MessageFactory

/**
 *
 * @author lruegeme
 */
class NLUEntitySerializer : RosSerializer<NLUEntity, clf_speech_msgs.Entity>() {
    override fun getDataType(): Class<NLUEntity> {
        return NLUEntity::class.java
    }

    override fun getMessageType(): Class<clf_speech_msgs.Entity> {
        return clf_speech_msgs.Entity::class.java
    }

    @Throws(DeserializationException::class)
    override fun deserialize(msg: clf_speech_msgs.Entity): NLUEntity {
        return NLUEntity(msg.key, msg.value, msg.role, msg.group)
    }

    @Throws(SerializationException::class)
    override fun serialize(data: NLUEntity, fact: MessageFactory): clf_speech_msgs.Entity {
        val e : clf_speech_msgs.Entity = fact.newFromType(clf_speech_msgs.Entity._TYPE)
        e.value = data.entity
        e.key = data.key
        e.role = data.role
        return e
    }

    companion object {
        private val logger = Logger.getLogger(NLUEntitySerializer::class.java)
    }
}
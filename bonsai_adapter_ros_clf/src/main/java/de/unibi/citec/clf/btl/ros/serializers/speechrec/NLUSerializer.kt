package de.unibi.citec.clf.btl.ros.serializers.speechrec

import de.unibi.citec.clf.btl.data.speechrec.Language
import de.unibi.citec.clf.btl.data.speechrec.NLU
import de.unibi.citec.clf.btl.data.speechrec.NLUEntity
import de.unibi.citec.clf.btl.ros.MsgTypeFactory
import de.unibi.citec.clf.btl.ros.RosSerializer
import org.apache.log4j.Logger
import org.ros.message.MessageFactory

/**
 *
 * @author lruegeme
 */
class NLUSerializer : RosSerializer<NLU, clf_speech_msgs.NLU>() {
    override fun getDataType(): Class<NLU> {
        return NLU::class.java
    }

    override fun getMessageType(): Class<clf_speech_msgs.NLU> {
        return clf_speech_msgs.NLU::class.java
    }

    @Throws(DeserializationException::class)
    override fun deserialize(msg: clf_speech_msgs.NLU): NLU {

        val nluentities = msg.entities.stream().map {
            MsgTypeFactory.getInstance().createType(it, NLUEntity::class.java)
        }.toList()

        return NLU(msg.text, msg.intent, msg.conf, nluentities, Language.valueOf(msg.lang))
    }

    @Throws(SerializationException::class)
    override fun serialize(data: NLU, fact: MessageFactory): clf_speech_msgs.NLU {
        val msg = fact.newFromType<clf_speech_msgs.NLU>(clf_speech_msgs.NLU._TYPE)

        return msg.apply {
            text = data.text
            intent = data.intent
            conf = data.confidence
            entities.addAll(data.map {
                val emsg : clf_speech_msgs.Entity = fact.newFromType(clf_speech_msgs.Entity._TYPE)
                emsg.key = it?.key
                emsg.value = it?.value
                emsg.role = it?.role
                emsg
            })
        }
    }

    companion object {
        private val logger = Logger.getLogger(NLUSerializer::class.java)
    }
}
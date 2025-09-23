

package de.unibi.citec.clf.btl.ros.serializers.speechrec;

import de.unibi.citec.clf.btl.data.speech.Utterance;
import de.unibi.citec.clf.btl.ros.RosSerializer;
//import de.unibi.citec.clf.btl.xml.XomSerializer;
//import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.ParsingException;
import std_msgs.String;
import org.ros.message.MessageFactory;
import java.io.IOException;


/**
 *
 * @author ffriese
 */
@Deprecated
public class UtteranceSerializer extends RosSerializer<Utterance, String> {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UtteranceSerializer.class);

    public UtteranceSerializer() {
    }

    @Override
    public Class<Utterance> getDataType() {
		return Utterance.class;
    }

     @Override
    public Class<String> getMessageType() {
        return String.class;
    }

    @Override
    public Utterance deserialize(String msg) throws DeserializationException {
        Utterance u = null;
        //try {
        //    u = XomTypeFactory.getInstance().createType(new Builder().build(msg.getData(), null), Utterance.class);
        //} catch (XomSerializer.DeserializationException | IOException | ParsingException e) {
        //    e.printStackTrace();
        //}
        return u;
    }

    @Override
    public String serialize(Utterance data, MessageFactory fact) throws SerializationException {
        String s = fact.newFromType(String._TYPE);
        s.setData(data.toString());
        return s;
    }



}
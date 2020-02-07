
package de.unibi.citec.clf.btl.ros.serializers.object;


import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import org.ros.message.MessageFactory;

/**
 *
 * @author
 */
public class HypothesisSerializer extends RosSerializer<ObjectShapeData.Hypothesis, object_tracking_msgs.Hypothesis> {

    @Override
    public Class<object_tracking_msgs.Hypothesis> getMessageType() {
        return object_tracking_msgs.Hypothesis.class;
    }

    @Override
    public Class<ObjectShapeData.Hypothesis> getDataType() {
        return ObjectShapeData.Hypothesis.class;
    }

    @Override
    public object_tracking_msgs.Hypothesis serialize(ObjectShapeData.Hypothesis data, MessageFactory fact) throws SerializationException {
        object_tracking_msgs.Hypothesis ret = fact.newFromType(object_tracking_msgs.Hypothesis._TYPE);
        ret.setLabel(data.getClassLabel());
        ret.setReliability((float)data.getReliability());
        return ret;
    }
    
    @Override
    public ObjectShapeData.Hypothesis deserialize(object_tracking_msgs.Hypothesis msg) throws DeserializationException {
        ObjectShapeData.Hypothesis hyp = new ObjectShapeData.Hypothesis();
        hyp.setReliability(msg.getReliability());
        hyp.setClassLabel(msg.getLabel());
        return hyp;
    }

}

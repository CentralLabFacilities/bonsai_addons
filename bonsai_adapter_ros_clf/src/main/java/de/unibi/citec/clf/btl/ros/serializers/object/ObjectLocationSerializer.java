
package de.unibi.citec.clf.btl.ros.serializers.object;


import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import java.util.LinkedList;
import java.util.List;

import org.ros.message.MessageFactory;

/**
 *
 * @author
 */
public class ObjectLocationSerializer extends RosSerializer<ObjectLocationData, object_tracking_msgs.ObjectLocation> {

    @Override
    public object_tracking_msgs.ObjectLocation serialize(ObjectLocationData data, MessageFactory fact) throws SerializationException {
        object_tracking_msgs.ObjectLocation ret = fact.newFromType(object_tracking_msgs.ObjectLocation._TYPE);
        List<object_tracking_msgs.Hypothesis> hypos = new LinkedList<>();
        /*for(ObjectLocationData.Hypothesis hyp:data.getLocationHypotheses()){
            object_tracking_msgs.Hypothesis h = fact.newFromType(object_tracking_msgs.Hypothesis._TYPE);
            h.setLabel(hyp.getClassLabel());
            h.setReliability((float) hyp.getReliability());
            hypos.add(h);
        }*/
        sensor_msgs.RegionOfInterest roi = fact.newFromType(sensor_msgs.RegionOfInterest._TYPE);
        /*Rectangle box= data.getPolygon().getAwtPolygon().getBounds();
        roi.setHeight(box.height);
        roi.setWidth(box.width);
        roi.setXOffset(box.x);
        roi.setYOffset(box.y);
        ret.setHypotheses(hypos);
        ret.setBoundingBox(roi);*/
        return ret;
    }

    @Override
    public ObjectLocationData deserialize(object_tracking_msgs.ObjectLocation msg) throws DeserializationException {
        ObjectLocationData ret = new ObjectLocationData();
        ret.setPolygon(MsgTypeFactory.getInstance().createType(msg.getBoundingBox(), PrecisePolygon.class));
        for (object_tracking_msgs.Hypothesis hyp : msg.getHypotheses()) {
            ret.addHypothesis(MsgTypeFactory.getInstance().createType(hyp, ObjectLocationData.Hypothesis.class));
        }
        return ret;
    }

    @Override
    public Class<object_tracking_msgs.ObjectLocation> getMessageType() {
        return object_tracking_msgs.ObjectLocation.class;
    }

    @Override
    public Class<ObjectLocationData> getDataType() {
        return ObjectLocationData.class;
    }
}

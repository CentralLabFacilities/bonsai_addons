
package de.unibi.citec.clf.btl.ros.serializers.object;


import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.units.LengthUnit;
import org.ros.message.MessageFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author
 */
public class ObjectShapeSerializer extends RosSerializer<ObjectShapeData, object_tracking_msgs.ObjectShape> {

    @Override
    public object_tracking_msgs.ObjectShape serialize(ObjectShapeData data, MessageFactory fact) throws SerializationException {
        object_tracking_msgs.ObjectShape ret = fact.newFromType(object_tracking_msgs.ObjectShape._TYPE);
        List<object_tracking_msgs.Hypothesis> hypos = new LinkedList<>();
        for (ObjectShapeData.Hypothesis hyp : data.getHypotheses()) {
            object_tracking_msgs.Hypothesis h = fact.newFromType(object_tracking_msgs.Hypothesis._TYPE);
            h.setLabel(hyp.getClassLabel());
            h.setReliability((float) hyp.getReliability());
            hypos.add(h);
        }
        sensor_msgs.RegionOfInterest roi = fact.newFromType(sensor_msgs.RegionOfInterest._TYPE);
        /*Rectangle box= data.getPolygon().getAwtPolygon().getBounds();
        roi.setHeight(box.height);
        roi.setWidth(box.width);
        roi.setXOffset(box.x);
        roi.setYOffset(box.y);*/
        geometry_msgs.Point p = fact.newFromType(geometry_msgs.Point._TYPE);
        p.setX(data.getCenter().getX(LengthUnit.METER));
        p.setY(data.getCenter().getY(LengthUnit.METER));
        p.setZ(data.getCenter().getZ(LengthUnit.METER));

        ret.setHypotheses(hypos);
        ret.setBoundingBox(roi);
        ret.setCenter(p);
        ret.setWidth((float) data.getWidth(LengthUnit.METER));
        ret.setHeight((float) data.getHeight(LengthUnit.METER));
        ret.setDepth((float) data.getDepth(LengthUnit.METER));

        ret.setName(data.getId());

        return ret;
    }

    @Override
    public ObjectShapeData deserialize(object_tracking_msgs.ObjectShape msg) throws DeserializationException {
        ObjectShapeData ret = new ObjectShapeData();

        ret.setId(msg.getName());

        BoundingBox3D box = new BoundingBox3D();
        Pose3D center = new Pose3D();
        center.setTranslation(new Point3D(msg.getCenter().getX(), msg.getCenter().getY(), msg.getCenter().getZ(), LengthUnit.METER));
        box.setPose(center);
        box.setSize(new Point3D(msg.getWidth(),msg.getHeight(),msg.getDepth()));
        ret.setBoundingBox(box);

        for (object_tracking_msgs.Hypothesis hyp : msg.getHypotheses()) {
            ret.addHypothesis(MsgTypeFactory.getInstance().createType(hyp, ObjectShapeData.Hypothesis.class));
        }

        /*ret.setCenter(MsgTypeFactory.getInstance().createType(msg.getCenter(), Point3D.class));
        ret.setHeight(msg.getHeight(), LengthUnit.METER);
        ret.setWidth(msg.getWidth(), LengthUnit.METER);
        ret.setDepth(msg.getDepth(), LengthUnit.METER);
        ret.setId(msg.getName());*/
        return ret;
    }

    @Override
    public Class<object_tracking_msgs.ObjectShape> getMessageType() {
        return object_tracking_msgs.ObjectShape.class;
    }

    @Override
    public Class<ObjectShapeData> getDataType() {
        return ObjectShapeData.class;
    }
}

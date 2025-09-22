package de.unibi.citec.clf.btl.ros.serializers.world;


import de.unibi.citec.clf.btl.data.world.Entity;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;

/**
 * @author^
 */
public class EntitySerializer extends RosSerializer<Entity, ecwm_msgs.Entity> {

    @Override
    public ecwm_msgs.Entity serialize(Entity data, MessageFactory fact) throws SerializationException {
        ecwm_msgs.Entity msg = fact.newFromType(ecwm_msgs.Entity._TYPE);

        msg.setName(data.getId());
        msg.setType(data.getModelName());
        msg.setFrameId(data.getFrameId());
        if (data.getPose() != null)
            msg.setPose(MsgTypeFactory.getInstance().createMsg(data.getPose(), geometry_msgs.Pose._TYPE));

        return msg;
    }

    @Override
    public Entity deserialize(ecwm_msgs.Entity msg) throws DeserializationException {
        MsgTypeFactory fac = MsgTypeFactory.getInstance();

        Pose3D pose = fac.createType(msg.getPose(), Pose3D.class);
        pose.setFrameId(msg.getFrameId());
        Entity data = new Entity(msg.getName(), msg.getType(), pose);
        data.setFrameId(msg.getFrameId());
        return data;
    }

    @Override
    public Class<ecwm_msgs.Entity> getMessageType() {
        return ecwm_msgs.Entity.class;
    }

    @Override
    public Class<Entity> getDataType() {
        return Entity.class;
    }

}

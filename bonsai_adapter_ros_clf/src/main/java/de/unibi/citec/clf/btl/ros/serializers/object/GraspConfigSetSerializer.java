
package de.unibi.citec.clf.btl.ros.serializers.object;


import de.unibi.citec.clf.btl.data.object.GraspConfig;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;

/**
 *
 * @author
 */
public class GraspConfigSetSerializer extends RosSerializer<GraspConfig, augmented_manipulation_msgs.GraspConfigSet> {

    @Override
    public Class<augmented_manipulation_msgs.GraspConfigSet> getMessageType() {
        return augmented_manipulation_msgs.GraspConfigSet.class;
    }

    @Override
    public Class<GraspConfig> getDataType() {
        return GraspConfig.class;
    }

    @Override
    public augmented_manipulation_msgs.GraspConfigSet serialize(GraspConfig data, MessageFactory fact) throws SerializationException {
        augmented_manipulation_msgs.GraspConfigSet ret = fact.newFromType(augmented_manipulation_msgs.GraspConfigSet._TYPE);
        ret.setConfigName(data.getConfigName());
        ret.setGroupName(data.getGroupName());
        return ret;
    }
    
    @Override
    public GraspConfig deserialize(augmented_manipulation_msgs.GraspConfigSet msg) throws DeserializationException {
        GraspConfig ret = new GraspConfig();
        ret.setConfigName(msg.getConfigName());
        ret.setGroupName(msg.getGroupName());
        return ret;
    }

}

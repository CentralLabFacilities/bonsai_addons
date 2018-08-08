package de.unibi.citec.btl.ros.serializers.status;

import de.unibi.citec.clf.btl.data.status.BatteryState;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;

/**
 *
 * @author lruegeme
 */
public class PalDeviceMsgBatteryStateToBatteryState extends RosSerializer<BatteryState, pal_device_msgs.BatteryState> {

    @Override
    public pal_device_msgs.BatteryState serialize(BatteryState data, MessageFactory fact) throws SerializationException {
        final pal_device_msgs.BatteryState msg = fact.newFromType(pal_device_msgs.BatteryState._TYPE);
        return msg;
    }

    @Override
    public BatteryState deserialize(pal_device_msgs.BatteryState msg) throws DeserializationException {
        final BatteryState data = new BatteryState(0,false);
        return data;
    }

    @Override
    public Class<pal_device_msgs.BatteryState> getMessageType() {
        return pal_device_msgs.BatteryState.class;
    }

    @Override
    public Class<BatteryState> getDataType() {
        return BatteryState.class;
    }

}

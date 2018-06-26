
package de.unibi.citec.clf.btl.ros.serializers.object;

import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;


import org.ros.message.MessageFactory;

/**
 * @author
 */
public class ObjectShapeListFromMarkerArraySerializer extends RosSerializer<ObjectShapeList, visualization_msgs.MarkerArray> {

    @Override
    public visualization_msgs.MarkerArray serialize(ObjectShapeList data, MessageFactory fact) throws SerializationException {
        visualization_msgs.MarkerArray ret = fact.newFromType(visualization_msgs.MarkerArray._TYPE);
        return ret;
    }

    @Override
    public ObjectShapeList deserialize(visualization_msgs.MarkerArray msg) throws DeserializationException {
        ObjectShapeList ret = new ObjectShapeList();
        for (visualization_msgs.Marker marker : msg.getMarkers()) {
            ObjectShapeData newData = new ObjectShapeData();
            ObjectShapeData.Hypothesis newHyp = new ObjectShapeData.Hypothesis();
            newHyp.setClassLabel(marker.getText());
            newHyp.setReliability(1.0);
            newData.addHypothesis(newHyp);
            ret.add(newData);
        }
        return ret;
    }

    @Override
    public Class<visualization_msgs.MarkerArray> getMessageType() {
        return visualization_msgs.MarkerArray.class;
    }

    @Override
    public Class<ObjectShapeList> getDataType() {
        return ObjectShapeList.class;
    }
}

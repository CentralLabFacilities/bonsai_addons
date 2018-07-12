
package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.TrackingActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import clf_perception_vision_msgs.ToggleCFtldTrackingWithBB;
import clf_perception_vision_msgs.ToggleCFtldTrackingWithBBRequest;
import clf_perception_vision_msgs.ToggleCFtldTrackingWithBBResponse;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author jkummert
 */
public class RosTrackingActuator extends RosNode implements TrackingActuator {

    String topic;

    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    private ServiceClient<ToggleCFtldTrackingWithBBRequest, ToggleCFtldTrackingWithBBResponse> clientTrigger;

    public RosTrackingActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            clientTrigger           = connectedNode.newServiceClient(topic, ToggleCFtldTrackingWithBB._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(clientTrigger!=null) clientTrigger.shutdown();
    }

    @Override
    public Future<Boolean> startTracking(List<Integer> boundingbox){
        final ToggleCFtldTrackingWithBBRequest req = clientTrigger.newMessage();
        //set data
        req.getRoi().setXOffset(boundingbox.get(0));
        req.getRoi().setYOffset(boundingbox.get(1));
        req.getRoi().setHeight(boundingbox.get(2));
        req.getRoi().setWidth(boundingbox.get(3));
        final ResponseFuture<ToggleCFtldTrackingWithBBResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        return res.toBooleanFuture();
    }

    @Override
    public void stopTracking(){
        final ToggleCFtldTrackingWithBBRequest req = clientTrigger.newMessage();
        //dont set data
        final ResponseFuture<ToggleCFtldTrackingWithBBResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
    }
}

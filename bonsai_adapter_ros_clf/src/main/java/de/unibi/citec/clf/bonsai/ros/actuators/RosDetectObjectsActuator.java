
package de.unibi.citec.clf.bonsai.ros.actuators;


import de.unibi.citec.clf.bonsai.actuators.RecognizeObjectsActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatchList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import object_tracking_msgs.DetectObjects;
import object_tracking_msgs.DetectObjectsRequest;
import object_tracking_msgs.DetectObjectsResponse;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ffriese
 */
public class RosDetectObjectsActuator extends RosNode implements RecognizeObjectsActuator {

    String topic;
    long timeout = 10000;
    long actuator_timeout;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<DetectObjectsRequest, DetectObjectsResponse> clientTrigger;
    public RosDetectObjectsActuator(GraphName gn) {
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
            clientTrigger = connectedNode.newServiceClient(topic, DetectObjects._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e.getMessage());
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(clientTrigger!=null)clientTrigger.shutdown();
    }

    @Override
    public List<ObjectShapeData> recognize() throws InterruptedException, ExecutionException {
        if (timeout > 0) {
            logger.debug("using timeout of " + timeout + "ms");
            actuator_timeout = System.currentTimeMillis() + timeout;
        }
        DetectObjectsRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<DetectObjectsResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        while (!res.isDone()) {
            if(actuator_timeout < System.currentTimeMillis()){
                logger.error("service call timed out!");
                return null;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(RosDetectObjectsActuator.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
        }
        if(!res.succeeded()){
            return null;
        }
        List<ObjectShapeData> ret = new List(ObjectShapeData.class);
        for (int i = 0; i < res.get().getObjectLocationList().size(); ++i) {
            try {
                ret.add(new ObjectShapeData(MsgTypeFactory.getInstance().createType(res.get().getObjectLocationList().get(i), ObjectLocationData.class)));
                
            } catch (RosSerializer.DeserializationException ex) {
                Logger.getLogger(RosDetectObjectsActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    @Override
    public PlanePatchList getLastDetectedPlanes() {
        return null;
    }
}

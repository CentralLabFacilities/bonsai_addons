
package de.unibi.citec.clf.bonsai.ros.actuators;


import de.unibi.citec.clf.bonsai.actuators.RecognizeObjectsActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatch;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatchList;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import object_tracking_msgs.RecognizeObjects;
import object_tracking_msgs.RecognizeObjectsRequest;
import object_tracking_msgs.RecognizeObjectsResponse;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jkummert
 */
public class RosRecognizeObjectsActuator extends RosNode implements RecognizeObjectsActuator {

    String topic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<RecognizeObjectsRequest, RecognizeObjectsResponse> clientTrigger;
    private PlanePatchList lastDetectedPlanes;

    public RosRecognizeObjectsActuator(GraphName gn) {
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
            clientTrigger = connectedNode.newServiceClient(topic, RecognizeObjects._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e.getMessage());
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if (clientTrigger != null) clientTrigger.shutdown();
    }

    @Override
    public List<ObjectShapeData> recognize() throws InterruptedException, ExecutionException {
        RecognizeObjectsRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<RecognizeObjectsResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        while (!res.succeeded()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(RosRecognizeObjectsActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        List<ObjectShapeData> ret = new List(ObjectShapeData.class);
        for (int i = 0; i < res.get().getObjects().size(); ++i) {
            try {
                ret.add(MsgTypeFactory.getInstance().createType(res.get().getObjects().get(i), ObjectShapeData.class));

            } catch (RosSerializer.DeserializationException ex) {
                Logger.getLogger(RosRecognizeObjectsActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        lastDetectedPlanes = new PlanePatchList();
        for (int i = 0; i < res.get().getSupportSurfaces().size(); i++) {
            try {
                PlanePatch plane = MsgTypeFactory.getInstance().createType(res.get().getSupportSurfaces().get(i), PlanePatch.class);
                lastDetectedPlanes.add(plane);
            } catch (Exception e) {
                logger.error("error getting plane from message: " + e.getMessage());
            }
        }
        logger.debug("found " + lastDetectedPlanes + " planes");

        return ret;
    }

    @Override
    public PlanePatchList getLastDetectedPlanes() {
        return lastDetectedPlanes;
    }
}


package de.unibi.citec.clf.bonsai.ros.actuators;


import de.unibi.citec.clf.bonsai.actuators.DetectPeopleActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.person.BodySkeleton;
import openpose_ros_msgs.DetectPeople;
import openpose_ros_msgs.DetectPeopleRequest;
import openpose_ros_msgs.DetectPeopleResponse;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;

/**
 *
 * @author jkummert
 */
public class RosDetectPeopleActuator extends RosNode implements DetectPeopleActuator {

    String topic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<DetectPeopleRequest, DetectPeopleResponse> clientTrigger;

    public RosDetectPeopleActuator(GraphName gn) {
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
            clientTrigger = connectedNode.newServiceClient(topic, DetectPeople._TYPE);
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
    public List<BodySkeleton> getPeople() throws InterruptedException, ExecutionException {
        final DetectPeopleRequest req = clientTrigger.newMessage();
        //set data
        final ResponseFuture<DetectPeopleResponse> res = new ResponseFuture<>();
        clientTrigger.call(req, res);
        while (!res.succeeded()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(RosDetectPeopleActuator.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
        }
        List<BodySkeleton> ret = new List(BodySkeleton.class);
        DetectPeopleResponse response = res.get();
        for (int i = 0; i < response.getPeopleList().size(); ++i) {
            try {
                ret.add(MsgTypeFactory.getInstance().createType(res.get().getPeopleList().get(i), BodySkeleton.class));
            } catch (RosSerializer.DeserializationException ex) {
                Logger.getLogger(RosDetectPeopleActuator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //return future that has the response 
        return ret;

        //return future that only checks for success of the call (servce returned true) and discards the response
        //return res.toBooleanFuture();
    }

}

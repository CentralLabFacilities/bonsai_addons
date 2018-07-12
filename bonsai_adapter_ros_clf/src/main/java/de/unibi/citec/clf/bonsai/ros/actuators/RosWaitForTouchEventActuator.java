package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.WaitForTouchEventActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import naoqi_bridge_msgs.SetString;
import naoqi_bridge_msgs.SetStringRequest;
import naoqi_bridge_msgs.SetStringResponse;

import java.util.concurrent.Future;

/**
 * @author ffriese
 */
public class RosWaitForTouchEventActuator extends RosNode implements WaitForTouchEventActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<SetStringRequest, SetStringResponse> client;

    public RosWaitForTouchEventActuator(GraphName gn) {
        initialized = false;
        nodeName = gn;
    }


    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serviceTopic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            client = connectedNode.newServiceClient(serviceTopic, SetString._TYPE);
            initialized = true;
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyNode() {
        if (client != null) {
            client.shutdown();
        }
    }



    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }


    @Override
    public Future<Boolean> waitForTouchEvent(String sensor_name) {
        SetStringRequest r = client.newMessage();
        r.setData(sensor_name);
        final ResponseFuture<SetStringResponse> res = new ResponseFuture<>();
        client.call(r, res);
        return res.toBooleanFuture();
    }
}

package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.StartStopActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import std_srvs.SetBool;
import std_srvs.SetBoolRequest;
import std_srvs.SetBoolResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosEnableGoogleSpeechRecAndSimpleNPLActuator extends RosNode implements StartStopActuator {

    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<SetBoolRequest, SetBoolResponse> client;

    public RosEnableGoogleSpeechRecAndSimpleNPLActuator(GraphName gn) {
        initialized = false;
        nodeName = gn;
    }

    @Override
    public void startProcessing() throws IOException {
        processing(true);
    }

    @Override
    public void stopProcessing() throws IOException {
        processing(false);
    }

    private void processing(boolean enable){
        final SetBoolRequest req = client.newMessage();
        req.setData(enable);

        final ResponseFuture<SetBoolResponse> res = new ResponseFuture<>();
        client.call(req, res);

        try {
            SetBoolResponse sbr = res.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serviceTopic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            client = connectedNode.newServiceClient(serviceTopic, SetBool._TYPE);
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
}

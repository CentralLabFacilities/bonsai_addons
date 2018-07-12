package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.SpeechRecActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import std_srvs.Trigger;
import std_srvs.TriggerRequest;
import std_srvs.TriggerResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosSpeechRecActuator extends RosNode implements SpeechRecActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<TriggerRequest, TriggerResponse> client;


    public RosSpeechRecActuator(GraphName gn) {
        initialized = false;
        nodeName = gn;
    }

    @Override
    public Future<String> listen() throws IOException {
        final TriggerRequest req = client.newMessage();

        final ResponseFuture<TriggerResponse> res = new ResponseFuture<>();
        client.call(req, res);
        Future<String> understood = new Future<String>() {
            @Override
            public boolean cancel(boolean b) {
                return res.cancel(b);
            }

            @Override
            public boolean isCancelled() {
                return res.isCancelled();
            }

            @Override
            public boolean isDone() {
                return res.isDone();
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                return res.get().getMessage();
            }

            @Override
            public String get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return res.get(l, timeUnit).getMessage();
            }
        };
        return understood;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serviceTopic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            client = connectedNode.newServiceClient(serviceTopic, Trigger._TYPE);
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

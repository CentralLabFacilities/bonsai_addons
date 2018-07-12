package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.NLPActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import pepper_clf_msgs.NLP;
import pepper_clf_msgs.NLPRequest;
import pepper_clf_msgs.NLPResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosNLPActuator extends RosNode implements NLPActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<NLPRequest, NLPResponse> client;

    public RosNLPActuator(GraphName gn) {
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
            client = connectedNode.newServiceClient(serviceTopic, NLP._TYPE);
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
    public Future<String> match(String utterance) throws IOException {
        final NLPRequest req = client.newMessage();
        req.setUtterance(utterance);

        final ResponseFuture<NLPResponse> res = new ResponseFuture<>();
        client.call(req, res);

        Future<String> matched = new Future<String>() {
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
                return res.get().getAnswer();
            }

            @Override
            public String get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return res.get(l, timeUnit).getAnswer();
            }
        };
        return matched;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }
}

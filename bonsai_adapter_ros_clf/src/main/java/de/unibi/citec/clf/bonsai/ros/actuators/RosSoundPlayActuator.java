package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.SoundPlayActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import pepper_clf_msgs.SoundPlay;
import pepper_clf_msgs.SoundPlayRequest;
import pepper_clf_msgs.SoundPlayResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosSoundPlayActuator extends RosNode implements SoundPlayActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<SoundPlayRequest, SoundPlayResponse> client;

    public RosSoundPlayActuator(GraphName gn) {
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
            client = connectedNode.newServiceClient(serviceTopic, SoundPlay._TYPE);
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
    public Future<Boolean> play(String name) throws IOException {
        final SoundPlayRequest req = client.newMessage();
        req.setSound(name);


        final ResponseFuture<SoundPlayResponse> res = new ResponseFuture<>();
        client.call(req, res);

        Future<Boolean> future = new Future<Boolean>() {
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
            public Boolean get() throws InterruptedException, ExecutionException {
                return res.get().getSuccess();
            }

            @Override
            public Boolean get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return res.get(l, timeUnit).getSuccess();
            }
        };
        return future;

    }
}

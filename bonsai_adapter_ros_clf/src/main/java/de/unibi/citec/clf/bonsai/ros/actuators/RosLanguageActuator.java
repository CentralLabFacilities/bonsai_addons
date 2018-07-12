package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.LanguageActuator;
import de.unibi.citec.clf.bonsai.actuators.data.SetLanguageResult;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import pepper_clf_msgs.SetTTSLanguage;
import pepper_clf_msgs.SetTTSLanguageRequest;
import pepper_clf_msgs.SetTTSLanguageResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ffriese
 */
public class RosLanguageActuator extends RosNode implements LanguageActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<SetTTSLanguageRequest, SetTTSLanguageResponse> client;

    public RosLanguageActuator(GraphName gn) {
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
            client = connectedNode.newServiceClient(serviceTopic, SetTTSLanguage._TYPE);
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
    public Future<SetLanguageResult> setTTSLanguage(String language) {
        SetTTSLanguageRequest req = client.newMessage();
        req.setLanguage(language);

        final ResponseFuture<SetTTSLanguageResponse> res = new ResponseFuture<>();
        client.call(req, res);

        Future<SetLanguageResult> previous_language = new Future<SetLanguageResult>() {
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
            public SetLanguageResult get() throws InterruptedException, ExecutionException {
                SetTTSLanguageResponse resp = res.get();
                SetLanguageResult r = new SetLanguageResult(resp.getSuccess(), resp.getOldLanguage());
                return r;
            }

            @Override
            public SetLanguageResult get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                SetTTSLanguageResponse resp = res.get(l, timeUnit);
                SetLanguageResult r = new SetLanguageResult(resp.getSuccess(), resp.getOldLanguage());
                return r;
            }
        };
        return previous_language;
    }
}

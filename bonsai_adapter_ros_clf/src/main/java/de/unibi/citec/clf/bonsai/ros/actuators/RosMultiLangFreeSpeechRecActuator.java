package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.MultiLangFreeSpeechRecActuator;
import de.unibi.citec.clf.bonsai.actuators.TranslationActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.speechrec.MultiLangFreeSpeech;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import pepper_clf_msgs.MLSpeechRec;
import pepper_clf_msgs.MLSpeechRecRequest;
import pepper_clf_msgs.MLSpeechRecResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosMultiLangFreeSpeechRecActuator extends RosNode implements MultiLangFreeSpeechRecActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<MLSpeechRecRequest, MLSpeechRecResponse> client;

    public RosMultiLangFreeSpeechRecActuator(GraphName gn) {
        initialized = false;
        nodeName = gn;
    }

    @Override
    public Future<MultiLangFreeSpeech> listen() throws IOException {
        final MLSpeechRecRequest req = client.newMessage();

        final ResponseFuture<MLSpeechRecResponse> res = new ResponseFuture<>();
        client.call(req, res);
        Future<MultiLangFreeSpeech> understood = new Future<MultiLangFreeSpeech>() {
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
            public MultiLangFreeSpeech get() throws InterruptedException, ExecutionException {
                MultiLangFreeSpeech mlfs = new MultiLangFreeSpeech();
                MLSpeechRecResponse response = res.get();
                mlfs.setConfidence(response.getConfidence());
                mlfs.setLanguage(TranslationActuator.Language.fromCode(response.getLanguage()));
                mlfs.setUnderstood_utternace(response.getUnderstoodSentence());
                return mlfs;
            }

            @Override
            public MultiLangFreeSpeech get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                MultiLangFreeSpeech mlfs = new MultiLangFreeSpeech();
                MLSpeechRecResponse response = res.get(l, timeUnit);
                mlfs.setConfidence(response.getConfidence());
                mlfs.setLanguage(TranslationActuator.Language.fromCode(response.getLanguage()));
                mlfs.setUnderstood_utternace(response.getUnderstoodSentence());
                return mlfs;
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
            client = connectedNode.newServiceClient(serviceTopic, MLSpeechRec._TYPE);
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

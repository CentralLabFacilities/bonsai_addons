package de.unibi.citec.clf.bonsai.ros.actuators;

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
import pepper_clf_msgs.TranslateString;
import pepper_clf_msgs.TranslateStringRequest;
import pepper_clf_msgs.TranslateStringResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rfeldhans
 */
public class RosTranslationActuator extends RosNode implements TranslationActuator {
    private GraphName nodeName;
    private String serviceTopic;
    private ServiceClient<TranslateStringRequest, TranslateStringResponse> client;

    public RosTranslationActuator(GraphName gn) {
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
            client = connectedNode.newServiceClient(serviceTopic, TranslateString._TYPE);
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
    public Future<MultiLangFreeSpeech> translate(MultiLangFreeSpeech sentence, Language to) throws IOException {
        final TranslateStringRequest req = client.newMessage();
        req.setLanguage(sentence.getLanguage().getLanguageCode());
        req.setTargetLanguage(to.getLanguageCode());
        req.setSentence(sentence.getUnderstood_utternace());

        final ResponseFuture<TranslateStringResponse> res = new ResponseFuture<>();
        client.call(req, res);

        Future<MultiLangFreeSpeech> translated = new Future<MultiLangFreeSpeech>() {
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
                MultiLangFreeSpeech ret = new MultiLangFreeSpeech();
                ret.setUnderstood_utternace(res.get().getTranslatedSentence());
                ret.setLanguage(to);
                ret.setConfidence(0.5);
                return ret;
            }

            @Override
            public MultiLangFreeSpeech get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                MultiLangFreeSpeech ret = new MultiLangFreeSpeech();
                ret.setUnderstood_utternace(res.get(l, timeUnit).getTranslatedSentence());
                ret.setLanguage(to);
                ret.setConfidence(0.5);
                return ret;
            }
        };
        return translated;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }
}

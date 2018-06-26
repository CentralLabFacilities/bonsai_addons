package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.LearnPersonActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import clf_perception_vision_msgs.DoIKnowThatPerson;
import clf_perception_vision_msgs.DoIKnowThatPersonRequest;
import clf_perception_vision_msgs.DoIKnowThatPersonResponse;
import clf_perception_vision_msgs.LearnPerson;
import clf_perception_vision_msgs.LearnPersonRequest;
import clf_perception_vision_msgs.LearnPersonResponse;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;

/**
 *
 * @author jkummert
 */
public class RosLearnPersonActuator extends RosNode implements LearnPersonActuator {

    String topicLearn;
    String topicRecognize;
    private final GraphName nodeName;
    ServiceClient<DoIKnowThatPersonRequest, DoIKnowThatPersonResponse> recognizeClient;
    ServiceClient<LearnPersonRequest, LearnPersonResponse> learnClient;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosLearnPersonActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            recognizeClient = connectedNode.newServiceClient(topicRecognize, DoIKnowThatPerson._TYPE);
            learnClient = connectedNode.newServiceClient(topicLearn, LearnPerson._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        initialized = true;
        logger.fatal("on start, RosLearnPersonActuator done");
    }

    @Override
    public void destroyNode() {
        if(learnClient!=null) learnClient.shutdown();
        if(recognizeClient!=null) recognizeClient.shutdown();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void configure(IObjectConfigurator ioc) throws ConfigurationException {
        this.topicLearn = ioc.requestValue("topicLearn");
        this.topicRecognize = ioc.requestValue("topicRecognize");
    }

    @Override
    public Future<Boolean> learnPerson(String id, String name) {
        final LearnPersonRequest req = learnClient.newMessage();
        req.setName(name);
        req.setUuid(id);

        final ResponseFuture<LearnPersonResponse> res = new ResponseFuture<>();
        learnClient.call(req, res);

        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean bln) {
                return res.cancel(bln);
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
            public Boolean get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    public Future<String> doIKnowThatPerson(String uuid) {
        final DoIKnowThatPersonRequest req = recognizeClient.newMessage();
        req.setUuid(uuid);

        final ResponseFuture<DoIKnowThatPersonResponse> res = new ResponseFuture<>();
        recognizeClient.call(req, res);

        return new Future<String>() {
            @Override
            public boolean cancel(boolean bln) {
                return res.cancel(bln);
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
                return res.get().getName();
            }

            @Override
            public String get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
}

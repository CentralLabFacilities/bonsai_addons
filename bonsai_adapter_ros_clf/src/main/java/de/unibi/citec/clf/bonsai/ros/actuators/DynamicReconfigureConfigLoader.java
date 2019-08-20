package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.StringActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.object.Actuator;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import openpose_ros_msgs.GetFollowRoiRequest;
import openpose_ros_msgs.GetFollowRoiResponse;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.service.ServiceClient;

import conf_loader_msgs.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author lruegeme
 */
public class DynamicReconfigureConfigLoader extends RosNode implements Actuator {

    String topic;
    private GraphName nodeName;
    private ServiceClient<LoadConfigRequest, LoadConfigResponse> clientConfig;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public enum options {
        MOVE_BASE_NORMAL(1),
        MOVE_BASE_FOLLOWING(2);

        private int value;
        private options(int value){
            this.value = value;
        }
    }

    public DynamicReconfigureConfigLoader(GraphName gn) {
        this.initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) {
        this.topic = conf.requestValue("topic");
    }

    public void setConfiguration(options data) throws IOException {
        if (clientConfig != null) {
            final LoadConfigRequest req = clientConfig.newMessage();
            req.config.code = data.value;
            final ResponseFuture<LoadConfigResponse> res = new ResponseFuture<>();
            clientConfig.call(req, res);

            LoadConfigResponse tmp = null;
            try {
                tmp = res.get();
                logger.fatal(tmp);
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException(e);
            }
        }

    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        clientConfig = connectedNode.newServiceClient(topic, LoadConfig._TYPE);
        initialized = true;
        logger.fatal("on start " + getClass().getSimpleName());
    }

    @Override
    public void destroyNode() {
        if (clientConfig != null) clientConfig.shutdown();
    }

}

package de.unibi.citec.clf.bonsai.ros.actuators;

import com.github.rosjava_actionlib.ActionClient;
import de.unibi.citec.clf.bonsai.actuators.MotionControlActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import java.util.ArrayList;
import java.util.List;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import pepper_clf_msgs.*;

import java.util.Map;
import java.util.concurrent.Future;
import naoqi_bridge_msgs.JointAnglesWithSpeed;
import org.ros.node.topic.Publisher;
import sensor_msgs.JointState;

/**
 * @author jkummert
 */
public class RosNaoQiMotionControlActuator extends RosNode implements MotionControlActuator {

    String topic;
    private GraphName nodeName;
    private Publisher<JointState> publisher;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosNaoQiMotionControlActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(topic, JointState._TYPE);
        initialized = true;
        logger.debug("on start, RosNaoQiMotionControlActuator done");
    }

    @Override
    public void destroyNode() {
        if (publisher != null) {
            publisher.shutdown();
        }
    }

    @Override
    public Future<Boolean> enableCorrection(boolean enable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStiffness(Map<String, Double> joints) {
        JointState msg = publisher.newMessage();

        List<String> names = new ArrayList<>();
        double[] efforts = new double[joints.keySet().size()];

        int i = 0;
        for (String name : joints.keySet()) {
            names.add(name);
            efforts[i] = joints.get(name);
            i++;
        }

        msg.setName(names);
        msg.setEffort(efforts);

        publisher.publish(msg);
    }
}

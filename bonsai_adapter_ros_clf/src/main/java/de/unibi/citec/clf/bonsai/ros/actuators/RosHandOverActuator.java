package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalStatus;
import actionlib_msgs.GoalStatusArray;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.bonsai.actuators.HandOverActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import hand_over_msgs.HandOverActionFeedback;
import hand_over_msgs.HandOverActionGoal;
import hand_over_msgs.HandOverActionResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import org.ros.message.Duration;

/**
 * Created by lruegeme
 */
public class RosHandOverActuator extends RosNode implements HandOverActuator {

    String serverTopic;
    private GraphName nodeName;

    private ActionClient<HandOverActionGoal, HandOverActionFeedback, HandOverActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosHandOverActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serverTopic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.serverTopic, HandOverActionGoal._TYPE, HandOverActionFeedback._TYPE, HandOverActionResult._TYPE);
        if (ac.waitForActionServerToStart(new Duration(2.0))) {
            initialized = true;
            logger.debug("RosHandOverActuator started");
        } else {
            logger.debug("RosHandOverActuatortimeout after 2sec " + this.serverTopic);
        }

    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }



    @Override
    public Future<Boolean> handOver(String group_name, byte type) throws IOException {
        if (!ac.waitForActionServerToStart(new Duration(1))) {
            logger.error("action server not started on " + this.serverTopic);
        }

        HandOverActionGoal goalMessage = ac.newGoalMessage();
        goalMessage.getGoal().setGroupName(group_name);
        goalMessage.getGoal().setType(type);
        logger.info("sending hand_over goal for group " + group_name + " with type " + type + " and id:" + goalMessage.getGoalId());
        return ac.sendGoal(goalMessage).toBooleanFuture();
    }



}

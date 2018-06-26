
package de.unibi.citec.clf.bonsai.ros.actuators;



import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;
import actionlib_msgs.GoalStatusArray;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.bonsai.actuators.GuidingActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import force_guiding_msgs.GuidingActionFeedback;
import force_guiding_msgs.GuidingActionGoal;
import force_guiding_msgs.GuidingActionResult;
import org.ros.message.Duration;
import org.ros.node.topic.Publisher;

/**
 *
 * @author llach
 */

//TODO add msgs package, use new actionlib stuff

public class RosGuidingActuator extends RosNode implements GuidingActuator, ActionClientListener<GuidingActionFeedback, GuidingActionResult> {

    String serverTopic;
    private GraphName nodeName;
    private ActionClient<GuidingActionGoal, GuidingActionFeedback, GuidingActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private Publisher<GoalID> cancelPublisher = null;
    private GoalID currentgoal;

    public RosGuidingActuator(GraphName gn) {
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
        ac = new ActionClient(connectedNode, this.serverTopic, GuidingActionGoal._TYPE, GuidingActionFeedback._TYPE, GuidingActionResult._TYPE);
        cancelPublisher = connectedNode.newPublisher(nodeName.toString() + "/cancel", GoalID._TYPE);
        ac.attachListener(this);
        initialized = true;
        logger.fatal("on start, RosGuidingActuator done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }


    @Override
    public Future<Boolean> startGuiding() throws IOException {

        if (!ac.waitForActionServerToStart(new Duration(1))) {
            logger.error("action server not started on " + this.serverTopic);
        }

        GuidingActionGoal goalMessage = ac.newGoalMessage();
        currentgoal = goalMessage.getGoalId();
        logger.info("sending guiding goal id:" + goalMessage.getGoalId());
        return ac.sendGoal(goalMessage).toBooleanFuture();
    }

    @Override
    public void stopGuiding() throws IOException {

        ac.sendCancel(currentgoal);

        logger.info("cancelling guiding goal");

    }

    @Override
    public void resultReceived(GuidingActionResult message) {
        logger.trace("!!!! result recieved  !!!!");
    }

    @Override
    public void feedbackReceived(GuidingActionFeedback message) {
        logger.trace("feedback recieved");
    }

    @Override
    public void statusReceived(GoalStatusArray status) {
        logger.trace("status recieved ");
        List<GoalStatus> statusList = status.getStatusList();
        for(GoalStatus gs:statusList) {
            //logger.fatal("GoalID: " + gs.getGoalId().getId() + " -- GoalStatus: " + gs.getStatus() + " -- " + gs.getText());
        }
    }
}

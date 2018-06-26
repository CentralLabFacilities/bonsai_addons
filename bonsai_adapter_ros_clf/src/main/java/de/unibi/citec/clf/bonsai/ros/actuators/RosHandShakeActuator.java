package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalStatus;
import actionlib_msgs.GoalStatusArray;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.bonsai.actuators.HandShakeActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import hand_shaker_msgs.ShakeHandActionFeedback;
import hand_shaker_msgs.ShakeHandActionGoal;
import hand_shaker_msgs.ShakeHandActionResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import org.ros.node.topic.Publisher;

//import hand_shaker.ShakeHandGoal;
import org.ros.message.Duration;

/**
 * Created by lruegeme on 3/14/17.
 */
//TODO add msgs package, use new actionlib stuff
public class RosHandShakeActuator extends RosNode implements HandShakeActuator, ActionClientListener<ShakeHandActionFeedback, ShakeHandActionResult> {

    String serverTopic;
    private GraphName nodeName;
    //todo actionlib
    //private Publisher<ShakeHandGoal> publisher;
    private ActionClient<ShakeHandActionGoal, ShakeHandActionFeedback, ShakeHandActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosHandShakeActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) {
        this.serverTopic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        //publisher = connectedNode.newPublisher(serverTopic, hand_shaker.ShakeHandGoal._TYPE);
        ac = new ActionClient(connectedNode, this.serverTopic, ShakeHandActionGoal._TYPE, ShakeHandActionFeedback._TYPE, ShakeHandActionResult._TYPE);
        ac.attachListener(this);
        initialized = true;
        logger.fatal("on start, RosHandShakeActuator done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
        //publisher.shutdown();
    }

    @Override
    public void simpleShakeHand() throws IOException {
        //if (publisher != null) {
        //    hand_shaker.ShakeHandGoal goal = publisher.newMessage();
        //    goal.setGroupName("right_arm");
        //    publisher.publish(goal);
        //    logger.info("published " + goal);
        //} else {
        //    throw new IOException("publisher not initialized");
        //}
    }

    @Override
    public Future<Boolean> shakeHand() throws IOException {

        if (!ac.waitForActionServerToStart(new Duration(1))) {
            logger.error("action server not started on " + this.serverTopic);
        }

        //running = true;
        //done = false;

        logger.fatal("CURRENT GOAL STATE: " + ac.getGoalState());
        ac.attachListener(this);

        ShakeHandActionGoal goalMessage = ac.newGoalMessage();
        goalMessage.getGoal().setGroupName("right_arm");
        logger.info("sending shake_hand goal id:" + goalMessage.getGoalId());
        return ac.sendGoal(goalMessage).toBooleanFuture();
    }

    @Override
    public void resultReceived(ShakeHandActionResult message) {
        logger.trace("!!!! result recieved  !!!!");
    }

    @Override
    public void feedbackReceived(ShakeHandActionFeedback message) {
        logger.trace("feedback recieved");
    }

    @Override
    public void statusReceived(GoalStatusArray status) {
        logger.trace("status recieved ");
        List<GoalStatus> statusList = status.getStatusList();
        for(GoalStatus gs:statusList) {
            //logger.fatal("GoalID: " + gs.getGoalId().getId() + " -- GoalStatus: " + gs.getStatus() + " -- " + gs.getText());
        }
        //logger.fatal("Current state of our goal: " + ClientStateMachine.ClientStates.translateState(ac.getGoalState()));
    }
}

package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatusArray;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.bonsai.actuators.FollowPeopleActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import org.ros.message.Duration;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import pepper_clf_msgs.*;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RosFollowPeopleActuator extends RosNode implements FollowPeopleActuator, ActionClientListener<FollowPersonActionFeedback, FollowPersonActionResult> {

    String topic;
    private GraphName nodeName;
    private ActionClient<FollowPersonActionGoal, FollowPersonActionFeedback, FollowPersonActionResult> ac = null;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private GoalID id;

    public RosFollowPeopleActuator(GraphName gn){
        initialized = false;
        this.nodeName = gn;
    }


    @Override
    public Future<Boolean> startFollowing(String uuid) throws InterruptedException, ExecutionException {
        if (!ac.waitForActionServerToStart(new Duration(1))) {
            logger.error("action server not started on " + this.topic);
        }
        FollowPersonActionGoal goalMessage = ac.newGoalMessage();
        FollowPersonGoal goal = goalMessage.getGoal();
        goal.setUuid(uuid);
        id = goalMessage.getGoalId();

        Future<Boolean> res = ac.sendGoal(goalMessage).toBooleanFuture();
        return res;
    }

    @Override
    public void cancel() {
        ac.sendCancel(id);
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.topic, FollowPersonActionGoal._TYPE, FollowPersonActionFeedback._TYPE, FollowPersonActionResult._TYPE);
        ac.attachListener(this);
        initialized = true;
        logger.fatal("on start, FollowActuator done");

    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void resultReceived(FollowPersonActionResult t) {

        logger.trace("result recieved");

    }

    @Override
    public void feedbackReceived(FollowPersonActionFeedback t) {

        logger.trace("feedback recieved");

    }

    @Override
    public void statusReceived(GoalStatusArray gsa) {

        //logger.trace("status recieved ");
    }
}

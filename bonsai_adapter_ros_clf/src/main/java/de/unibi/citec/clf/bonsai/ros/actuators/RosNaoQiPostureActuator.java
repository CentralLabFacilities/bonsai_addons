package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalStatus;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.PostureActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import pepper_clf_msgs.*;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ffriese
 */
public class RosNaoQiPostureActuator extends RosNode implements PostureActuator {

    String topic;
    private GraphName nodeName;
    private ActionClient<AnimationActionGoal, AnimationActionFeedback, AnimationActionResult> ac;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private int timeoutInSeconds = 5; // return if there was no action-feedback for that many seconds

    public RosNaoQiPostureActuator(GraphName gn) {
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
        ac = new ActionClient(connectedNode, this.topic, AnimationActionGoal._TYPE, AnimationActionFeedback._TYPE, AnimationActionResult._TYPE);
        initialized = true;
        logger.debug("on start, RosNaoQiSSLActuator done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
    }


    @Override
    public Future<Boolean> executeMotion(@Nonnull String animation, @Nullable String group) {
        AnimationActionGoal msg = ac.newGoalMessage();
        msg.getGoal().setAnimationName(animation);
        ActionFuture<AnimationActionGoal, AnimationActionFeedback, AnimationActionResult> fut = ac.sendGoal(msg);

        return new Future<Boolean>() {

            org.ros.message.Time lastFeedback = org.ros.message.Time.fromMillis(Time.currentTimeMillis());

            @Override
            public boolean cancel(boolean b) {
                return fut.cancel(b);
            }

            @Override
            public boolean isCancelled() {
                return fut.isCancelled();
            }

            @Override
            public boolean isDone() {
                AnimationActionFeedback fb = fut.getLatestFeedback();
                if(fb != null) lastFeedback = fb.getHeader().getStamp();
                return org.ros.message.Time.fromMillis(Time.currentTimeMillis()).subtract(lastFeedback).secs>timeoutInSeconds
                        || fut.isDone();
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                return fut.get().getStatus().getStatus() == GoalStatus.SUCCEEDED;
            }

            @Override
            public Boolean get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return fut.get(l, timeUnit).getStatus().getStatus() == GoalStatus.SUCCEEDED;
            }
        };
    }

    @Override
    public List<String> listMotions(@Nullable String group) {
        return null;
    }

    @Override
    public Future<Boolean> assumePose(String pose, String group) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> isInPose(@NotNull String pose, @org.jetbrains.annotations.Nullable String group) {
        throw new NotImplementedException();
    }
}

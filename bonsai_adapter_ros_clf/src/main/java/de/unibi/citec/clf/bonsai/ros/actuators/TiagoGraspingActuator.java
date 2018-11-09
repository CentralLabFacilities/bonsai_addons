package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalID;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.GraspActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import org.apache.log4j.Logger;
import org.ros.message.Duration;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import clf_grasping_msgs.GraspItemActionFeedback;
import clf_grasping_msgs.GraspItemActionGoal;
import clf_grasping_msgs.GraspItemActionResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author lruegeme
 */

public class TiagoGraspingActuator extends RosNode implements GraspActuator {


    class MoveitResultFuture implements Future<MoveitResult> {

        private ActionFuture<GraspItemActionGoal, GraspItemActionFeedback, GraspItemActionResult> af;

        MoveitResultFuture(ActionFuture<GraspItemActionGoal, GraspItemActionFeedback, GraspItemActionResult> future) {
            this.af = future;
        }

        @Override
        public boolean cancel(boolean b) {
            return af.cancel(b);
        }

        @Override
        public boolean isCancelled() {
            return af.isCancelled();
        }

        @Override
        public boolean isDone() {
            return af.isDone();
        }

        @Override
        public MoveitResult get() throws InterruptedException, ExecutionException {
            return MoveitResult.getById(af.get().getResult().getErrorCode());
        }

        @Override
        public MoveitResult get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            int error = af.get(l,timeUnit).getResult().getErrorCode();
            return MoveitResult.getById(error);
        }
    }

    private static final Logger logger = Logger.getLogger(TiagoGraspingActuator.class);
    private String serverTopic; //tiago_mtc
    private GraphName nodeName;
    private String METHOD_GRASP = "grasp_object";
    private ActionClient<GraspItemActionGoal, GraspItemActionFeedback, GraspItemActionResult> ac;
    private @Nullable
    GoalID lastAcGoalId;

    public TiagoGraspingActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.serverTopic + "/" + METHOD_GRASP, GraspItemActionGoal._TYPE, GraspItemActionFeedback._TYPE, GraspItemActionResult._TYPE);
        lastAcGoalId = null;

        if (ac.waitForActionServerToStart(new Duration(2.0))) {
            initialized = true;
            logger.debug("RosMoveBase NavAct started");
        }
    }

    @Override
    public void destroyNode() {

    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public Future<MoveitResult> graspObject(@Nonnull ObjectShapeData osd, @Nullable String group) throws IOException {
        return graspObject(osd.getId(), group);
    }

    @Override
    public Future<MoveitResult> graspObject(@Nonnull String objectId, @Nullable String group) throws IOException {
        GraspItemActionGoal msg = ac.newGoalMessage();
        msg.getGoal().setId(objectId);
        lastAcGoalId = msg.getGoalId();
        ActionFuture<GraspItemActionGoal, GraspItemActionFeedback, GraspItemActionResult> fut = this.ac.sendGoal(msg);

        return new MoveitResultFuture(fut);
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serverTopic = conf.requestValue("topic");
        this.METHOD_GRASP = conf.requestOptionalValue("method_GraspItem", METHOD_GRASP);
    }
}
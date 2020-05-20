
package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import actionlib_msgs.GoalStatusArray;
import augmented_manipulation_msgs.AugmentedPickupActionFeedback;
import augmented_manipulation_msgs.AugmentedPickupActionGoal;
import augmented_manipulation_msgs.AugmentedPickupActionResult;
import augmented_manipulation_msgs.GraspConfigSet;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionClientListener;
import de.unibi.citec.clf.bonsai.actuators.PicknPlaceActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import org.apache.commons.lang.NotImplementedException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType;
import de.unibi.citec.clf.btl.data.grasp.KatanaGripperData;
import de.unibi.citec.clf.btl.data.object.GraspConfig;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import org.apache.log4j.Logger;
import org.ros.message.Time;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author ffriese
 */

//TODO add msgs package, use new actionlib stuff
public class RosPicknPlaceActuator extends RosNode implements PicknPlaceActuator,
        ActionClientListener<AugmentedPickupActionFeedback, AugmentedPickupActionResult> {

    private static final Logger logger = Logger.getLogger(RosPicknPlaceActuator.class);

    private static final String METHOD_GETSURFACE = "getSurfaceByHeight";
    private static final String METHOD_PLAN_MOVE = "planToPose";
    private static final String METHOD_MOVE_JOINTS = "moveJoints";
    private static final String METHOD_LIST_ANGLES = "listAngles";
    private static final String METHOD_LIST_POSES = "listPoses";
    private static final String METHOD_FIND_NEAREST_POSE = "findNearestPose";
    private static final String METHOD_MOTORS_ON = "motorsOn";
    private static final String METHOD_MOTORS_OFF = "motorsOff";
    // private static final String METHOD_SET_MOVEMENTS = "setPose";
    private static final String METHOD_OPEN_GRIPPER = "openGripper";
    private static final String METHOD_CLOSE_GRIPPER = "closeGripper";
    // private static final String METHOD_CLOSE_GRIPPER_BY_FORCE = "closeGripperByForce";
    private static final String METHOD_OPEN_GRIPPER_WHEN_TOUCHING = "openGripperWhenTouching";
    private static final String METHOD_IS_SOMESTHING_IN_GRIPPER = "isSomethingInGripper";
    private static final String METHOD_FREEZE = "freeze";
    private static final String METHOD_UNBLOCK = "unblock";
    private static final String METHOD_GOTO = "goto";
    private static final String METHOD_GET_GRIPPER_SENSORS = "getGripperSensors";
    // private static final String METHOD_GRASP_OBJECT = "graspObjectName";
    private static final String METHOD_PLACE_OBJECT_ON_SURFACE = "placeObjectOnSurface";
    private static final String METHOD_PLACE_OBJECT_IN_REGION = "placeObjectInRegion";
    private static final String METHOD_FIND_OBJECTS = "findObjects";
    private static final String METHOD_FILTER_GRASPS = "setFilterType";
    private static final double TIMEOUT = 10;

    private ActionClient<AugmentedPickupActionGoal, AugmentedPickupActionFeedback, AugmentedPickupActionResult> ac;

    private GraspReturnType grasp;
    private KatanaGripperData katanaGripperData;
    private List<Double> sensorData;

    private String actionTopic;
    private final GraphName nodeName;
    private ConnectedNode node;

    public RosPicknPlaceActuator(GraphName gn) {
        this.ac = null;
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.actionTopic = conf.requestValue("action_topic");
    }
    
    @Override
    public void feedbackReceived(AugmentedPickupActionFeedback t) {
        System.out.println("received feedback: " + t.getFeedback().getState());
    }

    @Override
    public void destroyNode() {
        return;
    }

    @Override
    public Future<MoveitResult> graspObject(@Nonnull ObjectShapeData osd, @Nullable String group) throws IOException {
        return graspObject(osd.getId(),group);
    }

    @Override
    public Future<MoveitResult> graspObject(String objectName, String group) throws IOException {
        Future<GraspReturnType> grt = graspObject(objectName, group, false);
        return new Future<MoveitResult>() {
            @Override
            public boolean cancel(boolean b) {
                return grt.cancel(b);
            }

            @Override
            public boolean isCancelled() {
                return grt.isCancelled();
            }

            @Override
            public boolean isDone() {
                return grt.isDone();
            }

            @Override
            public MoveitResult get() throws InterruptedException, ExecutionException {
                return grt.get().toMoveitResult();
            }

            @Override
            public MoveitResult get(long l, java.util.concurrent.TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return grt.get(l,timeUnit).toMoveitResult();
            }
        };
    }

    @Override
    public Future<MoveitResult> placeObject(@Nonnull String supportSurface, @Nullable String group) throws IOException {
        throw new NotImplementedException("use placeObjectOnSurface/placeObjectOnSurface/placeObjectOn methods");
    }

    @Override
    public Future<MoveitResult> placeObject(@Nonnull Pose3D position, @Nullable String supportSurface, @Nullable String group) throws IOException {
        throw new NotImplementedException("use placeObjectOnSurface/placeObjectOnSurface/placeObjectOn methods");
    }

    @Override
    public Future<MoveitResult> placeObjectOnArea(@Nonnull BoundingBox3D area, @Nullable String supportSurface, @Nullable String group) throws IOException {
        throw new NotImplementedException("use placeObjectOnSurface/placeObjectOnSurface/placeObjectOn methods");
    }

    @Override
    public Future<MoveitResult> placeObjectInArea(@Nonnull BoundingBox3D area, @Nullable String group) throws IOException {
        throw new NotImplementedException("use placeObjectOnSurface/placeObjectOnSurface/placeObjectOn methods");
    }

    @Override
    public Future<GraspReturnType> isObjectGraspable(String objectName, String group) throws IOException {
        return graspObject(objectName, group, true);
    }

    public Future<GraspReturnType> graspObject(String objectName, String group, boolean planOnly) throws IOException {

        AugmentedPickupActionGoal goal = ac.newGoalMessage();

        goal.getGoal().setAllowedPlanningTime(10.0);
        goal.getGoal().setObjectName(objectName);
        goal.getGoal().getPlanningOptions().setPlanOnly(planOnly);
        goal.getGoal().getPlanningOptions().setLookAround(false);
        goal.getGoal().getPlanningOptions().setReplan(false);
        goal.getGoal().getPlanningOptions().setReplanDelay(2.0);

        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getRobotState().getJointState().getHeader().setSeq(0);
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getRobotState().getJointState().getHeader().setFrameId("none");
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getRobotState().getMultiDofJointState().getHeader().setSeq(0);
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getRobotState().getMultiDofJointState().getHeader().setFrameId("none");
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getWorld().getOctomap().getHeader().setSeq(0);
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getWorld().getOctomap().getHeader().setFrameId("none");
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getWorld().getOctomap().getOctomap().getHeader().setSeq(0);
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getWorld().getOctomap().getOctomap().getHeader().setFrameId("none");

        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().setIsDiff(true);
        goal.getGoal().getPlanningOptions().getPlanningSceneDiff().getRobotState().setIsDiff(true);

        GraspConfig graspConf = new GraspConfig();
        graspConf.setFrameId("none");
        
        if (group.contains("left")){
               graspConf.setGroupName("left_arm");
               graspConf.setConfigName("left_hand");
           } else {
               graspConf.setGroupName("right_arm");
               graspConf.setConfigName("right_hand");
           }

        try {
            GraspConfigSet graspConfigSet = MsgTypeFactory.getInstance().createMsg(graspConf, augmented_manipulation_msgs.GraspConfigSet._TYPE);
            goal.getGoal().getGraspConfigSets().add(graspConfigSet);

        } catch (RosSerializer.SerializationException ex) {
            java.util.logging.Logger.getLogger(RosPicknPlaceActuator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        goal.getHeader().setSeq(0);
        goal.getHeader().setFrameId("none");
        goal.getHeader().setStamp(Time.fromMillis(Instant.now().toEpochMilli()));

        grasp = new GraspReturnType();
        Future<Boolean> ret = ac.sendGoal(goal).toBooleanFuture();
        Future<GraspReturnType> fut = new Future<GraspReturnType>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return ret.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return ret.isCancelled();
            }

            @Override
            public boolean isDone() {
                return ret.isDone();
            }

            @Override
            public GraspReturnType get() throws InterruptedException, ExecutionException {
                return grasp;
            }

            @Override
            public GraspReturnType get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return grasp;
            }

        };
        return fut;

    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        ac = new ActionClient(connectedNode, this.actionTopic,
                AugmentedPickupActionGoal._TYPE,
                AugmentedPickupActionFeedback._TYPE,
                AugmentedPickupActionResult._TYPE);

        ac.attachListener(this);

        initialized = true;

    }

    //ActionclientListener stuff
    @Override
    public void statusReceived(GoalStatusArray gsa) {

        //   System.out.println("received status" + gsa.getHeader());
    }

    @Override
    public void resultReceived(AugmentedPickupActionResult t) {
        int moveit_error_code = t.getResult().getErrorCode().getVal();
        switch (moveit_error_code) {
            case moveit_msgs.MoveItErrorCodes.SUCCESS:
                grasp.setGraspResult(GraspReturnType.GraspResult.SUCCESS);
                break;
            case moveit_msgs.MoveItErrorCodes.PLANNING_FAILED:
            case moveit_msgs.MoveItErrorCodes.INVALID_MOTION_PLAN:
                grasp.setGraspResult(GraspReturnType.GraspResult.POSITION_UNREACHABLE);
                break;
            case moveit_msgs.MoveItErrorCodes.FAILURE:
            case moveit_msgs.MoveItErrorCodes.CONTROL_FAILED:
                grasp.setGraspResult(GraspReturnType.GraspResult.ROBOT_CRASHED);
                break;
            case moveit_msgs.MoveItErrorCodes.MOTION_PLAN_INVALIDATED_BY_ENVIRONMENT_CHANGE:
                System.out.println("MOVEIT ENVIRONMENT CHANGE");
                grasp.setGraspResult(GraspReturnType.GraspResult.NO_RESULT);
                break;
            case moveit_msgs.MoveItErrorCodes.NO_IK_SOLUTION:
                System.out.println("MOVEIT NO IK SOLUTION");
                grasp.setGraspResult(GraspReturnType.GraspResult.NO_RESULT);
                break;
            case moveit_msgs.MoveItErrorCodes.TIMED_OUT:
                System.out.println("MOVEIT TIMEOUT");
                grasp.setGraspResult(GraspReturnType.GraspResult.FAIL);
                break;
            default:
                System.out.println("MOVEIT ERROR CODE:" + moveit_error_code);
                grasp.setGraspResult(GraspReturnType.GraspResult.FAIL);
        }

    }
    
    @Override
    public Future<GraspReturnType> placeObjectOnSurface(float heigth) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<GraspReturnType> placeObjectOn(ObjectShapeData region) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void filterGrasps(String filter) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Future<List<String>> listPoses() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<String> findNearestPose() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Pose3D> getPosition() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Void> moveJoint(int joint, double value) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<List<Double>> listJoints() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> goTo(Pose3D pose) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> directMovement(String name) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> planMovement(String name) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void motorsOff() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void motorsOn() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openGripper() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeGripper() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openGripperWhenTouching(int waitSeconds) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<KatanaGripperData> getGipperSensorData() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeGripperByForce() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void freeze() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unblock() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<GraspReturnType> placeObjectOnSurface(String surfaceName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<Boolean> isSomethingInGripper() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fitObjectsToPrimitives() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

package de.unibi.citec.clf.bonsai.ros.actuators;

import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;
import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import de.unibi.citec.clf.bonsai.actuators.GazeActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;

import de.unibi.citec.clf.btl.data.geometry.Point3D;
import org.ros.message.Duration;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.ros.node.topic.Publisher;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import naoqi_bridge_msgs.JointAnglesWithSpeed;
import naoqi_bridge_msgs.JointTrajectoryActionFeedback;
import naoqi_bridge_msgs.JointTrajectoryActionGoal;
import naoqi_bridge_msgs.JointTrajectoryActionResult;
import naoqi_bridge_msgs.JointTrajectoryGoal;
import org.ros.message.MessageFactory;
import trajectory_msgs.JointTrajectory;
import trajectory_msgs.JointTrajectoryPoint;

/**
 *
 * @author jkummert
 */
public class RosGazeActuator extends RosNode implements GazeActuator {

    String topic;
    String actionTopic;
    float maxPitch;
    float maxYaw;
    float minPitch;
    float minYaw;
    private GraphName nodeName;
    private Publisher<JointAnglesWithSpeed> publisher;
    private ActionClient<JointTrajectoryActionGoal, JointTrajectoryActionFeedback, JointTrajectoryActionResult> ac;
    private GoalID last_ac_goal_id;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private List<String> jointNames;
    MessageFactory msgFactory;

    public RosGazeActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(topic, JointAnglesWithSpeed._TYPE);
        ac = new ActionClient(connectedNode, this.actionTopic, JointTrajectoryActionGoal._TYPE, JointTrajectoryActionFeedback._TYPE, JointTrajectoryActionResult._TYPE);
        last_ac_goal_id = null;

        jointNames = new ArrayList<>();
        jointNames.add("HeadPitch");
        jointNames.add("HeadYaw");

        msgFactory = connectedNode.getTopicMessageFactory();
        initialized = true;
        logger.fatal("on start, RosGazeActuator done");
    }

    @Override
    public void destroyNode() {
        if(publisher!=null) publisher.shutdown();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    /**
     * Publishes a gaze target to the motion controller. Use this if you want to
     * set multiple goals in a short span of time and do not care about the
     * exact execution and timing of each goal
     *
     * @param pitch
     * @param yaw
     */
    @Override
    public void setGazeTarget(float pitch, float yaw) {
        this.setGazeTarget(pitch, yaw, 0.125f);
    }

    @Override
    public void setGazeTarget(float pitch, float yaw, float speed) {
        pitch = pitch < 0 ? Math.max(pitch, minPitch) : Math.min(pitch, maxPitch);
        yaw = yaw < 0 ? Math.max(yaw, minYaw) : Math.min(yaw, maxYaw);
        Map<String, Float> joints = new HashMap<>();
        joints.put("HeadPitch", pitch);
        joints.put("HeadYaw", yaw);
        setGazeTarget(joints, false, speed);
    }

    @Override
    public void setGazeTargetPitch(float pitch){
        pitch = pitch < 0 ? Math.max(pitch, minPitch) : Math.min(pitch, maxPitch);
        Map<String, Float> joints = new HashMap<>();
        joints.put("HeadPitch", pitch);
        setGazeTarget(joints, false);
    }

    @Override
    public void setGazeTargetYaw(float yaw){
        yaw = yaw < 0 ? Math.max(yaw, minYaw) : Math.min(yaw, maxYaw);
        Map<String, Float> joints = new HashMap<>();
        joints.put("HeadYaw", yaw);
        setGazeTarget(joints, false);
    }

    public void setGazeTarget(Map<String, Float> joints, boolean relative, float speed){
        JointAnglesWithSpeed msg = publisher.newMessage();

        List<String> names = new ArrayList<>();
        float[] angles = new float[joints.keySet().size()];

        int i =0;
        for(String name: joints.keySet()){
            names.add(name);
            angles[i] =joints.get(name);
            i++;
        }
        msg.setJointNames(names);
        msg.setJointAngles(angles);
        msg.setRelative((byte) (relative? 1 : 0));
        msg.setSpeed(speed);

        publisher.publish(msg);
    }

    public void setGazeTarget(Map<String, Float> joints, boolean relative){
        this.setGazeTarget(joints, relative,0.125f);
    }

    @Override
    public Future<Void> lookAt(Point3D pose) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Future<Void> lookAt(Point3D pose, long duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void configure(IObjectConfigurator ioc) throws ConfigurationException {
        this.topic = ioc.requestValue("topic");
        this.actionTopic = ioc.requestValue("actionTopic");
        this.maxPitch = (float) ioc.requestDouble("maxPitch");
        this.minPitch = (float) ioc.requestDouble("minPitch");
        this.maxYaw = (float) ioc.requestDouble("maxYaw");
        this.minYaw = (float) ioc.requestDouble("minYaw");
    }

    /**
     * Send a gaze target to the motion controller through an action server. Use
     * this if you care about feedback and timing of your goal.
     *
     * @param pitch
     * @param yaw
     * @return
     */
    @Override
    public Future<Boolean> setGazeTargetAsync(float pitch, float yaw, float duration) {
        pitch = pitch < 0 ? Math.max(pitch, minPitch) : Math.min(pitch, maxPitch);
        yaw = yaw < 0 ? Math.max(yaw, minYaw) : Math.min(yaw, maxYaw);
        Map<String, Double> joints = new HashMap<>();
        joints.put("HeadPitch", (double) pitch);
        joints.put("HeadYaw", (double) yaw);
        return setGazeTargetAsync(joints, duration);
    }

    @Override
    public Future<Boolean> setGazeTargetPitchAsync(float pitch, float duration) {
        pitch = pitch < 0 ? Math.max(pitch, minPitch) : Math.min(pitch, maxPitch);
        Map<String, Double> joints = new HashMap<>();
        joints.put("HeadPitch", (double) pitch);
        return setGazeTargetAsync(joints, duration);
    }

    @Override
    public Future<Boolean> setGazeTargetYawAsync(float yaw, float duration) {
        yaw = yaw < 0 ? Math.max(yaw, minYaw) : Math.min(yaw, maxYaw);
        Map<String, Double> joints = new HashMap<>();
        joints.put("HeadYaw", (double) yaw);
        return setGazeTargetAsync(joints, duration);
    }

    public Future<Boolean> setGazeTargetAsync(Map<String, Double> joints, float duration) {
        JointTrajectoryActionGoal msg = ac.newGoalMessage();
        JointTrajectoryGoal goal = msg.getGoal();

        List<String> names = new ArrayList<>();
        double[] angles = new double[joints.keySet().size()];

        int i =0;
        for(String name: joints.keySet()){
            names.add(name);
            angles[i] =joints.get(name);
            i++;
        }
        JointTrajectory traj = goal.getTrajectory();
        traj.setJointNames(names);
        List<JointTrajectoryPoint> trajPoints = traj.getPoints();

        JointTrajectoryPoint trajPoint = msgFactory.newFromType(trajectory_msgs.JointTrajectoryPoint._TYPE);
        double[] trajPointsArray = angles;
        trajPoint.setPositions(trajPointsArray);
        int duration_secs = (int) duration;
        int duration_nsecs = (int) ((duration - duration_secs) * 1000000000); // converts seconds to nano seconds
        Duration move_duration = new Duration(duration_secs, duration_nsecs);
        trajPoint.setTimeFromStart(move_duration);
        trajPoints.add(trajPoint);

        last_ac_goal_id = msg.getGoalId();
        ActionFuture<JointTrajectoryActionGoal, JointTrajectoryActionFeedback, JointTrajectoryActionResult> fut = this.ac.sendGoal(msg);
        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean bln) {
                return fut.cancel(bln);
            }

            @Override
            public boolean isCancelled() {
                return fut.isCancelled();
            }

            @Override
            public boolean isDone() {
                return fut.isDone();
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                return fut.get().getStatus().getStatus() == GoalStatus.SUCCEEDED;
            }

            @Override
            public Boolean get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                return fut.get(l, tu).getStatus().getStatus() == GoalStatus.SUCCEEDED;
            }
        };
    }

    @Override
    public Future<Boolean> setGazeTargetAsync(float pitch, float yaw) {
        return this.setGazeTargetAsync(pitch, yaw, 2);  // default value for head move duration is 2 sec
    }
    
    @Override
    public void manualStop() throws IOException {
        if (last_ac_goal_id != null) {
            ac.sendCancel(last_ac_goal_id);
        }
    }
}

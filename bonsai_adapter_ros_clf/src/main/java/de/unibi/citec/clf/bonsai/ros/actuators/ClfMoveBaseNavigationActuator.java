package de.unibi.citec.clf.bonsai.ros.actuators;

import com.github.rosjava_actionlib.ActionClient;
import com.github.rosjava_actionlib.ActionFuture;
import actionlib_msgs.GoalID;

import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;
import de.unibi.citec.clf.bonsai.actuators.NavigationActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.navigation.CommandResult;
import de.unibi.citec.clf.btl.data.navigation.DriveData;
import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
import de.unibi.citec.clf.btl.data.navigation.TurnData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.RotationalSpeedUnit;
import de.unibi.citec.clf.btl.units.SpeedUnit;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.ros.MsgTypeFactory;
import de.unibi.citec.clf.btl.ros.RosSerializer;
import geometry_msgs.PoseStamped;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import move_base_msgs.*;
import nav_msgs.GetPlan;
import nav_msgs.GetPlanRequest;
import nav_msgs.GetPlanResponse;
import org.ros.node.service.ServiceResponseListener;
import std_srvs.EmptyRequest;
import std_srvs.EmptyResponse;
import std_srvs.Empty;
import std_msgs.Header;

import javax.vecmath.Quat4d;

import org.ros.node.topic.Publisher;

/**
 * @author llach
 * @author ffriese
 */
public class ClfMoveBaseNavigationActuator extends RosMoveBaseNavigationActuator implements NavigationActuator {

    String drive_direct_topic;
    String costmap_topic;
    String make_plan_topic;
    private GraphName nodeName;
    private ActionClient<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult> ac;
    private ActionClient<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult> direct_ac;
    ServiceClient<GetPlanRequest, GetPlanResponse> getPlanClient;
    private GoalID last_ac_goal_id;
    private GoalID last_direct_ac_goal_id;
    private ServiceClient<EmptyRequest, EmptyResponse> sc;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    private Publisher<PoseStamped> testPub;

    public ClfMoveBaseNavigationActuator(GraphName gn) {
        super(gn);
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
        this.drive_direct_topic = conf.requestValue("driveDirectTopic");
        this.costmap_topic = conf.requestValue("costmapTopic");
        this.make_plan_topic = conf.requestValue("makePlanTopic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        ac = new ActionClient(connectedNode, this.topic, MoveBaseActionGoal._TYPE, MoveBaseActionFeedback._TYPE, MoveBaseActionResult._TYPE);
        direct_ac = new ActionClient(connectedNode, this.drive_direct_topic, MoveBaseActionGoal._TYPE, MoveBaseActionFeedback._TYPE, MoveBaseActionResult._TYPE);
        last_ac_goal_id = null;
        last_direct_ac_goal_id = null;
        testPub = connectedNode.newPublisher("/debug", PoseStamped._TYPE);
        try {
            sc = connectedNode.newServiceClient(this.costmap_topic, Empty._TYPE);
        } catch (ServiceNotFoundException e) {
            logger.error(e.getMessage());
            return;
        }
        try {
            getPlanClient = connectedNode.newServiceClient(make_plan_topic, GetPlan._TYPE);
        } catch (ServiceNotFoundException e) {
            logger.error(e.getMessage());
            return;
        }
        initialized = true;
        logger.debug("on start, RosMoveBaseNav done");
    }

    @Override
    public void destroyNode() {
        if(ac!=null) ac.finish();
        if(direct_ac!=null) direct_ac.finish();
        if(sc!=null) sc.shutdown();
    }

    @Override
    public void setGoal(NavigationGoalData data) throws IOException {
        if (data.getFrameId().isEmpty()) {
            data.setFrameId("map");//TODO: may result in errors!
        }
        this.navigateToCoordinate(data);
    }

    @Override
    public GlobalPlan tryGoal(NavigationGoalData data) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Future<GlobalPlan> getPlan(NavigationGoalData data, PositionData startPos) throws IOException {
        final GetPlanRequest req = getPlanClient.newMessage();

        Point startPosition = req.getStart().getPose().getPosition();
        Quaternion startOrientation = req.getStart().getPose().getOrientation();
        Rotation3D startRot = new Rotation3D(0.0, 0.0, 1.0, startPos.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        Quat4d startQ = startRot.getQuaternion();
        startPosition.setX(startPos.getX(LengthUnit.METER));
        startPosition.setY(startPos.getY(LengthUnit.METER));
        startPosition.setZ(0.0);
        startOrientation.setX(startQ.x);
        startOrientation.setY(startQ.y);
        startOrientation.setZ(startQ.z);
        startOrientation.setW(startQ.w);

        Header startHeader = req.getStart().getHeader();
        startHeader.setFrameId(startPos.getFrameId());

        Point goalPosition = req.getGoal().getPose().getPosition();
        Quaternion goalOrientation = req.getGoal().getPose().getOrientation();
        Rotation3D goalRot = new Rotation3D(0.0, 0.0, 1.0, data.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        Quat4d goalQ = goalRot.getQuaternion();
        goalPosition.setX(data.getX(LengthUnit.METER));
        goalPosition.setY(data.getY(LengthUnit.METER));
        goalPosition.setZ(0.0);
        goalOrientation.setX(goalQ.x);
        goalOrientation.setY(goalQ.y);
        goalOrientation.setZ(goalQ.z);
        goalOrientation.setW(goalQ.w);

        Header goalHeader = req.getGoal().getHeader();
        goalHeader.setFrameId(data.getFrameId());

        req.setTolerance((float) data.getCoordinateTolerance(LengthUnit.METER));

        testPub.publish(req.getGoal());
        testPub.publish(req.getStart());

        final ResponseFuture<GetPlanResponse> res = new ResponseFuture<>();
        getPlanClient.call(req, res);

        return new Future<GlobalPlan>() {
            @Override
            public boolean cancel(boolean bln) {
                return res.cancel(bln);
            }

            @Override
            public boolean isCancelled() {
                return res.isCancelled();
            }

            @Override
            public boolean isDone() {
                return res.isDone();
            }

            @Override
            public GlobalPlan get() throws InterruptedException, ExecutionException {
                GlobalPlan plan = new GlobalPlan();
                GetPlanResponse response = null;
                try {
                    response = res.get();
                } catch (InterruptedException | ExecutionException ex) {
                    logger.error("Could not read getplan result", ex);
                }
                if (response == null) {
                    logger.error("get plan response is null, returning empty plan");
                    return plan;
                }
                logger.debug("Plan size: " + response.getPlan().getPoses().size());
                Pose3D pose;
                NavigationGoalData newData;
                for (int i = Math.max(0,response.getPlan().getPoses().size()-11); i < response.getPlan().getPoses().size(); ++i) {
                    try {
                        pose = MsgTypeFactory.getInstance().createType(response.getPlan().getPoses().get(i), Pose3D.class);
                        newData = new NavigationGoalData(NODE_PREFIX, pose.getTranslation().getX(LengthUnit.METER), pose.getTranslation().getY(LengthUnit.METER), pose.getRotation().getYaw(AngleUnit.RADIAN), NavigationGoalData.ReferenceFrame.GLOBAL, LengthUnit.METER, AngleUnit.RADIAN);
                        newData.setFrameId(pose.getFrameId());
                        plan.add(newData);
                    } catch (RosSerializer.DeserializationException ex) {
                        logger.error("Could not deserialize poses of navigation plan", ex);
                    }
                }

                return plan;
            }

            @Override
            public GlobalPlan get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    public void drive(double distance, LengthUnit unit, double speed, SpeedUnit sunit) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void turn(double angle, AngleUnit unit, double speed, RotationalSpeedUnit sunit) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void manualStop() throws IOException {
        if (last_direct_ac_goal_id != null) {
            direct_ac.sendCancel(last_direct_ac_goal_id);
        }
        if (last_ac_goal_id != null) {
            ac.sendCancel(last_ac_goal_id);
        }
    }

    @Override
    public NavigationGoalData getCurrentGoal() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<CommandResult> moveRelative(DriveData drive, TurnData turn) {
        MoveBaseActionGoal msg = direct_ac.newGoalMessage();
        MoveBaseGoal goal = msg.getGoal();
        Point position = goal.getTargetPose().getPose().getPosition();
        Quaternion orientation = goal.getTargetPose().getPose().getOrientation();

        double x, y, angle;
        float move_vel, rot_vel;

        try {
            angle = turn.getAngle(AngleUnit.RADIAN);
        } catch (NullPointerException e) {
            angle = 0.0;
        }

        Rotation3D rot = new Rotation3D(0.0, 0.0, 1.0, angle, AngleUnit.RADIAN);
        Quat4d q = rot.getQuaternion();

        try {
            x = drive.getDirection().getX(LengthUnit.METER) * drive.getDistance(LengthUnit.METER);
        } catch (NullPointerException e) {
            x = 0.0;
        }

        try {
            y = drive.getDirection().getY(LengthUnit.METER) * drive.getDistance(LengthUnit.METER);
        } catch (NullPointerException e) {
            y = 0.0;
        }

        try{
            move_vel = (float)drive.getSpeed(SpeedUnit.METER_PER_SEC);
            goal.setPlanarVelocity(move_vel);
        } catch (NullPointerException e){
            // planar velocity not set. node will use standard velocity
        }

        try{
            rot_vel = (float)turn.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC);
            goal.setAngularVelocity(rot_vel);
        } catch (NullPointerException e){
            // angular velocity not set. node will use standard velocity
        }
        position.setX(x);
        position.setY(y);
        position.setZ(0.0);
        orientation.setX(q.x);
        orientation.setY(q.y);
        orientation.setZ(q.z);
        orientation.setW(q.w);

        last_direct_ac_goal_id = msg.getGoalId();
        ActionFuture<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult> fut = this.direct_ac.sendGoal(msg);
        Future<CommandResult> fcr = new CommandResultFuture(fut);

        return fcr;
    }

    @Override
    public Future<CommandResult> navigateToCoordinate(NavigationGoalData data) {
        MoveBaseActionGoal msg = ac.newGoalMessage();
        MoveBaseGoal goal = msg.getGoal();
        Point position = goal.getTargetPose().getPose().getPosition();
        Quaternion orientation = goal.getTargetPose().getPose().getOrientation();
        Rotation3D rot = new Rotation3D(0.0, 0.0, 1.0, data.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        Quat4d q = rot.getQuaternion();
        position.setX(data.getX(LengthUnit.METER));
        position.setY(data.getY(LengthUnit.METER));
        position.setZ(0.0);
        orientation.setX(q.x);
        orientation.setY(q.y);
        orientation.setZ(q.z);
        orientation.setW(q.w);

        Header h = goal.getTargetPose().getHeader();
        h.setFrameId(data.getFrameId());

        last_ac_goal_id = msg.getGoalId();
        ActionFuture<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult> fut = this.ac.sendGoal(msg);
        Future<CommandResult> fcr = new CommandResultFuture(fut);

        return fcr;
    }

    @Override
    public Future<CommandResult> navigateToInterrupt(NavigationGoalData data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<CommandResult> navigateRelative(NavigationGoalData data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearCostmap() throws IOException {

        EmptyRequest e = sc.newMessage();

        ServiceResponseListener srl = new ServiceResponseListener() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(RemoteException e) {

            }
        };

        sc.call(e, srl);
    }

}

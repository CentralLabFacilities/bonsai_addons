
package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.deprecated.BimanualGraspActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;
import org.apache.log4j.Logger;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Publisher;
import pepper_mtc_msgs.*;
import moveit_msgs.CollisionObject;
import shape_msgs.SolidPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author ffriese
 */

public class RosNaoQiBimanualGraspActuator extends RosNode implements BimanualGraspActuator {

    private static final Logger logger = Logger.getLogger(RosNaoQiBimanualGraspActuator.class);

    String visualizeServiceTopic;
    String executeServiceTopic;
    String planningServiceTopic;
    GraphName nodeName;
    ConnectedNode node;

    private ServiceClient<PepperVisualizeSolutionRequest, PepperVisualizeSolutionResponse> viz_sc;
    private ServiceClient<PepperExecuteSolutionRequest, PepperExecuteSolutionResponse> exec_sc;
    private ServiceClient<PepperFindGraspPlanRequest, PepperFindGraspPlanResponse> plan_sc;
    private Publisher<CollisionObject> objectSpawner;

    public RosNaoQiBimanualGraspActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.executeServiceTopic = conf.requestValue("executeServiceTopic");
        this.visualizeServiceTopic = conf.requestValue("visualizeServiceTopic");
        this.planningServiceTopic = conf.requestValue("planningServiceTopic");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        try {
            plan_sc = connectedNode.newServiceClient(this.planningServiceTopic, PepperFindGraspPlan._TYPE);
            viz_sc = connectedNode.newServiceClient(this.visualizeServiceTopic, PepperVisualizeSolution._TYPE);
            exec_sc = connectedNode.newServiceClient(this.executeServiceTopic, PepperExecuteSolution._TYPE);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }

        objectSpawner = connectedNode.newPublisher("/collision_object", CollisionObject._TYPE);
        initialized = true;
        node = connectedNode;
    }

    @Override
    public void destroyNode() {
       if(plan_sc!=null){plan_sc.shutdown();}
       if(viz_sc!=null){viz_sc.shutdown();}
       if(exec_sc!=null){exec_sc.shutdown();}
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }


    @Override
    public Future<List<String>> planBimanualGrasp(String object_uuid) {
        PepperFindGraspPlanRequest req = plan_sc.newMessage();
        req.setObjectUuid(object_uuid);
        ResponseFuture<PepperFindGraspPlanResponse> res = new ResponseFuture<>();
        plan_sc.call(req, res);
        Future<List<String> > fut = new Future<List<String>>() {
            @Override
            public boolean cancel(boolean b) {
                return res.cancel(b);
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
            public List<String> get() throws InterruptedException, ExecutionException {
                return res.get().getSolutions();
            }

            @Override
            public List<String> get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return res.get(l, timeUnit).getSolutions();
            }
        };
        return fut;
    }

    @Override
    public Future<Boolean> visualizeBimanualGrasp(String solution_uuid) {
        PepperVisualizeSolutionRequest req = viz_sc.newMessage();
        req.setSolutionId(solution_uuid);
        ResponseFuture<PepperVisualizeSolutionResponse> res = new ResponseFuture<>();
        viz_sc.call(req,res);

        return res.toBooleanFuture();
    }

    @Override
    public Future<Boolean> executeBimanualGrasp(String solution_uuid) {

        PepperExecuteSolutionRequest req = exec_sc.newMessage();
        req.setSolutionId(solution_uuid);
        ResponseFuture<PepperExecuteSolutionResponse> res = new ResponseFuture<>();
        exec_sc.call(req,res);

        return res.toBooleanFuture();
    }

    @Override
    public ObjectShapeData spawnCollisonObject() {
        CollisionObject c = objectSpawner.newMessage();
        c.setId("spawned_object");
        c.getHeader().setFrameId("base_footprint");
        SolidPrimitive p = node.getTopicMessageFactory().newFromType(SolidPrimitive._TYPE);
        p.setType(SolidPrimitive.BOX);
        double[] dims = new double[3];
        dims[0] = 0.15;
        dims[1] = 0.23;
        dims[2] = 0.10;
        p.setDimensions(dims);
        List<SolidPrimitive> ps = new ArrayList<>();
        List<Pose> poses = new ArrayList<>();
        Pose pose = node.getTopicMessageFactory().newFromType(Pose._TYPE);
        Point point = node.getTopicMessageFactory().newFromType(Point._TYPE);
        point.setX(0.31);
        point.setY(-0.02);
        point.setZ(0.93);
        Quaternion quat = node.getTopicMessageFactory().newFromType(Quaternion._TYPE);
        quat.setX(0);
        quat.setY(0);
        quat.setZ(0);
        quat.setW(1);
        pose.setPosition(point);
        pose.setOrientation(quat);
        poses.add(pose);
        ps.add(p);
        c.setPrimitives(ps);
        c.setPrimitivePoses(poses);
        c.setOperation(CollisionObject.ADD);
        objectSpawner.publish(c);

        ObjectShapeData perfectBox = new ObjectShapeData();
        perfectBox.setId(c.getId());
        perfectBox.setFrameId(c.getHeader().getFrameId());
        perfectBox.setBoundingBox(
                new BoundingBox3D(new Pose3D(new Point3D((float)dims[0], (float)dims[1], (float)dims[2]),
                        new Rotation3D(0.0,0.0,0.0,0.0, AngleUnit.RADIAN)),
                        new Point3D(0,0,0, LengthUnit.METER)));
        return perfectBox;

    }

    @Override
    public void removeCollisionObject(String id) {

        CollisionObject c = objectSpawner.newMessage();
        c.setId(id);
        c.setOperation(CollisionObject.REMOVE);
        objectSpawner.publish(c);
    }

}

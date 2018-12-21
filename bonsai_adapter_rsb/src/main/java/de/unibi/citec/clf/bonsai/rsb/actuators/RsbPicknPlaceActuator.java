package de.unibi.citec.clf.bonsai.rsb.actuators;


import de.unibi.citec.clf.bonsai.actuators.PicknPlaceActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType;
import de.unibi.citec.clf.btl.data.grasp.KatanaGripperData;
import de.unibi.citec.clf.btl.data.grasp.KatanaPoseData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.RemoteServer;
import rst.generic.DictionaryType.Dictionary;
import rst.generic.KeyValuePairType.KeyValuePair;
import rst.geometry.BoundingBox3DFloatType.BoundingBox3DFloat;
import rst.geometry.PointCloud3DFloatType.PointCloud3DFloat;
import rst.geometry.PoseType.Pose;
import rst.geometry.RotationType.Rotation;
import rst.geometry.Shape3DFloatType.Shape3DFloat;
import rst.geometry.TranslationType.Translation;
import rst.kinematics.JointAnglesType.JointAngles;
import rst.math.Vec3DDoubleType.Vec3DDouble;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RsbPicknPlaceActuator extends RsbNode implements PicknPlaceActuator {

    public static final String OPTION_TIMEOUT = "timeout";
    private double timeout = 2500;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalDouble(OPTION_TIMEOUT, timeout);
    }

    private static final String METHOD_GETSURFACE = "getSurfaceByHeight";
    private static final String METHOD_PLAN_MOVE = "planToPose";
    private static final String METHOD_MOVE_JOINTS = "moveJoints";
    private static final String METHOD_LIST_ANGLES = "listAngles";
    private static final String METHOD_LIST_POSES = "listPoses";
    private static final String METHOD_FIND_NEAREST_POSE = "findNearestPose";
    private static final String METHOD_MOTORS_ON = "motorsOn";
    private static final String METHOD_MOTORS_OFF = "motorsOff";
    private static final String METHOD_SET_MOVEMENTS = "setPose";
    private static final String METHOD_OPEN_GRIPPER = "openGripper";
    private static final String METHOD_CLOSE_GRIPPER = "closeGripper";
    private static final String METHOD_CLOSE_GRIPPER_BY_FORCE = "closeGripperByForce";
    private static final String METHOD_OPEN_GRIPPER_WHEN_TOUCHING = "openGripperWhenTouching";
    private static final String METHOD_IS_SOMESTHING_IN_GRIPPER = "isSomethingInGripper";
    private static final String METHOD_FREEZE = "freeze";
    private static final String METHOD_UNBLOCK = "unblock";
    private static final String METHOD_GOTO = "goto";
    private static final String METHOD_GET_GRIPPER_SENSORS = "getGripperSensors";
    private static final String METHOD_GRASP_OBJECT = "graspObjectName";
    private static final String METHOD_PLACE_OBJECT_ON_SURFACE = "placeObjectOnSurface";
    private static final String METHOD_PLACE_OBJECT_IN_REGION = "placeObjectInRegion";
    private static final String METHOD_FIND_OBJECTS = "findObjects";
    private static final String METHOD_FILTER_GRASPS = "setFilterType";
    private static final double TIMEOUT = 10;

    private Logger logger = Logger.getLogger(getClass());
    private RemoteServer server;
    private static final Object serverLock = new Object();

    public RsbPicknPlaceActuator() throws InitializeException {
    }

    @Override
    public Future<Boolean> planMovement(String name) throws IOException {
        logger.debug("calling " + METHOD_PLAN_MOVE);
        try {
            synchronized (serverLock) {
                Future<Boolean> success = server.callAsync(
                        METHOD_PLAN_MOVE, name);
                return success;
            }
        } catch (RSBException e) {
            logger.error("Error calling " + METHOD_PLAN_MOVE);
            throw new IOException("Error calling " + METHOD_PLAN_MOVE,
                    e);
        }
    }

    @Override
    public Future<Pose3D> getPosition() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<GraspReturnType> isObjectGraspable(String objectName, String group) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<GraspReturnType> placeObjectOn(ObjectShapeData region)
            throws IOException {
        return placeObjectOnSurface(region.getId());
    }

    @Override
    public void fitObjectsToPrimitives() throws IOException {
        try {
            logger.debug("calling" + METHOD_FIND_OBJECTS);
            synchronized (serverLock) {
                server.callAsync(METHOD_FIND_OBJECTS);
            }
        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error calling findObjects: " + e.getMessage());
            throw new IOException("Error calling findObjects", e);
        }
    }

    @Override
    public void startNode() throws InitializeException {
        final ProtocolBufferConverter<JointAngles> converter0 = new ProtocolBufferConverter<>(
                JointAngles.getDefaultInstance());
        final ProtocolBufferConverter<Dictionary> converter1 = new ProtocolBufferConverter<>(
                Dictionary.getDefaultInstance());
        final ProtocolBufferConverter<PointCloud3DFloat> converter2 = new ProtocolBufferConverter<>(
                PointCloud3DFloat.getDefaultInstance());
        final ProtocolBufferConverter<Shape3DFloat> converter3 = new ProtocolBufferConverter<>(
                Shape3DFloat.getDefaultInstance());
        final ProtocolBufferConverter<BoundingBox3DFloat> converter4 = new ProtocolBufferConverter<>(
                BoundingBox3DFloat.getDefaultInstance());
        final ProtocolBufferConverter<Vec3DDouble> converter5 = new ProtocolBufferConverter<>(
                Vec3DDouble.getDefaultInstance());
        final ProtocolBufferConverter<Pose> converter6 = new ProtocolBufferConverter<>(
                Pose.getDefaultInstance());

        // register data types
        ConverterRepository<ByteBuffer> repo = DefaultConverterRepository
                .getDefaultConverterRepository();

        repo.addConverter(converter0);
        repo.addConverter(converter1);
        repo.addConverter(converter2);
        repo.addConverter(converter3);
        repo.addConverter(converter4);
        repo.addConverter(converter5);
        repo.addConverter(converter6);

        try {
            server = RsbRemoteServerRepository.getInstance()
                    .requestRemoteServer(scope, timeout);
        } catch (RSBException e) {
            throw new InitializeException(
                    "Can not activate rsb server for scope: " + scope, e);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }

    private static abstract class AbstractConvertFuture<V, P> implements
            Future<V> {

        protected Future<P> parent;

        public AbstractConvertFuture(Future<P> parent) {
            this.parent = parent;
        }

        @Override
        public boolean cancel(boolean arg0) {
            return parent.cancel(arg0);
        }

        @Override
        public boolean isCancelled() {
            return parent.isCancelled();
        }

        @Override
        public boolean isDone() {
            return parent.isDone();
        }

        @Override
        public V get() throws InterruptedException,
                ExecutionException {
            return convert(parent.get());
        }

        @Override
        public V get(long timeout,
                java.util.concurrent.TimeUnit unit)
                throws InterruptedException, ExecutionException,
                TimeoutException {
            return convert(parent.get(timeout, unit));
        }

        abstract public V convert(P e);
    }

    /**
     * Call this method to close Connection to the RsbArmServer
     *
     * @throws RSBException
     * @throws InterruptedException
     */
    public void deactivate() throws RSBException, InterruptedException {
        server.deactivate();
    }

    @Override
    public Future<Void> moveJoint(int joint, double value) throws IOException {
        JointAngles.Builder anglesBuilder = JointAngles.newBuilder();

        for (int i = 0; i < 6; i++) {

            if (i != joint) {
                anglesBuilder.addAngles((float) -1);
            } else {
                anglesBuilder.addAngles((float) value);
            }
        }

        try {
            logger.debug("calling moveJoint");
            synchronized (serverLock) {
                Future<Void> f = server.callAsync(METHOD_MOVE_JOINTS,
                        anglesBuilder.build());
                return f;
            }
        } catch (RSBException e) {
            logger.error("Error calling moveJoint: " + e.getMessage());
            throw new IOException("Error calling moveJoint", e);
        }

    }

    @Override
    public Future<List<Double>> listJoints() throws IOException {
        logger.debug("calling: " + METHOD_LIST_ANGLES);

        try {
            Future<Event> e;
            synchronized (serverLock) {
                e = server.callAsync(METHOD_LIST_ANGLES);
            }
            Future<List<Double>> f = new AbstractConvertFuture<List<Double>, Event>(
                    e) {
                @Override
                public List<Double> convert(Event e) {
                    LinkedList<Double> joints = new LinkedList<>();
                    JointAngles ja = (JointAngles) e.getData();
                    List<Float> flist = ja.getAnglesList();
                    for (float f : flist) {
                        joints.add((double) f);
                    }
                    return joints;
                }
            };
            return f;

        } catch (RSBException e) {
            logger.error("Error calling listAngles: " + e.toString());
            throw new IOException("Error calling listAngles", e);
        }
    }

    @Override
    public Future<Boolean> goTo(Pose3D pose) throws IOException {

        logger.warn("calling " + METHOD_GOTO + ", but its not implemented");
        //TODO

        return null;
    }

    @Override
    public Future<List<String>> listPoses() throws IOException {

        try {
            Future<Event> e;
            synchronized (serverLock) {
                e = server.callAsync(METHOD_LIST_POSES);
            }
            Future<List<String>> f = new AbstractConvertFuture<List<String>, Event>(e) {
                @Override
                public List<String> convert(Event e) {
                    Dictionary dict = (Dictionary) e.getData();
                    ArrayList<String> poses = new ArrayList<>();
                    for (KeyValuePair pair : dict.getEntriesList()) {
                        poses.add(pair.getKey());
                    }
                    return poses;
                }
            };
            return f;
        } catch (RSBException e) {
            logger.error("Error calling listPoses: " + e.getMessage());
            throw new IOException("Error calling listPoses", e);
        }
    }

    @Override
    public Future<Boolean> directMovement(String name) throws IOException {
        logger.debug("calling" + METHOD_SET_MOVEMENTS);
        try {
            synchronized (serverLock) {
                logger.debug("------servercall--------: call server: "
                        + Time.currentTimeMillis());

                Future<Boolean> success = server.callAsync(
                        METHOD_SET_MOVEMENTS, name);

                // boolean success = server.call(METHOD_SET_MOVEMENTS, name,
                // TIMEOUT);
                logger.debug("-----servercall--------: returned: "
                        + Time.currentTimeMillis() + " setMovment: "
                        + success);
                return success;
            }

        } catch (RSBException e) {
            e.printStackTrace();
            throw new IOException("Error calling " + METHOD_SET_MOVEMENTS, e);
        }

    }

    @Override
    public void motorsOff() throws IOException {
        try {
            logger.debug("calling" + METHOD_MOTORS_OFF);
            synchronized (serverLock) {
                server.call(METHOD_MOTORS_OFF, TIMEOUT);
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling motors off: " + e.getMessage());
            throw new IOException("Error calling motors off", e);
        }
    }

    @Override
    public void motorsOn() throws IOException {
        try {
            logger.debug("calling" + METHOD_MOTORS_ON);
            synchronized (serverLock) {
                server.call(METHOD_MOTORS_ON, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling motors on: " + e.getMessage());
            throw new IOException("Error calling motors on", e);
        }

    }

    @Override
    public void filterGrasps(String filter) throws IOException {
        try {
            logger.debug("calling" + METHOD_FILTER_GRASPS);
            synchronized (serverLock) {
                server.call(METHOD_FILTER_GRASPS, filter);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling motors on: " + e.getMessage());
            throw new IOException("Error calling motors on", e);
        }

    }

    @Override
    public void openGripper() throws IOException {
        logger.debug("calling " + METHOD_OPEN_GRIPPER);

        try {
            synchronized (serverLock) {
                server.call(METHOD_OPEN_GRIPPER, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error(e);
            logger.error("Error calling " + METHOD_OPEN_GRIPPER);
            throw new IOException("Error calling " + METHOD_OPEN_GRIPPER, e);
        }

    }

    @Override
    public void closeGripper() throws IOException {
        logger.debug("calling " + METHOD_CLOSE_GRIPPER);

        try {
            synchronized (serverLock) {
                server.call(METHOD_CLOSE_GRIPPER, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling " + METHOD_CLOSE_GRIPPER);
            throw new IOException("Error calling " + METHOD_CLOSE_GRIPPER, e);
        }

    }

    @Override
    public void openGripperWhenTouching(int waitSeconds) throws IOException {
        logger.debug("calling " + METHOD_OPEN_GRIPPER_WHEN_TOUCHING);

        try {
            System.out.println("servermethod called opwt");
            Thread.sleep(waitSeconds * 1000);
            synchronized (serverLock) {
                server.call(METHOD_OPEN_GRIPPER_WHEN_TOUCHING, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
            logger.error("Error calling " + METHOD_OPEN_GRIPPER_WHEN_TOUCHING);
            throw new IOException("Error calling "
                    + METHOD_OPEN_GRIPPER_WHEN_TOUCHING, e);
        }

    }

    @Override
    public Future<KatanaGripperData> getGipperSensorData() throws IOException {
        logger.debug("calling " + METHOD_GET_GRIPPER_SENSORS);
        System.out.println("calling get grippersensors");

        try {
            Future<Event> e;
            synchronized (serverLock) {
                e = server.callAsync(METHOD_GET_GRIPPER_SENSORS);
            }

            Future<KatanaGripperData> f;
            f = new AbstractConvertFuture<KatanaGripperData, Event>(e) {
                @Override
                public KatanaGripperData convert(Event e) {
                    Dictionary dic = (Dictionary) e.getData();

                    /*
                     * for(KeyValuePair pair : dic.getEntriesList()){ String movement =
                     * pair.getKey(); Value anglesValue = pair.getValue();
                     * System.out.println(movement + " " +anglesValue.toString()); }
                     */
                    List<KeyValuePair> keyValList = dic.getEntriesList();
                    KatanaGripperData gripData = new KatanaGripperData();
                    gripData.setTimestamp((long) keyValList.get(0).getValue().getInt(),
                            TimeUnit.MILLISECONDS);

                    for (KeyValuePair pair : keyValList) {
                        if (pair.getKey().equals("katana_r_inside_near_force_sensor")) {
                            gripData.setForceRightInsideNear(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_r_inside_far_force_sensor")) {
                            gripData.setForceRightInsideFar(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_r_outside_distance_sensor")) {
                            gripData.setInfraredRightOutside(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_r_tip_distance_sensor")) {
                            gripData.setInfraredRightFront(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_r_inside_near_distance_sensor")) {
                            gripData.setInfraredRightInsideNear(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_r_inside_far_distance_sensor")) {
                            gripData.setInfraredRightInsideFar(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_inside_near_force_sensor")) {
                            gripData.setForceLeftInsideNear(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_inside_far_force_sensor")) {
                            gripData.setForceLeftInsideFar(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_outside_distance_sensor")) {
                            gripData.setInfraredLeftOutside(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_tip_distance_sensor")) {
                            gripData.setInfraredLeftFront(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_inside_near_distance_sensor")) {
                            gripData.setInfraredLeftInsideNear(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_l_inside_far_distance_sensor")) {
                            gripData.setInfraredLeftInsideFar(pair.getValue().getInt());
                        }
                        if (pair.getKey().equals("katana_wrist_middle_distance_sensor")) {
                            gripData.setInfraredMiddle(pair.getValue().getInt());
                        }
                    }
                    return gripData;
                }
            };
            return f;
        } catch (RSBException ex) {
            ex.printStackTrace();
            logger.error("Error calling " + METHOD_GET_GRIPPER_SENSORS);
            throw new IOException(
                    "Error calling " + METHOD_GET_GRIPPER_SENSORS, ex);
        }
    }

    @Override
    public void closeGripperByForce() throws IOException {
        logger.debug("calling " + METHOD_CLOSE_GRIPPER_BY_FORCE);

        try {
            synchronized (serverLock) {
                server.call(METHOD_CLOSE_GRIPPER_BY_FORCE, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling " + METHOD_CLOSE_GRIPPER_BY_FORCE);
            throw new IOException("Error calling "
                    + METHOD_CLOSE_GRIPPER_BY_FORCE, e);
        }

    }

    @Override
    public void freeze() throws IOException {
        logger.debug("calling " + METHOD_FREEZE);

        try {
            synchronized (serverLock) {
                server.call(METHOD_FREEZE, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling " + METHOD_FREEZE);
            throw new IOException("Error calling " + METHOD_FREEZE, e);
        }

    }

    @Override
    public void unblock() throws IOException {
        logger.debug("calling " + METHOD_UNBLOCK);

        try {
            synchronized (serverLock) {
                server.call(METHOD_UNBLOCK, TIMEOUT);
            }
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            logger.error("Error calling " + METHOD_UNBLOCK);
            throw new IOException("Error calling " + METHOD_UNBLOCK, e);
        }

    }

    @Override
    public Future<MoveitResult> graspObject(@Nonnull ObjectShapeData osd, @Nullable String group) throws IOException {
        return graspObject(osd.getId(),group);
    }

    @Override
    public Future<MoveitResult> graspObject(@Nonnull String objectToGrasp, @Nullable String group) throws IOException {
        Future<GraspReturnType> grt = callMethodStringString(METHOD_GRASP_OBJECT, objectToGrasp, "");
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
    public Future<GraspReturnType> placeObjectOnSurface(String surfaceName)
            throws IOException {
        return callMethodString(METHOD_PLACE_OBJECT_ON_SURFACE, surfaceName);
    }

    @Override
    public Future<GraspReturnType> placeObjectOnSurface(float heigth) throws IOException {
        try {
            Event e = server.call(METHOD_GETSURFACE, heigth);
            logger.debug("placing on surface " + e.toString());
            return callMethodString(METHOD_PLACE_OBJECT_ON_SURFACE, (String) e.getData());

        } catch (InterruptedException | RSBException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public Future<Boolean> isSomethingInGripper() throws IOException {
        logger.debug("calling " + METHOD_IS_SOMESTHING_IN_GRIPPER);

        try {
            Future<Event> ev;
            synchronized (serverLock) {
                ev = server.callAsync(METHOD_IS_SOMESTHING_IN_GRIPPER);
            }
            Future<Boolean> f = new AbstractConvertFuture<Boolean, Event>(ev) {
                @Override
                public Boolean convert(Event e) {
                    return (boolean) e.getData();
                }
            };
            return f;
        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error in " + METHOD_IS_SOMESTHING_IN_GRIPPER);
            throw new IOException("Error " + METHOD_IS_SOMESTHING_IN_GRIPPER, e);
        }
    }

    @Override
    public Future<String> findNearestPose() throws IOException {
        logger.debug("calling " + METHOD_FIND_NEAREST_POSE);
        try {
            Future<Event> ev;
            synchronized (serverLock) {
                ev = server.callAsync(METHOD_FIND_NEAREST_POSE);
            }
            Future<String> f = new AbstractConvertFuture<String, Event>(ev) {
                @Override
                public String convert(Event e) {
                    return (String) e.getData();
                }
            };
            return f;
        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error in " + METHOD_FIND_NEAREST_POSE);
            throw new IOException("Error " + METHOD_FIND_NEAREST_POSE, e);
        }
    }

    /**
     * @param method
     * @param surfaceName
     * @return
     * @throws IOException
     */
    private Future<GraspReturnType> callMethodString(String method, String surfaceName)
            throws IOException {
        return callMethodStringString(method, surfaceName, "");
    }

    /**
     * @param method
     * @param name0
     * @return
     * @throws IOException
     */
    private Future<GraspReturnType> callMethodStringString(String method, String name0, String name1)
            throws IOException {
        logger.debug("calling " + method);

        String msg = name0;
        if (!name1.isEmpty()) {
            msg += ";" + name1;
        }

        try {
            Future<Dictionary> dic;
            synchronized (serverLock) {
                dic = server.callAsync(method, msg);
            }
            Future<GraspReturnType> f = new AbstractConvertFuture<GraspReturnType, Dictionary>(dic) {
                @Override
                public GraspReturnType convert(Dictionary e) {
                    return convertDictionaryToGraspRetType(e);
                }
            };
            return f;

        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error calling " + method);
            throw new IOException("Error calling " + method, e);
        }
    }

    /**
     * @param method
     * @param object
     * @return
     * @throws IOException
     */
    private Future<GraspReturnType> callMethodBB3D(String method, BoundingBox3DFloat object)
            throws IOException {
        logger.debug("calling " + method);

        try {
            Future<Dictionary> dic;
            synchronized (serverLock) {
                dic = server.callAsync(method, object);
            }
            Future<GraspReturnType> f = new AbstractConvertFuture<GraspReturnType, Dictionary>(dic) {
                @Override
                public GraspReturnType convert(Dictionary e) {
                    return convertDictionaryToGraspRetType(e);
                }
            };
            return f;

        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error calling " + method);
            throw new IOException("Error calling " + method, e);
        }
    }

    /**
     * @param method
     * @param pose
     * @return
     * @throws IOException
     */
    private Future<GraspReturnType> callMethodPose(String method, Pose pose)
            throws IOException {
        logger.debug("calling " + method);

        try {
            Future<Dictionary> dic;
            synchronized (serverLock) {
                dic = server.callAsync(method, pose);
            }
            Future<GraspReturnType> f = new AbstractConvertFuture<GraspReturnType, Dictionary>(dic) {
                @Override
                public GraspReturnType convert(Dictionary e) {
                    return convertDictionaryToGraspRetType(e);
                }
            };
            return f;

        } catch (RSBException e) {
            e.printStackTrace();
            logger.error("Error calling " + method);
            throw new IOException("Error calling " + method, e);
        }
    }

    private void setObstacles(ObjectShapeList otherObjects) throws IOException {
        logger.warn("calling setObstacles. ATTENTION: currently not functioning!!");

        /*
         logger.debug("setObstacles");
         Shape3DFloat.Builder shape = Shape3DFloat.newBuilder();

         for (ObjectShapeData data : otherObjects) {
         shape.addBox(convertObjShapeDataToBoundingbox(data));
         }

         try {
         Event e;
         synchronized (serverLock) {
         e = server.call(METHOD_SET_OBSTACLES, shape.build(), TIMEOUT);
         }

         } catch (RSBException | ExecutionException | TimeoutException e) {
         e.printStackTrace();
         logger.error("Error in set Obstacles ");
         throw new IOException("Error setObstacles", e);
         }
         */
    }

    private BoundingBox3DFloat convertObjShapeDataToBoundingbox(
            ObjectShapeData data) {

        BoundingBox3DFloat.Builder box = BoundingBox3DFloat.newBuilder();
        Translation.Builder trans = Translation.newBuilder();
        Pose.Builder pose = Pose.newBuilder();
        trans.setX(data.getCenter().getX(LengthUnit.METER));
        trans.setY(data.getCenter().getY(LengthUnit.METER));
        trans.setZ(data.getCenter().getZ(LengthUnit.METER));
        trans.setFrameId(data.getCenter().getFrameId());
        pose.setTranslation(trans.build());
        Rotation.Builder rot = Rotation.newBuilder();
        rot.setQw(0);
        rot.setQx(0);
        rot.setQy(0);
        rot.setQz(0);
        rot.setFrameId(data.getCenter().getFrameId());
        pose.setRotation(rot.build());
        // pose.

        box.setTransformation(pose.build());
        box.setDepth((float) data.getDepth(LengthUnit.METER));
        box.setHeight((float) data.getHeight(LengthUnit.METER));
        box.setWidth((float) data.getWidth(LengthUnit.METER));

        return box.build();
    }

    private GraspReturnType convertDictionaryToGraspRetType(Dictionary dict) {
        GraspReturnType grasp = new GraspReturnType();
        grasp.setX(dict.getEntries(0).getValue().getDouble(),
                LengthUnit.METER);
        grasp.setY(dict.getEntries(1).getValue().getDouble(),
                LengthUnit.METER);
        grasp.setZ(dict.getEntries(2).getValue().getDouble(),
                LengthUnit.METER);
        grasp.setRating(dict.getEntries(3).getValue().getDouble());

        switch (dict.getEntries(4).getValue().getString()) {
            case "SUCCESS":
                grasp.setGraspResult(GraspReturnType.GraspResult.SUCCESS);
                break;
            case "POSITION_UNREACHABLE":
                grasp.setGraspResult(GraspReturnType.GraspResult.POSITION_UNREACHABLE);
                break;
            case "ROBOT_CRASHED":
                grasp.setGraspResult(GraspReturnType.GraspResult.ROBOT_CRASHED);
                break;
            case "FAIL":
                grasp.setGraspResult(GraspReturnType.GraspResult.FAIL);
                break;
            case "COLLISION_HANDLED":
                grasp.setGraspResult(GraspReturnType.GraspResult.COLLISION_HANDLED);
                break;
            case "NO_RESULT":
                grasp.setGraspResult(GraspReturnType.GraspResult.NO_RESULT);
                break;
        }
        System.out.println("Grt: " + grasp.getGraspResult());
        return grasp;
    }

    private Pose convertKatanaPositionToPose(KatanaPoseData position) {
        Pose.Builder poseBuilder = Pose.newBuilder();
        Translation.Builder tranBuilder = Translation.newBuilder();
        tranBuilder.setX(position.getX(LengthUnit.METER));
        tranBuilder.setY(position.getY(LengthUnit.METER));
        tranBuilder.setZ(position.getZ(LengthUnit.METER));
        tranBuilder.setFrameId(position.getFrameId());
        poseBuilder.setTranslation(tranBuilder.build());
        Rotation.Builder rotBuilder = Rotation.newBuilder();
        rotBuilder.setQw((double) 1);
        rotBuilder.setQx(position.getPhi(AngleUnit.RADIAN));
        rotBuilder.setQy(position.getTheta(AngleUnit.RADIAN));
        rotBuilder.setQz(position.getPsi(AngleUnit.RADIAN));
        rotBuilder.setFrameId(position.getFrameId());
        poseBuilder.setRotation(rotBuilder.build());

        return poseBuilder.build();
    }

    private Future<Void> generateDefaultFutureVoid() {
        Future<Void> f = new Future<Void>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return true;
            }

            @Override
            public Void get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Void get(long timeout, java.util.concurrent.TimeUnit unit)
                    throws InterruptedException, ExecutionException,
                    TimeoutException {
                return null;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }
        };
        return f;
    }

}

package de.unibi.citec.clf.bonsai.rsb.actuators;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.apache.log4j.Logger;

import rsb.RSBException;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.RemoteServer;
import rst.generic.KeyValuePairType;
import rst.generic.ValueType;
import rst.navigation.CommandResultType;
import rst.navigation.CoordinateCommandType;
import rst.navigation.CoordinateCommandType.CoordinateCommand;
import rst.navigation.PathType;
import de.unibi.citec.clf.bonsai.actuators.NavigationActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import de.unibi.citec.clf.bonsai.rsb.util.FutureRst;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.navigation.CommandResult;
import de.unibi.citec.clf.btl.data.navigation.CommandResult.Result;
import de.unibi.citec.clf.btl.data.navigation.DriveData;
import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.navigation.TurnData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.RotationalSpeedUnit;
import de.unibi.citec.clf.btl.units.SpeedUnit;
import rsb.Event;
import rsb.InitializeException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author cklarhor
 */
public class RsbNavigationActuator extends RsbNode implements NavigationActuator {

    public static final String OPTION_TIMEOUT = "timeout";
    private double timeout = 2500;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalDouble(OPTION_TIMEOUT, timeout);
    }

    private final Logger logger = Logger.getLogger(getClass());
    private RemoteServer remoteServer;
    private static final Object remoteServerLock = new Object();
    private Future<CommandResult> lastResult = null;

    public RsbNavigationActuator() {

    }

    public void setBoolean(String key, boolean value) {
        final KeyValuePairType.KeyValuePair.Builder keyValue = KeyValuePairType.KeyValuePair.newBuilder();
        keyValue.setKey(key);
        keyValue.getValueBuilder().setType(ValueType.Value.Type.BOOL);
        keyValue.getValueBuilder().setBool(value);
        try {
            CommandResultType.CommandResult result = remoteServer.call("reconfigureNode", keyValue.build(), timeout);
            logger.fatal(result.getType() + ":" + result.getCode() + ":" + result.getDescription());
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException ex) {
            logger.fatal(ex, ex);
        }

    }

    @Override
    public Future<CommandResult> moveRelative(DriveData drive, TurnData turn) {
        if (drive == null && turn == null) {
            return generateErrorFuture("neither turn nor drive data given");
        }
        logger.info("Start move relative");
        final CoordinateCommandType.CoordinateCommand.Builder b = CoordinateCommandType.CoordinateCommand.newBuilder();
        b.getGoalBuilder().getTranslationBuilder().setX(0.0);
        b.getGoalBuilder().getTranslationBuilder().setY(0.0);
        b.getGoalBuilder().getTranslationBuilder().setZ(0.0);
        final Rotation3D tmpRot = new Rotation3D(0.0, 0.0, 0.0, AngleUnit.RADIAN);
        final Quat4d tmpQuat = tmpRot.getQuaternion();
        b.getGoalBuilder().getRotationBuilder().setQw(tmpQuat.w);
        b.getGoalBuilder().getRotationBuilder().setQx(tmpQuat.x);
        b.getGoalBuilder().getRotationBuilder().setQy(tmpQuat.y);
        b.getGoalBuilder().getRotationBuilder().setQz(tmpQuat.z);
        logger.debug("Turn quat4d: " + tmpQuat.w + "," + tmpQuat.x + "," + tmpQuat.y + "," + tmpQuat.z);
        if (drive != null) {
            logger.debug("driving " + drive.getDistance(LengthUnit.METER) + "m. FIXME SOMETIME"); //todo: dirty hack
            b.getGoalBuilder().getTranslationBuilder().setX(drive.getDistance(LengthUnit.METER) * drive.getDirection().getX(LengthUnit.METER));
            b.getGoalBuilder().getTranslationBuilder().setY(drive.getDistance(LengthUnit.METER) * drive.getDirection().getY(LengthUnit.METER));
            if (drive.hasSpeed()) {
                logger.debug("Setting drive speed to " + drive.getSpeed(SpeedUnit.METER_PER_SEC));
                b.getMotionParametersBuilder().setMaxVelocity((float) drive.getSpeed(SpeedUnit.METER_PER_SEC));
            }
            if (drive.hasMaxAcceleration()) {
                logger.debug("Setting MaxAcceleration");
                b.getMotionParametersBuilder().setMaxAcceleration((float) drive.getMaxAcceleration());
            }
        }
        if (turn != null) {
            logger.debug("turning angle: " + turn.getAngle(AngleUnit.RADIAN));
            final Rotation3D tmp = new Rotation3D(new Vector3d(0, 0, 1),
                    turn.getAngle(AngleUnit.RADIAN), AngleUnit.RADIAN);
            final Quat4d quat = tmp.getQuaternion();
            b.getGoalBuilder().getRotationBuilder().setQw(quat.w);
            b.getGoalBuilder().getRotationBuilder().setQx(quat.x);
            b.getGoalBuilder().getRotationBuilder().setQy(quat.y);
            b.getGoalBuilder().getRotationBuilder().setQz(quat.z);
            if (turn.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC) != 0 && turn.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC) != Double.NaN) {
                b.getMotionParametersBuilder().setMaxVelocity((float) turn.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC));
            }
        }
        try {
            Future<CommandResultType.CommandResult> rstF = remoteServer.callAsync("moveRelative", b.build());
            return new FutureRst<>(rstF, CommandResult.class);
        } catch (final RSBException e) {
            logger.debug(e.getMessage(), e);
            return generateErrorFuture(e.getMessage());
        }
    }

    public int getCost(NavigationGoalData data) throws InterruptedException, ExecutionException, RSBException, TimeoutException {
        logger.debug("getCost call");
        if (PositionData.ReferenceFrame.fromString(data.getFrameId()) != PositionData.ReferenceFrame.GLOBAL) {
            throw new ExecutionException("Error your goal is not global", null);
        }
        logger.debug("X: " + data.getX(LengthUnit.METER) + " Y:" + data.getY(LengthUnit.METER));
        final CoordinateCommandType.CoordinateCommand.Builder b = CoordinateCommandType.CoordinateCommand.newBuilder();
        b.getGoalBuilder().getTranslationBuilder().setX(data.getX(LengthUnit.METER));
        b.getGoalBuilder().getTranslationBuilder().setY(data.getY(LengthUnit.METER));
        b.getGoalBuilder().getTranslationBuilder().setZ(0.0);
        final Rotation3D tmp = new Rotation3D(new Vector3d(0, 0, 1), data.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        final Quat4d quat = tmp.getQuaternion();
        b.getGoalBuilder().getRotationBuilder().setQw(quat.w);
        b.getGoalBuilder().getRotationBuilder().setQx(quat.x);
        b.getGoalBuilder().getRotationBuilder().setQy(quat.y);
        b.getGoalBuilder().getRotationBuilder().setQz(quat.z);
        long result = remoteServer.call("getCostGlobal", b.build(), timeout);
        logger.debug("getCost called");
        return (int) result;
    }

    @Override
    public Future<CommandResult> navigateToCoordinate(NavigationGoalData data) {
        System.out.println("navigate call");
        final CoordinateCommandType.CoordinateCommand.Builder builder = CoordinateCommandType.CoordinateCommand
                .newBuilder();
        builder.getGoalBuilder().getTranslationBuilder().setX(data.getX(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setY(data.getY(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setZ(0.0);
        final Rotation3D tmp = new Rotation3D(0.0, data.getYaw(AngleUnit.RADIAN), 0.0, AngleUnit.RADIAN);
        final Quat4d quat = tmp.getQuaternion();
        builder.getGoalBuilder().getRotationBuilder().setQw(quat.w);
        builder.getGoalBuilder().getRotationBuilder().setQx(quat.x);
        builder.getGoalBuilder().getRotationBuilder().setQy(quat.y);
        builder.getGoalBuilder().getRotationBuilder().setQz(quat.z);

        try {
            Future<CommandResultType.CommandResult> rstF = remoteServer.callAsync("navigateToCoordinate", builder.build());
            return new FutureRst<>(rstF, CommandResult.class);
        } catch (final RSBException e) {
            logger.debug(e.getMessage(), e);
            return generateErrorFuture(e.getMessage());
        }
    }

    @Override
    public Future<CommandResult> navigateToInterrupt(NavigationGoalData data) {
        System.out.println("navigate call");
        final CoordinateCommandType.CoordinateCommand.Builder builder = CoordinateCommandType.CoordinateCommand
                .newBuilder();
        builder.getGoalBuilder().getTranslationBuilder().setX(data.getX(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setY(data.getY(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setZ(0.0);
        final Rotation3D tmp = new Rotation3D(0.0, data.getYaw(AngleUnit.RADIAN), 0.0, AngleUnit.RADIAN);
        final Quat4d quat = tmp.getQuaternion();
        builder.getGoalBuilder().getRotationBuilder().setQw(quat.w);
        builder.getGoalBuilder().getRotationBuilder().setQx(quat.x);
        builder.getGoalBuilder().getRotationBuilder().setQy(quat.y);
        builder.getGoalBuilder().getRotationBuilder().setQz(quat.z);

        try {
            Future<CommandResultType.CommandResult> rstF = remoteServer.callAsync("navigateToInterrupt", builder.build());
            return new FutureRst<>(rstF, CommandResult.class);
        } catch (final RSBException e) {
            logger.debug(e.getMessage(), e);
            return generateErrorFuture(e.getMessage());
        }
    }

    private Future<CommandResult> generateErrorFuture(final String msg) {
        return new Future<CommandResult>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return true;
            }

            @Override
            public CommandResult get() throws InterruptedException,
                    ExecutionException {
                return new CommandResult(msg, Result.CUSTOM_ERROR, 0);
            }

            @Override
            public CommandResult get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException,
                    TimeoutException {
                return new CommandResult(msg, Result.CUSTOM_ERROR, 0);
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
    }

    @Override
    public Future<CommandResult> navigateRelative(NavigationGoalData data) {
        System.out.println("navigate call");
        final CoordinateCommandType.CoordinateCommand.Builder builder = CoordinateCommandType.CoordinateCommand
                .newBuilder();
        builder.getGoalBuilder().getTranslationBuilder().setX(data.getX(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setY(data.getY(LengthUnit.METER));
        builder.getGoalBuilder().getTranslationBuilder().setZ(0.0);
        final Rotation3D tmp = new Rotation3D(new Vector3d(0, 0, 1), data.getYaw(AngleUnit.RADIAN), AngleUnit.RADIAN);
        final Quat4d quat = tmp.getQuaternion();
        builder.getGoalBuilder().getRotationBuilder().setQw(quat.w);
        builder.getGoalBuilder().getRotationBuilder().setQx(quat.x);
        builder.getGoalBuilder().getRotationBuilder().setQy(quat.y);
        builder.getGoalBuilder().getRotationBuilder().setQz(quat.z);

        try {
            Future<CommandResultType.CommandResult> rstF = remoteServer.callAsync("navigateRelative", builder.build());
            return new FutureRst<>(rstF, CommandResult.class);
        } catch (final RSBException e) {
            logger.debug(e.getMessage(), e);
            return generateErrorFuture(e.getMessage());
        }
    }

    @Override
    public void manualStop() throws IOException {
        try {
            Future<Event> e = remoteServer.callAsync("stop");
            long end = (long) (Time.currentTimeMillis() + timeout);
            while (!e.isDone() && end < Time.currentTimeMillis()) {
                Thread.sleep(100);
            }
        } catch (RSBException | InterruptedException e) {
            throw new IOException(e);
        }

    }

    @Override
    public NavigationGoalData getCurrentGoal() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearCostmap() throws IOException {
        try {
            Future<Event> e = remoteServer.callAsync("clearCostmap");
            long end = (long) (Time.currentTimeMillis() + timeout);
            while (!e.isDone() && end < Time.currentTimeMillis()) {
                Thread.sleep(100);
            }
        } catch (RSBException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void startNode() throws InitializeException {
        final ConverterRepository<ByteBuffer> conv = DefaultConverterRepository.getDefaultConverterRepository();
        conv.addConverter(new ProtocolBufferConverter<>(CoordinateCommand.getDefaultInstance()));
        conv.addConverter(new ProtocolBufferConverter<>(CommandResultType.CommandResult.getDefaultInstance()));
        conv.addConverter(new ProtocolBufferConverter<>(PathType.Path.getDefaultInstance()));
        conv.addConverter(new ProtocolBufferConverter<>(KeyValuePairType.KeyValuePair.getDefaultInstance()));
        try {
            remoteServer = RsbRemoteServerRepository.getInstance().requestRemoteServer(scope, timeout);
        } catch (RSBException ex) {
            logger.fatal(ex.getMessage(), ex);
        }
    }

    public void drive(double distance, LengthUnit unit, double speed, SpeedUnit sunit) throws IOException{
        throw new NotImplementedException();
    }

    public void turn(double angle, AngleUnit unit, double speed, RotationalSpeedUnit sunit) throws IOException{
        throw new NotImplementedException();
    }

    public Future<GlobalPlan> getPlan(NavigationGoalData data, PositionData startPos) throws IOException{
        throw new NotImplementedException();
    }

    public GlobalPlan tryGoal(NavigationGoalData data) throws IOException{
        throw new NotImplementedException();
    }

    public void setGoal(NavigationGoalData data) throws IOException{
        throw new NotImplementedException();
    }

    @Override
    public void destroyNode() {
        //todo
    }
}

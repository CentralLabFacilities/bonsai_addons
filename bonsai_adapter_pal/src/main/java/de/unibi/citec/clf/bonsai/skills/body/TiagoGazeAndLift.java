package de.unibi.citec.clf.bonsai.skills.body;

import de.unibi.citec.clf.bonsai.actuators.GazeActuator;
import de.unibi.citec.clf.bonsai.actuators.JointControllerActuator;
import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill;
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus;
import de.unibi.citec.clf.bonsai.engine.model.ExitToken;
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator;
import de.unibi.citec.clf.bonsai.engine.model.config.SkillConfigurationException;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Set the robot gaze and z-lift of the TIAGo. Doing this in one skill instead of two is usually faster.
 *
 * <pre>
 *
 * Options:
 *  #_HORIZONTAL:   [String] Optional (Default: 0)
 *                      -> Horizontal direction to look to in rad (right - left) (Tiago: -1.24 to 1.24)
 *  #_VERTICAL:     [String] Optional (Default: 0)
 *                      -> Vertical direction to look to in rad (down - up) (Tiago: -0.98 to 0.79)
 *  #_LIFT:         [String]
 *                      -> Z lift position, range depending on the robot (Tiago: 0.0-0.35)
 *  #_MOVE_DURATION:[String] Optional (Default: 4000)
 *                      -> Time the head takes to move to the position in milliseconds
 *  #_TIMEOUT:     [integer] Optional (default: 6000)
 *                      -> Amount of time robot waits for actuator to be done in milliseconds
 *
 * Slots:
 *
 * ExitTokens:
 *  success:    Head movement completed successfully
 *  success.timeout
 *
 * Sensors:
 *
 * Actuators:
 *  GazeActuator: [GazeActuator]
 *      -> Used to control the head movement
 *  ZLiftActuator: [JointControllerActuator]
 *      -> Used to control the lift movement
 *
 * </pre>
 *
 * @author mvieth
 */
public class TiagoGazeAndLift extends AbstractSkill {

    private static final String KEY_HORIZONTAL = "#_HORIZONTAL";
    private static final String KEY_VERTICAL = "#_VERTICAL";
    private static final String KEY_LIFT = "#_LIFT";
    private static final String KEY_MOVE_DURATION = "#_MOVE_DURATION";
    private static final String KEY_TIMEOUT = "#_TIMEOUT";
    private static final String KEY_BLOCKING = "#_BLOCKING";

    private double horizontal = 0.0;
    private double vertical = 0.0;
    private double lift_pos;

    private int move_duration = 4000;

    private long timeout = 6000;
    private boolean blocking = true;

    private GazeActuator gazeActuator;
    private JointControllerActuator liftActuator;

    private ExitToken tokenSuccess;

    private ExitToken tokenTimeout;

    private Future<Void> gazeFuture;
    private Future<Boolean> liftFuture;

    @Override
    public void configure(ISkillConfigurator configurator) throws SkillConfigurationException {
        horizontal = configurator.requestOptionalDouble(KEY_HORIZONTAL, horizontal); // TODO check if angles are in range?
        vertical = configurator.requestOptionalDouble(KEY_VERTICAL, vertical);
        lift_pos = configurator.requestDouble(KEY_LIFT);
        move_duration = configurator.requestOptionalInt(KEY_MOVE_DURATION, move_duration);
        timeout = configurator.requestOptionalInt(KEY_TIMEOUT, (int) timeout);
        blocking = configurator.requestOptionalBool(KEY_BLOCKING, blocking);

        gazeActuator = configurator.getActuator("GazeActuator", GazeActuator.class);
        liftActuator = configurator.getActuator("ZLiftActuator", JointControllerActuator.class);

        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS());

        if (timeout > 0) {
            tokenTimeout = configurator.requestExitToken(ExitStatus.SUCCESS().ps("timeout")); // TODO SUCCESS or ERROR?
        }
    }

    @Override
    public boolean init() {
        if (timeout > 0) {
            logger.debug("using timeout of " + timeout + " ms");
            timeout += Time.currentTimeMillis();
        }

        // TODO is this necessary?
        // try {
        //     gazeActuator.manualStop();
        // } catch (IOException ex) {
        //     logger.warn("Could not cancel gaze action goal.");
        // }

        logger.debug("setting head pose to: (" + vertical + " / " + horizontal + ") with duration: " + move_duration);

        int scaling_factor = 10;

        float x_rel = (float) (Math.cos(horizontal) * Math.cos(vertical) * scaling_factor);
        float y_rel = (float) (Math.sin(horizontal) * Math.cos(vertical) * scaling_factor);
        float z_rel = (float) (Math.sin(vertical) * scaling_factor);

        Point3D target = new Point3D(x_rel, y_rel, z_rel, LengthUnit.METER, "torso_lift_link");

        logger.info("Looking at point: (x: " + x_rel+ " / y: " +y_rel+ " / z:  "+ z_rel +" / frame: torso_lift_link) with duration: " + move_duration);

        gazeFuture = gazeActuator.lookAt(target, move_duration);

        try {
            liftFuture = liftActuator.moveTo((float) lift_pos, move_duration, TimeUnit.MILLISECONDS);
        } catch (IOException ex) {
            logger.error(ex);
            gazeFuture.cancel(true);
            return false;
        }

        return true;
    }

    @Override
    public ExitToken execute() {
        if (blocking && !(liftFuture.isDone() && gazeFuture.isDone())) {
            if (timeout > 0 && timeout < Time.currentTimeMillis()) {
                logger.info("TiagoGazeAndLift timeout");
                if(!liftFuture.isDone()) logger.info("Lift is not done");
                if(!gazeFuture.isDone()) logger.info("Gaze is not done");
                liftFuture.cancel(true);
                gazeFuture.cancel(true);
                return tokenTimeout;
            }
            return ExitToken.loop(50);
        }

        return tokenSuccess;
    }

    @Override
    public ExitToken end(ExitToken curToken) {
        return curToken;
    }
}

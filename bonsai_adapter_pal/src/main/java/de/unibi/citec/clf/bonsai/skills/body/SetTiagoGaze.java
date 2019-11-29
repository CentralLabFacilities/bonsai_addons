package de.unibi.citec.clf.bonsai.skills.body;

import de.unibi.citec.clf.bonsai.actuators.GazeActuator;
import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill;
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus;
import de.unibi.citec.clf.bonsai.engine.model.ExitToken;
import de.unibi.citec.clf.bonsai.engine.model.config.SkillConfigurationException;
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.units.LengthUnit;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Set the robot gaze. Note that the target coordinate is relative to the torso lift link!
 *
 * <pre>
 *
 * Options:
 *  #_HORIZONTAL:   [String] Optional (Default: 0)
 *                      -> Horizontal direction to look to in rad (right - left) (Tiago: -1.24 to 1.24)
 *  #_VERTICAL:     [String] Optional (Default: 0)
 *                      -> Vertical direction to look to in rad (down - up) (Tiago: -0.98 to 0.79)
 *  #_MOVE_DURATION:[String] Optional (Default: 2000)
 *                      -> Time the head takes to move to the position in milliseconds
 *  #_BLOCKING:     [boolean] Optional (default: true)
 *                      -> If true skill ends after head movement was completed
 *  #_TIMEOUT:     [integer] Optional (default: 5000)
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
 *
 * </pre>
 *
 * @author dleins
 */
public class SetTiagoGaze extends AbstractSkill {

    private static final String KEY_HORIZONTAL = "#_HORIZONTAL";
    private static final String KEY_VERTICAL = "#_VERTICAL";
    private static final String KEY_MOVE_DURATION = "#_MOVE_DURATION";
    private static final String KEY_BLOCKING = "#_BLOCKING";
    private static final String KEY_TIMEOUT = "#_TIMEOUT";

    private double horizontal = 0.0;
    private double vertical = 0.0;

    private int move_duration = 2000;

    private boolean blocking = true;

    private long timeout = 5000;

    private GazeActuator gazeActuator;

    private ExitToken tokenSuccess;

    private ExitToken tokenSuccessTimeout;

    private Future<Void> gazeFuture;

    @Override
    public void configure(ISkillConfigurator configurator) throws SkillConfigurationException {

        blocking = configurator.requestOptionalBool(KEY_BLOCKING, blocking);
        horizontal = configurator.requestOptionalDouble(KEY_HORIZONTAL, horizontal); // TODO check if angles are in range?
        vertical = configurator.requestOptionalDouble(KEY_VERTICAL, vertical);
        move_duration = configurator.requestOptionalInt(KEY_MOVE_DURATION, move_duration);
        timeout = configurator.requestOptionalInt(KEY_TIMEOUT, (int) timeout);

        gazeActuator = configurator.getActuator("GazeActuator", GazeActuator.class);

        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS());

        if (timeout > 0) {
            tokenSuccessTimeout = configurator.requestExitToken(ExitStatus.SUCCESS().ps("timeout"));
        }

    }

    @Override
    public boolean init() {

        if (timeout > 0) {
            logger.debug("using timeout of " + timeout + " ms");
            timeout += Time.currentTimeMillis();
        }

        try {
            gazeActuator.manualStop();
        } catch (IOException ex) {
            logger.warn("Could not cancel gaze action goal.");
        }

        logger.debug("setting head pose to: (" + vertical + " / " + horizontal + ") with duration: " + move_duration);

        int scaling_factor = 10;

        float x_rel = (float) (Math.cos(horizontal) * Math.cos(vertical) * scaling_factor);
        float y_rel = (float) (Math.sin(horizontal) * Math.cos(vertical) * scaling_factor);
        float z_rel = (float) (Math.sin(vertical) * scaling_factor);

        Point3D target = new Point3D(x_rel, y_rel, z_rel, LengthUnit.METER, "torso_lift_link");

        logger.info("Looking at point: (x: " + x_rel+ " / y: " +y_rel+ " / z:  "+ z_rel +" / frame: torso_lift_link) with duration: " + move_duration);

        gazeFuture = gazeActuator.lookAt(target, move_duration);

        return true;
    }

    @Override
    public ExitToken execute() {

        if (timeout > 0) {
            if (Time.currentTimeMillis() > timeout) {
                logger.info("SetTiagoGaze timeout");
                gazeFuture.cancel(true);
                return tokenSuccessTimeout;
            }
        }

        if (blocking && !gazeFuture.isDone()) {
            //logger.trace("Gaze done: " + gazeFuture.isDone() + " gaze cancelled: " + gazeFuture.isCancelled());
            return ExitToken.loop(50);
        }

        return tokenSuccess;
    }

    @Override
    public ExitToken end(ExitToken curToken) {
        return curToken;
    }
}

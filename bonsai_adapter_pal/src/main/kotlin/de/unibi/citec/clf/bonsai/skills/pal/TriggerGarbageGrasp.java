package de.unibi.citec.clf.bonsai.skills.pal;

import de.unibi.citec.clf.bonsai.actuators.GarbageGraspActuator;
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill;
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus;
import de.unibi.citec.clf.bonsai.engine.model.ExitToken;
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator;
import de.unibi.citec.clf.bonsai.engine.model.config.SkillConfigurationException;
import de.unibi.citec.clf.btl.util.GarbageGraspResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * TODO
 * <pre>
 *
 * </pre>
 *
 * @author dleins
 */
public class TriggerGarbageGrasp extends AbstractSkill {

    private ExitToken tokenSuccess;
    private ExitToken tokenError;
    private ExitToken tokenNavFailed;
    private ExitToken tokenPlanFailed;
    private ExitToken tokenGraspFailed;

    private GarbageGraspActuator garbageActuator;
    private Future<GarbageGraspResult> future;

    @Override
    public void configure(ISkillConfigurator configurator) throws SkillConfigurationException {

        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS());
        tokenError = configurator.requestExitToken(ExitStatus.ERROR());
        tokenNavFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("navFailed"));
        tokenPlanFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("planFailed"));
        tokenGraspFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("graspFailed"));

        garbageActuator = configurator.getActuator("GarbageActuator", GarbageGraspActuator.class);

    }

    @Override
    public boolean init() {

        try {
            future = garbageActuator.getResult();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public ExitToken execute() {

        if (future.isDone()) {
            try {
                GarbageGraspResult.Result result = future.get().getResultType();

                switch (result) {
                    case SUCCESS:
                        return tokenSuccess;
                    case NAV_FAILED:
                        return tokenNavFailed;
                    case PLAN_FAILED:
                        return tokenPlanFailed;
                    case GRASP_FAILED:
                        return tokenGraspFailed;
                    default:
                        return tokenError;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return tokenError;
            }
        }
        return ExitToken.loop(50);
    }

    @Override
    public ExitToken end(ExitToken curToken) {
        return curToken;
    }

}

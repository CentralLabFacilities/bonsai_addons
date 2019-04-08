package de.unibi.citec.clf.bonsai.skills.pal;

import de.unibi.citec.clf.bonsai.actuators.StoringGroceriesActuator;
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill;
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus;
import de.unibi.citec.clf.bonsai.engine.model.ExitToken;
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator;
import de.unibi.citec.clf.bonsai.engine.model.config.SkillConfigurationException;
import de.unibi.citec.clf.btl.util.StoringGroceriesResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * TODO
 * <pre>
 *
 * </pre>
 *
 * @author mvieth
 */
public class StoringGroceriesSkill extends AbstractSkill {

    private ExitToken tokenSuccess;
    private ExitToken tokenError;
    private ExitToken tokenNavFailed;
    private ExitToken tokenPlanFailed;
    private ExitToken tokenGraspFailed;

    private StoringGroceriesActuator actuator;
    private Future<StoringGroceriesResult> future;
    private String action = "";

    @Override
    public void configure(ISkillConfigurator configurator) throws SkillConfigurationException {
        action = configurator.requestValue("#_ACTION");
        if(!action.equals("pick") && !action.equals("place")) {
            logger.error("Storing groceries: unknown action");
        }
        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS());
        tokenError = configurator.requestExitToken(ExitStatus.ERROR());
        tokenNavFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("navFailed"));
        tokenPlanFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("planFailed"));
        tokenGraspFailed = configurator.requestExitToken(ExitStatus.ERROR().ps("graspFailed"));

        actuator = configurator.getActuator("StoringGroceriesActuator", StoringGroceriesActuator.class);

    }

    @Override
    public boolean init() {

        try {
            future = actuator.getResult(action);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public ExitToken execute() {

        if (future.isDone()) {
            try {
                StoringGroceriesResult.Result result = future.get().getResultType();

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

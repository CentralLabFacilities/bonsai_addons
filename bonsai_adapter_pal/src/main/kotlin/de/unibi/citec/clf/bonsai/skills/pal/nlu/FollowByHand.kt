package de.unibi.citec.clf.bonsai.skills.pal.nlu

import de.unibi.citec.clf.bonsai.actuators.ExecuteUntilCancelActuator
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.`object`.Sensor
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus
import de.unibi.citec.clf.bonsai.engine.model.ExitToken
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator
import de.unibi.citec.clf.bonsai.util.helper.SimpleNLUHelper
import de.unibi.citec.clf.bonsai.util.helper.SimpleSpeechHelper
import de.unibi.citec.clf.btl.data.speechrec.NLU
import java.util.concurrent.Future


/**
 * Follow by Hand with speechSensorNLU
 *
 * @author lruegeme
 */
class FollowByHand : AbstractSkill() {

    // used tokens
    private var tokenSuccess: ExitToken? = null

    private var speechSensorName = "NLUSensor"
    private var intent = "stop"

    private var followAct: ExecuteUntilCancelActuator? = null
    private var speechManager: SimpleNLUHelper? = null
    private var speechSensor: Sensor<NLU>? = null
    private var speechActuator: SpeechActuator? = null


    private var stateConfirm = false

    private var action: Future<Boolean>? = null

    private var intentNo = "confirm_no"
    private var intentYes = "confirm_yes"

    override fun configure(configurator: ISkillConfigurator) {

        // request all tokens that you plan to return from other methods
        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS())
        followAct = configurator.getActuator(
            "FollowByHand",
            ExecuteUntilCancelActuator::class.java
        )
        speechSensor = configurator.getSensor<NLU>(speechSensorName, NLU::class.java)
        speechActuator = configurator.getActuator("SpeechActuator", SpeechActuator::class.java)

    }

    override fun init(): Boolean {
        action = followAct?.executeAction()
        speechManager = SimpleNLUHelper(speechSensor, true)

        speechManager?.startListening()

        return action != null
    }

    override fun execute(): ExitToken? {
        if(stateConfirm) {
            return waitConfirm()
        }

        if (speechManager?.hasNewUnderstanding() != true) {
            return ExitToken.loop(50)
        }

        if (speechManager!!.allUnderstoodIntents.contains(intent)) {
            speechActuator?.say("should we stop?")
            stateConfirm = true
            return ExitToken.loop()
        }

        return ExitToken.loop(50)

    }

    private fun waitConfirm() : ExitToken? {
        if (speechManager?.hasNewUnderstanding() != true) {
            return ExitToken.loop(50)
        }

        if (speechManager!!.allUnderstoodIntents.contains(intentYes)) {
            return tokenSuccess
        } else if (speechManager!!.allUnderstoodIntents.contains(intentNo)) {
            speechActuator?.say("continue")
            speechManager?.startListening()
            stateConfirm = false
            return ExitToken.loop()
        }

        return ExitToken.loop(50)

    }

    override fun end(curToken: ExitToken): ExitToken {
        speechActuator?.say("please let go")
        action?.cancel(true)
        return curToken
    }

}

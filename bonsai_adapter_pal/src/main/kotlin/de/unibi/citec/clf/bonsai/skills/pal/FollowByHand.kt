package de.unibi.citec.clf.bonsai.skills.pal


import de.unibi.citec.clf.bonsai.actuators.ExecuteUntilCancelActuator
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.`object`.Sensor
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus
import de.unibi.citec.clf.bonsai.engine.model.ExitToken
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator
import de.unibi.citec.clf.bonsai.util.helper.SimpleSpeechHelper
import de.unibi.citec.clf.btl.data.speechrec.Language
import de.unibi.citec.clf.btl.data.speechrec.Utterance
import java.util.concurrent.Future


/**
 * Follow by Hand
 *
 * @author lruegeme
 */
class FollowByHand : AbstractSkill() {

    // used tokens
    private var tokenSuccess: ExitToken? = null

    private var speechSensorName = "SpeechSensorStop"
    private var nonTerminal = "stop"

    private var followAct: ExecuteUntilCancelActuator? = null
    private var speechSensor: Sensor<Utterance>? = null
    private var speechManager: SimpleSpeechHelper? = null
    private var speechActuator: SpeechActuator? = null

    private var speechSensorConfirm: Sensor<Utterance>? = null
    private var speechManagerConfirm: SimpleSpeechHelper? = null
    private var stateConfirm = false

    private var nonTerminalNo = "confirm_no"
    private var nonTerminalYes = "confirm_yes"

    private var action: Future<Boolean>? = null

    override fun configure(configurator: ISkillConfigurator) {

        // request all tokens that you plan to return from other methods
        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS())
        followAct = configurator.getActuator(
            "FollowByHand",
            ExecuteUntilCancelActuator::class.java
        )
        speechSensor = configurator.getSensor(speechSensorName, Utterance::class.java)
        speechSensorConfirm = configurator.getSensor("SpeechSensorConfirm", Utterance::class.java)
        speechActuator = configurator.getActuator("SpeechActuator", SpeechActuator::class.java)

    }

    override fun init(): Boolean {
        action = followAct?.executeAction()
        speechManager = SimpleSpeechHelper(speechSensor, true)
        speechManagerConfirm  = SimpleSpeechHelper(speechSensorConfirm, true)

        speechManager?.startListening()

        return action != null
    }

    override fun execute(): ExitToken? {
        if(stateConfirm) {
            return waitConfirm()
        }

        if (!(speechManager?.hasNewUnderstanding() ?: false)) {
            return ExitToken.loop(50)
        }

        if (!(speechManager?.getUnderstoodWords(nonTerminal)?.isEmpty() ?: true)) {
            stateConfirm = true
            speechActuator?.sayAsync("should we stop?", Language.EN)?.get()
            speechManagerConfirm?.startListening()
            return ExitToken.loop()
        }

        return ExitToken.loop(50)

    }

    private fun waitConfirm() : ExitToken? {
        if (!(speechManagerConfirm?.hasNewUnderstanding() ?: false)) {
            return ExitToken.loop(50)
        }

        if (!(speechManagerConfirm?.getUnderstoodWords(nonTerminalYes)?.isEmpty() ?: false)) {
            return tokenSuccess
        } else if (!(speechManagerConfirm?.getUnderstoodWords(nonTerminalNo)?.isEmpty() ?: false)) {
            speechActuator?.sayAsync("continue", Language.EN)?.get()
            speechManager?.startListening()
            stateConfirm = false
            return ExitToken.loop()
        }

        return ExitToken.loop(50)

    }

    override fun end(curToken: ExitToken): ExitToken {
        speechManager?.removeHelper()
        speechManagerConfirm?.removeHelper()
        speechActuator?.sayAsync("please let go")?.get()
        action?.cancel(true)
        return curToken
    }

}

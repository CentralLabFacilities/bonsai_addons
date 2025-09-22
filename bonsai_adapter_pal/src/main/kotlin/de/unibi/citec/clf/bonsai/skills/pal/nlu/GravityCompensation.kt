package de.unibi.citec.clf.bonsai.skills.pal.nlu

import de.unibi.citec.clf.bonsai.actuators.ExecuteUntilCancelActuator
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.`object`.MemorySlotReader
import de.unibi.citec.clf.bonsai.core.`object`.Sensor
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus
import de.unibi.citec.clf.bonsai.engine.model.ExitToken
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator
import de.unibi.citec.clf.bonsai.util.helper.SimpleNLUHelper
import de.unibi.citec.clf.btl.data.speech.Language
import de.unibi.citec.clf.btl.data.speech.LanguageType
import de.unibi.citec.clf.btl.data.speech.NLU
import java.util.concurrent.Future


/**
 * Follow by Hand with speechSensorNLU
 *
 * @author lruegeme
 */
class GravityCompensation : AbstractSkill() {
    companion object {
        private const val KEY_USE_LANGUAGE = "#_USE_LANGUAGE"
    }

    // used tokens
    private var tokenSuccess: ExitToken? = null

    private var speechSensorName = "NLUSensor"
    private var intent = "stop"

    private var followAct: ExecuteUntilCancelActuator? = null
    private var speechManager: SimpleNLUHelper? = null
    private var speechSensor: Sensor<NLU>? = null
    private var speechActuator: SpeechActuator? = null
    private var langSlot: MemorySlotReader<LanguageType>? = null

    private var speakerlang: Language = Language.EN

    private var stateConfirm = false

    private var action: Future<Boolean>? = null

    private var intentNo = "confirm_no"
    private var intentYes = "confirm_yes"

    override fun configure(configurator: ISkillConfigurator) {

        // request all tokens that you plan to return from other methods
        tokenSuccess = configurator.requestExitToken(ExitStatus.SUCCESS())
        followAct = configurator.getActuator(
            "GravityCompensation",
            ExecuteUntilCancelActuator::class.java
        )
        speechSensor = configurator.getSensor(speechSensorName, NLU::class.java)
        speechActuator = configurator.getActuator("SpeechActuator", SpeechActuator::class.java)

        if(configurator.requestOptionalBool(KEY_USE_LANGUAGE, false)) {
            langSlot = configurator.getReadSlot("Language", LanguageType::class.java)
        }

    }

    override fun init(): Boolean {
        speakerlang  = langSlot?.recall<LanguageType>()?.value ?: speakerlang
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
            speechActuator?.sayTranslated("should we stop?", speakerlang, Language.EN)?.get()
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
            speechActuator?.sayTranslated("continue", speakerlang, Language.EN)?.get()
            speechManager?.startListening()
            stateConfirm = false
            return ExitToken.loop()
        }

        return ExitToken.loop(50)

    }

    override fun end(curToken: ExitToken): ExitToken {
        if (speakerlang == Language.DE) {
            speechActuator?.sayAsync("Bitte lasse meinen Greifer los", Language.DE)?.get()
        } else {
            speechActuator?.sayTranslated("please let go", speakerlang, Language.EN)?.get()
        }
        action?.cancel(true)
        return curToken
    }

}

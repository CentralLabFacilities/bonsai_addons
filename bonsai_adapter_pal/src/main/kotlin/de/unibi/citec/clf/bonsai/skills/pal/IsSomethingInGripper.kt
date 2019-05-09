package de.unibi.citec.clf.bonsai.skills.pal

import de.unibi.citec.clf.bonsai.core.`object`.Sensor
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus
import de.unibi.citec.clf.bonsai.engine.model.ExitToken
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator
import de.unibi.citec.clf.btl.data.speechrec.Utterance
import std_msgs.Bool

class IsSomethingInGripper : AbstractSkill(){
    private lateinit var tokenSuccessYes: ExitToken
    private lateinit var tokenSuccessNo: ExitToken
    private var gripperSensor: Sensor<Boolean>? = null

    override fun configure(configurator: ISkillConfigurator) {
        tokenSuccessYes = configurator.requestExitToken(ExitStatus.SUCCESS().ps("yes"))
        tokenSuccessNo = configurator.requestExitToken(ExitStatus.SUCCESS().ps("no"))
        gripperSensor = configurator.getSensor("IsSomethingInGripper", Boolean::class.java)
    }

    override fun init(): Boolean {
        return true
    }

    override fun execute(): ExitToken {
        var full : Boolean = gripperSensor?.readLast(0)?: return ExitToken.fatal()
        return if (full) tokenSuccessYes else tokenSuccessNo
    }

    override fun end(curToken: ExitToken): ExitToken {
        return  curToken
    }
}
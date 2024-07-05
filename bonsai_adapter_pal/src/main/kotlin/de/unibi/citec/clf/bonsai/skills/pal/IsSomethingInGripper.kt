package de.unibi.citec.clf.bonsai.skills.pal

import de.unibi.citec.clf.bonsai.core.SensorListener
import de.unibi.citec.clf.bonsai.core.`object`.Sensor
import de.unibi.citec.clf.bonsai.engine.model.AbstractSkill
import de.unibi.citec.clf.bonsai.engine.model.ExitStatus
import de.unibi.citec.clf.bonsai.engine.model.ExitToken
import de.unibi.citec.clf.bonsai.engine.model.config.ISkillConfigurator
import de.unibi.citec.clf.btl.data.speechrec.Utterance
import net.sf.saxon.functions.ConstantFunction.True
import std_msgs.Bool

class IsSomethingInGripper : AbstractSkill() {
    private var use_both = true

    private lateinit var tokenSuccessYes: ExitToken
    private lateinit var tokenSuccessNo: ExitToken
    private var gripperSensor: Sensor<Boolean>? = null
    private var gripperSensorOther: Sensor<Boolean>? = null

    override fun configure(configurator: ISkillConfigurator) {
        tokenSuccessYes = configurator.requestExitToken(ExitStatus.SUCCESS().ps("yes"))
        tokenSuccessNo = configurator.requestExitToken(ExitStatus.SUCCESS().ps("no"))
        use_both = configurator.requestOptionalBool("use_both", use_both)
        gripperSensor = configurator.getSensor("IsSomethingInGripper", Boolean::class.java)
        if (use_both) gripperSensorOther = configurator.getSensor("IsSomethingInGripper2", Boolean::class.java)
    }

    override fun init(): Boolean {
        return true
    }

    override fun execute(): ExitToken {
        val full : Boolean = gripperSensor?.readLast(0)?: return ExitToken.fatal()
        val fullOther : Boolean = gripperSensorOther?.readLast(0)?: if (use_both) return ExitToken.fatal() else false

        return if (full || fullOther) tokenSuccessYes else tokenSuccessNo
    }

    override fun end(curToken: ExitToken): ExitToken {
        return  curToken
    }


}
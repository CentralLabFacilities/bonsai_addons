package de.unibi.citec.clf.bonsai.actuators

import de.unibi.citec.clf.bonsai.core.`object`.Actuator

import java.io.IOException
import java.util.concurrent.Future

/**
 *
 * @author lruegeme
 */
interface ExecuteUntilCancelActuator : Actuator {

    @Throws(IOException::class)
    fun executeAction(): Future<Boolean>

}

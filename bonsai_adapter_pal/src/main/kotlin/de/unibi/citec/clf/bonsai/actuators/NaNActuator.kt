package de.unibi.citec.clf.bonsai.actuators

import de.unibi.citec.clf.bonsai.core.`object`.Actuator

import java.io.IOException
import java.util.concurrent.Future

/**
 *
 * @author lruegeme
 */
interface Component : Actuator {

    @Throws(IOException::class)
    fun doNothing(): Future<Boolean>

}

package de.unibi.citec.clf.bonsai.test.actuators

import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import java.util.concurrent.Future

class SpeechActuatorStub : SpeechActuator {
    override fun say(text: String) {
        return
    }

    override fun configure(conf: IObjectConfigurator?) {

    }

    override fun cleanUp() {
    }

    override fun sayAsync(text: String): Future<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, prosodyConfig: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, async: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sayAccentuated(accented_text: String?, async: Boolean, prosodyConfig: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
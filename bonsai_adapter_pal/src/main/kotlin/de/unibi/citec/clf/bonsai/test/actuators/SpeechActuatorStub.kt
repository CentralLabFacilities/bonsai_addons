package de.unibi.citec.clf.bonsai.test.actuators

import de.unibi.citec.clf.bonsai.actuators.SpeechActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.btl.data.speechrec.Language
import java.util.concurrent.Future

class SpeechActuatorStub : SpeechActuator {
    @Deprecated("Deprecated in Java")
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

    override fun sayTranslated(text: String, language: Language): Future<String?> {
        TODO("Not yet implemented")
    }

    override fun enableASR(enable: Boolean): Future<Boolean> {
        TODO("Not yet implemented")
    }


}
package de.unibi.citec.clf.bonsai.ros.sensors

import de.unibi.citec.clf.bonsai.core.SensorListener
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import de.unibi.citec.clf.bonsai.ros.RosSensor
import geometry_msgs.WrenchStamped
import org.apache.log4j.Logger
import org.ros.message.MessageListener
import org.ros.namespace.GraphName
import org.ros.node.ConnectedNode
import org.ros.node.topic.Subscriber
import java.io.IOException
import java.util.*

class ForceThresholdSensor(val typeClass: Class<Boolean>, val rosType: Class<WrenchStamped>, val nodeName: GraphName) : RosSensor<Boolean, WrenchStamped>(typeClass, rosType), MessageListener<WrenchStamped> {

    private val logger = Logger.getLogger(this.javaClass)
    private var subscriber: Subscriber<WrenchStamped>? = null

    private val listeners = HashSet<SensorListener<Boolean>>()
    private var topic: String? = null

    private val mutex = Any()

    private var last_value: WrenchStamped? = null
    private var last = false
    private var threshold = -12.0
    private var higher = false
    private var component = WrenchForceComponent.z

    enum class WrenchForceComponent {
        x, y, z
    }
    val values = WrenchForceComponent.entries.map { it.name }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.topic = conf.requestValue("topic")
        this.threshold = conf.requestOptionalDouble("threshold", threshold)
        this.higher = conf.requestOptionalBool("needOverThreshold", higher)
        val componentString = conf.requestOptionalValue("component", "z")

        if(componentString !in values) throw ConfigurationException("${this.javaClass}: Configuration 'component' not in $values (is $componentString)")
        component = WrenchForceComponent.valueOf(componentString)
    }

    override fun getTarget(): String {
        return "$topic/wrench.force.${component.name}"
    }

    override fun addSensorListener(listener: SensorListener<Boolean>) {
        listeners.add(listener)
    }

    override fun removeSensorListener(listener: SensorListener<Boolean>) {
        listeners.remove(listener)
    }

    override fun removeAllSensorListeners() {
        listeners.clear()
    }


    @Throws(IOException::class, InterruptedException::class)
    override fun readLast(timeout: Long): Boolean? {
        synchronized (mutex) {
            logger.debug("sensor read")
            logger.error("current state is $last, last wrench was $last_value")
            logger.error("checked if '$target' () ${if (higher) ">" else "<"} $threshold")
            return last
        }
    }

    override fun hasNext(): Boolean {
        return true
    }

    override fun clear() {

    }

    override fun destroyNode() {
        logger.debug("CLEANUP CALLED")
        subscriber!!.shutdown()
    }

    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun onStart(connectedNode: ConnectedNode) {
        logger.debug("connecting ForceThresholdSensor ...")
        subscriber = connectedNode.newSubscriber(topic, WrenchStamped._TYPE)
        subscriber!!.addMessageListener(this)
        initialized = true
    }

    //Message Handler
    override fun onNewMessage(t: WrenchStamped) {
        synchronized (mutex) {
            last_value = t
            val value = when (component){
                WrenchForceComponent.x -> t.wrench.force.x
                WrenchForceComponent.y -> t.wrench.force.y
                WrenchForceComponent.z -> t.wrench.force.z
            }

            last = if (higher) value > threshold else value < threshold
        }

        listeners.forEach { l: SensorListener<Boolean> -> l.newDataAvailable(last) }
    }
}
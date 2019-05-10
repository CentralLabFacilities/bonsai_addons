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

    private val logger = Logger.getLogger(ForceThresholdSensor::class.java)
    private var subscriber: Subscriber<WrenchStamped>? = null

    private val listeners = HashSet<SensorListener<Boolean>>()
    private var topic: String? = null

    private var last = false
    private var threshold = -12.0
    private var higher = false
    private var component = "z"

    val values = listOf("x","y","z")

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
        this.topic = conf.requestValue("topic")
        this.threshold = conf.requestOptionalDouble("threshold", threshold)
        this.higher = conf.requestOptionalBool("needOverThreshold", higher)
        this.component = conf.requestOptionalValue("component", component)

        if(component !in listOf("x","y","z")) throw ConfigurationException("${this.javaClass}: Configuration 'component' not in $values (is $component)")
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
        logger.debug("sensor read")
        return last
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
        subscriber = connectedNode.newSubscriber(topic, std_msgs.Float64._TYPE)
        subscriber!!.addMessageListener(this)
        initialized = true
    }

    //Message Handler
    override fun onNewMessage(t: WrenchStamped) {
        val value = t.wrench.force.z
        logger.debug("Foce read: "+value)
        last = if (higher) value > threshold else value < threshold
        listeners.forEach { l: SensorListener<Boolean> -> l.newDataAvailable(last) }
    }
}
/**
 * Contains the RSB specific implementations of sensors, actuators and a
 * factory.
 * 
 * <p>
 * Central class is {@link de.unibi.airobots.bonsai.rsb.RsbFactory} which is an
 * implementation of {@link de.unibi.airobots.bonsai.core.CoreObjectFactory}.
 * This factory manages the creation of RSB-based sensors and actuators. The
 * general idea is that all sensors and actuators can reconnect to RSB
 * infrastructure units like servers or informers. This task will not be
 * performed by every sensor or actuator to avoid cluttering the sensor or
 * actuator implementation code with connection housekeeping. Instead, instances
 * of {@link de.unibi.airobots.bonsai.rsb.ReconnectableManager} perform these
 * tasks. The <code>RsbFactory</code> contains one instance of such a manager for
 * each RSB infrastructure Unit delegating the connection task to these managers. A sensor or
 * actuator that wants to be reconnected to such an infrastructure unit, it must
 * implement one of the <code>XxxReconnectable</code> interfaces.
 * </p>
 * 
 * <p>
 * The basic principle to register new RSB-specific sensors or actuators is to
 * provide two classes:
 * <ul>
 * <li>The actual sensor implementation</li>
 * <li>A configuration class for the sensor</li>
 * </ul>
 * The task of the configuration object is to validate and store the
 * configuration for a sensor that the user provided in the configuration file
 * and to create new instance of the sensor/actuator based on this
 * configuration. {@link de.unibi.airobots.bonsai.rsb.RSBFactory} describes how
 * these configuration objects are discovered.
 * </p>
 */
package de.unibi.citec.clf.bonsai.rsb;



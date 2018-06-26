package de.unibi.citec.clf.bonsai.rsb.configuration;



import de.unibi.citec.clf.bonsai.core.object.Actuator;
import de.unibi.citec.clf.bonsai.core.object.ActuatorToConfigure;
import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Base class for configuration objects that create actuators. For convenience
 * reasons the default implementation of the {@link #parseOptions(Map)} method
 * already parses a required server scope.
 * 
 * @author lkettenb
 * @author jwienke
 */
public abstract class ConfiguredRsbActuator extends ConfiguredRsbCoreObject {
	
	private static final Logger logger = Logger.getLogger(ConfiguredRsbActuator.class);

    /**
     * Option for remote server based classes defining the remote server name.
     */
    public static final String OPTION_SERVER_SCOPE = "scope";

    /**
     * Scope of the server if parsed by {@link #parseOptions(Map)}.
     */
    protected String serverScope = "/";


    /**
     * Concrete class for the actuator requested by the user in the
     * configuration.
     */
    private Class<? extends Actuator> actuatorClass = null;

    /**
     * Override this!
     * 
     * @param actuatorToConfigure
     *            requested configuration
     * @return <code>true</code> if this configuration object is suitable for
     *         the requested actuator
     */
    public static boolean isSuitableFor(ActuatorToConfigure actuatorToConfigure) {
        return false;
    }

    /**
     * Create a new specific actuator instance.
     * 
     * @return new instance
     * @throws CoreObjectCreationException
     *             problem creating the new instance
     */
    public abstract Actuator createInstance()
            throws CoreObjectCreationException;

    /**
     * {@inheritDoc}
     * 
     * Default implementation parsing the server name.
     */
    @Override
    public void parseOptions(Map<String, String> options)
            throws IllegalArgumentException {

        if (!options.containsKey(OPTION_SERVER_SCOPE)) {
            logger.warn("I'am a very important message your server scope is missing");
            throw new IllegalArgumentException(
                    "Server scope missing in arguments for requested type");
        }
        this.serverScope = options.get(OPTION_SERVER_SCOPE);
        logger.debug("ServerScope: "+serverScope);

    }

    /**
     * Returns the configured server scope if this class'
     * {@link #parseOptions(Map)} method is used.
     * 
     * @return configured server scope
     */
    public String getServerScope() {
        return serverScope;
    }

    /**
     * Setter for the implementation class requested by the user.
     * 
     * @param actuatorClass
     *            new actuator class
     */
    public void setActuatorClass(Class<? extends Actuator> actuatorClass) {
        this.actuatorClass = actuatorClass;
    }

    /**
     * Returns the concrete implementation class for the actuator requested in
     * the configuration.
     * 
     * @return implementation class for the actuator
     */
    public Class<? extends Actuator> getActuatorClass() {
        return actuatorClass;
    }

}

package de.unibi.citec.clf.bonsai.rsb.configuration;



import java.util.Map;

import de.unibi.citec.clf.bonsai.core.object.CoordinateTransformerToConfigure;
import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.util.CoordinateTransformer;

/**
 * Base class for configuration objects that create working memories. For
 * convenience reasons the default implementation of the
 * {@link #parseOptions(Map)} method already parses a required server name.
 *
 * @author lziegler
 */
public abstract class ConfiguredRsbCoordinateTransformer extends ConfiguredRsbCoreObject {

    /**
     * Concrete class for the memory requested by the user in the configuration.
     */
    private Class<? extends CoordinateTransformer> transformerClass = null;

    /**
     * Override this!
     *
     * @param toConfigure requested configuration
     * @return <code>true</code> if this configuration object is suitable for
     * the requested actuator
     */
    public static boolean isSuitableFor(
            CoordinateTransformerToConfigure toConfigure) {
        return false;
    }

    /**
     * Create a new specific actuator instance.
     *
     * @return new instance
     * @throws CoreObjectCreationException problem creating the new instance
     */
    public abstract CoordinateTransformer createInstance()
            throws CoreObjectCreationException;

    /**
     * {@inheritDoc}
     *
     * Default implementation parsing the memory name.
     */
    @Override
    public void parseOptions(Map<String, String> options)
            throws IllegalArgumentException {

    }

    /**
     * Setter for the implementation class requested by the user.
     *
     * @param transformerClass new working memory class
     */
    public void setCoordinateTransformerClass(Class<? extends CoordinateTransformer> transformerClass) {
        this.transformerClass = transformerClass;
    }

    /**
     * Returns the concrete implementation class for the CoordinateTransformer
     * requested in the configuration.
     *
     * @return implementation class for the CoordinateTransformer
     */
    public Class<? extends CoordinateTransformer> getCoordinateTransformerClass() {
        return transformerClass;
    }
}

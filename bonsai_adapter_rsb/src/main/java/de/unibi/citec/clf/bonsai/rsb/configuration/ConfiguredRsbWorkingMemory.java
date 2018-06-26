package de.unibi.citec.clf.bonsai.rsb.configuration;



import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.core.object.SlotToConfigure;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemoryToConfigure;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemory;
import java.util.Map;
import java.util.Set;

/**
 * Base class for configuration objects that create working memories. For
 * convenience reasons the default implementation of the
 * {@link #parseOptions(Map)} method already parses a required server name.
 *
 * @author lziegler
 */
public abstract class ConfiguredRsbWorkingMemory extends ConfiguredRsbCoreObject {

    /**
     * Option for remote server based classes defining the remote server name.
     */
    public static final String OPTION_MEMORY_NAME = "memoryName";
    /**
     * Name of the server if parsed by {@link #parseOptions(Map)}.
     */
    private String memoryName = "";
    /**
     * Concrete class for the memory requested by the user in the configuration.
     */
    private Class<? extends WorkingMemory> memoryClass = null;

    /**
     * Override this!
     *
     * @param memoryToConfigure requested configuration
     * @return <code>true</code> if this configuration object is suitable for
     * the requested actuator
     */
    public static boolean isSuitableFor(
            WorkingMemoryToConfigure memoryToConfigure) {
        return false;
    }

    /**
     * Create a new specific actuator instance.
     *
     * @return new instance
     * @throws CoreObjectCreationException problem creating the new instance
     */
    public abstract WorkingMemory createInstance()
            throws CoreObjectCreationException;

    /**
     * {@inheritDoc}
     *
     * Default implementation parsing the memory name.
     */
    @Override
    public void parseOptions(Map<String, String> options)
            throws IllegalArgumentException {

        if (!options.containsKey(OPTION_MEMORY_NAME)) {
            throw new IllegalArgumentException(
                    "Memory name missing in arguments for requested type");
        }

        this.memoryName = options.get(OPTION_MEMORY_NAME);

    }

    /**
     * Returns the configured memory name if this class'
     * {@link #parseOptions(Map)} method is used.
     *
     * @return configured memory name
     */
    public String getMemoryName() {
        return memoryName;
    }

    /**
     * Setter for the implementation class requested by the user.
     *
     * @param memoryClass new working memory class
     */
    public void setWorkingMemoryClass(Class<? extends WorkingMemory> memoryClass) {
        this.memoryClass = memoryClass;
    }

    /**
     * Returns the concrete implementation class for the working memory
     * requested in the configuration.
     *
     * @return implementation class for the working memory
     */
    public Class<? extends WorkingMemory> getWorkingMemoryClass() {
        return memoryClass;
    }

    public abstract void configureMemorySlots(Set<SlotToConfigure> slots);
}

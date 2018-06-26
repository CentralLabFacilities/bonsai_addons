package de.unibi.citec.clf.bonsai.rsb.configuration;



import java.util.Map;

/**
 * Wraps one configured RSB core object with its settings.
 * <p>
 * TODO:
 * <ul>
 * <li>Provide reasonable defaults for all options in all subclasses.</li>
 * </ul>
 * <p>
 * Every subclass must be default constructible.
 * 
 * @author jwienke
 */
public abstract class ConfiguredRsbCoreObject {

    /**
     * Key under which the core object is registered in the configuration.
     */
    private String key = "";

    /**
     * Parse options for this configuration.
     * 
     * @param options
     *            options in key value form
     * @throws IllegalArgumentException
     *             options not suitable for creating such a configuration object
     */
    public abstract void parseOptions(Map<String, String> options)
            throws IllegalArgumentException;


    /**
     * Returns the names of subscribers that need to be initialized for this
     * configured object to work.
     * 
     * Implement this in subclasses if a subscriber is needed for the object.
     * 
     * @return array of remote server names
     */
    public String[] getSubscribersToInitialize() {
        return new String[0];
    }

    /**
     * Returns the names of active memories that need to be initialized for this
     * configured object to work.
     * 
     * Implement this in subclasses if an active memory is needed for the
     * object.
     * 
     * @return array of remote server names
     */
    public String[] getActiveMemoriesToInitialize() {
        return new String[0];
    }

    /**
     * Returns the key under which the core object was registered in the
     * configuration.
     * 
     * @return key string
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for the key under which the core object was configured by the
     * user.
     * 
     * @param key
     *            new key
     */
    public void setKey(String key) {
        this.key = key;
    }

}

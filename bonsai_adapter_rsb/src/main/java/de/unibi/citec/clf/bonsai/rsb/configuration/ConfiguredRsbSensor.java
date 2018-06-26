package de.unibi.citec.clf.bonsai.rsb.configuration;


import de.unibi.citec.clf.bonsai.core.object.SensorToConfigure;
import de.unibi.citec.clf.bonsai.core.object.Sensor;

import java.util.Map;

/**
 * Base class for RSB-based sensor configuration objects. The default implementation of {@link #parseOptions(Map)}
 * requires a scope for convenience reasons.
 *
 * @author jwienke
 * @author lkettenb
 * @param <T> data type returned by the sensor
 */
public abstract class ConfiguredRsbSensor<T> extends ConfiguredRsbCoreObject {

    /**
     * Option for publisher based sensors defining the publisher name.
     */
    public static final String OPTION_SCOPE = "scope";

    public static final String OPTION_BUFFER_SIZE = "bufferSize";

    /**
     * Data type to be returned by the sensor.
     */
    private Class<T> dataType = null;

    /**
     * Name of the configured publisher if {@link #parseOptions(Map)} was used.
     */
    private String scope = "";

    /**
     * Buffer size option for internal buffering of data.
     */
    private int bufferSize = 1;

    /**
     * Create a new specific sensor instance.
     *
     * @return new instance
     */
    public abstract Sensor<T> createInstance();

    /**
     * {@inheritDoc}
     *
     * Default implementation parsing the publisher name.
     */
    @Override
    public void parseOptions(Map<String, String> options)
            throws IllegalArgumentException {

        if (!options.containsKey(OPTION_SCOPE)) {
            throw new IllegalArgumentException(
                    "Scope missing in arguments.");
        }

        scope = options.get(OPTION_SCOPE);

        if (options.containsKey(OPTION_BUFFER_SIZE)) {
            try {
                bufferSize = Integer.parseInt(options.get(OPTION_BUFFER_SIZE));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Can not parse buffer size from string: "
                        + options.get(OPTION_BUFFER_SIZE), e);
            }
        }
    }

    /**
     * Override this and check for suitable data type!
     *
     * @param sensor requested sensor
     * @return <code>true</code> if configuration can create such a requested sensor
     */
    public static boolean isSuitableFor(SensorToConfigure sensor) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSubscribersToInitialize() {
        return new String[]{scope};
    }

    /**
     * Sets the data type the configured sensor is intended to return.
     *
     * @param dataType new data type
     */
    public void setDataType(Class<T> dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns the data type the configured sensor will return.
     *
     * @return data type class
     */
    public Class<T> getDataType() {
        return dataType;
    }

    /**
     * Returns the configured publisher name for the sensor if {@link #parseOptions(Map)} was used.
     *
     * @return publisher name
     */
    public String getScope() {
        return scope;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}

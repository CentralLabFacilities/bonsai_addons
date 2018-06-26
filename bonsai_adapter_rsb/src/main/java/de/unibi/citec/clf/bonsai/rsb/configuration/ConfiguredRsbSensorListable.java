package de.unibi.citec.clf.bonsai.rsb.configuration;



import java.util.List;

import de.unibi.citec.clf.bonsai.core.object.Sensor;

/**
 * Base class for XCF-based sensor configuration objects. The default
 * implementation of {@link #parseOptions(Map)} requires a publisher name for
 * convenience reasons.
 * 
 * @author lziegler
 * @param <T>
 *            data type of the list items returned by the sensor
 */
public abstract class ConfiguredRsbSensorListable<S extends List<T>, T> extends
		ConfiguredRsbSensor<T> {
	
	/**
     * Data type to be returned by the sensor.
     */
    private Class<S> listType = null;

	/**
	 * Create a new specific sensor instance.
	 * 
	 * @return new instance
	 */
	public abstract Sensor<S> createInstanceListable();
	
    /**
     * Sets the data type the configured sensor is intended to return.
     * 
     * @param listType
     *            new data type
     */
    public void setListType(Class<S> listType) {
        this.listType = listType;
    }

    /**
     * Returns the data type the configured sensor will return.
     * 
     * @return data type class
     */
    public Class<S> getListType() {
        return listType;
    }

}

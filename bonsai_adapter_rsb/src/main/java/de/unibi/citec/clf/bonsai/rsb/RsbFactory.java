package de.unibi.citec.clf.bonsai.rsb;

import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.bonsai.core.object.Actuator;
import de.unibi.citec.clf.bonsai.core.CoreObjectFactory;
import de.unibi.citec.clf.bonsai.core.configuration.FactoryConfigurationResults;
import de.unibi.citec.clf.bonsai.core.configuration.ObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.core.exception.InitializationException;
import de.unibi.citec.clf.bonsai.core.object.ActuatorToConfigure;
import de.unibi.citec.clf.bonsai.core.object.CoordinateTransformerToConfigure;
import de.unibi.citec.clf.bonsai.core.object.ManagedCoreObject;
import de.unibi.citec.clf.bonsai.core.object.MemorySlot;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import de.unibi.citec.clf.bonsai.core.object.SensorToConfigure;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemory;
import de.unibi.citec.clf.bonsai.util.reflection.ServiceDiscovery;
import de.unibi.citec.clf.bonsai.rsb.configuration.ConfiguredRsbSensorListable;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemoryToConfigure;
import de.unibi.citec.clf.bonsai.util.reflection.ReflectionServiceDiscovery;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.XOPConverter;
import rsb.converter.XOPsConverter;
import de.unibi.citec.clf.bonsai.core.object.TransformLookup;
import de.unibi.citec.clf.bonsai.rsb.configuration.ConfiguredRsbamWorkingMemorySlot;
import de.unibi.citec.clf.bonsai.rsb.slots.RsbWorkingMemorySlot;
import java.lang.reflect.Constructor;
import java.util.LinkedList;

import rct.TransformerFactory;
import rsb.InitializeException;
import rsb.converter.ProtocolBufferConverter;
import rst.geometry.PoseType;
import rst.hri.PersonHypothesesType;

/**
 * Factory for Bonsai core objects that use the RSB middleware.
 *
 *
 * @author lziegler
 * @author lkettenb
 */
public class RsbFactory implements CoreObjectFactory {

    private Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings("rawtypes")
    protected Set<Class<? extends ConfiguredRsbSensorListable>> knownListSensors = new HashSet<>();
    protected Map<String, ConfiguredRsbSensorListable<? extends List<?>, ?>> configuredListSensorsByKey = new HashMap<>();

    protected TransformLookup transformer = null;

    static final String SERVICE_PKG_SENSOR = "de.unibi.citec.clf.bonsai.rsb.sensors";
    static final String SERVICE_PKG_ACTUATOR = "de.unibi.citec.clf.bonsai.rsb.actuators";
    static final String SERVICE_PKG_SLOTS = "de.unibi.citec.clf.bonsai.rsb.slots";
    protected ServiceDiscovery serviceDiscoverySensor = new ReflectionServiceDiscovery(SERVICE_PKG_SENSOR);
    protected ServiceDiscovery serviceDiscoveryActuator = new ReflectionServiceDiscovery(SERVICE_PKG_ACTUATOR);
    protected ServiceDiscovery serviceDiscoverySlots = new ReflectionServiceDiscovery(SERVICE_PKG_SLOTS);

    protected Set<Class<? extends RsbNode>> knownActuators = new HashSet<>();
    protected Set<Class<? extends RsbSensor>> knownSensors = new HashSet<>();
    protected Set<Class<? extends RsbWorkingMemorySlot>> knowSlots = new HashSet<>();

    protected Map<String, Boolean> isActuatorInitialized = new HashMap<>();
    protected Map<String, Actuator> initializedActuatorsByKey = new HashMap<>();

    protected Map<String, Boolean> isSensorInitialized = new HashMap<>();
    protected Map<String, Sensor> initializedSensorsByKey = new HashMap<>();

    protected Map<String, ConfiguredObject> configuredObjectsByKey = new HashMap<>();

    protected RsbamWorkingMemory memory = null;
    protected boolean memoryRunning = false;

    private class ConfiguredObject {

        public Class clazz;
        public ObjectConfigurator conf;
    }

    private class ConfiguredActuator extends ConfiguredObject {

        public Class implemented = null;
    }

    private class ConfiguredSensor extends ConfiguredObject {

        public Class wire;
        public Class data;
        public Class list;
    }

    /**
     * Constructor.
     */
    public RsbFactory() {
        ConverterRepository<ByteBuffer> rep = DefaultConverterRepository
                .getDefaultConverterRepository();
        rep.addConverter(new XOPConverter());
        rep.addConverter(new XOPsConverter());
        rep.addConverter(new ProtocolBufferConverter<>(PersonHypothesesType.PersonHypotheses.getDefaultInstance()));
        rep.addConverter(new ProtocolBufferConverter<>(PoseType.Pose.getDefaultInstance()));
                }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCreateActuator(String key, Class<? extends Actuator> actuatorClass) {
        return configuredObjectsByKey.containsKey(key);
        //ConfiguredObject obj = configuredObjectsByKey.get(key);
        //return actuatorClass.isAssignableFrom(obj.clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCreateSensor(String key, Class<?> dataType) {
        return configuredObjectsByKey.containsKey(key);
        //ConfiguredObject obj = configuredObjectsByKey.get(key);
        //return actuatorClass.isAssignableFrom(obj.clazz);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCreateSensor(String key, Class<? extends List<?>> listType, Class<?> dataType) {
        return configuredObjectsByKey.containsKey(key);
        //ConfiguredObject obj = configuredObjectsByKey.get(key);
        //return actuatorClass.isAssignableFrom(obj.clazz);

    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public FactoryConfigurationResults configureActuators(Set<ActuatorToConfigure> actuators)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.info("Configuring actuators: " + actuators);
        FactoryConfigurationResults results = new FactoryConfigurationResults();

        actuatorLoop:
        for (ActuatorToConfigure actuator : actuators) {
            logger.debug("Processing actuator to configure: " + actuator);

            // find the actuator configuration that can handle this requested
            for (Class<? extends RsbNode> actuatorClass : knownActuators) {

                logger.debug("Checking if class " + actuatorClass + " satifies actuator " + actuator);

                boolean isSuitable = actuatorClass.equals(actuator.getActuatorClass())
                        && actuator.getInterfaceClass().isAssignableFrom(actuatorClass);

                if (!isSuitable) {
                    logger.debug("Actuator class " + actuatorClass + " does not satify actuator " + actuator);
                    logger.trace("actuator class: " + actuatorClass);
                    logger.trace("actuator needs: " + actuator.getActuatorClass());
                    logger.trace("actuator class == " + (actuatorClass.equals(actuator.getActuatorClass())));
                    logger.trace("actuator impl " + actuator.getInterfaceClass().isAssignableFrom(actuatorClass));
                    continue;
                }
                logger.debug("Actuator class " + actuatorClass + " satisfies actuator " + actuator);

                ConfiguredActuator configured = new ConfiguredActuator();
                configured.clazz = actuatorClass;
                configured.conf = ObjectConfigurator.createConfigPhase();
                configured.implemented = actuator.getInterfaceClass();

                try {
                    // if this object is suitable, create an instance and configure it
                    Constructor<?> cons = actuatorClass.getConstructor();
                    ManagedCoreObject object = (ManagedCoreObject) cons.newInstance();
                    object.configure(configured.conf);
                    configured.conf.activateObjectPhase(actuator.getActuatorOptions());

                    configuredObjectsByKey.put(actuator.getKey(), configured);
                    isActuatorInitialized.put(actuator.getKey(), false);

                    // stop searching for this actuator
                    continue actuatorLoop;

                } catch (ConfigurationException e) {
                    logger.debug("error configuring Object:" + actuator.getKey() + " number errors:" + configured.conf.getExceptions().size());
                    results.exceptions.add(e);
                    for (ConfigurationException ex : configured.conf.getExceptions()) {
                        results.exceptions.add(ex);
                        logger.trace(ex.getMessage());
                    }

                    for (Map.Entry<String, Class> entry : configured.conf.getUnusedOptionalParams().entrySet()) {
                        logger.trace("unused opt param: " + entry.getKey());
                    }
                    continue actuatorLoop;
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                    throw new CoreObjectCreationException(ex);
                }

            }

            logger.error("Error while configuring " + actuator.getKey()
                    + "! Implementation for Actuator "
                    + actuator.getActuatorClass() + " is unknown. ");

        }

        return results;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactoryConfigurationResults configureWorkingMemories(
            Set<WorkingMemoryToConfigure> memories)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.info("Configuring memories: " + memories);
        FactoryConfigurationResults results = new FactoryConfigurationResults();

        if (memories.size() != 1) {
            results.exceptions.add(new CoreObjectCreationException("more than one memory?"));
        }

        WorkingMemoryToConfigure conf = null;
        for (WorkingMemoryToConfigure toconf : memories) {
            conf = toconf;
            break;
        }

        logger.debug("Processing working memory to configure: " + conf);
        if (!conf.getMemoryClass().equals(RsbamWorkingMemory.class)) {
            logger.debug("memory with key " + conf.getKey() + " is not of class" + RsbamWorkingMemory.class);
            results.exceptions.add(new IllegalArgumentException(
                    "memory with key " + conf.getKey() + " is not of class" + RsbamWorkingMemory.class));
            return results;
        }

        ObjectConfigurator objconf = ObjectConfigurator.createConfigPhase();

        Set<ConfiguredRsbamWorkingMemorySlot> slots = new HashSet<>();
        logger.debug("Processing slots for memory");
        for (Class<? extends RsbWorkingMemorySlot> s : knowSlots) {
            try {
                logger.trace("processings slot: " + s.getName());
                Constructor<?> cons = s.getConstructor();
                RsbWorkingMemorySlot slot = (RsbWorkingMemorySlot) cons.newInstance();
                logger.debug("add slot for type: " + slot.getDataType() + "("+slot.getClass()+")");
                ConfiguredRsbamWorkingMemorySlot cslot = new ConfiguredRsbamWorkingMemorySlot(slot.getDataType(), (Class<? extends MemorySlot<?>>) s);
                slots.add(cslot);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        if (memory != null) {
            memory.destroyNode();
            memory = null;
        }
        RsbamWorkingMemory memory = null;
        try {
            memory = new RsbamWorkingMemory(slots);
            memory.configure(objconf);
            objconf.activateObjectPhase(conf.getMemoryOptions());
            memoryRunning = false;
            memory.configure(objconf);
            this.memory = memory;
            this.memoryRunning = false;
        } catch (InitializeException ex) {
            logger.debug(ex);
            results.exceptions.add(ex);
        } catch (ConfigurationException e) {
            logger.debug("error configuring memory:" + conf.getKey() + " number errors:" + objconf.getExceptions().size());
            results.exceptions.add(e);
            for (ConfigurationException ex : objconf.getExceptions()) {
                results.exceptions.add(ex);
                logger.trace(ex.getMessage());
            }

            for (Map.Entry<String, Class> entry : objconf.getUnusedOptionalParams().entrySet()) {
                logger.trace("unused opt param: " + entry.getKey());
            }

        }

        return results;

    }

    private void configureListSensor(SensorToConfigure sensor, FactoryConfigurationResults res) {
        res.exceptions.add(new CoreObjectCreationException("dunno bout list sensors"));
    }

    private void configureDataSensor(SensorToConfigure sensor, FactoryConfigurationResults results) {
        // find the sensor configuration that can handle this requested

        ConfiguredSensor configured = new ConfiguredSensor();

        configured.conf = ObjectConfigurator.createConfigPhase();
        configured.wire = sensor.getWireClass();
        configured.data = sensor.getDataTypeClass();
        configured.list = sensor.getListTypeClass();

        for (Class<? extends RsbSensor> sensorClass : knownSensors) {

            logger.debug("Checking if class " + sensorClass + " satifies sensor " + sensor);

            boolean isSuitable = sensorClass.equals(sensor.getSensorClass());
            isSuitable &= GeneratedMessage.class.isAssignableFrom(sensor.getWireClass());

            if (!isSuitable) {
                logger.debug("Sensor class " + sensorClass + " does not satify sensor " + sensor);
                logger.trace("Sensor class: " + sensorClass);
                logger.trace("Sensor needs: " + sensor.getSensorClass());
                logger.trace("Sensor class == " + sensorClass.equals(sensor.getSensorClass()));
                logger.trace("Sensor wire " + sensor.getWireClass());
                logger.trace("Sensor wire ==" + GeneratedMessage.class.isAssignableFrom(sensor.getWireClass()));
                logger.trace("Sensor wire2 ==" + sensor.getWireClass().isAssignableFrom(GeneratedMessage.class));
                continue;
            }
            logger.debug("Sensor class " + sensorClass + " satisfies sensor " + sensor);

            configured.clazz = sensorClass;

            try {
                // if this object is suitable, create an instance and configure it
                Constructor<?>[] declaredConstructors = sensorClass.getDeclaredConstructors();
                if (declaredConstructors.length != 1) {
                    throw new NoSuchMethodException("sensor wrong constructors?");
                }
                ManagedCoreObject object = (ManagedCoreObject) declaredConstructors[0].newInstance(
                        configured.data, configured.wire);

                RsbSensor rsb = (RsbSensor) object;
                if (!rsb.rstType.isAssignableFrom(sensor.getWireClass())) {
                    logger.error("wrong wire format");
                    continue;
                }

                object.configure(configured.conf);
                configured.conf.activateObjectPhase(sensor.getSensorOptions());

                configuredObjectsByKey.put(sensor.getKey(), configured);
                isSensorInitialized.put(sensor.getKey(), false);

                // stop searching for this actuator
                return;

            } catch (ConfigurationException e) {
                logger.debug("error configuring Object:" + sensor.getKey() + " number errors:" + configured.conf.getExceptions().size());
                results.exceptions.add(e);
                for (ConfigurationException ex : configured.conf.getExceptions()) {
                    results.exceptions.add(ex);
                    logger.trace(ex.getMessage());
                }

                for (Map.Entry<String, Class> entry : configured.conf.getUnusedOptionalParams().entrySet()) {
                    logger.trace("unused opt param: " + entry.getKey());
                }
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                logger.error(ex);
            }

        }

        logger.error("Error while configuring " + sensor.getKey()
                + "! Implementation for Sensor "
                + sensor.getSensorClass() + " is unknown. ");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public FactoryConfigurationResults configureSensors(Set<SensorToConfigure> sensors)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.info("Configuring sensors: " + sensors);
        FactoryConfigurationResults results = new FactoryConfigurationResults();

        List<Exception> exceptions = new LinkedList<>();

        sensorLoop:
        for (SensorToConfigure sensor : sensors) {
            logger.debug("Processing sensor to configure: " + sensor);

            if (sensor.isListSensor()) {
                configureListSensor(sensor, results);
            } else {
                configureDataSensor(sensor, results);
            }

        }
        return results;

//        logger.info("Configuring sensors: " + sensors);
//        FactoryConfigurationResults results = new FactoryConfigurationResults();
//        // iterate over all sensors to configure
//        sensorLoop:
//        for (SensorToConfigure sensor : sensors) {
//            // for (SensorToConfigure sensor : sensors) {
//
//            logger.debug("Processing sensor configuration: " + sensor);
//
//            // check that there is no other sensor with the requested key
//            if (configuredSensorsByKey.containsKey(sensor.getKey())) {
//                throw new IllegalArgumentException("Duplicate sensor key '"
//                        + sensor.getKey() + "' requested.");
//            }
//
//            if (!sensor.isListSensor()) {
//                // find the sensor configuration that can handle this requested
//                // actuator
//                for (Class<? extends ConfiguredRsbSensor> configurationClass : knownSensors) {
//
//                    logger.debug("Checking if configuration class "
//                            + configurationClass + " can create sensor "
//                            + sensor);
//
//                    try {
//
//                        Method isSuitableForMethod = configurationClass
//                                .getMethod(IS_SUITABLE_METHOD_NAME,
//                                        SensorToConfigure.class
//                                );
//                        Boolean isSuitable = (Boolean) isSuitableForMethod
//                                .invoke(null, sensor);
//
//                        // if this configuration object is suitable, create an
//                        // instance and initialize it
//                        if (isSuitable) {
//
//                            logger.debug("Configuration class "
//                                    + configurationClass
//                                    + " can create sensor " + sensor);
//
//                            ConfiguredRsbSensor<?> configuredSensor = configurationClass
//                                    .newInstance();
//                            configuredSensor.setKey(sensor.getKey());
//                            configuredSensor.setDataType((Class) sensor
//                                    .getDataTypeClass());
//                            configuredSensor.parseOptions(sensor
//                                    .getSensorOptions());
//
//                            configuredSensorsByKey.put(sensor.getKey(),
//                                    configuredSensor);
//
//                            // stop searching for this sensor
//                            continue sensorLoop;
//
//                        } else {
//                            logger.debug("Configuration class "
//                                    + configurationClass
//                                    + " cannot create sensor " + sensor);
//                        }
//
//                    } catch (NoSuchMethodException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (IllegalAccessException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class "
//                                + "and must be accessible.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (InvocationTargetException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class "
//                                + "and must work without exceptions.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (InstantiationException e) {
//                        assert false : "Configuration objects must be "
//                                + "default constructible.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (ClassCastException e) {
//                        assert false : "isSuitableFor must check "
//                                + "that the data type is accepted.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (CoreObjectCreationException e) {
//                        results.exceptions.add(e);
//                    }
//                }
//            } else {
//
//                // find the sensor configuration that can handle this requested
//                // actuator
//                for (Class<? extends ConfiguredRsbSensorListable> configurationClass : knownListSensors) {
//
//                    logger.debug("Checking if configuration class "
//                            + configurationClass + " can create sensor "
//                            + sensor);
//
//                    try {
//
//                        Method isSuitableForMethod = configurationClass
//                                .getMethod(IS_SUITABLE_METHOD_NAME,
//                                        SensorToConfigure.class
//                                );
//                        Boolean isSuitable = (Boolean) isSuitableForMethod
//                                .invoke(null, sensor);
//
//                        // if this configuration object is suitable, create an
//                        // instance and initialize it
//                        if (isSuitable) {
//
//                            logger.debug("Configuration class "
//                                    + configurationClass
//                                    + " can create list sensor " + sensor);
//
//                            ConfiguredRsbSensorListable<List<?>, ?> configuredSensor = configurationClass
//                                    .newInstance();
//                            configuredSensor.setKey(sensor.getKey());
//                            configuredSensor.setDataType((Class) sensor
//                                    .getDataTypeClass());
//                            configuredSensor.setListType((Class) sensor
//                                    .getListTypeClass());
//                            configuredSensor.parseOptions(sensor
//                                    .getSensorOptions());
//
//                            configuredListSensorsByKey.put(sensor.getKey(),
//                                    configuredSensor);
//
//                            // stop searching for this sensor
//                            continue sensorLoop;
//
//                        } else {
//                            logger.debug("Configuration class "
//                                    + configurationClass
//                                    + " cannot create sensor " + sensor);
//                        }
//
//                    } catch (NoSuchMethodException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (IllegalAccessException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class "
//                                + "and must be accessible.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (InvocationTargetException e) {
//                        assert false : "A method 'isSuitableFor' "
//                                + "must exist for each configuration class "
//                                + "and must work without exceptions.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (InstantiationException e) {
//                        assert false : "Configuration objects must be "
//                                + "default constructible.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (ClassCastException e) {
//                        assert false : "isSuitableFor must check "
//                                + "that the data type is accepted.";
//                        throw new CoreObjectCreationException(e);
//                    } catch (CoreObjectCreationException e) {
//                        results.exceptions.add(e);
//                    }
//
//                }
//            }
//        }
//        return results;
    }

    @Override
    public FactoryConfigurationResults configureCoordinateTransformer(CoordinateTransformerToConfigure conf)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.info("Configuring transformer: " + conf);

        FactoryConfigurationResults results = new FactoryConfigurationResults();

        logger.debug("Processing transformer to configure: " + conf);

        if (!conf.getTransformerClass().equals(RsbRctCoordinateTransformer.class
        )) {
            results.exceptions.add(
                    new CoreObjectCreationException("wrong transformer class " + conf.getTransformerClass()));
        }

        try {
            transformer = new RsbRctCoordinateTransformer();
        } catch (InitializeException | TransformerFactory.TransformerFactoryException ex) {
            results.exceptions.add(ex);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Actuator> T createActuator(String key, Class<T> actuatorClass)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.trace("create actuator: " + actuatorClass);
        //check if actuator was already initialized
        if (isActuatorInitialized.get(key)) {
            return (T) initializedActuatorsByKey.get(key);
        }

        // first check that the requested actuator can be created
        if (!canCreateActuator(key, actuatorClass)) {
            throw new IllegalArgumentException("No actuator with key '" + key
                    + "' and interface class '" + actuatorClass
                    + "' can be created by this factory.");
        }

        ConfiguredObject obj = configuredObjectsByKey.get(key);
        Actuator actuator;

        try {
            Constructor<?> cons = obj.clazz.getConstructor();
            actuator = (Actuator) cons.newInstance();
            actuator.configure(obj.conf);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            logger.error("failed to create instance");
            throw new CoreObjectCreationException(ex);
        } catch (ConfigurationException e) {
            throw new CoreObjectCreationException(e);
        }

        if (actuator instanceof RsbNode) {
            try {
                ((RsbNode) actuator).startNode();
            } catch (InitializeException ex) {
                throw new CoreObjectCreationException("cant initialize node for: " + actuator.getClass());
            }
        } else {
            logger.fatal("critical fail");
            throw new CoreObjectCreationException("cant execute node for: " + actuator.getClass());
        }

        isActuatorInitialized.put(key, true);
        initializedActuatorsByKey.put(key, actuator);

        try {
            return (T) actuator;
        } catch (ClassCastException e) {
            assert false : "canCreateActuator seems to be wrong...";

            throw new IllegalArgumentException("No actuator with key '" + key
                    + "' and interface class '" + actuatorClass
                    + "' can be created by this factory.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Sensor<T> createSensor(String key, Class<T> dataType)
            throws IllegalArgumentException, CoreObjectCreationException {

        logger.trace("create sensor for: " + dataType);
        //check if actuator was already initialized
        if (isSensorInitialized.get(key)) {
            return (Sensor<T>) initializedSensorsByKey.get(key);
        }

        // first check that the requested actuator can be created
        if (!canCreateSensor(key, dataType)) {
            throw new IllegalArgumentException("No Sensor with key '" + key
                    + "' and data class '" + dataType
                    + "' can be created by this factory.");
        }

        ConfiguredSensor obj;
        Sensor sensor;

        try {
            obj = (ConfiguredSensor) configuredObjectsByKey.get(key);
            Constructor<?>[] declaredConstructors = obj.clazz.getDeclaredConstructors();
            if (declaredConstructors.length != 1) {
                throw new NoSuchMethodException("sensor wrong constructors?");
            }
            sensor = (Sensor) declaredConstructors[0].newInstance(obj.data, obj.wire);
            sensor.configure(obj.conf);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            logger.error("failed to create instance");
            throw new CoreObjectCreationException(ex);
        } catch (ConfigurationException ex) {
            throw new CoreObjectCreationException(ex);
        } catch (ClassCastException ex) {
            assert false : "canCreateActuator seems to be wrong...";
            throw new CoreObjectCreationException(ex);
        }

        if (sensor instanceof RsbNode) {
            try {
                ((RsbNode) sensor).startNode();
            } catch (InitializeException ex) {
                logger.error(ex);
                throw new CoreObjectCreationException("cant execute node for: " + sensor.getClass());
            }
        } else {
            logger.fatal("critical fail");
            throw new CoreObjectCreationException("cant execute node for: " + sensor.getClass());
        }

        isSensorInitialized.put(key, true);
        initializedSensorsByKey.put(key, sensor);

        try {
            return (Sensor<T>) sensor;
        } catch (ClassCastException e) {
            assert false : "canCreateActuator seems to be wrong...";

            throw new IllegalArgumentException("No Sensor with key '" + key + "' and data class '" + dataType
                    + "' can be created by this factory.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S extends List<T>, T> Sensor<S> createSensor(String key,
            Class<S> listType, Class<T> dataType)
            throws IllegalArgumentException, CoreObjectCreationException {

        // first check that the requested sensor can be created
        if (!canCreateSensor(key, listType, dataType)) {
            throw new IllegalArgumentException("No list sensor with key '"
                    + key + "', listType '" + listType + "' and dataType '"
                    + dataType + "' can be created by this factory.");
        }

        ConfiguredRsbSensorListable<S, T> configuration = (ConfiguredRsbSensorListable<S, T>) configuredListSensorsByKey
                .get(key);
        Sensor<S> sensor = configuration.createInstanceListable();

        try {
            return sensor;
        } catch (ClassCastException e) {
            assert false : "canCreateSensor seems to be wrong...";

            throw new IllegalArgumentException("No sensor with key '" + key
                    + "', list type class '" + listType
                    + "' and data type class '" + dataType
                    + "' can be created by this factory.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void initialize(Map<String, String> options)
            throws IllegalArgumentException, InitializationException {
        knownActuators = serviceDiscoveryActuator.discoverServicesByInterface(RsbNode.class);
        knownSensors = serviceDiscoverySensor.discoverServicesByInterface(RsbSensor.class);
        knowSlots = serviceDiscoverySlots.discoverServicesByInterface(RsbWorkingMemorySlot.class);

//        // search for subclasses of ConfiguredRsbSensorListable
//        for (Class<? extends ConfiguredRsbSensor> clazz : knownSensors) {
//            if (ConfiguredRsbSensorListable.class.isAssignableFrom(clazz)) {
//                knownListSensors
//                        .add((Class<? extends ConfiguredRsbSensorListable>) clazz);
//            }
//
//        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends WorkingMemory> T createWorkingMemory(String key)
            throws IllegalArgumentException, CoreObjectCreationException {
        // first check that the requested actuator can be created
        if (!canCreateWorkingMemory(key)) {
            throw new IllegalArgumentException("No working memory with key '"
                    + key + "' can be created by this factory.");
        }

        if (memory != null && !memoryRunning) {
            try {
                memory.startNode();
                memoryRunning = true;
            } catch (InitializeException ex) {
                throw new CoreObjectCreationException("could not start memory", ex);
            }
        }

        try {
            return (T) memory;
        } catch (ClassCastException e) {
            assert false : "canCreateWorkingMemory seems to be wrong...";

            throw new IllegalArgumentException("No working memory with key '"
                    + key + "' can be created by this factory.");
        }
    }

    @Override
    public boolean canCreateWorkingMemory(String key) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TransformLookup> T createCoordinateTransformer() throws IllegalArgumentException,
            CoreObjectCreationException {

        // first check that the requested actuator can be created
        if (!canCreateCoordinateTransformer()) {
            throw new IllegalArgumentException("No coordinate transformer can be created by this factory.");
        }

        if (transformer == null) {
            throw new CoreObjectCreationException("transformer missing");
        }

        return (T) transformer;
    }

    @Override
    public boolean canCreateCoordinateTransformer() {
        return true;
    }

    @Override
    public void cleanUp() {
        initializedActuatorsByKey.values().stream().filter((a) -> (a instanceof RsbNode)).forEachOrdered((a) -> {
            ((RsbNode) a).destroyNode();
        });

        initializedSensorsByKey.values().stream().filter((s) -> (s instanceof RsbNode)).forEachOrdered((s) -> {
            ((RsbNode) s).destroyNode();
        });

        if (memory != null) {
            memory.destroyNode();
            memory = null;
        }

        transformer = null;
    }

    @Override
    public FactoryConfigurationResults createAndCacheAllConfiguredObjects() throws CoreObjectCreationException {

        FactoryConfigurationResults res = new FactoryConfigurationResults();

        configuredObjectsByKey.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            ConfiguredObject obj = entry.getValue();

            try {
                if (obj instanceof ConfiguredActuator) {
                    ConfiguredActuator act = (ConfiguredActuator) obj;
                } else if (obj instanceof ConfiguredSensor) {
                    ConfiguredSensor sen = (ConfiguredSensor) obj;
                } else {
                    logger.debug(key + "is neither sensor or actuator");
                }

            } catch (ClassCastException | IllegalArgumentException | CoreObjectCreationException ex) {
                logger.fatal("object " + key + " with class " + obj.clazz + " cached creation error");
                res.exceptions.add(new CoreObjectCreationException("object " + key + " with class " + obj.clazz + " cached creation error"));
            }
        });

        logger.debug("all nodes should be started now");

        return res;
    }
}

//package de.unibi.citec.clf.bonsai.rsb;


//
//
//import static org.easymock.classextension.EasyMock.createMock;
//
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Mock for XcfFactory.
// * 
// * @author lziegler
// */
//public class RsbFactoryMock extends RsbFactory {
//
//	/**
//	 * Constructor.
//	 */
//	public RsbFactoryMock() {
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@SuppressWarnings("rawtypes")
//	@Override
//	public boolean canCreateSensor(String key,
//			Class<? extends List<?>> listType, Class<?> dataType) {
//
//		System.out.println("contains key: "
//				+ configuredListSensorsByKey.containsKey(key));
//		System.out.println("known list sensors: " + knownListSensors.size());
//		for (Class<? extends ConfiguredXcfSensorListable> clazz : knownListSensors) {
//			System.out.println(clazz);
//		}
//		System.out.println("configuredListSensorsByKey: "
//				+ configuredListSensorsByKey.size());
//		for (String key0 : configuredListSensorsByKey.keySet()) {
//			System.out.println(key0 + ": "
//					+ configuredListSensorsByKey.get(key0));
//		}
//
//		if (!configuredListSensorsByKey.containsKey(key)) {
//			return false;
//		}
//
//		System.out.println("is assignable: "
//				+ dataType.isAssignableFrom(configuredListSensorsByKey.get(key)
//						.getDataType()));
//
//		return super.canCreateSensor(key, listType, dataType);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public void initialize(Map<String, String> options)
//			throws IllegalArgumentException, InitializationException {
//
//		// setup xcf connection
//		try {
//
//			xcfManager = createMock(XcfManager.class);
//
//			// subscriber manager
//			if (options.containsKey(OPTION_SUBSCRIBER_CHECK_INTERVAL)) {
//				subscriberManager = new RsbListenerReconnectableManager(
//						xcfManager, Long.parseLong(options
//								.get(OPTION_SUBSCRIBER_CHECK_INTERVAL)));
//			} else {
//				subscriberManager = new RsbListenerReconnectableManager(
//						xcfManager);
//			}
//
//			// remote server manager
//			if (options.containsKey(OPTION_REMOTE_SERVER_CHECK_INTERVAL)) {
//				remoteServerManager = new RsbServerReconnectableManager(
//						xcfManager, Long.parseLong(options
//								.get(OPTION_REMOTE_SERVER_CHECK_INTERVAL)));
//			} else {
//				remoteServerManager = new RsbServerReconnectableManager(
//						xcfManager);
//			}
//
//			// errors on initial subscription
//			if (options.containsKey(OPTION_ERROR_ON_INITIAL_SUBSCRIPTION)) {
//				subscriberManager.setErrorOnXcfTypeInitialization(Boolean
//						.parseBoolean(options
//								.get(OPTION_ERROR_ON_INITIAL_SUBSCRIPTION)));
//			} else {
//				subscriberManager
//						.setErrorOnXcfTypeInitialization(ReconnectableManager.DEFAULT_ERROR_ON_TYPE_INITIALIZATION);
//			}
//
//			// discover sensors and actuators
//			discoverSensorsAndActuators();
//
//		} catch (NumberFormatException e) {
//			throw new IllegalArgumentException("Error parsing a number.", e);
//		}
//
//	}
//
//}

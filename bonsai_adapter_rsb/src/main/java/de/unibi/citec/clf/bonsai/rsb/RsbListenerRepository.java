package de.unibi.citec.clf.bonsai.rsb;



import java.util.HashMap;
import java.util.Map;

import rsb.Factory;
import rsb.Listener;
import rsb.RSBException;
import de.unibi.citec.clf.bonsai.core.object.Sensor;

public class RsbListenerRepository {

	private static class HandlerId<T, S extends Sensor<?>> {
		private String scope;
		private Class<T> type;
		private Class<S> sensor;

		public HandlerId(Class<T> type, Class<S> sensor, String scope) {
			this.scope = scope;
			this.type = type;
			this.sensor = sensor;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((scope == null) ? 0 : scope.hashCode());
			result = prime * result
					+ ((sensor == null) ? 0 : sensor.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof HandlerId))
				return false;
			@SuppressWarnings("rawtypes")
			HandlerId other = (HandlerId) obj;
			if (scope == null) {
				if (other.scope != null)
					return false;
			} else if (!scope.equals(other.scope))
				return false;
			if (sensor == null) {
				if (other.sensor != null)
					return false;
			} else if (!sensor.equals(other.sensor))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}

	private static Map<HandlerId<?, ?>, Listener> rsbListenerMap = new HashMap<>();
	private static Object mapLock = new Object();
	private static RsbListenerRepository instance;

	private RsbListenerRepository() {
	}

	public static RsbListenerRepository getInstance() {
		if (instance == null) {
			instance = new RsbListenerRepository();
		}
		return instance;
	}

	public <T, S extends Sensor<?>> Listener requestListener(
			Class<T> type, Class<S> sensor, String scope) throws RSBException {
		
		HandlerId<T, S> id = new HandlerId<>(type, sensor, scope);
		
		synchronized (mapLock) {
			Factory factory = Factory.getInstance();
			if (!rsbListenerMap.containsKey(id)) {
				final Listener l = factory.createListener(scope);
				l.activate();
				rsbListenerMap.put(id, l);
			}
			return rsbListenerMap.get(id);
		}
	}
}

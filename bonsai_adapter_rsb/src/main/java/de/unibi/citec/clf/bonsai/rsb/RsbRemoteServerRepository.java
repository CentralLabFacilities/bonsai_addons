package de.unibi.citec.clf.bonsai.rsb;



import java.util.HashMap;
import java.util.Map;

import rsb.Factory;
import rsb.RSBException;
import rsb.patterns.RemoteServer;

public class RsbRemoteServerRepository {

	private static Map<String, RemoteServer> rsbRemoteServerMap = new HashMap<>();
	private static RsbRemoteServerRepository instance;
	private static Object mapLock = new Object();

	private RsbRemoteServerRepository() {
	}

	public static RsbRemoteServerRepository getInstance() {
		if (instance == null) {
			instance = new RsbRemoteServerRepository();
		}
		return instance;
	}

	public RemoteServer requestRemoteServer(String scope) throws RSBException {
		Factory factory = Factory.getInstance();
		synchronized (mapLock) {

			if (!rsbRemoteServerMap.containsKey(scope)) {
				rsbRemoteServerMap.put(scope, factory.createRemoteServer(scope));
				rsbRemoteServerMap.get(scope).activate();
			}
			if (rsbRemoteServerMap.get(scope) == null || !rsbRemoteServerMap.get(scope).isActive()) {
				rsbRemoteServerMap.put(scope, factory.createRemoteServer(scope));
				rsbRemoteServerMap.get(scope).activate();
			}
			return rsbRemoteServerMap.get(scope);
		}
	}

	public RemoteServer requestRemoteServer(String scope, double timeout) throws RSBException {
		Factory factory = Factory.getInstance();
		synchronized (mapLock) {
			if (!rsbRemoteServerMap.containsKey(scope)) {
				final RemoteServer s = factory.createRemoteServer(scope, timeout);
				s.activate();
				rsbRemoteServerMap.put(scope, s);
			}
			return rsbRemoteServerMap.get(scope);
		}
	}
}

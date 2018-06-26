package de.unibi.citec.clf.bonsai.rsb.actuators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.patterns.RemoteServer;
import de.unibi.citec.clf.bonsai.actuators.PostureActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lruegeme
 */
public class RsbPostureActuator extends RsbNode implements PostureActuator {

    private RemoteServer server;
    
    public static final String OPTION_TIMEOUT = "timeout";
    private long timeout = -1;

    /**
     * Constructs a new {@link RsbStartStopActuator}.
     *
     * @throws rsb.InitializeException
     */
    public RsbPostureActuator()
        throws InitializeException {
        
    }

    @Override
    public Future<Boolean> executeMotion(String motion, String group) {
        String call = group + " " + motion;
        Future<Boolean> ret = null;
        try {
            ret = server.callAsync("assumePose", call);
        } catch (RSBException ex) {
            Logger.getLogger(RsbPostureActuator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<String> listMotions(String group) {
        List<String> poses = new LinkedList<>();
        try {
            Event ret = server.call("getPoses", timeout);
            String raw = (String) ret.getData();
            poses.addAll(fetchGroup(group, raw));
        } catch (RSBException | ExecutionException | InterruptedException | TimeoutException ex) {
            Logger.getLogger(RsbPostureActuator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return poses;
    }

    protected List<String> fetchGroup(String group, String json) {
        List<String> poses = new LinkedList<>();
        JSONObject list = new JSONObject(json);
        if (group.equals("all")) {
            for (String g : list.keySet()) {
                JSONArray groupPoses = list.getJSONArray(group);
                for (Object o : groupPoses) {
                    poses.add((String) o);
                }
            }
        } else {
            JSONArray groupPoses = list.getJSONArray(group);
            for (Object o : groupPoses) {
                poses.add((String) o);
            }
        }

        return poses;
    }
    


    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }

    @Override
    public void startNode() throws InitializeException {
        try {
            server = RsbRemoteServerRepository.getInstance().requestRemoteServer(scope, timeout);
        } catch (RSBException e) {
            throw new InitializeException(
                "Can not activate rsb server for scope: " + scope, e);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }

    @Override
    public Future<Boolean> assumePose(String pose, String group) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

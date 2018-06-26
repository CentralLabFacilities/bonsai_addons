
package de.unibi.citec.clf.bonsai.behavioral;


import de.unibi.citec.clf.bonsai.behavioral.config.Action;
import de.unibi.citec.clf.bonsai.behavioral.config.BehaviorConfiguration;
import de.unibi.citec.clf.bonsai.behavioral.config.Statemachine;
import de.unibi.citec.clf.bonsai.engine.communication.RSBController;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import rsb.Event;
import rsb.Factory;
import rsb.Handler;
import rsb.Listener;
import rst.statemachine.StateChangeType;

/**
 *
 * @author lruegeme
 */
class BehaviorALControl {

    private final Map<String, String> includeMapping;

    private static class TransitionWatcher implements Handler {

        private static String curStart = "";
        private static boolean finished = false;
        private static boolean success = false;

        public static synchronized void setCurrent(String s) {
            //logger.debug("setCurrent: "+s);
            curStart = s;
            finished = false;
        }

        public static synchronized String getCurrent() {
            //logger.debug("getcurrent: "+curStart);
            return curStart;
        }

        public static synchronized void setFinished(boolean s) {
            //logger.debug("setFinished: "+s);
            finished = s;
        }

        public static synchronized boolean getFinished() {
            //logger.debug("getFinished: "+finished);
            return finished;
        }

        public static synchronized void setSuccess(boolean s) {
            //logger.debug("setSuccess: "+s);
            success = s;
        }

        public static synchronized boolean getSuccess() {
            //logger.debug("getSuccess: "+success);
            return success;
        }

        //TODO mutexen
        @Override
        public void internalNotify(Event e) {
            if (e.getType() != StateChangeType.StateChange.class) {
                logger.warn("event has wrong type. (not StateChange)");
                return;
            }

            //logger.info("e.getMetaData():" +e.getMetaData());
            //logger.info("e.getId():" +e.getId());
            StateChangeType.StateChange a = (StateChangeType.StateChange) e.getData();

            if (a.getFromState().isEmpty()) {
                logger.trace("[SM]STARTING: >> " + a.getToState());
                if (a.getToState().equals(TransitionWatcher.getCurrent())) {
                    TransitionWatcher.setFinished(false);
                }
            } else if (a.getToState().isEmpty()) {
                logger.trace("[SM]FINISHED: << " + a.getFromState());
                TransitionWatcher.setFinished(true);
                if (a.getFromState().equals("End")) {
                    TransitionWatcher.setSuccess(true);
                } else {
                    TransitionWatcher.setSuccess(false);
                }
            } else {
                logger.trace("[SM]TRANSITION:" + a.getFromState() + " >>[" + a.getCause() + "]>> " + a.getToState());
            }

        }
    }

    BehaviorConfiguration conf;
    RSBController remote;
    Statemachine current = null;

    private static final Logger logger = Logger.getLogger(BehaviorALControl.class);

    BehaviorALControl(String bonsaiScope, BehaviorConfiguration c, Map<String, String> includeMapping) throws Exception {
        remote = new RSBController(bonsaiScope + "/server");
        conf = c;
        this.includeMapping = includeMapping;

        Listener listener = Factory.getInstance().createListener(bonsaiScope + "/info/transitions");
        listener.addHandler(new TransitionWatcher(), true);
        listener.activate();

    }

    public boolean isRunning() {
        return !TransitionWatcher.getCurrent().isEmpty();
    }

    public boolean isFinished() {
        return TransitionWatcher.getFinished();
    }

    public boolean isSuccess() {
        return TransitionWatcher.getSuccess();
    }

    public boolean execute(Map<String, String> params) {
        logger.info("executing current with params:" + params);
        Action tar = current.byName(params.get("name"));
        Map<String, String> merged = new HashMap<>();
        if (tar != null) {
            if (tar.params != null) {
                merged.putAll(tar.params);
            }
            merged.putAll(params);
            merged.remove("name");
            remote.setParams(merged);

            boolean b = remote.start(tar.target);
            if (b) {
                TransitionWatcher.setCurrent(tar.target);
            }
            return b;
        }

        logger.error(params.get("name") + " not in actions for current statemachine:" + current.name);
        return false;

    }

    public boolean load(String ev) {
        Statemachine tar = conf.statemachineByName(ev);
        if (tar != null) {
            logger.info("loading statemachine: " + tar.name);
            String error = remote.load(tar.getConfig(), tar.getTask(),includeMapping);
            logger.info(error);
            //TODO: Hack stuff, check if okay
            Boolean noError;
            noError = !(error.equals("got wrong return type") || error.equals("unknown error"));
            current = tar;
            TransitionWatcher.setCurrent("");
            return noError;
        } else {
            TransitionWatcher.setCurrent("");
            return false;
        }
    }

    public List<String> getStatemachines() {
        LinkedList<String> ret = new LinkedList<>();
        for(Statemachine sm : conf.statemachines) {
            ret.add(sm.name);
        }
        return ret;
    }

    public Map<String,String> getParamMap(String action) {
        Action a = current.byName(action);
        if(a !=null) {
            return a.params;
        }
        return null;
    }

    public List<String> getActions() {
        LinkedList<String> ret = new LinkedList<>();
        for(Action a : current.actions) {
            ret.add(a.name);
        }
        return ret;
    }

}

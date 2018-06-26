package de.unibi.citec.clf.bonsai.behavioral;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import rsb.Event;
import rsb.Factory;
import rsb.RSBException;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;
import rst.generic.DictionaryType;
import rst.generic.KeyValuePairType;
import rst.generic.ValueType;

/**
 * @author lruegeme
 */
public class RSBBehaviorALServer {

    private static final Logger logger = Logger.getLogger(RSBBehaviorALServer.class);
    public static final String CB_START = "startAction";
    public static final String CB_LISTACT = "listActions";
    public static final String CB_DONE = "isDone";
    public static final String CB_PARAMS = "getActionParams";
    public static final String CB_SETSM = "changeScenario";
    public static final String CB_LISTSM = "listScenarios";
    private BehaviorALControl bhc;

    public RSBBehaviorALServer(BehaviorALControl bhc) {
        this.bhc = bhc;
    }


    private class WaitForFinished extends EventCallback {

        @Override
        public Event invoke(Event event) throws Exception {
            logger.info("called finished: " + event.getData());
            logger.info("r:" + bhc.isRunning() + " f:" + bhc.isFinished() + " s:" + bhc.isSuccess());

            if (!bhc.isRunning()) {
                throw new Exception("Statemachine not running");
            }

            while (!bhc.isFinished()) {
                Thread.sleep(100);
            }

            return new Event(Boolean.class, bhc.isSuccess());
        }
    }

    private class StartCB extends EventCallback {

        @Override
        public Event invoke(Event event) {
            logger.info("called start: " + event.getData());
            if (event.getType() == String.class) {
                String ev = (String) event.getData();
                return new Event(Boolean.class, bhc.execute(parseParams(ev)));
            } else if (event.getType() == DictionaryType.Dictionary.class) {
                DictionaryType.Dictionary ev = (DictionaryType.Dictionary) event.getData();
                return new Event(Boolean.class, bhc.execute(parseParams(ev)));
            }
            logger.error("wrong event type");
            return null;
        }

        private Map<String, String> parseParams(DictionaryType.Dictionary dict) {
            Map<String, String> map = new HashMap<>();
            for (KeyValuePairType.KeyValuePair kv : dict.getEntriesList()) {
                if ((kv.getValue().getType() == ValueType.Value.Type.STRING)) {
                    map.put(kv.getKey(), kv.getValue().getString());
                }
            }
            return map;
        }

        private Map<String, String> parseParams(String ev) {
            Map<String, String> map = new HashMap<>();
            String[] params = ev.split(",| ");
            for (String a : params) {
                if (a.contains("=")) {
                    map.put(a.substring(0, a.indexOf("=")), a.substring(a.indexOf("=") + 1, a.length()));
                } else if (!map.containsKey("name")) {
                    map.put("name", a);
                }
            }
            return map;
        }
    }

    private class ListActionsCB extends EventCallback {

        @Override
        public Event invoke(Event event) {
            List<String> sm = bhc.getActions();
            String ret = "";
            for (String s : sm) {
                if (!ret.isEmpty()) ret += ";";
                ret += s;
            }
            return new Event(String.class, ret);
        }
    }

    private class GetParamsCB extends EventCallback {

        @Override
        public Event invoke(Event event) {
            if (event.getType() != String.class) {
                logger.error("wrong event type");
                return null;
            }
            String action = (String) event.getData();
            Map<String,String> params = bhc.getParamMap(action);

            DictionaryType.Dictionary.Builder b = DictionaryType.Dictionary.newBuilder();
            KeyValuePairType.KeyValuePair.Builder kv = KeyValuePairType.KeyValuePair.newBuilder();
            for (String key : params.keySet()) {
                kv.setKey(key);
                ValueType.Value.Builder v = kv.getValueBuilder();
                v.setType(ValueType.Value.Type.STRING);
                v.setString(params.get(key));
                //kv.setValue();
                b.addEntries(kv.build());
            }

            //set action name
            kv.setKey("name");
            ValueType.Value.Builder v = kv.getValueBuilder();
            v.setType(ValueType.Value.Type.STRING);
            v.setString(action);
            b.addEntries(kv.build());

            return new Event(DictionaryType.Dictionary.class, b.build());
        }
    }

    private class SetStatemchineCB extends EventCallback {

        @Override
        public Event invoke(Event event) {
            if (event.getType() != String.class) {
                logger.error("wrong event type");
                return null;
            }
            return new Event(Boolean.class, bhc.load((String) event.getData()));
        }
    }

    private class ListStatemachinesCB extends EventCallback {

        @Override
        public Event invoke(Event event) {
            List<String> sm = bhc.getStatemachines();
            String ret = "";
            for (String s : sm) {
                if (!ret.isEmpty()) ret += ";";
                ret += s;
            }
            return new Event(String.class, ret);
        }
    }


    public void init(String scope) {
        final LocalServer server = Factory.getInstance().createLocalServer(
                scope);
        try {
            server.addMethod(CB_START, new StartCB());

            server.addMethod(CB_LISTACT, new ListActionsCB());
            server.addMethod(CB_DONE, new WaitForFinished());

            server.addMethod(CB_PARAMS, new GetParamsCB());
            server.addMethod(CB_SETSM, new SetStatemchineCB());
            server.addMethod(CB_LISTSM, new ListStatemachinesCB());
        } catch (RSBException ex) {
            Logger.getLogger(RSBBehaviorALServer.class.getName()).fatal(ex);
        }

        try {
            server.activate();
        } catch (RSBException ex) {
            Logger.getLogger(RSBBehaviorALServer.class.getName()).fatal(ex);
        }

        logger.info("RSB Behavior server started on scope:" + scope);
    }

}

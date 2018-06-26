package de.unibi.citec.clf.bonsai.engine.communication;



import de.unibi.citec.clf.bonsai.engine.control.StateMachineController;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import de.unibi.citec.clf.bonsai.engine.scxml.BonsaiTransition;
import org.apache.commons.scxml.model.Transition;
import org.apache.log4j.Logger;
import rsb.Event;
import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;
import rst.generic.DictionaryType;
import rst.generic.KeyValuePairType;
import rst.generic.ValueType;

/**
 *
 * @author semeyerz
 */
public class RSBServer implements SCXMLServer{

    private static Logger logger = Logger.getLogger(RSBServer.class);
    private StateMachineController smc;
    
    private Informer<Void> alivePingInformer;
    private Informer<String> stateMapInformer;

    public RSBServer(StateMachineController smc) {
        this.smc = smc;
    }

    private static Map<String, String> extractDictonary(DictionaryType.Dictionary d) {
        Map<String, String> map = new HashMap<>();
        List<KeyValuePairType.KeyValuePair> entriesList = d.getEntriesList();
        for (KeyValuePairType.KeyValuePair kv : entriesList) {
            if (kv.getValue().getType() == ValueType.Value.Type.STRING) {
                map.put(kv.getKey(), kv.getValue().getString());
            }
        }
//        logger.fatal("extracted map:"+map);
        return map;
    }

    private String saveFile(String name, String data) {
        FileOutputStream fop = null;
        File file;

        try {

            file = new File(name);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = data.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            logger.debug("written:" + file.getAbsolutePath());

            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void init(String scope) {
        
        

        DefaultConverterRepository.getDefaultConverterRepository().addConverter(
                new ProtocolBufferConverter<>(DictionaryType.Dictionary.getDefaultInstance()));

        final LocalServer server = Factory.getInstance().createLocalServer(
                scope);

        try {
            server.addMethod("getTransition", new GetTransitionCallback());
            server.addMethod("getStateList", new AllStateIdCallback());
            server.addMethod("getCurrentStates", new CurrentStatesCallback());
            server.addMethod("pause", new PauseCallback());
            server.addMethod("stop", new StopCallback());
            server.addMethod("continue", new ContinueCallback());
            server.addMethod("fireEvent", new FireEventCallback());
            server.addMethod("execute", new ExecuteCallback());
            server.addMethod("setConfigPath", new ConfigPathCallback());
            server.addMethod("setTaskPath", new TaskPathCallback());
            server.addMethod("sendConfig", new ConfigCallback());
            server.addMethod("sendTask", new TaskCallback());
            server.addMethod("load", new LoadCallback());
            server.addMethod("setParams", new SetParamCallback());
            server.addMethod("stopAutomaticEvents", new ToggleEventsCallback());
        } catch (RSBException ex) {
            logger.fatal(ex);
        }

        try {
            server.activate();
        } catch (RSBException ex) {
            logger.fatal(ex);
            System.exit(1);
        }

        logger.info("RSB server started on scope:" + scope);
        Factory factory = Factory.getInstance();
         try {
            alivePingInformer = factory.createInformer("/bonsai/alive");
            alivePingInformer.activate();
            stateMapInformer = factory.createInformer("/bonsai/states");
            stateMapInformer.activate();
        } catch (InitializeException ex) {
            logger.fatal(ex);
        } catch (RSBException ex) {
            logger.fatal(ex);
        }
    }

    @Override
    public boolean sendStatesWithTransitions() {
        return false;
    }

    @Override
    public void sendCurrentStates(List<String> stateList) {
        String states = "";
        for(String s : stateList) {
            if(!states.isEmpty()) states += ";";
            states += s;
        }

        Event event = new Event(stateMapInformer.getScope(), String.class, states);
        try {
            stateMapInformer.send(event);
        } catch (RSBException ex) {
            logger.warn(ex);
        }
    }

    @Override
    public void sendCurrentStatesAndTransitions(List<String> states, List<BonsaiTransition> transitions) {

    }

    @Override
    public void sendStatus(String status) {
        Event event = new Event(alivePingInformer.getScope(), String.class, status);
        try {
            alivePingInformer.send(event);
        } catch (RSBException ex) {
            logger.warn(ex);
        }
    }

    private class ToggleEventsCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            if(event.getType() != Boolean.class) {
                logger.error("wrong parameter");
                return new Event(Boolean.class, false);
            }

            smc.enableAutomaticEvents(!(Boolean) event.getData());
            return new Event(Boolean.class, true);
        }
    }

    private class CurrentStatesCallback extends EventCallback {

        @Override
        public Event invoke(Event event){
            List<String> list = smc.getCurrentStateList();
            String states = "";
            for (String s : list) {
                if (!states.isEmpty()) {
                    states += ",";
                }
                states += s;
            }

            return new Event(String.class, states);
        }
    }

    private class AllStateIdCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            List<String> list = smc.getAllStateIds();
            String states = "";
            for (String s : list) {
                if (!states.isEmpty()) {
                    states += ",";
                }
                states += s;
            }

            return new Event(String.class, states);
        }
    }

    private class ExecuteCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            logger.trace("executed callback called");
            if (event != null && event.getType() == String.class) {
                smc.executeStateMachine((String) event.getData());
            } else {
                smc.executeStateMachine();
            }

            return new Event(Boolean.class, true);
        }
    }

    private class LoadCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            String errors = smc.load().toString();
            //logger.fatal("loading results");
            return new Event(String.class, errors);
        }
    }

    private class ConfigPathCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            String ev = event.getData().toString();
            if (!ev.isEmpty()) {
                smc.setConfigPath(ev);
                return new Event(Boolean.class, true);
            } else {
                return new Event(Boolean.class, false);
            }
        }

    }

    private class ConfigCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            String ev = event.getData().toString();
            if (!ev.isEmpty()) {
                smc.setConfigPath(saveFile("config", ev));
                return new Event(Boolean.class, true);
            } else {
                return new Event(Boolean.class, false);
            }
        }

    }

    private class TaskCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            String ev = event.getData().toString();
            if (!ev.isEmpty()) {
                smc.setTaskPath(saveFile("task", ev));
                return new Event(Boolean.class, true);
            } else {
                return new Event(Boolean.class, false);
            }
        }
    }

    private class TaskPathCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            String ev = event.getData().toString();
            if (!ev.isEmpty()) {
                smc.setTaskPath(ev);
                return new Event(Boolean.class, true);
            } else {
                return new Event(Boolean.class, false);
            }
        }
    }

    private class SetParamCallback extends EventCallback {

        @Override
        public Event invoke(Event event){
            logger.trace("setParams called");
            if (event.getType() == DictionaryType.Dictionary.class) {
                smc.setDatamodelParams(extractDictonary((DictionaryType.Dictionary) event.getData()));
            }
            return new Event(Boolean.class, false);
        }

    }

    private class PauseCallback extends EventCallback {

        @Override
        public Event invoke(Event event){
            smc.pauseStateMachine();
            return new Event(Boolean.class, true);
        }
    }

    private class StopCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            smc.stopStateMachine();
            return new Event(Boolean.class, true);
        }
    }

    private class ContinueCallback extends EventCallback {

        @Override
        public Event invoke(Event event){
            smc.continueStateMachine();
            return new Event(Boolean.class, true);
        }
    }

    private class FireEventCallback extends EventCallback {

        @Override
        public Event invoke(Event event){
            String ev = event.getData().toString();
            System.out.println("fireEventCallback with " + ev);
            if (!ev.isEmpty()) {
                smc.fireEvent(ev);
                return new Event(Boolean.class, true);
            } else {
                return new Event(Boolean.class, false);
            }

        }
    }

    private class GetTransitionCallback extends EventCallback {

        @Override
        public Event invoke(Event event) {
            Collection<Transition> c = smc.getPossibleTransitions();
            String states = "";
            for (Transition s : c) {
                if (!states.isEmpty()) {
                    states += ",";
                }
                states += s.getEvent();
            }

            return new Event(String.class, states);
        }
    }

}

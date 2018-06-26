
package de.unibi.citec.clf.bonsai.engine.communication;


import de.unibi.citec.clf.bonsai.core.configuration.XmlConfigurationParser;
import de.unibi.citec.clf.bonsai.engine.SCXMLDecoder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import nu.xom.ParsingException;
import org.xml.sax.SAXException;
import rsb.Event;
import rsb.Factory;
import rsb.RSBException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.RemoteServer;
import rst.generic.DictionaryType;
import rst.generic.DictionaryType.Dictionary;
import rst.generic.KeyValuePairType;
import rst.generic.ValueType;
import rst.statemachine.StateChangeType.StateChange;

/**
 *
 * @author lruegeme
 */
public class RSBController implements SCXMLRemote {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RSBController.class);

    private RemoteServer remoteServer;
    
    public RSBController(String scope) throws Exception {
     
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(
            new ProtocolBufferConverter<>(StateChange.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(
            new ProtocolBufferConverter<>(Dictionary.getDefaultInstance()));

        remoteServer = Factory.getInstance().createRemoteServer(scope);

        try {
            remoteServer.activate();
            logger.debug("initialized remote connetion to statemachine " + scope);
        } catch (RSBException ex) {
            Logger.getLogger(RSBController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.exit(0);
        //System.exit(0);

        //System.exit(0);
        //System.exit(0);
    }
    
    @Override
    public List<String> getCurrentStates() {
        List<String> states = new LinkedList<>();
        try {
            Event e = remoteServer.call("getCurrentStates");
            String s = (String) e.getData();
            String[] split = s.split(",");
            states.addAll(Arrays.asList(split));
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e);
        }
        
        return states;
    }
    
    @Override
    public boolean stopAutomaticEvents(boolean b) {
        try {
            Event e = remoteServer.call("stopAutomaticEvents",new Event(Boolean.class, b));
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public void exit() {
        try {
            remoteServer.deactivate();
        } catch (RSBException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getStateIds() {
        List<String> states = new LinkedList<>();
        try {
            Event e = remoteServer.call("getStateList");
            String s = (String) e.getData();
            String[] split = s.split(",");
            states.addAll(Arrays.asList(split));
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e);
        }
        
        return states;
    }

    @Override
    public String load(String pathToConfig, String pathToTask, Map<String, String> includeMapping) {
        try {
            logger.info("resolving config file... "+pathToConfig);
            String config = XmlConfigurationParser.transformXML(new File(pathToConfig)).toXML();
            logger.info("resolving scxml file...  "+pathToTask);
            String task = SCXMLDecoder.transformSCXML(new File(pathToTask), includeMapping);
            logger.debug("@@@ config: @@@");
            logger.debug(config);
            logger.debug("@@@ TASK: @@@");
            logger.debug(task);

            logger.trace("set config");
            remoteServer.call("sendConfig", new Event(String.class, config));
            // remoteServer.call("setConfigPath", new Event(String.class, pathToConfig));
            // System.out.println("WARNING: config has to be resolved on remote machine");
            logger.trace("set task");
            remoteServer.call("sendTask", new Event(String.class, task));

            logger.debug("trigger load");
            Event e = remoteServer.call("load");
            if (e.getType() != String.class) {
                return "got wrong return type";
            } else {
                return (String)e.getData();
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException | IOException | TransformerException | SAXException | ParsingException ex) {
            logger.error(ex);
        }
        return "unknown error";
    }

    @Override
    public boolean start() {
        return start("");
    }

    @Override
    public boolean setParams(Map<String, String> map) {

        final DictionaryType.Dictionary.Builder b = DictionaryType.Dictionary.newBuilder();
        KeyValuePairType.KeyValuePair.Builder kv = KeyValuePairType.KeyValuePair.newBuilder();
        for (String key : map.keySet()) {
            kv.setKey(key);
            ValueType.Value.Builder v = kv.getValueBuilder();
            v.setType(ValueType.Value.Type.STRING);
            v.setString(map.get(key));
            //kv.setValue();
            b.addEntries(kv.build());
        }

        try {
            Event e = remoteServer.call("setParams", new Event(DictionaryType.Dictionary.class, b.build()));
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (ExecutionException | InterruptedException | TimeoutException | RSBException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Deprecated
    public boolean setParam(Dictionary dict) {
        try {
            Event e = remoteServer.call("setParams", new Event(DictionaryType.Dictionary.class, dict));
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (ExecutionException | InterruptedException | TimeoutException | RSBException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public boolean start(String state) {
        try {
            Event e = remoteServer.call("execute", new Event(String.class, state));
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (ExecutionException | InterruptedException | TimeoutException | RSBException ex) {
            logger.error(ex);
        }
        return false;
    }
    
    

    @Override
    public boolean fireEvent(String event) {
        try {
            Event e = remoteServer.call("fireEvent", new Event(String.class, event));
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (ExecutionException | InterruptedException | TimeoutException | RSBException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public List<String> getTransitions() {
        List<String> list = new LinkedList<>();
        try {
            Event e = remoteServer.call("getTransition");
            String s = (String) e.getData();
            String[] split = s.split(",");
            list.addAll(Arrays.asList(split));

        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(RSBController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public boolean pause() {
        try {
            Event e = remoteServer.call("pause");
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public boolean resume() {
        try {
            Event e = remoteServer.call("continue");
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public boolean stop() {
        try {
            Event e = remoteServer.call("stop");
            if (e.getType() != Boolean.class) {
                return false;
            } else {
                return (Boolean) e.getData();
            }
        } catch (RSBException | InterruptedException | ExecutionException | TimeoutException ex) {
            logger.error(ex);
        }
        return false;
    }

}

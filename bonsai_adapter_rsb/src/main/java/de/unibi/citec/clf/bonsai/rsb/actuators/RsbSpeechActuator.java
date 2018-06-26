package de.unibi.citec.clf.bonsai.rsb.actuators;



import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Logger;

import rsb.Event;
import rsb.RSBException;
import rsb.patterns.RemoteServer;
import de.unibi.citec.clf.bonsai.actuators.SpeechActuator;
import de.unibi.citec.clf.bonsai.actuators.SpeechActuatorListener;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import java.util.ArrayList;
import rsb.InitializeException;


/**
 * Actuator which can be used to access the speech synthesizer.
 * 
 * @author ssharma
 */
public class RsbSpeechActuator extends RsbNode implements SpeechActuator {
    
    public static final String OPTION_TIMEOUT = "timeout";
    public static final String OPTION_PLAIN_TEXT = "plainText";

    private Logger logger = Logger.getLogger(getClass());
    private static final Object remoteServerLock = new Object();
    private RemoteServer remoteServer;
    private static ArrayList<SpeechActuatorListener> listeners = new ArrayList<>();
    
    private boolean plainText = false;
    private long timeout = 8000;

    public RsbSpeechActuator() throws RSBException {      
    }
    
    @Override 
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
        plainText = conf.requestOptionalBool(OPTION_PLAIN_TEXT, plainText);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Void> sayAsync(String text) throws IOException {
        logger.info("Biron: " + text);
        if (isAllowedSayChar(text)) {

            synchronized (remoteServerLock) {
                if (remoteServer == null) {
                    throw new IllegalStateException("No remote server set.");
                }
            }

            Element elem = new Element("raw");
            elem.appendChild(text);
            Document doc = new Document(elem);
            try {
                logger.debug("call listener");
                Future<Void> result;
                   String e = "";
                    // e.setScope(new Scope(scopePS));
                    e = doc.toXML();
                    
                    if(plainText) e = text;
                synchronized (remoteServerLock) {
                    logger.debug("say async");
                    result = remoteServer.callAsync("say_raw",e);
                }
                callListeners(text, result);
                return result;
            } catch (RSBException e) {
                logger.error(e);
                throw new IOException(e);
            }
        } else {
        	return new Future<Void>() {
				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					return true;
				}
				@Override
				public Void get() throws InterruptedException,
						ExecutionException {
					return null;
				}
				@Override
				public Void get(long timeout, TimeUnit unit)
						throws InterruptedException, ExecutionException,
						TimeoutException {
					return null;
				}
				@Override
				public boolean isCancelled() {
					return false;
				}
				@Override
				public boolean isDone() {
					return true;
				}
			};
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws java.io.IOException
     */
    @Override
    public void say(String text) throws IOException {
		try {
                    Future<Void> res = sayAsync(text);
                    logger.error("say called");
                    res.get(timeout, TimeUnit.MILLISECONDS);
                    logger.error("say returned");
		} catch (InterruptedException | ExecutionException e) {
			throw new IOException(e);
		} catch (TimeoutException e) {
                    logger.error("say timed out "+timeout);
                }
    }

    @Override
    public void sayAccentuated(String accentuatedText, boolean async) throws IOException {

        if (isAllowedSayChar(accentuatedText)) {

            synchronized (remoteServerLock) {
                if (remoteServer == null) {
                    throw new IllegalStateException("No remote server set.");
                }
            }

            Element elem = new Element("accentuatedText");
            Element text = new Element("text");
            Element bool = new Element("bool");

            text.appendChild(accentuatedText);
            bool.appendChild(Boolean.toString(async));
            elem.appendChild(text);
            elem.appendChild(bool);

            Document doc = new Document(elem);

            try {
            	Future<Void> result;
                synchronized (remoteServerLock) {

                    Event e = new Event();
                    // e.setScope(new Scope(scopePS));
                    e.setData(doc.toXML());
                    
                    if(plainText) e.setData(accentuatedText);
                    
                    final Future<Event> eventResult = remoteServer.callAsync("say_accented", e);
                    result = new Future<Void>() {
    					@Override
    					public boolean isDone() {
    						return eventResult.isDone();
    					}
    					@Override
    					public boolean isCancelled() {
    						return eventResult.isCancelled();
    					}
    					@Override
    					public Void get(long timeout, TimeUnit unit) throws InterruptedException,
    							ExecutionException, TimeoutException {
    						eventResult.get(timeout, unit);
    						return null;
    					}
    					@Override
    					public Void get() throws InterruptedException, ExecutionException {
    						eventResult.get();
    						return null;
    					}
    					@Override
    					public boolean cancel(boolean mayInterruptIfRunning) {
    						return eventResult.cancel(mayInterruptIfRunning);
    					}
    				};
                }
                
                
                callListeners(accentuatedText, result);
                if (!async){
					result.get(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (RSBException|InterruptedException | ExecutionException
					| TimeoutException e) {
                logger.error(e);
                throw new IOException(e);
            }
        }
    }

    @Override
    public void sayAccentuated(String accentuatedText, boolean async, String prosodyConfig) throws IOException {

        if (isAllowedSayChar(accentuatedText)) {

            synchronized (remoteServerLock) {
                if (remoteServer == null) {
                    throw new IllegalStateException("No remote server set.");
                }
            }

            Element elem = new Element("accentuatedText");
            Element text = new Element("text");
            Element bool = new Element("bool");
            Element prosody = new Element("prosody");

            text.appendChild(accentuatedText);
            bool.appendChild(Boolean.toString(async));
            prosody.appendChild(prosodyConfig);

            elem.appendChild(text);
            elem.appendChild(bool);
            elem.appendChild(prosody);

            Document doc = new Document(elem);
            try {
            	Future<Void> result;
                synchronized (remoteServerLock) {

                    Event e = new Event();
                    // e.setScope(new Scope(scopePS));
                    e.setData(doc.toXML());
                    if(plainText) e.setData(accentuatedText);
                    final Future<Event> eventResult = remoteServer.callAsync("say_accented", e);
                    result = new Future<Void>() {
    					@Override
    					public boolean isDone() {
    						return eventResult.isDone();
    					}
    					@Override
    					public boolean isCancelled() {
    						return eventResult.isCancelled();
    					}
    					@Override
    					public Void get(long timeout, TimeUnit unit) throws InterruptedException,
    							ExecutionException, TimeoutException {
    						eventResult.get(timeout, unit);
    						return null;
    					}
    					@Override
    					public Void get() throws InterruptedException, ExecutionException {
    						eventResult.get();
    						return null;
    					}
    					@Override
    					public boolean cancel(boolean mayInterruptIfRunning) {
    						return eventResult.cancel(mayInterruptIfRunning);
    					}
    				};
                }
                
                
                callListeners(accentuatedText, result);
                if (!async){
					result.get(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (RSBException|InterruptedException | ExecutionException
					| TimeoutException e) {
                logger.error(e);
                throw new IOException(e);
            }
        }
    }

    private static void callListeners(String text, Future<Void> result) {
        for (SpeechActuatorListener listener : listeners) {
            listener.newUtterance(text, result);
        }
    }

    @Override
    public void sayAccentuated(String accented_text) throws IOException {
        sayAccentuated(accented_text, false);
    }

    @Override
    public void sayAccentuated(String accented_text, String prosodyConfig) throws IOException {
        sayAccentuated(accented_text, false, prosodyConfig);
    }

    /**
     * check if string contains symbols which throw a null-pointer exception
     * 
     * @param str
     *            say string which should be spoken
     * @return true if the string is allowed else false
     */
    private boolean isAllowedSayChar(String str) {

        Pattern p = Pattern.compile("[ ]*?[.,!?;:()]+");
        Matcher m = p.matcher(str);

        if (m.matches() || str.equals("")) {
            logger.error("String to say contains one of the following symbols: ,.!?():; see: " + str);
            return false;
        }
        return true;
    }

    @Override
    public void startNode() throws InitializeException {
        try {
            remoteServer = RsbRemoteServerRepository.getInstance().requestRemoteServer(scope, timeout);
        } catch (RSBException ex) {
            throw new InitializeException(ex);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }
}

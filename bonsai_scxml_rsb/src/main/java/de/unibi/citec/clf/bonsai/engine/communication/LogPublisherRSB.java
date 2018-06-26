
package de.unibi.citec.clf.bonsai.engine.communication;



import java.util.logging.Level;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.RSBException;


/**
 *
 * @author lruegeme
 */
public class LogPublisherRSB extends AppenderSkeleton {
    
    //TODO better Log Type
    
    private final AsyncAppender asynAppender = new AsyncAppender();
    private String basescope = "";
    private static final Logger logger = Logger.getLogger(LogPublisherRSB.class);
   // private final Informer<rst.logging.MessageType> informer2;
    private final Informer<String>  informer;
    
    public LogPublisherRSB(String scope)  throws InitializeException  {
        basescope = scope;
        informer = Factory.getInstance().createInformer(basescope);
        try {
            informer.activate();
            //  informer2 = Factory.getInstance().createInformer(basescope);
            // informer2.activate();
        } catch (RSBException ex) {
            java.util.logging.Logger.getLogger(LogPublisherRSB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        init();
        
    }
    
    private void init() {
        asynAppender.addAppender(this);
	Logger.getRootLogger().addAppender(asynAppender);
    }

    @Override
    protected void append(LoggingEvent le) {
        
//        try {
//            informer.send(le.getLevel().toInt() + " " + le.getLoggerName() + " " + le.getMessage().toString());
//        } catch (RSBException ex) {
//            Logger.getRootLogger().removeAppender(asynAppender);
//            logger.error(ex);
//        }
        
    }

    @Override
    public void close() {
        //informer.deactivate();
        Logger.getRootLogger().removeAppender(asynAppender);
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
    
    
}

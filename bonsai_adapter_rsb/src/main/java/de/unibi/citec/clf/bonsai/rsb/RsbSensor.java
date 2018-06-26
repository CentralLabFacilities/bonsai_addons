package de.unibi.citec.clf.bonsai.rsb;

import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import org.apache.log4j.Logger;
import rsb.Event;
import rsb.Handler;

/**
 *
 * @author lruegeme
 * @param <DataType>
 * @param <RstType>
 */
public abstract class RsbSensor <DataType extends Object, RstType extends Object> extends RsbNode implements Handler, Sensor<DataType>{
    
    
    public static final String OPTION_BUFFER_SIZE = "bufferSize";
    
    protected Class<DataType> dataTypeClass;
    protected Class<RstType> rstType;
    
    private final Logger logger = Logger.getLogger(getClass());

    protected int bufferSize;
   
    public RsbSensor(Class<DataType> typeClass, Class<RstType> rstType) {
        this.rstType = rstType;
        this.dataTypeClass = typeClass;
    }

    @Override 
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        bufferSize = conf.requestOptionalInt(OPTION_BUFFER_SIZE, 50);
    }
    
    @Override
    public final Class<DataType> getDataType() {
        return dataTypeClass;
    }

    public final Class<RstType> getMsgType() {
        return rstType;
    }
    
    @Override
    public void internalNotify(Event event) {
        handleEvent(event);
    }
    
    public abstract void handleEvent(Event data);
}

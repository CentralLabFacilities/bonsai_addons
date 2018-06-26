package de.unibi.citec.clf.bonsai.rsb;

import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import rsb.InitializeException;
import rsb.converter.ProtocolBufferConverter;

import java.util.List;

/**
 *
 * @author lruegeme
 */
public abstract class RsbNode {
    
    public static final String OPTION_SCOPE = "scope";
    protected String scope;
    
    public abstract void startNode() throws InitializeException;
    
    public abstract void destroyNode();
    
    public final void cleanUp() {}

    public void configure(IObjectConfigurator conf) {
        scope = conf.requestValue(OPTION_SCOPE);
    }

}

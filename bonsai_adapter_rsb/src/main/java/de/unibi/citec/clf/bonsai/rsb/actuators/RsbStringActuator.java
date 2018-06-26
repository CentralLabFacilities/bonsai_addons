package de.unibi.citec.clf.bonsai.rsb.actuators;


import de.unibi.citec.clf.bonsai.actuators.StringActuator;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;

import java.io.IOException;

import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.RSBException;

/**
 *
 * @author jgerlach
 */
public class RsbStringActuator extends RsbNode implements StringActuator {

    String basescope;

    private Informer<String> informer;

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RsbStringActuator() {
    }

    private Informer<String> createInformerString(Factory factory, String scope) {
        Informer<String> informer;
        try {
            informer = factory.createInformer(scope);
            informer.activate();
        } catch (RSBException ex) {
            throw new IllegalArgumentException("Unable to activate informer for scope: " + scope, ex);
        }
        return informer;
    }

    @Override
    public String getTarget() {
        return basescope;
    }

    @Override
    public void sendString(String data) throws IOException {
        try {
            informer.publish(data);
        } catch (RSBException ex) {
            logger.error("Can not send data " + data + ". Reason: " + ex.getMessage(), ex);
            throw new IOException(ex);
        }
    }

    @Override
    public void startNode() throws InitializeException {
        basescope = scope;
        Factory factory = Factory.getInstance();

        informer = createInformerString(factory, scope);
    }

    @Override
    public void destroyNode() {
        if (informer != null) {
            try {
                informer.deactivate();
            } catch (RSBException | InterruptedException ex) {
                logger.warn("destroy node failed", ex);
            }
        }
    }
}

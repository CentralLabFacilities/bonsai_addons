package de.unibi.citec.clf.bonsai.engine.communication;


import org.apache.log4j.Logger;
import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.statemachine.StateChangeType.StateChange;

/**
 *
 * @author semeyerz
 */
public class StateChangePublisherRSB extends StateChangePublisher {

    Informer<StateChange> informer;
    private static final Logger LOG = Logger.getLogger(StateChangePublisherRSB.class);

    public StateChangePublisherRSB(String scope) throws InitializeException {

        // Get a factory instance to create RSB objects.
        Factory factory = Factory.getInstance();

        // Instantiate generic ProtocolBufferConverter with
        // SimpleImage exemplar.
        final ProtocolBufferConverter<StateChange> converter = new ProtocolBufferConverter<>(
            StateChange.getDefaultInstance());

        // Register converter for the SimpleImage type.
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);

        // Create an informer on scope "/exmaple/informer".
        informer = factory.createInformer(scope);
        LOG.info("StateChangePublisher started on: "+scope);

        try {
            // Activate the informer to be ready for work
            informer.activate();
        } catch (RSBException e) {
            throw new InitializeException(e);
        }
    }

    @Override
    public void publish(String from, String to, String transition) {
        Logger.getLogger(StateChangePublisherRSB.class.getName()).trace("Publishing RSB event: [" + from 
                                                                        + "] to [" + to 
                                                                        + "] transition: [" + transition + "]");
        StateChange sct;
        sct = StateChange.newBuilder().setCause(transition).setFromState(from).setToState(to).build();
        try {
            informer.send(sct);
        } catch (RSBException ex) {
            Logger.getLogger(StateChangePublisherRSB.class.getName()).fatal(ex);
        }
    }

}

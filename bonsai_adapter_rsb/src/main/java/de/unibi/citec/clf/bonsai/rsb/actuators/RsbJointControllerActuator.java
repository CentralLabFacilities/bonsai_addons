
package de.unibi.citec.clf.bonsai.rsb.actuators;



import de.unibi.citec.clf.bonsai.actuators.JointControllerActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import de.unibi.citec.clf.bonsai.rsb.RsbRemoteServerRepository;
import de.unibi.citec.clf.btl.rst.helper.DictHelper;
import org.apache.log4j.Logger;
import rsb.RSBException;
import rsb.converter.ConverterRepository;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.RemoteServer;
import rst.generic.DictionaryType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.logging.Level;
import rsb.InitializeException;

/**
 * @author llach
 */
public class RsbJointControllerActuator extends RsbNode implements JointControllerActuator {
    
    public static final String OPTION_TIMEOUT = "timeout";
    private double timeout = 2500;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalDouble(OPTION_TIMEOUT, timeout);
    }

    private static final String METHOD_ZLIFT = "setZliftPosition";
    private static final String METHOD_HEAD = "setHeadAngles";

    private Logger logger = Logger.getLogger(getClass());
    private RemoteServer server;
    private static final Object serverLock = new Object();

    public RsbJointControllerActuator() {
        
    }

    @Override
    public Future<Boolean> goToZliftHeight(float dist) throws IOException {
        System.out.println("Invoked goToTliftHeight with height: " + dist);
        logger.debug("calling " + METHOD_ZLIFT);
        try {
            synchronized (serverLock) {
                Future<Boolean> success = server.callAsync(
                        METHOD_ZLIFT, dist);
                return success;
            }
        } catch (RSBException e) {
            logger.error("Error calling " + METHOD_ZLIFT + e.getMessage());
            throw new IOException("Error calling " + METHOD_ZLIFT,
                    e);
        }
    }

    @Override
    public Future<Boolean> goToHeadPose(float j0, float j1) throws IOException {
        System.out.println("Invoked goToHeadPose with j0: " + j0 + "and j1: " + j1);
        rst.generic.DictionaryType.Dictionary.Builder dict =  DictionaryType.Dictionary.newBuilder();

        dict.addEntries(DictHelper.doubleValue("j0",j0));
        dict.addEntries(DictHelper.doubleValue("j1",j1));

        try {
            synchronized (serverLock) {
                Future<Boolean> success = server.callAsync(
                        METHOD_HEAD, dict.build());
                return success;
            }
        } catch (RSBException e) {
            logger.error("Error calling " + METHOD_HEAD + e.getMessage());
            throw new IOException("Error calling " + METHOD_HEAD,
                    e);
        }
    }

    @Override
    public void startNode() throws InitializeException {
        final ConverterRepository<ByteBuffer> conv = DefaultConverterRepository.getDefaultConverterRepository();
        conv.addConverter(new ProtocolBufferConverter<>(DictionaryType.Dictionary.getDefaultInstance()));
        
        try {
            server = RsbRemoteServerRepository.getInstance()
                    .requestRemoteServer(scope, timeout);
        } catch (RSBException e) {
            java.util.logging.Logger.getLogger(RsbJointControllerActuator.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void destroyNode() {
         //todo
    }


}



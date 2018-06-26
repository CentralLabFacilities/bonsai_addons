package de.unibi.citec.clf.bonsai.rsb.actuators;


import de.unibi.citec.clf.bonsai.actuators.GazeActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbNode;
import static de.unibi.citec.clf.bonsai.rsb.actuators.RsbJointControllerActuator.OPTION_TIMEOUT;

import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import rsb.InitializeException;
import rsb.RSBException;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsb.Factory;
import rsb.Informer;
import rst.geometry.SphericalDirectionFloatType;
import rst.geometry.SphericalDirectionFloatType.SphericalDirectionFloat;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author lruegeme
 */
public class RsbGazeActuator extends RsbNode implements GazeActuator {
    
    public static final String OPTION_TIMEOUT = "timeout";
    private long timeout = 1000;

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        super.configure(conf);
        timeout = conf.requestOptionalInt(OPTION_TIMEOUT, (int) timeout);
    }
    
    private String scopeGazeTarget;
    private Informer<SphericalDirectionFloat> informer;

    public RsbGazeActuator() {
    }

    @Override
    public void setGazeTarget(float azimuth, float elevation) {
        SphericalDirectionFloat sdf = SphericalDirectionFloatType.SphericalDirectionFloat.newBuilder().setAzimuth(azimuth).setElevation(elevation).build();
        try {
            informer.publish(sdf);
            if(timeout > 0) {
                Thread.sleep(timeout);
            }
        } catch (RSBException | InterruptedException ex) {
            Logger.getLogger(RsbGazeActuator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void setGazeTargetPitch(float pitch){
        throw new NotImplementedException();
    }


    @Override
    public void setGazeTargetYaw(float yaw){
        throw new NotImplementedException();
    }

    @Override
    public Future<Boolean> setGazeTargetAsync(float pitch, float yaw) {
        throw new NotImplementedException();
    }

    @Override
    public Future<Boolean> setGazeTargetAsync(float pitch, float yaw, float duration) {
        throw new NotImplementedException();
    }

    @Override
    public Future<Boolean> setGazeTargetPitchAsync(float pitch, float duration) {
        throw new NotImplementedException();
    }

    @Override
    public Future<Boolean> setGazeTargetYawAsync(float yaw, float duration) {
        throw new NotImplementedException();
    }

    @Override
    public Future<Void> lookAt(Pose3D pose) {
       return lookAt(pose, 0.0f);
    }

    @Override
    public Future<Void> lookAt(Pose3D pose, float duration) {
        throw new NotImplementedException();
    }


    @Override
    public void startNode() throws InitializeException {
        Factory factory = Factory.getInstance();

        informer = factory.createInformer(scopeGazeTarget);
        try {
            informer.activate();
        } catch (RSBException ex) {
            throw new InitializeException(ex);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }

    public void manualStop() {
        //todo
    }
}

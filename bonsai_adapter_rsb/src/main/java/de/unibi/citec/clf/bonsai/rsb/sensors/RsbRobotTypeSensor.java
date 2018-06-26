package de.unibi.citec.clf.bonsai.rsb.sensors;


import de.unibi.citec.clf.bonsai.core.object.Sensor;
import java.io.IOException;
import de.unibi.citec.clf.bonsai.core.SensorListener;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.btl.data.grasp.RobotType;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 
 */
public class RsbRobotTypeSensor implements Sensor<RobotType> {

    private RobotType t = null;
    private Logger logger = Logger.getLogger(RsbRobotTypeSensor.class);
    
    public RsbRobotTypeSensor(Class<?> typeClass, Class<?> wire) {
    }
    
    @Override
    public Class<RobotType> getDataType() {
        return RobotType.class;
    }

    @Override
    public RobotType readLast(long timeout) throws IOException, InterruptedException {
        if(t==null) {
            t = fetchRobotType();
        }
        return t;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public void clear() {
    }

    @Override
    public void addSensorListener(SensorListener<RobotType> listener) {
    }

    @Override
    public void removeSensorListener(SensorListener<RobotType> listener) {
    }

    @Override
    public void removeAllSensorListeners() {
    }

    @Override
    public void cleanUp() {
        
    }

    private RobotType fetchRobotType() {
        Map<String, String> env = System.getenv();
        String type = env.get("ROBOT_TYPE");
        //logger.fatal(env);
        if( type!=null && type.equals("meka")) {
            return new RobotType(RobotType.Robot.MEKA);
        } else {
            return new RobotType(RobotType.Robot.BIRON);
        }
        
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        
    }

}

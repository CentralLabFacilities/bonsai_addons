
package de.unibi.citec.clf.bonsai.rsb;



import de.unibi.citec.clf.bonsai.core.BonsaiManager;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import de.unibi.citec.clf.bonsai.core.configuration.XmlConfigurationParser;
import de.unibi.citec.clf.btl.data.map.BinarySlamMap;
import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author alangfel
 */
public class Ros4rsbTest {

    public static void main(String[] args) {
        
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        
        XmlConfigurationParser parser = new XmlConfigurationParser();
        BonsaiManager manager = BonsaiManager.getInstance();

        manager.configure("src/test/resources/ros4rsbtest.xml", parser);

        Sensor<PositionData> positionSensor = manager.createSensor("PositionSensor", PositionData.class);
        Sensor<PositionData> odomSensor = manager.createSensor("OdomSensor", PositionData.class);
        //Sensor<void> stallSensor = manager.createSensor("StallSensor", void);
        Sensor<GlobalPlan> globalPlanSensor = manager.createSensor("GlobalPlanSensor", GlobalPlan.class);
        Sensor<BinarySlamMap> slamMapSensor = manager.createSensor("SlamMapSensor", BinarySlamMap.class);

        for (int i = 0; i < 10; i++) {
            PositionData pos;
            GlobalPlan global;
            BinarySlamMap slamMap;
            try {

                System.out.println("waiting for data...");
                //TODO check for null returns
                pos = positionSensor.readLast(1000);
                System.out.println("Slampose");
                System.out.println(pos);
                
                pos = odomSensor.readLast(1000);
                System.out.println("Odometry");
                System.out.println(pos);

                
                global = globalPlanSensor.readLast(1000);
                System.out.println("GlobalPlan");
                if(global != null) {
                    System.out.println(global);
                } else {
                    System.out.println("no global plan!");
                }
                
                slamMap = slamMapSensor.readLast(1000);
                System.out.println("SlamMap");
                System.out.println(slamMap);
                

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        System.exit(0);
    }
}



package de.unibi.citec.clf.bonsai.rsb;



import de.unibi.citec.clf.bonsai.core.BonsaiManager;
import de.unibi.citec.clf.bonsai.core.object.Sensor;
import de.unibi.citec.clf.bonsai.core.configuration.XmlConfigurationParser;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author kharmening
 */
public class Object4rsbTest {

    public static void main(String[] args) {
        
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        
        XmlConfigurationParser parser = new XmlConfigurationParser();
        BonsaiManager manager = BonsaiManager.getInstance();

        manager.configure("src/test/resources/object4rsbtest.xml", parser);

        Sensor<PersonDataList> personSensor = manager.createSensor("PersonSensor", PersonDataList.class);
        
        for (int i = 0; i < 10; i++) {
            PersonDataList pos;
            try {

                System.out.println("waiting for data...");
                
                pos = personSensor.readLast(1000);
                if (pos==null) {
                    System.out.println("PersonSensor timed out (1000ms)");
                }
                System.out.println("received a PersonPositionList");
                //System.out.println(pos);
                
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        System.exit(0);
    }
    
}

package de.unibi.citec.clf.bonsai.rsb;



import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.bonsai.core.BonsaiManager;
import de.unibi.citec.clf.bonsai.core.exception.CommunicationException;
import de.unibi.citec.clf.bonsai.core.object.MemorySlot;
import de.unibi.citec.clf.bonsai.core.configuration.XmlConfigurationParser;
import org.apache.log4j.BasicConfigurator;

public class TestLoader {

    /**
     * @param args
     */
    public static void main(String[] args) throws CommunicationException, InterruptedException {
        BasicConfigurator.configure();
        XmlConfigurationParser parser = new XmlConfigurationParser();
        BonsaiManager manager = BonsaiManager.getInstance();

        manager.configure("src/test/resources/rsbtest.xml", parser);
        RsbamWorkingMemory memory = manager.createWorkingMemory("WorkingMemory");
        MemorySlot<Point2D> slot = memory.getSlot("test", Point2D.class);
        Point2D p0 = new Point2D(213, 32132, LengthUnit.METER);
        Point2D p1 = new Point2D(32, 321, LengthUnit.CENTIMETER);
        
        System.out.println("Memorize: " + p1);
        slot.memorize(p1);
        Thread.sleep(2000);
        System.out.println("Memorize: " + p0);
        slot.memorize(p0);

        Point2D result = slot.recall();
        System.out.println("Recall: " + result);
        
        System.out.println("Forget ...");
        slot.forget();
        
        result = slot.recall();
        System.out.println("Recall: " + result);
        
        System.out.println("Done");
    }
}

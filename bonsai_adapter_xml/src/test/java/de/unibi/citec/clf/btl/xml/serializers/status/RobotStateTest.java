package de.unibi.citec.clf.btl.xml.serializers.status;


import de.unibi.citec.clf.btl.data.status.RobotState;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fsiepman
 */
public class RobotStateTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        RobotState original = new RobotState();
        final long timestamp = System.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setCurrenState(RobotState.RobotStates.DRIVE_TO);
        original.setPreviousState(RobotState.RobotStates.WAITING);
        original.setTargetName("jipabndv");

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        RobotState parsed = XomTypeFactory.getInstance().createType(doc, RobotState.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getCurrenState(), parsed.getCurrenState());
        assertEquals(original.getPreviousState(), parsed.getPreviousState());
        assertEquals(original.getTargetName(), parsed.getTargetName());

    }

}

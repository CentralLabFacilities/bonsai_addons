package de.unibi.citec.clf.btl.xml.serializers.grasp;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.grasp.KatanaGripperData;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author ttoenige
 */
public class KatanaGripperDataTest {
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {
        KatanaGripperData original = new KatanaGripperData();
        final long timestamp = Time.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setForceRightInsideNear(1);
        original.setForceRightInsideFar(2);
        original.setInfraredRightOutside(3);
        original.setInfraredRightFront(4);
        original.setInfraredRightInsideNear(5);
        original.setInfraredRightInsideFar(6);
        original.setForceLeftInsideNear(7);
        original.setForceLeftInsideFar(8);
        original.setInfraredLeftOutside(9);
        original.setInfraredLeftFront(10);
        original.setInfraredLeftInsideNear(11);
        original.setInfraredLeftInsideFar(12);
        original.setInfraredMiddle(13);


        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        KatanaGripperData parsed = XomTypeFactory.getInstance().createType(doc, KatanaGripperData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getForceRightInsideNear(), parsed
                .getForceRightInsideNear(), 0.00001);
        assertEquals(original.getForceRightInsideFar(), parsed
                .getForceRightInsideFar(), 0.00001);
        assertEquals(original.getInfraredRightOutside(), parsed
                .getInfraredRightOutside(), 0.00001);
        assertEquals(original.getInfraredRightFront(), parsed
                .getInfraredRightFront(), 0.00001);
        assertEquals(original.getInfraredRightInsideNear(), parsed
                .getInfraredRightInsideNear(), 0.00001);
        assertEquals(original.getInfraredRightInsideFar(), parsed
                .getInfraredRightInsideFar(), 0.00001);
        assertEquals(original.getForceLeftInsideNear(), parsed
                .getForceLeftInsideNear(), 0.00001);
        assertEquals(original.getForceLeftInsideFar(), parsed
                .getForceLeftInsideFar(), 0.00001);
        assertEquals(original.getInfraredLeftOutside(), parsed
                .getInfraredLeftOutside(), 0.00001);
        assertEquals(original.getInfraredLeftFront(), parsed
                .getInfraredLeftFront(), 0.00001);
        assertEquals(original.getInfraredLeftInsideNear(), parsed
                .getInfraredLeftInsideNear(), 0.00001);
        assertEquals(original.getInfraredLeftInsideFar(), parsed
                .getInfraredLeftInsideFar(), 0.00001);
        assertEquals(original.getInfraredMiddle(), parsed.getInfraredMiddle(),
                0.00001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("KatanaGripperData.xml"));

        KatanaGripperData parsed = XomTypeFactory.getInstance().createType(doc, KatanaGripperData.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(1, parsed.getForceRightInsideNear(), 0.00001);
        assertEquals(2, parsed.getForceRightInsideFar(), 0.00001);
        assertEquals(3, parsed.getInfraredRightOutside(), 0.00001);
        assertEquals(4, parsed.getInfraredRightFront(), 0.00001);
        assertEquals(5, parsed.getInfraredRightInsideNear(), 0.00001);
        assertEquals(6, parsed.getInfraredRightInsideFar(), 0.00001);
        assertEquals(7, parsed.getForceLeftInsideNear(), 0.00001);
        assertEquals(8, parsed.getForceLeftInsideFar(), 0.00001);
        assertEquals(9, parsed.getInfraredLeftOutside(), 0.00001);
        assertEquals(10, parsed.getInfraredLeftFront(), 0.00001);
        assertEquals(11, parsed.getInfraredLeftInsideNear(), 0.00001);
        assertEquals(12, parsed.getInfraredLeftInsideFar(), 0.00001);
        assertEquals(13, parsed.getInfraredMiddle(), 0.00001);

    }

}

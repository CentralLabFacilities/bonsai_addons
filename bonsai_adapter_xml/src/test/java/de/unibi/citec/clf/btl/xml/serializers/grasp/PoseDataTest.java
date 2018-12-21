package de.unibi.citec.clf.btl.xml.serializers.grasp;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.grasp.PoseData;
import de.unibi.citec.clf.btl.units.LengthUnit;
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
public class PoseDataTest {
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {
        PoseData original = new PoseData();
        final long timestamp = Time.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setX(111, LengthUnit.MILLIMETER);
        original.setY(222, LengthUnit.MILLIMETER);
        original.setZ(333, LengthUnit.MILLIMETER);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        PoseData parsed = XomTypeFactory.getInstance().createType(doc, PoseData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getX(LengthUnit.MILLIMETER), parsed.getX(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(original.getY(LengthUnit.MILLIMETER), parsed.getY(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(original.getZ(LengthUnit.MILLIMETER), parsed.getZ(LengthUnit.MILLIMETER), 0.00001);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("PoseData.xml"));

        PoseData parsed = XomTypeFactory.getInstance().createType(doc, PoseData.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(18, parsed.getX(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(25, parsed.getY(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(34, parsed.getZ(LengthUnit.MILLIMETER), 0.00001);
    }

}

package de.unibi.citec.clf.btl.xml.serializers.grasp;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.grasp.KatanaPoseData;
import de.unibi.citec.clf.btl.units.AngleUnit;
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
public class KatanaPoseDataTest {
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {
        KatanaPoseData original = new KatanaPoseData();
        final long timestamp = System.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setX(111, LengthUnit.MILLIMETER);
        original.setY(222, LengthUnit.MILLIMETER);
        original.setZ(333, LengthUnit.MILLIMETER);
        original.setPhi(1.11, AngleUnit.RADIAN);
        original.setPsi(0.55, AngleUnit.RADIAN);
        original.setTheta(0.77, AngleUnit.RADIAN);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        KatanaPoseData parsed = XomTypeFactory.getInstance().createType(doc, KatanaPoseData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getX(LengthUnit.MILLIMETER), parsed.getX(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(original.getY(LengthUnit.MILLIMETER), parsed.getY(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(original.getZ(LengthUnit.MILLIMETER), parsed.getZ(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(original.getPhi(AngleUnit.RADIAN), parsed.getPhi(AngleUnit.RADIAN), 0.00001);
        assertEquals(original.getPsi(AngleUnit.RADIAN), parsed.getPsi(AngleUnit.RADIAN), 0.00001);
        assertEquals(original.getTheta(AngleUnit.RADIAN), parsed.getTheta(AngleUnit.RADIAN), 0.00001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("KatanaPoseData.xml"));

        KatanaPoseData parsed = XomTypeFactory.getInstance().createType(doc, KatanaPoseData.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(232, parsed.getX(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(343, parsed.getY(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(454, parsed.getZ(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(1.23, parsed.getPsi(AngleUnit.RADIAN), 0.00001);
        assertEquals(1.34, parsed.getPhi(AngleUnit.RADIAN), 0.00001);
        assertEquals(0.23, parsed.getTheta(AngleUnit.RADIAN), 0.00001);


    }

}

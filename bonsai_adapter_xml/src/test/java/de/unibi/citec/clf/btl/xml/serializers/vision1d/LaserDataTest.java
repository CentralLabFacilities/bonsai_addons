package de.unibi.citec.clf.btl.xml.serializers.vision1d;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.vision1d.LaserData;
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
 * @author jwienke
 */
public class LaserDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        LaserData original = new LaserData();
        final long timestamp = Time.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
//        original.setReadTime(new MicroTimestamp(123, 456));
        double scans[] = new double[]{0.5, 12.4, 10.4, -5.3, 0.01};
        original.setScanValues(scans, LengthUnit.METER);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        LaserData parsed = XomTypeFactory.getInstance().createType(doc, LaserData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        //       assertEquals(original.getReadTime(), parsed.getReadTime());
        assertEquals(original.getNumLaserPoints(), parsed.getNumLaserPoints());
        for (int i = 0; i < original.getNumLaserPoints(); i++) {
            assertEquals(original.getScanValues(LengthUnit.METER)[i], parsed.getScanValues(LengthUnit.METER)[i], 0.0001);
        }

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Laser.xml"));

        LaserData parsed = XomTypeFactory.getInstance().createType(doc, LaserData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(5, parsed.getNumLaserPoints());
        double originalValues[] = new double[]{0.01, 0.002, 0.003, 0.004, 0.005};
        for (int i = 0; i < parsed.getNumLaserPoints(); i++) {
            assertEquals(originalValues[i], parsed.getScanValues(LengthUnit.METER)[i], 0.0001);
        }

    }

}

package de.unibi.citec.clf.btl.xml.serializers.navigation;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.navigation.DriveData;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.SpeedUnit;
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
public class DriveDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        DriveData original = new DriveData();
        final long timestamp = System.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setDistance(-0.45, LengthUnit.METER);
        original.setSpeed(12.45, SpeedUnit.METER_PER_SEC);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        DriveData parsed = XomTypeFactory.getInstance().createType(doc, DriveData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getDistance(LengthUnit.METER), parsed.getDistance(LengthUnit.METER),
                0.0001);
        assertEquals(original.getSpeed(SpeedUnit.METER_PER_SEC), parsed
                .getSpeed(SpeedUnit.METER_PER_SEC), 0.0001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Drive.xml"));

        DriveData parsed = XomTypeFactory.getInstance().createType(doc, DriveData.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
//		assertEquals(new MicroTimestamp(123213412, 335435), parsed
//				.getReadTime());
        assertEquals(2.45, parsed.getDistance(LengthUnit.METER), 0.0001);
        assertEquals(-0.34, parsed.getSpeed(SpeedUnit.METER_PER_SEC), 0.0001);

    }

    @Test
    public void genericFromDocument() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Drive.xml"));

        DriveData parsed = XomTypeFactory.getInstance().createType(doc, DriveData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
//		assertEquals(new MicroTimestamp(123213412, 335435), parsed
//				.getReadTime());
        assertEquals(2.45, parsed.getDistance(LengthUnit.METER), 0.0001);
        assertEquals(-0.34, parsed.getSpeed(SpeedUnit.METER_PER_SEC), 0.0001);

    }

}

package de.unibi.citec.clf.btl.xml.serializers.navigation;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.navigation.TurnData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.RotationalSpeedUnit;
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
public class TurnDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        TurnData original = new TurnData();
        final long timestamp = Time.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setAngle(-0.45, AngleUnit.RADIAN);
        original.setSpeed(12.45, RotationalSpeedUnit.RADIANS_PER_SEC);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        TurnData parsed = XomTypeFactory.getInstance().createType(doc, TurnData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getAngle(AngleUnit.RADIAN), parsed.getAngle(AngleUnit.RADIAN),
                0.0001);
        assertEquals(original.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC), parsed
                .getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC), 0.0001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Turn.xml"));

        TurnData parsed = XomTypeFactory.getInstance().createType(doc, TurnData.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        //	assertEquals(new MicroTimestamp(123213412, 335435), parsed
        //			.getReadTime());
        assertEquals(2.45, parsed.getAngle(AngleUnit.RADIAN), 0.0001);
        assertEquals(-0.34, parsed.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC), 0.0001);

    }

    @Test
    public void genericFromDocument() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Turn.xml"));

        TurnData parsed = XomTypeFactory.getInstance().createType(doc, TurnData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        //	assertEquals(new MicroTimestamp(123213412, 335435), parsed
        //			.getReadTime());
        assertEquals(2.45, parsed.getAngle(AngleUnit.RADIAN), 0.0001);
        assertEquals(-0.34, parsed.getSpeed(RotationalSpeedUnit.RADIANS_PER_SEC), 0.0001);

    }

}

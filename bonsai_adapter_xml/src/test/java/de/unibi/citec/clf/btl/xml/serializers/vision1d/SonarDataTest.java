package de.unibi.citec.clf.btl.xml.serializers.vision1d;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.vision1d.SonarData;
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
 * @author ltwardon
 */
public class SonarDataTest {
    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {
        SonarData original = new SonarData(4.2, 1.1, LengthUnit.METER);
        final long timestamp = Time.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        SonarData parsed = XomTypeFactory.getInstance().createType(doc, SonarData.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getDistanceLeft(LengthUnit.METER), parsed.getDistanceLeft(LengthUnit.METER), 0.00001);
        assertEquals(original.getDistanceRight(LengthUnit.METER), parsed.getDistanceRight(LengthUnit.METER), 0.00001);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("SonarData.xml"));

        SonarData parsed = XomTypeFactory.getInstance().createType(doc, SonarData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(1.1, parsed.getDistanceLeft(LengthUnit.METER), 0.00001);
        assertEquals(4.2, parsed.getDistanceRight(LengthUnit.METER), 0.00001);
    }

}

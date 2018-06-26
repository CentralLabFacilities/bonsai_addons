package de.unibi.citec.clf.btl.xml.serializers.map;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.map.BinarySlamMap;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author jwienke
 */
public class BinarySlamMapTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws Exception {

        BinarySlamMap original = new BinarySlamMap();
        final long timestamp = System.currentTimeMillis();
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        original.setGenerator("test");
        original.setHeight(23445);
        original.setOriginX(-345);
        original.setOriginY(123);
        original.setResolution(0.034, LengthUnit.METER);
        original.setUri("blubber/bla");
        original.setWidth(34539);
        original.setX(0.45, LengthUnit.MILLIMETER);
        original.setY(-216.34, LengthUnit.MILLIMETER);
        original.setYaw(0.34, AngleUnit.RADIAN);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        BinarySlamMap parsed = XomTypeFactory.getInstance().createType(doc, BinarySlamMap.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getHeight(), parsed.getHeight());
        assertEquals(original.getOriginX(), parsed.getOriginX());
        assertEquals(original.getOriginY(), parsed.getOriginY());
        assertEquals(original.getResolution(LengthUnit.METER), parsed.getResolution(LengthUnit.METER), 0.00001);
        assertEquals(original.getUri(), parsed.getUri());
        assertEquals(original.getWidth(), parsed.getWidth());
        assertEquals(original.getX(LengthUnit.METER), parsed.getX(LengthUnit.METER), 0.0001);
        assertEquals(original.getY(LengthUnit.METER), parsed.getY(LengthUnit.METER), 0.0001);
        assertEquals(original.getYaw(AngleUnit.RADIAN), parsed.getYaw(AngleUnit.RADIAN), 0.0001);

    }

    //@Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("BinarySlamMap.xml"));

        BinarySlamMap parsed = XomTypeFactory.getInstance().createType(doc, BinarySlamMap.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(74, parsed.getOriginX());
        assertEquals(34, parsed.getOriginY());
        assertEquals(0.45, parsed.getResolution(LengthUnit.METER), 0.00001);
        assertEquals(745, parsed.getWidth());
        assertEquals(123, parsed.getHeight());
        assertEquals(0.34, parsed.getYaw(AngleUnit.RADIAN), 0.00001);
        assertEquals("test/uri", parsed.getUri());
        assertEquals(234.5, parsed.getX(LengthUnit.METER), 0.00001);
        assertEquals(-6345.42, parsed.getY(LengthUnit.METER), 0.00001);

    }

}

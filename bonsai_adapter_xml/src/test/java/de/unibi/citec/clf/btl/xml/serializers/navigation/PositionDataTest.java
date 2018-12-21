package de.unibi.citec.clf.btl.xml.serializers.navigation;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author jwienke
 */
public class PositionDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        PositionData original = new PositionData();
        final long timestamp = Time.currentTimeMillis();
        //original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        //original.setGenerator("test");
        original.setX(-12.34, LengthUnit.MILLIMETER);
        original.setY(34123.45, LengthUnit.MILLIMETER);
        original.setYaw(1.234, AngleUnit.RADIAN);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        PositionData parsed = XomTypeFactory.getInstance().createType(doc, PositionData.class);

        //assertEquals(original.getGenerator(), parsed.getGenerator());
        //assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getX(LengthUnit.MILLIMETER), parsed.getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getY(LengthUnit.MILLIMETER), parsed.getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getYaw(AngleUnit.RADIAN), parsed.getYaw(AngleUnit.RADIAN), 0.0001);

    }

}

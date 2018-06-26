package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.vision3d.KinectData;
import de.unibi.citec.clf.btl.units.AngleUnit;
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
 * @author lziegler
 */
public class KinectDataTest {

    private Logger logger = Logger.getLogger(getClass());
    private double delta = 0.0001;

    @Test
    public void selfCompatibility() throws Exception {

        KinectData original = new KinectData();
        original.setTilt(180.0, AngleUnit.DEGREE);
        original.setAcceleration(0.0, 1.0, 2.0);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        KinectData parsed = XomTypeFactory.getInstance().createType(doc, KinectData.class);

        assertEquals(Math.PI, parsed.getTilt(AngleUnit.RADIAN), delta);
        assertEquals(original.getTilt(AngleUnit.RADIAN),
                parsed.getTilt(AngleUnit.RADIAN), delta);

        assertEquals(original.getAccelerationX(), parsed.getAccelerationX(),
                delta);
        assertEquals(original.getAccelerationY(), parsed.getAccelerationY(),
                delta);
        assertEquals(original.getAccelerationZ(), parsed.getAccelerationZ(),
                delta);

        System.out.println(parsed);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser
                .build(TestUtils.makeTestFileName("KinectData.xml"));

        KinectData parsed = XomTypeFactory.getInstance().createType(doc, KinectData.class);


        assertEquals(new Timestamp(253543797l, 253543712l, TimeUnit.MILLISECONDS),
                parsed.getTimestamp());
        assertEquals(180.0, parsed.getTilt(AngleUnit.DEGREE), delta);

        assertEquals(0.0, parsed.getAccelerationX(), 0.0001);
        assertEquals(1.0, parsed.getAccelerationY(), 0.0001);
        assertEquals(2.0, parsed.getAccelerationZ(), 0.0001);
    }
}

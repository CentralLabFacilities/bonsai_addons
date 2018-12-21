package de.unibi.citec.clf.btl.xml.serializers.object;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.object.ObjectPositionData;
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
 * @author lziegler
 */
public class ObjectPositionDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {


        final long timestamp = Time.currentTimeMillis();

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        ObjectPositionData original = new ObjectPositionData();
        original.setPolygon(poly);
        original.setReference("world");
        original.setCoordinateKind("absolute");

        ObjectPositionData.Hypothesis originalHyp = new ObjectPositionData.Hypothesis();

        originalHyp.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        originalHyp.setGenerator("test");
        originalHyp.setReliability(0.1);
        originalHyp.setClassLabel("testclass");

        original.addHypothesis(originalHyp);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        ObjectPositionData parsed = XomTypeFactory.getInstance().createType(doc, ObjectPositionData.class);
        ObjectPositionData.Hypothesis parsedHyp = parsed
                .getLocationHypotheses().iterator().next();

        assertEquals(originalHyp.getGenerator(), parsedHyp.getGenerator());
        assertEquals(originalHyp.getTimestamp(), parsedHyp.getTimestamp());
        assertEquals(originalHyp.getClassLabel(), parsedHyp.getClassLabel());
        assertEquals(originalHyp.getReliability(), parsedHyp.getReliability(),
                0.0001);
        assertEquals(original.getReference(), parsed.getReference());
        assertEquals(original.getCoordinateKind(), parsed.getCoordinateKind());

        System.out.println(parsed);
    }

    //@Test
    public void fileCompatibility() throws Exception {

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("ObjectPositionData.xml"));

        ObjectPositionData parsed = XomTypeFactory.getInstance().createType(doc, ObjectPositionData.class);
        ObjectPositionData.Hypothesis parsedHyp = parsed
                .getLocationHypotheses().iterator().next();


        assertEquals("BoostDetector", parsedHyp.getGenerator());
        assertEquals(new Timestamp(253543797l, 253543712l, TimeUnit.MILLISECONDS), parsedHyp
                .getTimestamp());
        assertEquals("smacks", parsedHyp.getClassLabel());
        assertEquals(0.6, parsedHyp.getReliability(), 0.002);
        assertEquals("world", parsed.getReference());
        assertEquals("absolute", parsed.getCoordinateKind());
        assertEquals(poly, parsed.getPolygon());
    }

}

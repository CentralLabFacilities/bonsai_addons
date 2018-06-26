package de.unibi.citec.clf.btl.xml.serializers.vision2d;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.vision2d.RegionData;
import de.unibi.citec.clf.btl.data.vision2d.RegionData.Scope;
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
public class RegionDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        RegionData original = new RegionData();
        original.setScope(Scope.LOCAL);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        RegionData parsed = XomTypeFactory.getInstance().createType(doc, RegionData.class);

        assertEquals(original.getScope(), parsed.getScope());
        assertEquals(original.getPolygon(), parsed.getPolygon());

        System.out.println(parsed);
    }

    @Test
    public void fileCompatibility() throws Exception {

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("RegionData.xml"));

        RegionData parsed = XomTypeFactory.getInstance().createType(doc, RegionData.class);


        assertEquals(new Timestamp(253543797l, 253543712l, TimeUnit.MILLISECONDS), parsed
                .getTimestamp());
        assertEquals(Scope.LOCAL, parsed.getScope());
        //assertEquals(poly, parsed.getPolygon());
    }
}

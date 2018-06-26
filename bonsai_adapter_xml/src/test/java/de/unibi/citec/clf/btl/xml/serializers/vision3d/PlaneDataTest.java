package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.vision2d.RegionData.Scope;
import de.unibi.citec.clf.btl.data.vision3d.PlaneData;
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
public class PlaneDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        PlaneData original = new PlaneData();
        original.setScope(Scope.LOCAL);
        Point3D origin = new Point3D(1.0, 1.0, 1.0, LengthUnit.MILLIMETER);
        original.setOrigin(origin);
        Rotation3D rotation = new Rotation3D();
        original.setRotation(rotation);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        PlaneData parsed = XomTypeFactory.getInstance().createType(doc, PlaneData.class);

        assertEquals(original.getScope(), parsed.getScope());
        assertEquals(original.getPolygon(), parsed.getPolygon());

        assertEquals(original.getOrigin().getX(LengthUnit.MILLIMETER), parsed
                .getOrigin().getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getOrigin().getY(LengthUnit.MILLIMETER), parsed
                .getOrigin().getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getOrigin().getZ(LengthUnit.MILLIMETER), parsed
                .getOrigin().getZ(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(original.getRotation(), parsed.getRotation());

        System.out.println(parsed);
    }

    //@Test
    public void fileCompatibility() throws Exception {

        PrecisePolygon poly = new PrecisePolygon();
        poly.addPoint(0d, 0d, LengthUnit.MILLIMETER);
        poly.addPoint(1d, 1d, LengthUnit.MILLIMETER);
        poly.addPoint(2d, 2d, LengthUnit.MILLIMETER);

        Builder parser = new Builder();
        Document doc = parser
                .build(TestUtils.makeTestFileName("PlaneData.xml"));

        PlaneData parsed = XomTypeFactory.getInstance().createType(doc, PlaneData.class);


        assertEquals(new Timestamp(253543797l, 253543712l, TimeUnit.MILLISECONDS),
                parsed.getTimestamp());
        assertEquals(Scope.LOCAL, parsed.getScope());
        assertEquals(poly, parsed.getPolygon());

        assertEquals(1.0, parsed.getOrigin().getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(1.0, parsed.getOrigin().getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(1.0, parsed.getOrigin().getZ(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(new Rotation3D(), parsed.getRotation());
    }
}

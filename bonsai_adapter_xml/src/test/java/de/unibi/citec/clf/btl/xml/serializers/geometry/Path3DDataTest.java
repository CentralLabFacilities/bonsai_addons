package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.Path3D;
import de.unibi.citec.clf.btl.data.geometry.Path3D.Scope;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon3D;
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
public class Path3DDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        PrecisePolygon3D polygon = new PrecisePolygon3D();
        polygon.addPoint(0, 0, 0, LengthUnit.MILLIMETER);
        polygon.addPoint(1, 1, 1, LengthUnit.MILLIMETER);
        polygon.addPoint(2, 2, 2, LengthUnit.MILLIMETER);

        Path3D original = new Path3D(polygon, Scope.LOCAL);

        assertEquals(original.getPolygon().getMinX(LengthUnit.MILLIMETER),
                original.getPolygon().getMinX(LengthUnit.CENTIMETER) * 10, 0.0001);
        assertEquals(original.getPolygon().getMinX(LengthUnit.MILLIMETER),
                original.getPolygon().getMinX(LengthUnit.METER) * 1000, 0.0001);
        assertEquals(original.getPolygon().getMaxY(LengthUnit.MILLIMETER),
                original.getPolygon().getMaxY(LengthUnit.CENTIMETER) * 10, 0.0001);
        assertEquals(original.getPolygon().getMaxZ(LengthUnit.MILLIMETER),
                original.getPolygon().getMaxZ(LengthUnit.METER) * 1000, 0.0001);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        Path3D parsed = XomTypeFactory.getInstance().createType(doc, Path3D.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.getScope(), parsed.getScope());
        assertEquals(original.getPolygon().getMaxX(LengthUnit.MILLIMETER),
                parsed.getPolygon().getMaxX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getPolygon().getMinY(LengthUnit.MILLIMETER),
                parsed.getPolygon().getMinY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(original.getPolygon().getMinZ(LengthUnit.MILLIMETER),
                parsed.getPolygon().getMinZ(LengthUnit.MILLIMETER), 0.0001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Path3DData.xml"));

        Path3D parsed = XomTypeFactory.getInstance().createType(doc, Path3D.class);

        assertEquals("PlaneDetector", parsed.getGenerator());
        assertEquals(new Timestamp(253543797, 253543712, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(Scope.LOCAL, parsed.getScope());

        double count = 0.0;
        for (Point3D point : parsed.getPolygon()) {
            assertEquals(count, point.getX(LengthUnit.MILLIMETER), 0.0001);
            assertEquals(count, point.getY(LengthUnit.MILLIMETER), 0.0001);
            assertEquals(count, point.getZ(LengthUnit.MILLIMETER), 0.0001);
            count++;
        }

    }

}

package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.vision3d.PointCloud;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.junit.Test;

import javax.vecmath.Matrix3d;
import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.*;

/**
 * Test cases for {@link PointCloud}.
 *
 * @author plueckin
 */
public class PointCloudTest {

    //@Test
    public void selfCompatibility() throws Exception {

        PointCloud pc = new PointCloud();

        Point3D p0 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
        Point3D p1 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
        Point3D p2 = new Point3D(0.1, 0.2, 0.3, LengthUnit.MILLIMETER);
        pc.addPoint(p0);
        pc.addPoint(p1);
        pc.addPoint(p2);

        assertTrue("equals() not working", pc.getPoints().get(0).equals(pc.getPoints().get(1)));
        assertTrue("equals() not working", pc.getPoints().get(1).equals(pc.getPoints().get(0)));
        assertFalse("equals() not working", pc.getPoints().get(0).equals(pc.getPoints().get(2)));

        pc.setSensorOrigin(new Point3D(0, 0, 0, LengthUnit.MILLIMETER));

        Matrix3d rot = new Matrix3d(1, 2, 3, 4, 4, 5, 6, 7, 8);
        pc.setRotation(new Rotation3D(rot));

        Document doc = XomTypeFactory.getInstance().createDocument(pc);

        PointCloud parsed = XomTypeFactory.getInstance().createType(doc, PointCloud.class);

        assertTrue("serializing not working", pc.equals(parsed));
    }

    @Test
    public void fileCompatibility() throws Exception {

        PointCloud pc = new PointCloud();

        Point3D p0 = new Point3D(12, 12, 81, LengthUnit.MILLIMETER);
        Point3D p1 = new Point3D(1.6, 51, 511, LengthUnit.MILLIMETER);
        Point3D p2 = new Point3D(14, 34, 66, LengthUnit.MILLIMETER);
        Point3D p3 = new Point3D(82, 16, 44, LengthUnit.MILLIMETER);

        pc.addPoint(p0);
        pc.addPoint(p1);
        pc.addPoint(p2);
        pc.addPoint(p3);

        pc.setSensorOrigin(new Point3D(41.3, 12.1, 21.84, LengthUnit.MILLIMETER));

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("PointCloud.xml"));

        PointCloud parsed = XomTypeFactory.getInstance().createType(doc, PointCloud.class);

        assertEquals(new Timestamp(127194l, 127199l, TimeUnit.MILLISECONDS), parsed.getTimestamp());

        assertEquals(pc.getSensorOrigin().getX(LengthUnit.MILLIMETER),
                parsed.getSensorOrigin().getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pc.getSensorOrigin().getY(LengthUnit.MILLIMETER),
                parsed.getSensorOrigin().getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pc.getSensorOrigin().getZ(LengthUnit.MILLIMETER),
                parsed.getSensorOrigin().getZ(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(pc.getPoints().get(0).getX(LengthUnit.MILLIMETER),
                parsed.getPoints().get(0).getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pc.getPoints().get(1).getZ(LengthUnit.MILLIMETER),
                parsed.getPoints().get(1).getZ(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pc.getPoints().get(2).getY(LengthUnit.MILLIMETER),
                parsed.getPoints().get(2).getY(LengthUnit.MILLIMETER), 0.0001);

    }
}

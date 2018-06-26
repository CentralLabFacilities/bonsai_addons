package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.vision3d.PointCloud;
import de.unibi.citec.clf.btl.data.vision3d.PointCloudGrasping;
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
 * Test cases for {@link PointCloudGrasping}.
 *
 * @author plueckin
 */
public class PointCloudGraspingTest {

    //@Test
    public void selfCompatibility() throws Exception {

        PointCloud pc = new PointCloud();
        PointCloudGrasping pcg = new PointCloudGrasping();

        Point3D p0 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
        Point3D p1 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
        Point3D p2 = new Point3D(0.1, 0.2, 0.3, LengthUnit.MILLIMETER);
        pc.addPoint(p0);
        pc.addPoint(p1);
        pc.addPoint(p2);

        pcg.setDepth(50, LengthUnit.MILLIMETER);
        pcg.setHeight(20.5, LengthUnit.MILLIMETER);
        pcg.setWidth(33.33333, LengthUnit.MILLIMETER);

        pc.setSensorOrigin(new Point3D(0, 0, 0, LengthUnit.MILLIMETER));

        Matrix3d rot = new Matrix3d(1, 2, 3, 4, 4, 5, 6, 7, 8);
        pc.setRotation(new Rotation3D(rot));

        pcg.setConvexhull(pc);
        pcg.setGraspPoint(4, 1, 10.5);
        pcg.setLabel("pringles");

        Document doc = XomTypeFactory.getInstance().createDocument(pcg);

        PointCloudGrasping pcg2 = XomTypeFactory.getInstance().createType(doc, PointCloudGrasping.class);

        assertTrue("serializing not working", pcg.equals(pcg2));

    }

    @Test
    public void fileCompatibility() throws Exception {

        PointCloud pc = new PointCloud();
        PointCloudGrasping pcg = new PointCloudGrasping();

        Point3D p0 = new Point3D(12, 12, 81, LengthUnit.MILLIMETER);
        Point3D p1 = new Point3D(1.6, 51, 511, LengthUnit.MILLIMETER);
        Point3D p2 = new Point3D(14, 34, 66, LengthUnit.MILLIMETER);
        Point3D p3 = new Point3D(82, 16, 44, LengthUnit.MILLIMETER);

        pc.addPoint(p0);
        pc.addPoint(p1);
        pc.addPoint(p2);
        pc.addPoint(p3);

        pcg.setHeight(3.1, LengthUnit.MILLIMETER);
        pcg.setWidth(1.2, LengthUnit.MILLIMETER);
        pcg.setDepth(2.3, LengthUnit.MILLIMETER);

        pc.setSensorOrigin(new Point3D(41.3, 12.1, 21.84, LengthUnit.MILLIMETER));

        pcg.setConvexhull(pc);
        pcg.setLabel("pringles");
        pcg.setGraspPoint(12.2, 43.222, 0.456);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("PointCloudGrasping.xml"));

        PointCloudGrasping parsed = XomTypeFactory.getInstance().createType(doc, PointCloudGrasping.class);

        assertFalse("== not working",
                pcg.getHeight(LengthUnit.MILLIMETER) == pcg
                        .getDepth(LengthUnit.MILLIMETER));


        assertEquals(new Timestamp(666, 12345, TimeUnit.MILLISECONDS), parsed.getTimestamp());

        assertEquals(pcg.getLabel(), parsed.getLabel());

        assertEquals(pcg.getHeight(LengthUnit.MILLIMETER),
                parsed.getHeight(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getWidth(LengthUnit.MILLIMETER),
                parsed.getWidth(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getDepth(LengthUnit.MILLIMETER),
                parsed.getDepth(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(
                pcg.getConvexhull().getSensorOrigin()
                        .getX(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getSensorOrigin().getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(
                pcg.getConvexhull().getSensorOrigin()
                        .getY(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getSensorOrigin().getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(
                pcg.getConvexhull().getSensorOrigin()
                        .getZ(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getSensorOrigin().getZ(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(pcg.getGraspPoint().getX(LengthUnit.MILLIMETER), parsed
                .getGraspPoint().getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getGraspPoint().getY(LengthUnit.MILLIMETER), parsed
                .getGraspPoint().getY(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getGraspPoint().getZ(LengthUnit.MILLIMETER), parsed
                .getGraspPoint().getZ(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(pcg.getHeight(LengthUnit.MILLIMETER),
                parsed.getHeight(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getWidth(LengthUnit.MILLIMETER),
                parsed.getWidth(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(pcg.getDepth(LengthUnit.MILLIMETER),
                parsed.getDepth(LengthUnit.MILLIMETER), 0.0001);

        assertEquals(
                pcg.getConvexhull().getPoints().get(0)
                        .getX(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getPoints().get(0).getX(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(
                pcg.getConvexhull().getPoints().get(1)
                        .getZ(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getPoints().get(1).getZ(LengthUnit.MILLIMETER), 0.0001);
        assertEquals(
                pcg.getConvexhull().getPoints().get(2)
                        .getY(LengthUnit.MILLIMETER), parsed.getConvexhull()
                        .getPoints().get(2).getY(LengthUnit.MILLIMETER), 0.0001);

    }
}

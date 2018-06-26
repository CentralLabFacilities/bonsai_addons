package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.geometry.Cylinder;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.junit.Test;

import javax.vecmath.Matrix3d;
import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.*;

/**
 * Test cases for {@link Type}.
 *
 * @author lziegler
 */
public class CylinderTest {

    @Test
    public void selfCompatibility() throws Exception {

        double[] m = {3.0, 45.0, 2.0, 0.5, 0.1, 2.9, 3.89, 0.4, 23.9};
        Matrix3d mat = new Matrix3d(m);

        Rotation3D r = new Rotation3D(mat);
        Point3D p = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);

        Cylinder c0 = new Cylinder(p, r, 21.0, 42.0, LengthUnit.MILLIMETER);
        Cylinder c1 = new Cylinder(p, r, 21.0, 42.0, LengthUnit.MILLIMETER);

        assertTrue("equals() not working", c0.equals(c1));
        assertTrue("equals() not working", c1.equals(c0));

        c1.setHeight(45, LengthUnit.METER);

        assertFalse("equals() not working", c0.equals(c1));

        Document doc = XomTypeFactory.getInstance().createDocument(c0);

        Cylinder c2 = XomTypeFactory.getInstance().createType(doc, Cylinder.class);

        assertEquals("serializing not working", c0, c2);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Cylinder.xml"));

        Cylinder parsed = XomTypeFactory.getInstance().createType(doc, Cylinder.class);


        assertEquals(0.0, parsed.getPosition().getX(LengthUnit.MILLIMETER),
                0.00001);
        assertEquals(1.0, parsed.getPosition().getY(LengthUnit.MILLIMETER),
                0.00001);
        assertEquals(2.0, parsed.getPosition().getZ(LengthUnit.MILLIMETER),
                0.00001);
        assertEquals(6.0, parsed.getHeight(LengthUnit.MILLIMETER), 0.00001);
        assertEquals(7.0, parsed.getRadius(LengthUnit.MILLIMETER), 0.00001);

        assertEquals(0.0, parsed.getOrientation().getMatrix().m00, 0.00001);
        assertEquals(1.0, parsed.getOrientation().getMatrix().m01, 0.00001);
        assertEquals(2.0, parsed.getOrientation().getMatrix().m02, 0.00001);
        assertEquals(3.0, parsed.getOrientation().getMatrix().m10, 0.00001);
        assertEquals(4.0, parsed.getOrientation().getMatrix().m11, 0.00001);
        assertEquals(5.0, parsed.getOrientation().getMatrix().m12, 0.00001);
        assertEquals(6.0, parsed.getOrientation().getMatrix().m20, 0.00001);
        assertEquals(7.0, parsed.getOrientation().getMatrix().m21, 0.00001);
        assertEquals(8.0, parsed.getOrientation().getMatrix().m22, 0.00001);
    }
}

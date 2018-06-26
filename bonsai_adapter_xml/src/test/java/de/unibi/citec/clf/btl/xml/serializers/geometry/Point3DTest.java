package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Test cases for {@link Type}.
 *
 * @author lziegler
 */
public class Point3DTest {

    @Test
    public void selfCompatibility() throws Exception {

        Point3D p0 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER, "map");
        Point3D p1 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER, "map");
        Point3D p2 = new Point3D(0.1, 0.2, 0.3, LengthUnit.MILLIMETER, "base_link");

        assertTrue("equals() not working", p0.equals(p1));
        assertTrue("equals() not working", p1.equals(p0));

        assertFalse("equals() not working", p1.equals(p2));

        assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
                p2.getX(LengthUnit.METER) * 1000, 0.00001);
        assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
                p2.getY(LengthUnit.METER) * 1000, 0.00001);
        assertEquals("Conversion not working", p2.getZ(LengthUnit.MILLIMETER),
                p2.getZ(LengthUnit.METER) * 1000, 0.00001);
        assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
                p2.getX(LengthUnit.CENTIMETER) * 10, 0.00001);
        assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
                p2.getY(LengthUnit.CENTIMETER) * 10, 0.00001);
        assertEquals("Conversion not working", p2.getZ(LengthUnit.MILLIMETER),
                p2.getZ(LengthUnit.CENTIMETER) * 10, 0.00001);

        Document doc = XomTypeFactory.getInstance().createDocument(p0);

        Point3D p3 = XomTypeFactory.getInstance().createType(doc, Point3D.class);

        assertEquals("X not equal", p3.getX(LengthUnit.METER), p0.getX(LengthUnit.METER), 0.00001);
        assertEquals("Y not equal", p3.getY(LengthUnit.METER), p0.getY(LengthUnit.METER), 0.00001);
        assertEquals("Z not equal", p3.getZ(LengthUnit.METER), p0.getZ(LengthUnit.METER), 0.00001);
        assertEquals("frameid not equal", p3.getFrameId(), p0.getFrameId());

    }
}

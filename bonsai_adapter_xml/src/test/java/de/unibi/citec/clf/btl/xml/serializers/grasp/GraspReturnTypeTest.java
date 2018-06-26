package de.unibi.citec.clf.btl.xml.serializers.grasp;


import de.unibi.citec.clf.btl.data.grasp.GraspReturnType;
import de.unibi.citec.clf.btl.data.grasp.GraspReturnType.GraspResult;
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
public class GraspReturnTypeTest {

    @Test
    public void selfCompatibility() throws Exception {

        GraspReturnType p0 = new GraspReturnType(1, 2, 3, LengthUnit.MILLIMETER, 2.5, GraspResult.SUCCESS, "");
        GraspReturnType p1 = new GraspReturnType(1, 2, 3, LengthUnit.MILLIMETER, 2.5, GraspResult.SUCCESS, "");
        GraspReturnType p2 = new GraspReturnType(0.1, 0.2, 0.3, LengthUnit.MILLIMETER, 2.2, GraspResult.FAIL, "");

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

        GraspReturnType p3 = XomTypeFactory.getInstance().createType(doc, GraspReturnType.class);

        assertTrue("serializing not working", p0.equals(p3));
    }
}

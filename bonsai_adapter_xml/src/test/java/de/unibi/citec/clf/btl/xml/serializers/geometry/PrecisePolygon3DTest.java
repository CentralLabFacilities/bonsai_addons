package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon3D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link Type}.
 *
 * @author ltwardon
 */
public class PrecisePolygon3DTest {

    //@Test
    public void selfCompatibility() throws Exception {

        PrecisePolygon3D p0 = new PrecisePolygon3D();
        p0.addPoint(0, 0, 0, LengthUnit.MILLIMETER);
        p0.addPoint(2, 0, 1, LengthUnit.MILLIMETER);
        p0.addPoint(0, 2, 1, LengthUnit.MILLIMETER);

        PrecisePolygon3D p1 = new PrecisePolygon3D();
        p1.addPoint(0, 0, 0, LengthUnit.MILLIMETER);
        p1.addPoint(2, 0, 1, LengthUnit.MILLIMETER);

        assertFalse("equals() not working", p0.equals(p1));
        assertFalse("equals() not working", p1.equals(p0));

        p1.addPoint(0, 2, 1, LengthUnit.MILLIMETER);

        assertTrue("equals() not working", p0.equals(p1));
        assertTrue("equals() not working", p1.equals(p0));

        p1.addPoint(0, 3, 1, LengthUnit.MILLIMETER);

        assertFalse("equals() not working", p0.equals(p1));
        assertFalse("equals() not working", p1.equals(p0));

        Document doc = XomTypeFactory.getInstance().createDocument(p0);
        PrecisePolygon3D p2 = XomTypeFactory.getInstance().createType(doc, PrecisePolygon3D.class);

        assertTrue("serializing not working", p0.equals(p2));
    }

}

package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.*;

/**
 * Test cases for {@link Type}.
 *
 * @author lziegler
 */
public class Point2DTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        Point2D p0 = new Point2D(new Point2D(1, 2, LengthUnit.MILLIMETER, "map"));
        Point2D p1 = new Point2D(new Point2D(1, 2, LengthUnit.MILLIMETER, "map"));
        Point2D p2 = new Point2D(new Point2D(0.1, 0.2, LengthUnit.MILLIMETER, "map"));

        assertTrue("equals() not working", p0.equals(p1));
        assertTrue("equals() not working", p1.equals(p0));

        assertFalse("equals() not working", p1.equals(p2));

        assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
                p2.getX(LengthUnit.METER) * 1000, 0.00001);
        assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
                p2.getY(LengthUnit.METER) * 1000, 0.00001);
        assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
                p2.getX(LengthUnit.CENTIMETER) * 10, 0.00001);
        assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
                p2.getY(LengthUnit.CENTIMETER) * 10, 0.00001);

        XomTypeFactory factory = XomTypeFactory.getInstance();
        Document doc = factory.createDocument(p0);

        Point2D p3 = factory.createType(doc, Point2D.class);

        assertEquals("X not equal", p3.getX(LengthUnit.METER), p0.getX(LengthUnit.METER), 0.00001);
        assertEquals("Y not equal", p3.getY(LengthUnit.METER), p0.getY(LengthUnit.METER), 0.00001);

        //assertTrue("serializing not working", p0.equals(p3));
    }

    @Test
    public void fileCompatibility() throws Exception {

        Point2D actual = new Point2D(1.0, 2.0, LengthUnit.METER);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Point2D.xml"));

        Point2D parsed = XomTypeFactory.getInstance().createType(doc, Point2D.class);
        assertEquals(actual, parsed);
    }
}

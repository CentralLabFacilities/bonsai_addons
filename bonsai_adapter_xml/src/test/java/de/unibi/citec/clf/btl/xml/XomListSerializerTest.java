package de.unibi.citec.clf.btl.xml;


import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 */
public class XomListSerializerTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws Exception {

        final long timestamp = System.currentTimeMillis();

        List<Point2D> original = new List<Point2D>(Point2D.class);
        original.setGenerator("test");
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);

        Point2D p1 = new Point2D();
        p1.setGenerator("facesensor1");
        p1.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        p1.setX(235, LengthUnit.METER);
        original.add(p1);

        Point2D p2 = new Point2D();
        p2.setGenerator("facesensor2");
        p2.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        p2.setX(96755, LengthUnit.METER);
        original.add(p2);

        // Document doc = new Document(new
        // Element(List.getBaseTag()));
        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        List<Point2D> parsed = XomTypeFactory.getInstance().createTypeList(doc, Point2D.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.size(), parsed.size());
        assertEquals(original.get(0).getX(LengthUnit.METER), parsed.get(0)
                .getX(LengthUnit.METER), 0.0001);

    }

    @Test
    public void testDynamicInstantiation() throws Exception {

        final long timestamp = System.currentTimeMillis();

        List<Point2D> original = new List<Point2D>(Point2D.class);
        original.setGenerator("test");
        original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);

        Point2D face1 = new Point2D();
        face1.setGenerator("facesensor1");
        face1.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        face1.setX(563, LengthUnit.METER);
        original.add(face1);

        Point2D face2 = new Point2D();
        face2.setGenerator("facesensor2");
        face2.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        face2.setX(5789, LengthUnit.METER);
        original.add(face2);

        Document doc = XomTypeFactory.getInstance().createDocument(original);

        logger.debug("xml: " + doc.toXML());

        List<Point2D> parsed = XomTypeFactory.getInstance().createTypeList(
                doc, Point2D.class);

        assertEquals(original.getGenerator(), parsed.getGenerator());
        assertEquals(original.getTimestamp(), parsed.getTimestamp());
        assertEquals(original.size(), parsed.size());
        assertEquals(original.get(0).getX(LengthUnit.METER), parsed.get(0)
                .getX(LengthUnit.METER), 0.0001);
    }
}

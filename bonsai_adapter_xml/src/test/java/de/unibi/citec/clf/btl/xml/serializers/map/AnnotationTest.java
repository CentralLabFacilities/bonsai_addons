package de.unibi.citec.clf.btl.xml.serializers.map;


import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.map.Annotation;
import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.*;

/**
 * Test cases for {@link Annotation}.
 *
 * @author lkettenb
 */
public class AnnotationTest {

    @Test
    public void selfCompatibility() throws Exception {
        Annotation annotation = this.buildAnnotation();

        Document doc = XomTypeFactory.getInstance().createDocument(annotation);
        Annotation parsedAnnotation = XomTypeFactory.getInstance().createType(doc, Annotation.class);

        assertTrue("Parsing label not working.",
                annotation.getLabel().equals(parsedAnnotation.getLabel()));
        assertTrue("Parsing polygon not working.",
                annotation.getPolygon().contains(1.0, 1.0, LengthUnit.METER));
        assertFalse("Parsing polygon not working.",
                annotation.getPolygon().contains(1.0, 3.0, LengthUnit.METER));
        assertTrue("Parsing viewpoints not working.",
                annotation.getViewpoints().getFirst().getLabel().equals(
                        parsedAnnotation.getViewpoints().getFirst().getLabel()));
        assertTrue("Parsing viewpoints not working.",
                annotation.getViewpoints().getLast().getLabel().equals(
                        parsedAnnotation.getViewpoints().getLast().getLabel()));
    }

    public void fileCompability() throws Exception {
        Annotation actual = this.buildAnnotation();

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Annotation.xml"));

        Annotation parsed = XomTypeFactory.getInstance().createType(doc, Annotation.class);

        assertEquals(actual, parsed);

    }


    public Annotation buildAnnotation() {
        PrecisePolygon polygon = new PrecisePolygon();
        polygon.addPoint(0.0, 0.0, LengthUnit.METER);
        polygon.addPoint(0.0, 2.0, LengthUnit.METER);
        polygon.addPoint(2.0, 2.0, LengthUnit.METER);
        polygon.addPoint(2.0, 0.0, LengthUnit.METER);

        PositionData coordinates = new PositionData(0.0, 1.0, 2.0,
                new Timestamp(), LengthUnit.METER, AngleUnit.RADIAN);
        Viewpoint viewpoint = new Viewpoint(coordinates, "table1");
        Viewpoint viewpoint2 = new Viewpoint(coordinates, "table2");

        Annotation annotation = new Annotation("kitchen", polygon, viewpoint, viewpoint2);

        return annotation;
    }
}

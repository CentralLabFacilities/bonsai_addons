package de.unibi.citec.clf.btl.xml.serializers.geometry;

import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
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
 * @author lziegler
 */
public class BoundingBox3DTest {

    private Logger logger = Logger.getLogger(getClass());

    //@Test
    //TODO: refactor to use points intelligently
    public void selfCompatibility() throws Exception {

        BoundingBox3D original = new BoundingBox3D();
        Point3D origin = new Point3D(1.0, 1.0, 1.0, LengthUnit.MILLIMETER);
        Point3D size = new Point3D(2.0, 3.0, 4.0, LengthUnit.MILLIMETER);
        original.setSize(size);
        Rotation3D rotation = new Rotation3D();
        Pose3D pose = new Pose3D(origin, rotation);
        original.setPose(pose);


//		original.fillInto(doc.getRootElement());
        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

//		BoundingBox3D parsed = new BoundingBox3D();
//		BoundingBox3D.typeFromElement(doc.getRootElement(), parsed);
        BoundingBox3D parsed = XomTypeFactory.getInstance().createType(doc, BoundingBox3D.class);

        assertEquals(original.getPose().getTranslation(), parsed.getPose().getTranslation());
        assertEquals(original.getPose().getRotation(), parsed.getPose().getRotation());
        assertEquals(original.getSize(), parsed.getSize());

        System.out.println(parsed);
    }

    //@Test
    //TODO: refactor to use points intelligently
    public void fileCompatibility() throws Exception {

        Point3D origin = new Point3D(1.0, 2.0, 3.0, LengthUnit.MILLIMETER, "map");
        Point3D size = new Point3D(4.0, 5.0, 6.0, LengthUnit.MILLIMETER, "map");

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("BoundingBox3D.xml"));

//		BoundingBox3D parsed = new BoundingBox3D();
//		BoundingBox3D.typeFromElement(doc.getRootElement(), parsed);
        BoundingBox3D parsed = XomTypeFactory.getInstance().createType(doc, BoundingBox3D.class);

        assertEquals(new Timestamp(253543797, 253543712, TimeUnit.MILLISECONDS), parsed.getTimestamp());

        assertEquals(0.0, parsed.getPose().getRotation().getMatrix().m00, 0.00001);
        assertEquals(1.0, parsed.getPose().getRotation().getMatrix().m01, 0.00001);
        assertEquals(2.0, parsed.getPose().getRotation().getMatrix().m02, 0.00001);
        assertEquals(3.0, parsed.getPose().getRotation().getMatrix().m10, 0.00001);
        assertEquals(4.0, parsed.getPose().getRotation().getMatrix().m11, 0.00001);
        assertEquals(5.0, parsed.getPose().getRotation().getMatrix().m12, 0.00001);
        assertEquals(6.0, parsed.getPose().getRotation().getMatrix().m20, 0.00001);
        assertEquals(7.0, parsed.getPose().getRotation().getMatrix().m21, 0.00001);
        assertEquals(8.0, parsed.getPose().getRotation().getMatrix().m22, 0.00001);

        assertEquals(origin, parsed.getPose().getTranslation());
        assertEquals(size, parsed.getSize());
    }
}

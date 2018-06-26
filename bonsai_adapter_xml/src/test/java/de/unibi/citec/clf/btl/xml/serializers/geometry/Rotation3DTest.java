package de.unibi.citec.clf.btl.xml.serializers.geometry;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.junit.Test;

import javax.vecmath.Matrix3d;
import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link Type}.
 *
 * @author lziegler
 */
public class Rotation3DTest {

    @Test
    public void selfCompatibility() throws Exception {

        double[] m = {3.0, 45.0, 2.0, 0.5, 0.1, 2.9, 3.89, 0.4, 23.9};
        Matrix3d mat0 = new Matrix3d(m);
        Rotation3D rot0 = new Rotation3D(mat0);
        Matrix3d mat1 = new Matrix3d(m);
        Rotation3D rot1 = new Rotation3D(mat1);

        assertTrue("equals() not working", rot0.equals(rot1));
        assertTrue("equals() not working", rot1.equals(rot0));

        Document doc = XomTypeFactory.getInstance().createDocument(rot0);

        Rotation3D rot2 = XomTypeFactory.getInstance().createType(doc, Rotation3D.class);

        assertEquals(rot0, rot2);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("Rotation3D.xml"));

        Rotation3D parsed = XomTypeFactory.getInstance().createType(doc, Rotation3D.class);

        assertEquals(0.0, parsed.getMatrix().m00, 0.00001);
        assertEquals(1.0, parsed.getMatrix().m01, 0.00001);
        assertEquals(2.0, parsed.getMatrix().m02, 0.00001);
        assertEquals(3.0, parsed.getMatrix().m10, 0.00001);
        assertEquals(4.0, parsed.getMatrix().m11, 0.00001);
        assertEquals(5.0, parsed.getMatrix().m12, 0.00001);
        assertEquals(6.0, parsed.getMatrix().m20, 0.00001);
        assertEquals(7.0, parsed.getMatrix().m21, 0.00001);
        assertEquals(8.0, parsed.getMatrix().m22, 0.00001);
    }
}

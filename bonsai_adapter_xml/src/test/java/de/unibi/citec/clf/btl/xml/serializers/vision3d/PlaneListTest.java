package de.unibi.citec.clf.btl.xml.serializers.vision3d;


import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.vision2d.RegionData.Scope;
import de.unibi.citec.clf.btl.data.vision3d.PlaneData;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author ltwardon, lziegler
 */
public class PlaneListTest {

    @Test
    public void selfCompatibility() throws Exception {

        PlaneData plane1 = new PlaneData();
        plane1.setScope(Scope.LOCAL);
        PlaneData plane2 = new PlaneData();
        plane2.setScope(Scope.LOCAL);

        List<PlaneData> original = new List<PlaneData>(PlaneData.class);
        original.add(plane1);
        original.add(plane2);

        Document doc = XomTypeFactory.getInstance().createDocument(original);

        List<PlaneData> parsed = XomTypeFactory.getInstance().createTypeList(doc, PlaneData.class);

        assertEquals(original.size(), parsed.size());
        assertEquals(original.get(0).getScope(), parsed.get(0).getScope());
        assertEquals(original.get(1).getScope(), parsed.get(1).getScope());
    }

}

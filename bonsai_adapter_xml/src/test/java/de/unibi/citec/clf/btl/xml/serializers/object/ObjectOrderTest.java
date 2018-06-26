package de.unibi.citec.clf.btl.xml.serializers.object;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.data.object.ObjectOrder;
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
 * @author vlosing
 */
public class ObjectOrderTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        ObjectOrder objectOrder = new ObjectOrder();

        objectOrder.setObjectName("Cola");
        objectOrder.setOrdererName("Peter");
        objectOrder.setCategory("drinks");
        objectOrder.setOrdererFaceClassId(4);
        objectOrder.setTargetLocation(new Viewpoint());

        Document doc = XomTypeFactory.getInstance().createDocument(objectOrder);
        logger.info(doc.toXML());

        ObjectOrder parsed = XomTypeFactory.getInstance().createType(doc,
                ObjectOrder.class);

        assertEquals(objectOrder.getGenerator(), parsed.getGenerator());
        assertEquals(objectOrder.getTimestamp(), parsed.getTimestamp());
        assertEquals(objectOrder.getObjectName(), parsed.getObjectName());
        assertEquals(objectOrder.getOrdererName(), parsed.getOrdererName());
        assertEquals(objectOrder.getOrdererFaceClassId(), parsed.getOrdererFaceClassId());
        assertEquals(objectOrder.getCategory(), parsed.getCategory());
        assertEquals(objectOrder.getTargetLocation(),
                parsed.getTargetLocation());
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("ObjectOrder.xml"));

        ObjectOrder parsed = XomTypeFactory.getInstance().createType(doc,
                ObjectOrder.class);

        assertEquals("Sprite", parsed.getObjectName());
        assertEquals("Horst", parsed.getOrdererName());
        assertEquals("drinks", parsed.getCategory());
    }

}

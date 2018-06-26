package de.unibi.citec.clf.btl.xml.serializers.vision2d;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.vision2d.ImageData;
import de.unibi.citec.clf.btl.data.vision2d.ImageData.ColorMode;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author
 */
public class ImageDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        ImageData image = new ImageData();
        byte[] data = javax.xml.bind.DatatypeConverter.parseHexBinary("e04fd020ea3a6910a2d808002b30309d");
        //byte[] data = "test".getBytes();
        //System.err.println(data);


        image.setHeight(480);
        image.setWidth(640);
        image.setDepth(1);
        image.setColorMode(ImageData.ColorMode.RGB);
        image.setData(data);

        Document xml = XomTypeFactory.getInstance().createDocument(image);
        //System.err.println(xml.toXML());
        ImageData parsed = XomTypeFactory.getInstance().createType(xml, ImageData.class);

        assertEquals(image.getGenerator(), parsed.getGenerator());
        assertEquals(image.getWidth(), parsed.getWidth());
        assertEquals(image.getHeight(), parsed.getHeight());
        assertEquals(image.getDepth(), parsed.getDepth());
        assertEquals(image.getColorMode(), parsed.getColorMode());
        Assert.assertArrayEquals(image.getData(), parsed.getData());
    }

    @Test
    public void fileCompatibility() throws Exception {


        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("ImageData.xml"));
        ImageData parsed = XomTypeFactory.getInstance().createType(doc, ImageData.class);

        assertEquals(new Timestamp(1464095285645l, 1464095285645l, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(ColorMode.RGB, parsed.getColorMode());
        assertEquals(480, parsed.getHeight());
        assertEquals(640, parsed.getWidth());

    }

}

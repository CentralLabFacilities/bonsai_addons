package de.unibi.citec.clf.btl.xml.serializers.object;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.object.ObjectData;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author jwienke
 */
public class ObjectDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws Exception {

        final long timestamp = Time.currentTimeMillis();

        ObjectData original = new ObjectData();


        ObjectData.Hypothesis hyp = new ObjectData.Hypothesis();
        hyp.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        hyp.setGenerator("test");
        hyp.setReliability(0.1);
        hyp.setClassLabel("testclass");

        original.addHypothesis(hyp);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        ObjectData parsed = XomTypeFactory.getInstance().createType(doc, ObjectData.class);
        ObjectData.Hypothesis parsedHyp = parsed.getHypotheses().iterator().next();

        assertEquals(hyp.getGenerator(), parsedHyp.getGenerator());
        assertEquals(hyp.getTimestamp(), parsedHyp.getTimestamp());
        assertEquals(hyp.getClassLabel(), parsedHyp.getClassLabel());
        assertEquals(hyp.getReliability(), parsedHyp.getReliability(), 0.0001);

    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("ObjectData.xml"));

        ObjectData parsed = XomTypeFactory.getInstance().createType(doc, ObjectData.class);
        ObjectData.Hypothesis parsedHyp = parsed.getHypotheses().iterator().next();


        assertEquals("BoostDetector", parsedHyp.getGenerator());
        assertEquals(new Timestamp(253543797l, 253543712l, TimeUnit.MILLISECONDS), parsedHyp
                .getTimestamp());
        assertEquals("smacks", parsedHyp.getClassLabel());
        assertEquals(0.6, parsedHyp.getReliability(), 0.002);

    }

}

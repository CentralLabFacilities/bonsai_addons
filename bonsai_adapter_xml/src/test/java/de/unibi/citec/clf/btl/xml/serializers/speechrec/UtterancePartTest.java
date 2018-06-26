package de.unibi.citec.clf.btl.xml.serializers.speechrec;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.speechrec.UtterancePart;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
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
 * @author sjebbara
 */
public class UtterancePartTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws SerializationException, DeserializationException {

        UtterancePart actual = new UtterancePart();
        final long timestamp = System.currentTimeMillis();
        actual.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        actual.setGenerator("test");
        actual.setWord("auto");
        actual.setBegin(1);
        actual.setId(2);
        actual.setEnd(3);
        actual.setAcousticScore(4.567);
        actual.setLmScore(5.678);
        actual.setCombinedScore(6.789);

        Document doc = XomTypeFactory.getInstance().createDocument(actual);
        logger.info(doc.toXML());

        UtterancePart parsed = XomTypeFactory.getInstance().createType(doc, UtterancePart.class);

        assertEquals(actual, parsed);
    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("UtterancePart.xml"));

        UtterancePart parsed = XomTypeFactory.getInstance().createType(doc, UtterancePart.class);


        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        assertEquals(1, parsed.getBegin());
        assertEquals(2, parsed.getId());
        assertEquals(3, parsed.getEnd());
        assertEquals(4.567, parsed.getAcousticScore(), 0);
        assertEquals(5.678, parsed.getLmScore(), 0);
        assertEquals(6.789, parsed.getCombinedScore(), 0);
    }

}

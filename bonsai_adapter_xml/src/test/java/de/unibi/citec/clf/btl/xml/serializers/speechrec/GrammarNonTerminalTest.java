package de.unibi.citec.clf.btl.xml.serializers.speechrec;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.data.speechrec.UtterancePart;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
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
 * @author sjebbara
 */
public class GrammarNonTerminalTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws SerializationException, DeserializationException {

        GrammarNonTerminal actual = new GrammarNonTerminal();
        final long timestamp = System.currentTimeMillis();
        actual.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        actual.setGenerator("test");
        actual.setName("Phrase");

        UtterancePart utt = new UtterancePart();
        final long uttTimestamp = System.currentTimeMillis();
        utt.setTimestamp(uttTimestamp, TimeUnit.MILLISECONDS);
        utt.setGenerator("test");
        utt.setWord("das");
        utt.setBegin(1);
        utt.setId(2);
        utt.setEnd(3);
        utt.setAcousticScore(4.567);
        utt.setLmScore(5.678);
        utt.setCombinedScore(6.789);

        GrammarNonTerminal nt = new GrammarNonTerminal();
        final long ntTimestamp = System.currentTimeMillis();
        nt.setTimestamp(ntTimestamp, TimeUnit.MILLISECONDS);
        nt.setGenerator("test");
        nt.setName("Nomen");

        actual.addSymbol(utt);
        actual.addSymbol(nt);

        Document doc = XomTypeFactory.getInstance().createDocument(actual);
        logger.info(doc.toXML());

        GrammarNonTerminal parsed = XomTypeFactory.getInstance().createType(doc, GrammarNonTerminal.class);

        assertEquals(actual, parsed);
    }

    //@Test
    public void fileCompatibility() throws Exception {

        GrammarNonTerminal actual = new GrammarNonTerminal();
        final long timestamp = 12345;
        actual.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        actual.setGenerator("test");
        actual.setName("Phrase");

        UtterancePart utt = new UtterancePart();
        final long uttTimestamp = 12345;
        utt.setTimestamp(uttTimestamp, TimeUnit.MILLISECONDS);
        utt.setGenerator("test");
        utt.setWord("das");
        utt.setBegin(1);
        utt.setId(2);
        utt.setEnd(3);
        utt.setAcousticScore(4.567);
        utt.setLmScore(5.678);
        utt.setCombinedScore(6.789);

        GrammarNonTerminal nt = new GrammarNonTerminal();
        final long ntTimestamp = 12345;
        nt.setTimestamp(ntTimestamp, TimeUnit.MILLISECONDS);
        nt.setGenerator("test");
        nt.setName("Nomen");

        actual.addSymbol(utt);
        actual.addSymbol(nt);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("GrammarNonTerminal.xml"));

        GrammarNonTerminal parsed = XomTypeFactory.getInstance().createType(doc, GrammarNonTerminal.class);

        System.out.println(actual.getSubsymbols());
        System.out.println(parsed.getSubsymbols());

        assertEquals(actual, parsed);
    }
}

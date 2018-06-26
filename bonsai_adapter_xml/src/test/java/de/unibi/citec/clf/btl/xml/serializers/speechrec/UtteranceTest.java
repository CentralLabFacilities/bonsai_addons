package de.unibi.citec.clf.btl.xml.serializers.speechrec;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.speechrec.*;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link Type}.
 *
 * @author jwienke
 * @author lkettenb
 */
public class UtteranceTest {

    private static Logger logger = Logger.getLogger(UtteranceTest.class);

    @Before
    public void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws Exception {
        // TODO serialization needed
    }

    @Test
    public void fileCompatibility() throws Exception {
        Builder parser = new Builder();
        Document doc = parser
                .build(TestUtils.makeTestFileName("Utterance.xml"));

        Utterance parsed = XomTypeFactory.getInstance().createType(doc, Utterance.class);

        logger.info("Parsed file: " + parsed.getSimpleString());

        // TODO this test is a stub

        assertEquals(new Timestamp(1448972928781000l, TimeUnit.MILLISECONDS), parsed.getTimestamp());
    }

    @Test
    public void testSetGetParentParsing() throws ValidityException, ParsingException, DeserializationException {
        Builder parser = new Builder();
        Document doc;
        try {
            doc = parser.build(TestUtils.makeTestFileName("Utterance.xml"));
        } catch (IOException ex) {
            logger.fatal(ex);
            assertTrue(false);
            return;
        }

        Utterance utterance = XomTypeFactory.getInstance().createType(doc, Utterance.class);
        GrammarTree tree = utterance.getGrammarTree();

        try {
            tree.getParent();
            assertTrue(false);
        } catch (RuntimeException ex) {
            assertTrue(true);
        }

        assertTrue(testForParents(true, tree));
    }

    private static boolean testForParents(boolean isFirstElemt, GrammarNonTerminal tree) {
        for (GrammarSymbol s : tree.getSubsymbols()) {
            logger.debug("Found parent: " + s.getParent());
            // First or root element?
            if (isFirstElemt) {
                assertTrue(s.getParent() == null);
                isFirstElemt = false;
            } else {
                assertTrue(s.getParent() != null);
            }
            if (s instanceof UtterancePart) {
                UtterancePart t = (UtterancePart) s;
                logger.debug("Found terminal: " + t.getWord());
            } else if (s instanceof GrammarNonTerminal) {
                GrammarNonTerminal nt = (GrammarNonTerminal) s;
                logger.debug("Found non-terminal: " + nt.getName());
                return testForParents(false, nt);
            }
        }
        return true;
    }
}

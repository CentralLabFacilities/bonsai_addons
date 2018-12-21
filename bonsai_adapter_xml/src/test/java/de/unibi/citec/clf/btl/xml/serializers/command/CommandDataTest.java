package de.unibi.citec.clf.btl.xml.serializers.command;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.data.command.CommandData;
import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * @author ikillman
 */
public class CommandDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws ParsingException,
            de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException,
            de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException {

        CommandData command = new CommandData();
        final long timestamp = Time.currentTimeMillis();
        command.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        command.setAction("bring");
        command.setActionset(true);
        command.setLocation("appliance");
        command.setLocationset(true);
        command.setSecondLocation("location");
        command.setSecondLocationset(true);
        command.setPreposition("");
        command.setPrepositionset(true);
        command.setObject("drink");
        command.setObjectset(true);
        command.setRoom("kitchen");
        command.setRoomset(true);
        command.setPerson("jack");
        command.setPersonset(true);

        GrammarNonTerminal tree = new GrammarNonTerminal();
        command.setTree(tree);

        Document document = XomTypeFactory.getInstance().createDocument(command);

        logger.info(document.toXML());

        CommandData parsed = XomTypeFactory.getInstance().createType(document, CommandData.class);

        assertEquals(command.getAction(), parsed.getAction());
        assertEquals(command.getLocation(), parsed.getLocation());
        assertEquals(command.getSecondLocation(), parsed.getSecondLocation());
        assertEquals(command.getPreposition(), parsed.getPreposition());
        assertEquals(command.getObject(), parsed.getObject());
        assertEquals(command.getRoom(), parsed.getRoom());
        assertEquals(command.getPerson(), parsed.getPerson());

        assertEquals(command.getTree(), parsed.getTree());

        assertEquals(command.isActionset(), parsed.isActionset());
        assertEquals(command.isLocationset(), parsed.isLocationset());
        assertEquals(command.isSecondLocationset(), parsed.isSecondLocationset());
        assertEquals(command.isPrepositionset(), parsed.isPrepositionset());
        assertEquals(command.isObjectset(), parsed.isObjectset());
        assertEquals(command.isPersonset(), parsed.isPersonset());
        assertEquals(command.isRoomset(), parsed.isRoomset());

    }

    //@Test
    public void fileCompatibility() throws Exception {

        CommandData command = new CommandData();
        final long timestamp = Time.currentTimeMillis();
        command.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        command.setAction("bring");
        command.setActionset(true);
        command.setLocation("appliance");
        command.setLocationset(true);
        command.setSecondLocation("location");
        command.setSecondLocationset(true);
        command.setPreposition("");
        command.setPrepositionset(true);
        command.setObject("drink");
        command.setObjectset(true);
        command.setRoom("kitchen");
        command.setRoomset(true);
        command.setPerson("jack");
        command.setPersonset(true);

        GrammarNonTerminal tree = new GrammarNonTerminal();
        command.setTree(tree);

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("CommandData.xml"));

        CommandData parsed = XomTypeFactory.getInstance().createType(doc, CommandData.class);

        assertEquals("test", parsed.getGenerator());

        assertEquals(command.getAction(), parsed.getAction());
        assertEquals(command.getLocation(), parsed.getLocation());
        assertEquals(command.getSecondLocation(), parsed.getSecondLocation());
        assertEquals(command.getObject(), parsed.getObject());
        assertEquals(command.getTree(), parsed.getTree());
        assertEquals(command.getRoom(), parsed.getRoom());

        assertEquals(command.isActionset(), parsed.isActionset());
        assertEquals(command.isLocationset(), parsed.isLocationset());
        assertEquals(command.isSecondLocationset(), parsed.isSecondLocationset());
        assertEquals(command.isPrepositionset(), parsed.isPrepositionset());
        assertEquals(command.isObjectset(), parsed.isObjectset());
        assertEquals(command.isPersonset(), parsed.isPersonset());
        assertEquals(command.isRoomset(), parsed.isRoomset());
    }

}

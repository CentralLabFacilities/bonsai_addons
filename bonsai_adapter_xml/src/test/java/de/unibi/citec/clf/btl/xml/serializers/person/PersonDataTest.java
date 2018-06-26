package de.unibi.citec.clf.btl.xml.serializers.person;


import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.data.person.PersonData;
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
 * @author jwienke
 */
public class PersonDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws SerializationException, DeserializationException {

        PersonData original = new PersonData();
        //final long timestamp = System.currentTimeMillis();
        //original.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        //original.setGenerator("test");
        original.setName("peter");
        original.setUuid("tolle id");

        PersonAttribute pa = new PersonAttribute();
        pa.setShirtcolor(PersonAttribute.Shirtcolor.BLACK);
        pa.setGender(PersonAttribute.Gender.FEMALE);
        //pa.setGesture(PersonAttribute.Gesture.NEUTRAL);
        pa.setAge("1-2");
        pa.setPosture(PersonAttribute.Posture.LYING);
        original.setPersonAttribute(pa);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        PersonData parsed = XomTypeFactory.getInstance().createType(doc, PersonData.class);

        //assertEquals(original.getGenerator(), parsed.getGenerator());
        //assertEquals(original.getTimestamp(), parsed.getTimestamp());

        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getUuid(), parsed.getUuid());


    }


    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils.makeTestFileName("Person.xml"));

        PersonData parsed = XomTypeFactory.getInstance().createType(doc, PersonData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(new Timestamp(12345, 67890, TimeUnit.MILLISECONDS), parsed.getTimestamp());
        // assertEquals(false, parsed.isFacing());
        // assertEquals(Math.PI / 2.0, parsed.getGazing(), 0.00001);
        // assertEquals(123, parsed.getId());
        assertEquals("peter", parsed.getName());
        // assertEquals(456, parsed.getNeighbourDistance(LengthUnit.MILLIMETER), 0.0001);
        // assertEquals(12, parsed.getNeighbourId());
        // assertEquals(true, parsed.isPoi());
        // assertEquals(true, parsed.isTalking());
        // assertEquals(true, parsed.isWalking());
        // assertEquals(true, parsed.hasBody());
        // assertEquals(true, parsed.hasLegs());
        // assertEquals(PersonData.Gender.MALE, parsed.getGender());
        // assertEquals(32, parsed.getAgeFrom() + parsed.getAgeTo());
        // assertEquals(true, parsed.hasFace());
    }

}

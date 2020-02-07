package de.unibi.citec.clf.btl.xml.serializers.person;


import de.unibi.citec.clf.bonsai.core.time.Time;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link Type}.
 *
 * @author Denis Schulze <dschulze@techfak.uni-bielefeld.de>
 */
public class PersonListTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    //@Test
    public void selfCompatibility() throws SerializationException, DeserializationException {

        PersonData data1 = new PersonData();

        final long timestamp = Time.currentTimeMillis();
        data1.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        data1.setGenerator("test");
//        data1.setFacing(true);
//        data1.setGazing(4.993);
//        data1.setId(1234);
        data1.setName("peter");
//        data1.setNeighbourDistance(3.45, LengthUnit.MILLIMETER);
//        data1.setNeighbourId(42);
//        data1.setPoi(false);
//        data1.setTalking(true);
//        data1.setWalking(false);

        PersonData data2 = new PersonData();

        final long timestamp2 = Time.currentTimeMillis();
        data2.setTimestamp(timestamp2, TimeUnit.MILLISECONDS);
        data2.setGenerator("test2");
//        data2.setFacing(false);
//        data2.setGazing(410.45);
//        data2.setId(4321);
        data2.setName("hans");
//        data2.setNeighbourDistance(5.00, LengthUnit.MILLIMETER);
//        data2.setNeighbourId(1);
//        data2.setPoi(true);
//        data2.setTalking(false);
//        data2.setWalking(true);

        PersonDataList original = new PersonDataList();
        original.add(data1);
        original.add(data2);

        Document doc = XomTypeFactory.getInstance().createDocument(original);
        logger.info(doc.toXML());

        PersonDataList parsed = XomTypeFactory.getInstance().createType(doc, PersonDataList.class);

        PersonData parsed1 = parsed.get(0);
        PersonData parsed2 = parsed.get(1);

        assertEquals(data1.getGenerator(), parsed1.getGenerator());
        assertEquals(data1.getTimestamp(), parsed1.getTimestamp());
//        assertEquals(data1.isFacing(), parsed1.isFacing());
//        assertEquals(data1.getGazing(), parsed1.getGazing(), 0.00001);
//        assertEquals(data1.getId(), parsed1.getId());
        assertEquals(data1.getName(), parsed1.getName());
//        assertEquals(data1.getNeighbourDistance(LengthUnit.MILLIMETER), parsed1.getNeighbourDistance(LengthUnit.MILLIMETER), 0.0001);
//        assertEquals(data1.getNeighbourId(), parsed1.getNeighbourId());
//        assertEquals(data1.isPoi(), parsed1.isPoi());
//        assertEquals(data1.isTalking(), parsed1.isTalking());
//        assertEquals(data1.isWalking(), parsed1.isWalking());

        assertEquals(data2.getGenerator(), parsed2.getGenerator());
        assertEquals(data2.getTimestamp(), parsed2.getTimestamp());
//        assertEquals(data2.isFacing(), parsed2.isFacing());
//        assertEquals(data2.getGazing(), parsed2.getGazing(), 0.00001);
//        assertEquals(data2.getId(), parsed2.getId());
        assertEquals(data2.getName(), parsed2.getName());
//        assertEquals(data2.getNeighbourDistance(LengthUnit.MILLIMETER), parsed2.getNeighbourDistance(LengthUnit.MILLIMETER), 0.0001);
//        assertEquals(data2.getNeighbourId(), parsed2.getNeighbourId());
//        assertEquals(data2.isPoi(), parsed2.isPoi());
//        assertEquals(data2.isTalking(), parsed2.isTalking());
//        assertEquals(data2.isWalking(), parsed2.isWalking());

    }

    //@Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("PersonList.xml"));

        PersonDataList parsed = XomTypeFactory.getInstance().createType(doc, PersonDataList.class);

        PersonData parsed1 = parsed.get(0);

        assertEquals("test", parsed1.getGenerator());
        assertEquals(new Timestamp(127, 1271, TimeUnit.MILLISECONDS), parsed1.getTimestamp());
//        assertEquals(true, parsed1.isFacing());
//        assertEquals(4.993, parsed1.getGazing(), 0.00001);
//        assertEquals(1234, parsed1.getId());
        assertEquals("peter", parsed1.getName());
//        assertEquals(3.45, parsed1.getNeighbourDistance(LengthUnit.MILLIMETER), 0.0001);
//        assertEquals(42, parsed1.getNeighbourId());
//        assertEquals(false, parsed1.isPoi());
//        assertEquals(true, parsed1.isTalking());
//        assertEquals(false, parsed1.isWalking());

    }

}

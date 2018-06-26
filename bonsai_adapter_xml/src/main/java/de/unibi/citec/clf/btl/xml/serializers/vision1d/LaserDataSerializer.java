package de.unibi.citec.clf.btl.xml.serializers.vision1d;



import nu.xom.Attribute;

import java.util.StringTokenizer;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.common.MicroTimestampSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.vision1d.LaserData;

/**
 * Domain class for LaserData of BonSAI. The currently mounted Laser has a
 * resolution of 0.5 degrees and scans an angle of 180 degrees in front of the
 * robot, resulting in 361 values. Use LaserData.getAngleValue to obtain the
 * corresponding angles of value. The laser range is limited to 8 meters. The
 * resolution is about 10mm.
 * 
 * @author marc
 * @author jwienke
 * @author lziegler
 */
public class LaserDataSerializer extends XomSerializer<LaserData> {

    private final static Logger logger = Logger
            .getLogger(LaserDataSerializer.class);

    @Override
    public Class<LaserData> getDataType() {
        return LaserData.class;
    }

    @Override
    public String getBaseTag() {
        return "LASERDATA";
    }

    /**
     * Creates instance.
     */
    public LaserDataSerializer() {
    }

    @Override
    public LaserData doFromElement(Element element) throws ParsingException, DeserializationException {

        LaserData ld = new LaserData();

        Nodes nodes = element.query("*");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            MicroTimestampSerializer tSerializer = new MicroTimestampSerializer();
            if (node instanceof Element) {
                Element elem = (Element) node;
                if (elem.getLocalName().equals("MEASUREMENTS")) {
                    int num = ElementParser.getIntAttributeValue(elem, "num");
                    StringTokenizer valuesString = new StringTokenizer(
                            elem.getValue());
                    int valuesInt[] = new int[valuesString.countTokens()];
                    if (num != valuesString.countTokens()) {
                        logger.error("value in XML differs from object count: "
                                + num + " : " + valuesString.countTokens());
                    }
                    int j = 0;
                    while (valuesString.hasMoreTokens()) {
                        valuesInt[j] = Integer.parseInt(valuesString
                                .nextToken());
                        j++;
                    }
                    double valuesDouble[] = new double[valuesInt.length];
                    for (int k = 0; k < valuesInt.length; k++) {
                        valuesDouble[k] = ((double) valuesInt[k] / 1000);
                    }
                    ld.setScanValues(valuesDouble, LaserData.iLU);
                } else if (elem.getLocalName().equals(tSerializer.getBaseTag())) {
//                    ld.setReadTime(tSerializer.fromElement(elem)); TODO: fial
                }
            }
        }
        return ld;
    }

    @Override
    public void doSanitizeElement(Element parent) {
        // TODO Auto-generated method stub

    }

    /**
     * Serializes the {@link LaserDataSerializer} object into a given XOM
     * {@link Element} .
     * 
     * @param parent
     *            The {@link Element} to serialize the object into. The given
     *            {@link Element} object should have the base tag defined by
     *            this class. (see {@link #getClass().getSimpleName()})
     * @see #getClass().getSimpleName()
     */
    @Override
    public void doFillInto(LaserData data, Element parent) throws SerializationException {

        MicroTimestampSerializer mts = new MicroTimestampSerializer();
        Element readTimeElement = new Element(mts.getBaseTag());
//        mts.fillInto(data.getReadTime(), readTimeElement); TODO: fail
        parent.appendChild(readTimeElement);

        Element measurementsElement = new Element("MEASUREMENTS");
        measurementsElement.addAttribute(new Attribute("num", String
                .valueOf(data.getNumLaserPoints())));

        StringBuilder text = new StringBuilder();
        for (double m : data.getScanValues(LaserData.iLU)) {
            text.append((int) Math.round(m * 1000.0));
            text.append(' ');
        }
        measurementsElement.appendChild(text.toString());

        parent.appendChild(measurementsElement);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (!(obj instanceof LaserDataSerializer))
                return false;

            LaserDataSerializer other = (LaserDataSerializer) obj;

            super.equals(other);

        } catch (Exception e) {
            logger.error("equals() Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
}

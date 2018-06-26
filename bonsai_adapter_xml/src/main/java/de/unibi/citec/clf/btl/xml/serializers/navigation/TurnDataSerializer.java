package de.unibi.citec.clf.btl.xml.serializers.navigation;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.common.MicroTimestampSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.navigation.TurnData;

/**
 * This class is used to send direct linear turn commands (angle and speed).
 * 
 * @author lziegler
 * @author unknown
 */
public class TurnDataSerializer extends XomSerializer<TurnData> {

    private static Logger logger = Logger.getLogger(TurnDataSerializer.class);

    @Override
    public TurnData doFromElement(Element element) throws ParsingException, DeserializationException {
        TurnData td = new TurnData();

        Nodes nodes = element.query("*");
        for (int i = 0; i < nodes.size(); i++) {

            MicroTimestampSerializer readTime = new MicroTimestampSerializer();
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                if (elem.getLocalName().equals("SPEED")) {
                    td.setSpeed(ElementParser.getDoubleAttributeValue(elem,
                            "quant"), TurnData.iSU);
                } else if (elem.getLocalName().equals("ANGLE")) {
                    td.setAngle(ElementParser.getDoubleAttributeValue(elem,
                            "quant"), TurnData.iAU);
                } else if (elem.getLocalName().equals(readTime.getBaseTag())) {
                   // td.setReadTime(readTime.fromElement(elem)); //TODO: fail
                }
            }
        }
        logger.debug("parsed Turn Data: " + this);
        return td;
    }

    @Override
    public void doSanitizeElement(Element parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFillInto(TurnData data, Element parent) throws SerializationException {

        MicroTimestampSerializer tmpTs = new MicroTimestampSerializer();
        Element readTimeElement = new Element(tmpTs.getBaseTag());
//        tmpTs.fillInto(data.getReadTime(), readTimeElement); TODO: fail

        Element rotElement = new Element("SPEED");
        rotElement.addAttribute(new Attribute("quant", String.valueOf(data
                .getSpeed(TurnData.iSU))));

        Element transElement = new Element("ANGLE");
        transElement.addAttribute(new Attribute("quant", String.valueOf(data
                .getAngle(TurnData.iAU))));

        parent.appendChild(readTimeElement);
        parent.appendChild(rotElement);
        parent.appendChild(transElement);
    }

    @Override
    public Class<TurnData> getDataType() {
        return TurnData.class;
    }

    @Override
    public String getBaseTag() {
        return "TURNS";
    }

}

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
import de.unibi.citec.clf.btl.data.navigation.DriveData;

/**
 * This class is used to send direct linear drive commands (distance and speed).
 * 
 * @author lziegler
 * @author unknown
 */
public class DriveDataSerializer extends XomSerializer<DriveData> {

    private static Logger logger = Logger.getLogger(DriveData.class);

    @Override
    public Class<DriveData> getDataType() {
        return DriveData.class;
    }

    @Override
    public String getBaseTag() {
        return "DRIVE";
    }

    @Override
    public DriveData doFromElement(Element element) throws ParsingException, DeserializationException {

        DriveData dd = new DriveData();

        MicroTimestampSerializer readTime = new MicroTimestampSerializer();
        Nodes nodes = element.query("*");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                if (elem.getLocalName().equals("SPEED")) {
                    dd.setSpeed(ElementParser.getDoubleAttributeValue(elem,
                            "quant"), DriveData.iSU);
                } else if (elem.getLocalName().equals("DISTANCE")) {
                    dd.setDistance(ElementParser.getDoubleAttributeValue(elem,
                            "quant"), DriveData.iLU);
                } else if (elem.getLocalName().equals(readTime.getBaseTag())) {
                    //dd.setReadTime(readTime.fromElement(elem)); //TODO: fail
                }
            }
        }
        logger.debug("parsed Drive Data: " + this);
        return dd;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(DriveData data, Element parent) throws SerializationException {

        MicroTimestampSerializer tmpTs = new MicroTimestampSerializer();
        Element readTimeElement = new Element(tmpTs.getBaseTag());
        //tmpTs.fillInto(data.getReadTime(), readTimeElement); //TODO: fail
        parent.appendChild(readTimeElement);

        Element rotElement = new Element("SPEED");
        rotElement.addAttribute(new Attribute("quant", String.valueOf(data
                .getSpeed(DriveData.iSU))));
        parent.appendChild(rotElement);

        Element transElement = new Element("DISTANCE");
        transElement.addAttribute(new Attribute("quant", String.valueOf(data
                .getDistance(DriveData.iLU))));
        parent.appendChild(transElement);

    }

}

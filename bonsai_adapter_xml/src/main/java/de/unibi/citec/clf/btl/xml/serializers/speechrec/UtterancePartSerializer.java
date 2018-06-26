package de.unibi.citec.clf.btl.xml.serializers.speechrec;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import de.unibi.citec.clf.btl.data.speechrec.UtterancePart;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * Domain class representing a word in the speech recognizers output. Each part
 * is associated with an acoustic, a language model and a combined score.
 *
 * @author lschilli
 * @author sjebbara
 * @author lkettenb
 */

@Deprecated
public class UtterancePartSerializer extends XomSerializer<UtterancePart> {

	@Override
    public String getBaseTag() {
        return "UTTERANCEPART";
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void doFillInto(UtterancePart data, Element parent) throws SerializationException {

        Element actionElement = new Element("UtterancePartData");
        actionElement.addAttribute(new Attribute("word", data.getWord()));
        actionElement.addAttribute(new Attribute("id", String.valueOf(data.getId())));
        actionElement
                .addAttribute(new Attribute("begin", String.valueOf(data.getBegin())));
        actionElement.addAttribute(new Attribute("end", String.valueOf(data.getEnd())));
        actionElement.addAttribute(new Attribute("acousticScore", String
                .valueOf(data.getAcousticScore())));
        actionElement.addAttribute(new Attribute("lmScore", String
                .valueOf(data.getLmScore())));
        actionElement.addAttribute(new Attribute("combinedScore", String
                .valueOf(data.getCombinedScore())));
        parent.appendChild(actionElement);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public UtterancePart doFromElement(Element element) {
        UtterancePart type = new UtterancePart();

        Nodes positionNodes = element.query("UtterancePartData");
        Element positionElement = (Element) positionNodes.get(0);

        type.setWord(ElementParser.getAttributeValue(positionElement, "word"));
        type.setId(ElementParser.getIntAttributeValue(positionElement, "id"));
        type.setBegin(ElementParser.getIntAttributeValue(positionElement,
                "begin"));
        type.setEnd(ElementParser.getIntAttributeValue(positionElement, "end"));
        type.setAcousticScore(ElementParser.getDoubleAttributeValue(
                positionElement, "acousticScore"));
        type.setLmScore(ElementParser.getDoubleAttributeValue(positionElement,
                "lmScore"));
        type.setCombinedScore(ElementParser.getDoubleAttributeValue(
                positionElement, "combinedScore"));
        
        return type;
    }

	@Override
	public void doSanitizeElement(Element parent) {
		
	}

	@Override
	public Class<UtterancePart> getDataType() {
		return UtterancePart.class;
	}
}

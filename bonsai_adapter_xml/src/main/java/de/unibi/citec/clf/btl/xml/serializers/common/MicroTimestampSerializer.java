package de.unibi.citec.clf.btl.xml.serializers.common;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.common.MicroTimestamp;

/**
 * A timestamp with microseconds precision.
 * 
 * @author lziegler
 */
public class MicroTimestampSerializer {

	/**
	 * Creates a timestamp with a value of 0 seconds and 0 microseconds.
	 */
	public MicroTimestampSerializer() {
	}

	public String getBaseTag() {
		return "READTIME";
	}

	public void fillInto(MicroTimestamp t, Element parent) throws SerializationException {

		Element secondsElement = new Element("SECONDS");
		parent.appendChild(secondsElement);
		secondsElement.addAttribute(new Attribute("value", String
				.valueOf(t.getSeconds())));

		Element microSecondsElement = new Element("MICROSECONDS");
		parent.appendChild(microSecondsElement);
		microSecondsElement.addAttribute(new Attribute("value", String
				.valueOf(t.getMicroSeconds())));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof MicroTimestampSerializer)) {
			return false;
		}

		MicroTimestampSerializer other = (MicroTimestampSerializer) obj;
		return super.equals(other);
	}

	public MicroTimestamp fromElement(Element element)
			throws ParsingException, DeserializationException {
		
		MicroTimestamp mt = new MicroTimestamp();
		
		Nodes secondsNodes = element.query("SECONDS");
		if (secondsNodes.size() < 1
				|| !(secondsNodes.get(0) instanceof Element)) {
			throw new ParsingException("Cannot find Element with name SECONDS.");
		}
		Element secondsElement = (Element) secondsNodes.get(0);
		mt.setSeconds(ElementParser.getLongAttributeValue(secondsElement,
				"value"));

		Nodes microSecondsNodes = element.query("MICROSECONDS");
		if (microSecondsNodes.size() < 1
				|| !(microSecondsNodes.get(0) instanceof Element)) {
			throw new ParsingException(
					"Cannot find Element with name MICROSECONDS.");
		}
		Element microSecondsElement = (Element) microSecondsNodes.get(0);
		mt.setMicroSeconds(ElementParser.getLongAttributeValue(
				microSecondsElement, "value"));
		return mt;
	}

	public void sanitizeElement(Element parent) {
		
	}

}

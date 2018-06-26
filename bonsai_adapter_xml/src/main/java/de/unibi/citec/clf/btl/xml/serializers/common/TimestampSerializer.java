package de.unibi.citec.clf.btl.xml.serializers.common;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.units.TimeUnit;

/**
 * Domain class representing a timestamp with several parsing methods.
 * 
 * @author jwienke
 * @author unknown
 */
public class TimestampSerializer {

	private static Logger logger = Logger.getLogger(TimestampSerializer.class);
	private boolean legacyParsing = false;
	
	public TimestampSerializer() {
	}
	
	public TimestampSerializer(boolean legacyParsing) {
		this.legacyParsing= legacyParsing; 
	}

	public String getBaseTag() {
		return "TIMESTAMP";
	}

	public Timestamp fromElement(Element doc) {

		Long created = null;
		Long updated = null;

		Node node;
		for (int i = 0; i < doc.getChildCount(); i++) {
			node = doc.getChild(i);
			if (node instanceof Element) {
				Element elem = (Element) node;
				if (elem.getLocalName().equals("INSERTED")
						|| (isLegacyParsing() && elem.getLocalName().equals(
								"CREATED"))) {
					created = ElementParser
							.getLongAttributeValue(elem, "value");
				}
				if (elem.getLocalName().equals("UPDATED")) {
					updated = ElementParser
							.getLongAttributeValue(elem, "value");
				}
			}
		}

		// enable recovery strategies if desired
		if (isLegacyParsing()) {

			// first error case: only of both was found
			if ((created == null) && (updated != null)) {
				logger.warn("Timestamp contains only updated value. "
						+ "Guessing created from this value:\n" + doc.toXML());
				created = new Long(updated.longValue());
			} else if ((updated == null) && (created != null)) {
				logger.warn("Timestamp contains only created value. "
						+ "Guessing updated from this value:\n" + doc.toXML());
				updated = new Long(created.longValue());
			}

			// second error case: non of both was set, try direct text values as
			// timestamp
			if ((created == null) && (updated == null)) {
				try {
					Long value = Long.valueOf(doc.getValue());
					created = new Long(value.longValue());
					updated = new Long(value.longValue());
				} catch (NumberFormatException e) {
					logger.warn("Unable to interpret text contents "
							+ "of the timestamp as long value:\n" + doc.toXML());
				}
			}

		}

		if ((created != null) && (updated != null)) {

			Timestamp t = new Timestamp(created, updated, TimeUnit.MILLISECONDS);
			return t;
		} else {

			throw new IllegalArgumentException(
					"Unable to parse timestamp from:\n" + doc.toXML());
		}
	}

	public void fillInto(Timestamp t, Element parent) throws SerializationException {
		Element created = new Element("INSERTED");
		created.addAttribute(new Attribute("value", Long.toString(t.getCreated()
				.getTime())));
		parent.appendChild(created);

		Element updated = new Element("UPDATED");
		updated.addAttribute(new Attribute("value", Long.toString(t.getUpdated()
				.getTime())));
		parent.appendChild(updated);
	}

	public boolean isLegacyParsing() {
		return legacyParsing;
	}

}

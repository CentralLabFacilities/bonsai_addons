package de.unibi.citec.clf.bonsai.rsb.slots;


import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Logger;

import rsb.transport.XOMXOP;
import de.unibi.citec.clf.bonsai.core.exception.CommunicationException;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.rsb.RsbamWorkingMemory;
import de.unibi.citec.clf.bonsai.util.XPathUtilities;
import de.unibi.citec.clf.rsbam.model.MemoryRecord;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.ParsingException;

public class RsbStringSlot implements RsbWorkingMemorySlot<String> {

    private Logger logger = Logger.getLogger(getClass());
    private RsbamWorkingMemory memory;
    private String slot;
    String baseTag = "STRING";

    public RsbStringSlot() {
    }

    @Override
    public void initialize(RsbamWorkingMemory memory, String slot,
            Class<? extends String> dataType) {
        this.memory = memory;
        this.slot = slot;
    }

    @Override
    public void memorize(String object) throws CommunicationException {
        logger.trace("Memorizing");
        XOMXOP xopData;
        Document rawDoc = new Document(new Element(baseTag));
        rawDoc.getRootElement().appendChild(object);
        xopData = new XOMXOP(XPathUtilities.addParentXPath(rawDoc, slot));
        logger.trace("XOP: " + xopData.getDocumentAsText());
        memory.memorize(slot, baseTag, xopData);
    }

    @Override
    public void forget() throws CommunicationException {
        memory.forget(slot, baseTag);
    }

    @Override
    public String recall() throws CommunicationException {

        logger.trace("Recalling");

        String xml = null;

        Set<MemoryRecord> data = memory.recall(slot);

        for (MemoryRecord rec : data) {
            logger.fatal("records:" + data.size() + "\nmemrecord0:" + rec);
            xml = rec.getDataAsXml();
            break;
        }

        if (xml != null) {
            logger.trace("XOP: " + xml);
            Builder parser = new Builder();
            Document doc = null;
            try {
                doc = parser.build(new ByteArrayInputStream(xml.getBytes()));
            } catch (ParsingException | IOException ex) {
                logger.fatal("PARSE FATAL" + ex.getMessage(), ex);
                throw new CommunicationException("whatever error: " + data);
            }

            if (doc == null) {
                throw new CommunicationException("Document empty in XOP: " + data);
            }

            doc = XPathUtilities.removeParentXPath(doc, slot);
            if (doc.getRootElement().getLocalName().equals(baseTag)) {
                return doc.getRootElement().getValue();
            } else {
                return doc.toXML();
            }

        } else {
            logger.debug("XOP: null");
            return null;
        }
    }

    @Override
    public Class<String> getDataType() {
        return String.class;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[slot-xpath:'" + slot + "']";
    }
    
    @Override
    public void cleanUp() {
    //todo
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
    }
}

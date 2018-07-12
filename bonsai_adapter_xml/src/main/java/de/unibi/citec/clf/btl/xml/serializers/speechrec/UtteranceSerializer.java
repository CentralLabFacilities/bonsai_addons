package de.unibi.citec.clf.btl.xml.serializers.speechrec;



import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.xml.UnexpectedElementFormatException;
import org.apache.log4j.Logger;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.data.speechrec.GrammarSymbol;
import de.unibi.citec.clf.btl.data.speechrec.GrammarTree;
import de.unibi.citec.clf.btl.data.speechrec.Utterance;
import de.unibi.citec.clf.btl.data.speechrec.UtterancePart;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * Domain class representing an utterance. An utterance consisting of a word
 * sequence and a grammar tree. The grammar tree is null if the speech
 * recognizer does not configured to provide one. Currently the grammar tree is
 * unfortunately always null if the input is simulated.
 * 
 * @author lschilli
 * @author lkettenb
 */
public class UtteranceSerializer extends XomSerializer<Utterance> {

    private static final String ATTR_STABLE = "stable";
    private static final String TAG_SCORE = "score";
    private static final String ATTR_LM = "lm";
    private static final String ATTR_COMBINED = "combined";
    private static final String ATTR_ACOUSTIC = "acoustic";
    private static final String ATTR_END = "end";
    private static final String ATTR_BEGIN = "begin";
    private static final String ATRR_ID = "id";
    private static final String TAG_PART = "part";
    private static final String XPATH_SPEECH_HYP_SEQ = "/speech_hyp/seq/*";
    private static final String ATTR_FAULT = "fault";
    private static final String ATTR_SKIP = "skip";
    private static final String ATTR_CANCEL = "cancel";
    private static final String ATTR_REFID = "refid";
    private static final String TAG_GRAMMARTREE = "grammartree";
    private static final String ATTRIB_NAME = "name";
    private static final String TAG_NONTERMINAL = "nonterminal";
    private static final String TAG_TERMINAL = "terminal";

    public UtteranceSerializer() {
        setLegacyParsing(true);
    }

    @Override
    public String getBaseTag() {
        return "speech_hyp";
    }

    public Utterance doFromElement(Element element) {
        Utterance type = new Utterance();

        type.setStable(ElementParser.getBooleanAttributeValueUnchecked(element, ATTR_STABLE));
        parseSeq(element, type);
        type.setGrammarTree(parseGrammarTree(element, type));

        Elements nodes = element.getChildElements("TIMESTAMP");
        if (nodes.size() > 0) {
            Element tElem = nodes.get(0);
            try {
                long t = Long.parseLong(tElem.getValue());
                type.setTimestamp(t, TimeUnit.MILLISECONDS);
            } catch (NumberFormatException e) {
                Logger.getLogger(getClass()).warn("Can not parse timestamp");
            }
        }

        nodes = element.getChildElements("UTT_BEGIN");
        if (nodes.size() > 0) {
            Element tElem = nodes.get(0);
            try {
                long t = Long.parseLong(tElem.getValue());
                type.setBegin(t, TimeUnit.MILLISECONDS);
            } catch (NumberFormatException e) {
                Logger.getLogger(getClass()).warn("Can not parse utterance begin");
            }
        }

        nodes = element.getChildElements("UTT_END");
        if (nodes.size() > 0) {
            Element tElem = nodes.get(0);
            try {
                long t = Long.parseLong(tElem.getValue());
                type.setEnd(t, TimeUnit.MILLISECONDS);
            } catch (NumberFormatException e) {
                Logger.getLogger(getClass()).warn("Can not parse utterance begin");
            }
        }


        nodes = element.getChildElements("VALID");
        if (nodes.size() > 0) {
            Element tElem = nodes.get(0);
            Boolean b = "1".equals(tElem.getValue());
            type.setValid(b);
        }

        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(Utterance data, Element parent) throws SerializationException {

        // TODO bad hack
        throw new RuntimeException("Operation not supported atm.");

    }

    private static void handleSymbolSequence(Element elem, Utterance utt, GrammarNonTerminal supterm,
            GrammarSymbol parent) {
        Elements elems = elem.getChildElements();
        for (int i = 0; i < elems.size(); i++) {
            Element e = elems.get(i);
            if (TAG_TERMINAL.equals(e.getLocalName())) {
                UtterancePart uttp = new UtterancePart();
                    uttp.setWord(ElementParser.getTagValue(e));
                    uttp.setId(ElementParser.getIntAttributeValue(e, ATTR_REFID));
                    uttp.setBegin(0);
                    uttp.setEnd(1);
                //TODO generate from seq: UtterancePart uttp = utt.getUtterancePartById(ElementParser.getIntAttributeValue(e, ATTR_REFID));
                if (uttp == null) {
                    throw new RuntimeException("Cannot resolve word id in grammartree");
                }
                uttp.setParent(parent);
                supterm.addSymbol(uttp);
            } else {
                GrammarNonTerminal nonterm = new GrammarNonTerminal();
                nonterm.setName(e.getLocalName());
                nonterm.setParent(parent);
                handleSymbolSequence(e, utt, nonterm, nonterm);
                supterm.addSymbol(nonterm);
            }
        }
        

    }

    private static GrammarTree parseGrammarTree(Element root, Utterance utt) {
        
        GrammarTree tree = new GrammarTree();
        Element grmelem = root.getFirstChildElement(TAG_GRAMMARTREE);
        if (grmelem != null) {
            // handles empty case (silence) (hotfix)
            //if (grmelem.getAttribute(ATTR_CANCEL) == null) {
            //    return null;
            //}
            //tree.setCancel(ElementParser.getBooleanAttributeValue(grmelem, ATTR_CANCEL));
            //tree.setSkip(ElementParser.getBooleanAttributeValue(grmelem, ATTR_SKIP));
            //tree.setFault(ElementParser.getBooleanAttributeValue(grmelem, ATTR_FAULT));
            handleSymbolSequence(grmelem, utt, tree, null);
            return tree;
        } else {
            return null;
        }

    }

    private static void parseSeq(Element root, Utterance utt) {

        Nodes nodes = root.query(XPATH_SPEECH_HYP_SEQ);
        UtterancePart uttp = null;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                if (TAG_PART.equals(elem.getLocalName())) {
                    try {
                        ElementParser.getTagValue(elem);
                    } catch (UnexpectedElementFormatException e) {
                        continue;
                    }
                    
                    uttp = new UtterancePart();
                    uttp.setWord(ElementParser.getTagValue(elem));
                    uttp.setId(ElementParser.getIntAttributeValue(elem, ATRR_ID));
                    uttp.setBegin(0);
                    uttp.setEnd(1);
                    //uttp.setBegin(ElementParser.getIntAttributeValue(elem, ATTR_BEGIN));
                    //uttp.setEnd(ElementParser.getIntAttributeValue(elem, ATTR_END));
                } else if (TAG_SCORE.equals(elem.getLocalName())) {
                    uttp.setAcousticScore(ElementParser.getDoubleAttributeValue(elem, ATTR_ACOUSTIC));
                    uttp.setCombinedScore(ElementParser.getDoubleAttributeValue(elem, ATTR_COMBINED));
                    uttp.setLmScore(ElementParser.getDoubleAttributeValue(elem, ATTR_LM));
                    utt.addUtterancePart(uttp);
                }

            }
        }

    }

    @Override
    public void doSanitizeElement(Element parent) {

    }

    @Override
    public Class<Utterance> getDataType() {
        return Utterance.class;
    }
}

package de.unibi.citec.clf.btl.xml.serializers.speechrec;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.data.speechrec.GrammarSymbol;
import de.unibi.citec.clf.btl.data.speechrec.UtterancePart;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;

/**
 * Domain class representing a non terminal symbol within the grammartree of an
 * utterance
 *
 * @author lschilli
 * @author sjebbara
 * @author lkettenb
 */
public class GrammarNonTerminalSerializer extends XomSerializer<GrammarNonTerminal> {

	private UtterancePartSerializer uttSerializer = new UtterancePartSerializer();
	
    public GrammarNonTerminalSerializer() {
    }

    @Override
    public String getBaseTag() {
        return "GRAMMARNONTERMINAL";
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void doFillInto(GrammarNonTerminal data, Element parent) throws SerializationException {
        
        Element ntElement = new Element("GrammarNonTerminalData");
        if (data.getName() == null) {
            ntElement.addAttribute(new Attribute("name", ""));
        } else {
            ntElement.addAttribute(new Attribute("name", data.getName()));
        }
        for (GrammarSymbol s : data.getSubsymbols()) {
            if (s instanceof GrammarNonTerminal) {
                GrammarNonTerminal nt = (GrammarNonTerminal) s;
                Element subNode = new Element(getBaseTag());
                fillInto(nt, subNode);
                ntElement.appendChild(subNode);
            } else if (s instanceof UtterancePart) {
                UtterancePart u = (UtterancePart) s;
                Element subNode = new Element(uttSerializer.getBaseTag());
                uttSerializer.fillInto(u, subNode);
                ntElement.appendChild(subNode);
            }
        }
        parent.appendChild(ntElement);
    }

    /**
     * @throws DeserializationException 
     * @throws ParsingException 
     * @{inheritDoc
     */
    @Override
    public GrammarNonTerminal doFromElement(Element element) throws ParsingException, DeserializationException {

    	GrammarNonTerminal nt = new GrammarNonTerminal();
        Nodes nodes = element.query("GrammarNonTerminalData");
        Node ntNode = nodes.get(0);
        
        if (ntNode instanceof Element) {
            Element grammarSymbolElem = (Element) ntNode;
            String name = ElementParser.getAttributeValue(grammarSymbolElem,
                    "name");
            nt.setName(name);
            Elements subElements = grammarSymbolElem.getChildElements();
            for (int j = 0; j < subElements.size(); j++) {
                if (subElements.get(j).getLocalName()
                        .equals(uttSerializer.getBaseTag())) {
                    UtterancePart u = uttSerializer.fromElement(subElements.get(j));
                    nt.addSymbol(u);
                } else if (subElements.get(j).getLocalName()
                        .equals(getBaseTag())) {
                    GrammarNonTerminal childNT = fromElement(subElements.get(j));
                    nt.addSymbol(childNT);
                }
            }
        }
        return nt;
    }

	@Override
	public void doSanitizeElement(Element parent) {
	}

	@Override
	public Class<GrammarNonTerminal> getDataType() {
		return GrammarNonTerminal.class;
	}
}


package de.unibi.citec.clf.btl.xml.serializers.command;



import de.unibi.citec.clf.btl.data.command.VerbPhraseData;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 *
 * @author hneumann
 */
public class VerbPhraseDataSerializer extends XomSerializer<VerbPhraseData> {

    @Override
    public String getBaseTag() {
        return "VERBPHRASEDATA";
    }

    @Override
    public VerbPhraseData doFromElement(Element element) throws ParsingException, DeserializationException {
        VerbPhraseData data = new VerbPhraseData();
        Nodes nodes = element.query("*");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                switch (elem.getLocalName()) {
                    case "VERBPHRASE":
                        data.setVerbPhraseType(ElementParser.getAttributeValue(elem, "phrasetype"));
                        data.setVerbType(ElementParser.getAttributeValue(elem, "verbtype"));
                        data.setVerb(ElementParser.getAttributeValue(elem, "verb"));
                        break;
                    case "NOMINALPHRASES":
                        List<String> nominalPhrasesType = new ArrayList<>();
                        List<String> nominalPhrases = new ArrayList<>();
                        List<String> prepositions = new ArrayList<>();

                        Elements elementsList = elem.getChildElements();
                        for (int j = 0; j < elementsList.size(); j++) {
                            nominalPhrasesType.add(elementsList.get(j).getAttributeValue("phrasetype"));
                            nominalPhrases.add(elementsList.get(j).getAttributeValue("nominal"));
                            prepositions.add(elementsList.get(j).getAttributeValue("preposition"));
                        }
                        data.setNominalPhrasesType(nominalPhrasesType);
                        data.setNominalPhrases(nominalPhrases);
                        data.setPrepositions(prepositions);
                        break;
                }

            }
        }

        return data;
    }

    @Override
    public void doFillInto(VerbPhraseData data, Element parent) throws SerializationException {
        Element verbPhraseElement = new Element("VERBPHRASE");
        parent.appendChild(verbPhraseElement);
        verbPhraseElement.addAttribute(new Attribute("phrasetype", data.getVerbPhraseType()));
        verbPhraseElement.addAttribute(new Attribute("verbtype", data.getVerbType()));
        verbPhraseElement.addAttribute(new Attribute("verb", data.getVerb()));
        
        Element nominalPhrasesElement = new Element("NOMINALPHRASES");
        parent.appendChild(nominalPhrasesElement);
        
        for(int i = 0;i < data.getNominalPhrasesType().size(); i++){
            Element nominalPhrasesChildren = new Element("NOMINALPHRASE");
            nominalPhrasesChildren.addAttribute(new Attribute("phrasetype", data.getNominalPhrasesType().get(i)));
            nominalPhrasesChildren.addAttribute(new Attribute("nominal", data.getNominalPhrases().get(i)));
            nominalPhrasesChildren.addAttribute(new Attribute("preposition", data.getPrepositions().get(i)));
            nominalPhrasesElement.appendChild(nominalPhrasesChildren);
        }
        
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<VerbPhraseData> getDataType() {
        return VerbPhraseData.class;
    }

}

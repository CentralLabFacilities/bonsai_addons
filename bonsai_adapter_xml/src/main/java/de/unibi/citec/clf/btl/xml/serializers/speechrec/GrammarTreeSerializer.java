
package de.unibi.citec.clf.btl.xml.serializers.speechrec;



import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.data.speechrec.GrammarTree;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 *
 * @author gminareci
 */
public class GrammarTreeSerializer extends XomSerializer<GrammarTree>{
    
    GrammarNonTerminalSerializer gnts = new GrammarNonTerminalSerializer();

    @Override
    public GrammarTree doFromElement(Element element) throws ParsingException, DeserializationException {
        GrammarNonTerminal gnt = gnts.doFromElement(element);
        GrammarTree ret = new GrammarTree();
        ret.setName(gnt.getName());
        ret.setSubSymbols(gnt.getSubsymbols());
        return ret;
    }

    @Override
    public void doSanitizeElement(Element parent) {
        gnts.doSanitizeElement(parent);
    }

    @Override
    public void doFillInto(GrammarTree data, Element parent) throws SerializationException {
       gnts.doFillInto(data, parent);
    }

    @Override
    public Class<GrammarTree> getDataType() {
        return GrammarTree.class;
    }

    @Override
    public String getBaseTag() {
        return "GRAMMARTREE";
    }
    
}

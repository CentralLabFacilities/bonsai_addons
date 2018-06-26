package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.KBase;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class KBaseSerializer extends XomSerializer<KBase> {
    
    ArenaSerializer as = new ArenaSerializer();
    CrowdSerializer cs = new CrowdSerializer();
    RCObjectsSerializer os = new RCObjectsSerializer();
    ContextSerializer ps = new ContextSerializer();

    @Override
    public KBase doFromElement(Element element) throws ParsingException, DeserializationException {
        KBase base = new KBase();
        
        Elements subElements = element.getChildElements();
        for(int i = 0; i < subElements.size(); i++){
            if(subElements.get(i).getLocalName().equals(as.getBaseTag())){
                base.setArena(as.doFromElement(subElements.get(i)));
            }
            else if (subElements.get(i).getLocalName().equals(cs.getBaseTag())){
                base.setCrowd(cs.doFromElement(subElements.get(i)));
            }
            else if (subElements.get(i).getLocalName().equals(os.getBaseTag())){
                base.setRCObjects(os.doFromElement(subElements.get(i)));
            }
            else if (subElements.get(i).getLocalName().equals(ps.getBaseTag())){
                base.setContext(ps.doFromElement(subElements.get(i)));
            }
        }
        return base;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(KBase data, Element parent) throws SerializationException {        
        Element subNodeA= new Element(as.getBaseTag());
        as.fillInto(data.getArena(), subNodeA);
        parent.appendChild(subNodeA);
        
        Element subNodeC= new Element(cs.getBaseTag());
        cs.fillInto(data.getCrowd(), subNodeC);
        parent.appendChild(subNodeC);
        
        Element subNodeO= new Element(os.getBaseTag());
        os.fillInto(data.getRCObjects(), subNodeO);
        parent.appendChild(subNodeO);
        
        Element subNodeP= new Element(ps.getBaseTag());
        ps.fillInto(data.getContext(), subNodeP);
        parent.appendChild(subNodeP);
    }

    @Override
    public Class<KBase> getDataType() {
        return KBase.class;
    }

    @Override
    public String getBaseTag() {
        return "KBASE";
    }
    
}

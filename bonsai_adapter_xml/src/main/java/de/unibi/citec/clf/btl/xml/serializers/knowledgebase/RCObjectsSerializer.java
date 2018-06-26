package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.RCObject;
import de.unibi.citec.clf.btl.data.knowledgebase.RCObjects;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import java.util.LinkedList;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class RCObjectsSerializer extends XomSerializer<RCObjects> {
    
    RCObjectSerializer rcos = new RCObjectSerializer();

    @Override
    public RCObjects doFromElement(Element element) throws ParsingException, DeserializationException {
        RCObjects o = new RCObjects();
        LinkedList<RCObject> objects = new LinkedList();
        
        Elements subElements = element.getChildElements();
        for(int i = 0; i < subElements.size(); i++){
            if(rcos.getBaseTag().equals(subElements.get(i).getLocalName())){
                RCObject rcobj = rcos.doFromElement(subElements.get(i));
                objects.add(rcobj);
            }
        }
        o.setRCObjects(objects);
        return o;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(RCObjects data, Element parent) throws SerializationException {
        for(RCObject obj : data.getRCObjects()){
            Element subNode = new Element(rcos.getBaseTag());
            rcos.fillInto(obj, subNode);
            parent.appendChild(subNode);
        }
    }

    @Override
    public Class<RCObjects> getDataType() {
        return RCObjects.class;
    }

    @Override
    public String getBaseTag() {
        return "RCOBJECTS";
    }

    
}

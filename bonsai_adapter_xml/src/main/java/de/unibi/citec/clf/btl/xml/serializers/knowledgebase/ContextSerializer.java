package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.Context;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class ContextSerializer extends XomSerializer<Context> {

    
    RoomSerializer rs = new RoomSerializer();
    LocationSerializer ls = new LocationSerializer();
    RCObjectSerializer rcos = new RCObjectSerializer();

    @Override
    public Context doFromElement(Element element) throws ParsingException, DeserializationException {

        Context pd = new Context();
        /*
        pd.setLastQuestion(ElementParser.getAttributeValue(element, "lastquestion"));
        
        LinkedList<BDO> content = new LinkedList();
        
        Elements subElements = element.getChildElements();
        for(int i = 0; i < subElements.size(); i++){
            
            if(subElements.get(i).getLocalName().equals(ls.getBaseTag())){
                Location loc = ls.doFromElement(subElements.get(i));
                content.add(loc);
            }
            else if (subElements.get(i).getLocalName().equals(rs.getBaseTag())){
                Room rm = rs.doFromElement(subElements.get(i));
                content.add(rm);
            }
            if(subElements.get(i).getLocalName().equals(ps.getBaseTag())){
                Person pers = ps.doFromElement(subElements.get(i));
                content.add(pers);
            }
            else if (subElements.get(i).getLocalName().equals(rcos.getBaseTag())){
                RCObject rcobj = rcos.doFromElement(subElements.get(i));
                content.add(rcobj);
            }
        }
        pd.setLastquestionSubject(content);
        */
        return pd;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(Context data, Element parent) throws SerializationException {
        /*
        if (data.getLastQuestion() == null) {
            parent.addAttribute(new Attribute("lastquestion", ""));
        } else {
            parent.addAttribute(new Attribute("lastquestion", data.getLastQuestion()));
        }
            
        for(BDO content : data.getLastquestionSubject()){
            if(content instanceof Location){
                Location loc = (Location) content;
                Element subNode = new Element(ls.getBaseTag());
                ls.fillInto(loc, subNode);
                parent.appendChild(subNode);
            } else if(content instanceof Room){
                Room rm = (Room) content;
                Element subNode = new Element(rs.getBaseTag());
                rs.fillInto(rm, subNode);
                parent.appendChild(subNode);
            } else if(content instanceof Person){
                Person pers = (Person) content;
                Element subNode = new Element(ps.getBaseTag());
                ps.fillInto(pers, subNode);
                parent.appendChild(subNode);
            } else if(content instanceof RCObject){
                RCObject rcobj = (RCObject) content;
                Element subNode = new Element(rcos.getBaseTag());
                rcos.fillInto(rcobj, subNode);
                parent.appendChild(subNode);
            } 
        }*/
    }

    @Override
    public Class<Context> getDataType() {
        return Context.class;
    }

    @Override
    public String getBaseTag() {
        return "CONTEXT";
    }


}

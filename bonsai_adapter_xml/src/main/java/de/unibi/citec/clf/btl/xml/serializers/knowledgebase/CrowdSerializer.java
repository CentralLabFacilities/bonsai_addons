package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.knowledgebase.Crowd;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.xml.serializers.person.PersonDataSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class CrowdSerializer extends XomSerializer<Crowd> {
    
    PersonDataSerializer ps = new PersonDataSerializer();

    @Override
    public Crowd doFromElement(Element element) throws ParsingException, DeserializationException {
        Crowd c = new Crowd();
        List<PersonData> crowd = new List<>(PersonData.class);
        
        Elements subElements = element.getChildElements();
        for(int i = 0; i < subElements.size(); i++){
            if(ps.getBaseTag().equals(subElements.get(i).getLocalName())){
                PersonData p = ps.doFromElement(subElements.get(i));
                crowd.add(p);
            }
        }
        c.setPersons(crowd);
        return c;
    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public void doFillInto(Crowd data, Element parent) throws SerializationException {
        for(PersonData pers : data.getPersons()){
            Element subNode = new Element(ps.getBaseTag());
            ps.fillInto(pers, subNode);
            parent.appendChild(subNode);
        }
    }

    @Override
    public Class<Crowd> getDataType() {
        return Crowd.class;
    }

    @Override
    public String getBaseTag() {
        return "CROWD";
    }

    
}

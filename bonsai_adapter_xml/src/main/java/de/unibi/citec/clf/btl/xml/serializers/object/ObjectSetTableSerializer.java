
package de.unibi.citec.clf.btl.xml.serializers.object;




import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.object.ObjectOrder;
import de.unibi.citec.clf.btl.data.object.ObjectSetTable;
import de.unibi.citec.clf.btl.xml.serializers.navigation.PositionDataSerializer;
import de.unibi.citec.clf.btl.xml.tools.ElementParser;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.apache.log4j.Logger;

/**
 *
 * @author semueller
 */
public class ObjectSetTableSerializer extends XomSerializer<ObjectSetTable> {

    private static Logger logger = Logger.getLogger(ObjectOrder.class);
    
    private static String ATTR_OBJECT_NAME = "OBJECTNAME";
    private static String ATTR_GRASPDIFFICULTY = "GRASPDIFFICULTY";
    private static String ATTR_BOOL_MOVED = "ISMOVED";
    private static String ATTR_ORIGINAL_LOCATION = "ORIGINALLOCATION";
    private static String ATTR_CURRENT_LOCATION = "CURRENTLOCATION";
    
    //CURRENT AND ORIGINAL LOCATION
    PositionDataSerializer posSerializer = new PositionDataSerializer();

    @Override
    public void doFillInto(ObjectSetTable type, Element parent) throws SerializationException {

        parent.addAttribute(new Attribute(ATTR_OBJECT_NAME, type.getName() ));
        parent.addAttribute(new Attribute(ATTR_GRASPDIFFICULTY, String.valueOf(type.getGraspDifficulty())));
        parent.addAttribute(new Attribute(ATTR_BOOL_MOVED, String.valueOf(type.isMoved())));
        
        Element posOrg = new Element(ATTR_ORIGINAL_LOCATION);
        Element posCurr = new Element(ATTR_CURRENT_LOCATION);
        posSerializer.fillInto(type.getOriginalLocation(), posOrg);
        posSerializer.fillInto(type.getCurrentLocation(), posCurr);
        parent.appendChild(posOrg);//appending even if location == null could make toruble?
        parent.appendChild(posCurr);
    }
    
    @Override
    public ObjectSetTable doFromElement(Element element) throws ParsingException, DeserializationException {
        ObjectSetTable type = new ObjectSetTable();
        
        logger.debug("fromElement called");
        
        type.setName(ElementParser.getAttributeValue(element, ATTR_OBJECT_NAME));
        type.setGraspDifficulty(ElementParser.getIntAttributeValue(element, ATTR_GRASPDIFFICULTY));
        type.setMoved(ElementParser.getBooleanAttributeValue(element, ATTR_BOOL_MOVED));
        
        try{
            Element posOrig = ElementParser.getFirstChildElement(element, ATTR_ORIGINAL_LOCATION );
            type.setOriginalLocation(posSerializer.fromElement(posOrig));
            Element posCurr = ElementParser.getFirstChildElement(element, ATTR_CURRENT_LOCATION);
            type.setCurrentLocation(posSerializer.fromElement(posCurr));
                    
        } catch (IllegalArgumentException e) {
            logger.warn("tag for original/ current location missing in data!");
        }
        
        return type;
    }



    @Override
    public Class<ObjectSetTable> getDataType() {
        return ObjectSetTable.class;
    }

    @Override
    public String getBaseTag() {
        return "OBJECTNAME";
    }
    
    @Override
    public void doSanitizeElement(Element parent) {
    }


}

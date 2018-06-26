package de.unibi.citec.clf.btl.xml.serializers.navigation;



import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * Data class for plans from the global path planner component.
 * 
 * @author jwienke
 */
public class GlobalPlanSerializer extends XomSerializer<GlobalPlan> {

    private NavigationGoalDataSerializer navSerializer = new NavigationGoalDataSerializer();

    private static Logger logger = Logger.getLogger(GlobalPlanSerializer.class);

    @Override
    public String getBaseTag() {
        return "GLOBALPLAN";
    }

    @Override
    public GlobalPlan doFromElement(Element element) throws ParsingException, DeserializationException {
        GlobalPlan type = new GlobalPlan();
        logger.debug("Parsing plan:\n" + element.toXML());

        Nodes nodes = element.query(navSerializer.getBaseTag());
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                NavigationGoalData wp = navSerializer.fromElement(elem);
                type.add(wp);
            }
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFillInto(GlobalPlan type, Element parent) throws SerializationException {

        for (NavigationGoalData ngd : type) {
            Element ngdElement = new Element(navSerializer.getBaseTag());
            parent.appendChild(ngdElement);
            navSerializer.fillInto(ngd, ngdElement);
        }

    }

    @Override
    public void doSanitizeElement(Element parent) {
    }

    @Override
    public Class<GlobalPlan> getDataType() {
        return GlobalPlan.class;
    }

}

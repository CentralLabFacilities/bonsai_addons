package de.unibi.citec.clf.btl.xml.serializers.command;


import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.data.command.CommandData;
import de.unibi.citec.clf.btl.data.speechrec.GrammarNonTerminal;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.serializers.speechrec.GrammarNonTerminalSerializer;

/**
 *
 * @author gminareci, ikillman
 */
public class CommandDataSerializer extends XomSerializer<CommandData> {

    private GrammarNonTerminalSerializer treeSerializer = new GrammarNonTerminalSerializer();

    @Override
    public String getBaseTag() {
        return "COMMANDDATA";
    }

    @Override
    public CommandData doFromElement(Element element)
            throws ParsingException, DeserializationException {

    	CommandData commandData = new CommandData();

        String act = element.getFirstChildElement("ACTION").getValue();
        String loc = element.getFirstChildElement("LOCATION").getValue();
        String loc2 = element.getFirstChildElement("SECONDLOCATION").getValue();
        String obj = element.getFirstChildElement("OBJECT").getValue();
        String per = element.getFirstChildElement("PERSON").getValue();
        String prep = element.getFirstChildElement("PREPOSITION").getValue();
        String room = element.getFirstChildElement("ROOM").getValue();

        Element treeElement = element.getFirstChildElement("TREE");
        Element grammarElement = treeElement
                .getFirstChildElement(treeSerializer.getBaseTag());
        GrammarNonTerminal tr = treeSerializer.fromElement(grammarElement);

        commandData.setAction(act);
        commandData.setLocation(loc);
        commandData.setSecondLocation(loc2);
        commandData.setObject(obj);
        commandData.setPerson(per);
        commandData.setRoom(room);
        commandData.setPreposition(prep);
        commandData.setTree(tr);

        commandData.setActionset(Boolean.parseBoolean(
                element.getFirstChildElement("ACTIONSET").getValue()));
        commandData.setObjectset(Boolean.parseBoolean(
                element.getFirstChildElement("OBJECTSET").getValue()));
        commandData.setLocationset(Boolean.parseBoolean(
                element.getFirstChildElement("LOCATIONSET").getValue()));
        commandData.setSecondLocationset(Boolean.parseBoolean(
                element.getFirstChildElement("SECONDLOCATIONSET").getValue()));
        commandData.setPersonset(Boolean.parseBoolean(
                element.getFirstChildElement("PERSONSET").getValue()));
        commandData.setRoomset(Boolean.parseBoolean(
                element.getFirstChildElement("ROOMSET").getValue()));
        commandData.setPrepositionset(Boolean.parseBoolean(
                element.getFirstChildElement("PREPOSITIONSET").getValue()));
        
        return commandData;
    }

    @Override
    public void doFillInto(CommandData data, Element parent) throws SerializationException {

        Elements acts = parent.getChildElements("ACTION");
        for (int i = 0; i < acts.size(); i++) {
            parent.removeChild(acts.get(i));
        }
        Elements locs = parent.getChildElements("LOCATION");
        for (int i = 0; i < locs.size(); i++) {
            parent.removeChild(locs.get(i));
        }
        Elements locs2 = parent.getChildElements("SECONDLOCATION");
        for (int i = 0; i < locs2.size(); i++) {
            parent.removeChild(locs2.get(i));
        }
        Elements objs = parent.getChildElements("OBJECT");
        for (int i = 0; i < objs.size(); i++) {
            parent.removeChild(objs.get(i));
        }
        Elements trs = parent.getChildElements("TREE");
        for (int i = 0; i < trs.size(); i++) {
            parent.removeChild(trs.get(i));
        }
        Elements pers = parent.getChildElements("PERSON");
        for (int i = 0; i < pers.size(); i++) {
            parent.removeChild(pers.get(i));
        }
        Elements rooms = parent.getChildElements("ROOM");
        for (int i = 0; i < rooms.size(); i++) {
            parent.removeChild(rooms.get(i));
        }

        Element setobj = new Element("OBJECTSET");
        parent.appendChild(setobj);
        setobj.appendChild(data.isObjectset() ? "TRUE" : "FALSE");

        Element setact = new Element("ACTIONSET");
        parent.appendChild(setact);
        setact.appendChild(data.isActionset() ? "TRUE" : "FALSE");

        Element setloc = new Element("LOCATIONSET");
        parent.appendChild(setloc);
        setloc.appendChild(data.isLocationset() ? "TRUE" : "FALSE");

        Element setper = new Element("PERSONSET");
        parent.appendChild(setper);
        setper.appendChild(data.isPersonset() ? "TRUE" : "FALSE");

        Element setroom = new Element("ROOMSET");
        parent.appendChild(setroom);
        setroom.appendChild(data.isRoomset() ? "TRUE" : "FALSE");
        
        Element setprep = new Element("PREPOSITIONSET");
        parent.appendChild(setprep);
        setprep.appendChild(data.isPrepositionset()? "TRUE" : "FALSE");
        
        Element setloc2 = new Element("SECONDLOCATIONSET");
        parent.appendChild(setloc2);
        setloc2.appendChild(data.isSecondLocationset()? "TRUE" : "FALSE");

        if (parent.getChildElements("ACTION").size() == 0) {
            Element e = new Element("ACTION");
            parent.appendChild(e);
        }

        if (parent.getChildElements("LOCATION").size() == 0) {
            Element e = new Element("LOCATION");
            parent.appendChild(e);
        }
        
        if (parent.getChildElements("SECONDLOCATION").size() == 0) {
            Element e = new Element("SECONDLOCATION");
            parent.appendChild(e);
        }

        if (parent.getChildElements("OBJECT").size() == 0) {
            Element e = new Element("OBJECT");
            parent.appendChild(e);
        }

        if (parent.getChildElements("PERSON").size() == 0) {
            Element e = new Element("PERSON");
            parent.appendChild(e);
        }

        if (parent.getChildElements("ROOM").size() == 0) {
            Element e = new Element("ROOM");
            parent.appendChild(e);
        }
        if (parent.getChildElements("PREPOSITION").size() == 0) {
            Element e = new Element("PREPOSITION");
            parent.appendChild(e);
        }
        
        parent.getFirstChildElement("ACTION").appendChild(data.getAction());
        parent.getFirstChildElement("LOCATION").appendChild(data.getLocation());
        parent.getFirstChildElement("SECONDLOCATION").appendChild(data.getSecondLocation());
        parent.getFirstChildElement("OBJECT").appendChild(data.getObject());
        parent.getFirstChildElement("PERSON").appendChild(data.getPerson());
        parent.getFirstChildElement("ROOM").appendChild(data.getRoom());
        parent.getFirstChildElement("PREPOSITION").appendChild(data.getPreposition());

        Element treeE1 = new Element("TREE");
        Element treeElP = new Element(treeSerializer.getBaseTag());
        treeSerializer.fillInto(data.getTree(), treeElP);
        treeE1.appendChild(treeElP);
        parent.appendChild(treeE1);

    }

	@Override
	public void doSanitizeElement(Element parent) {
		
	}

	@Override
	public Class<CommandData> getDataType() {
		return CommandData.class;
	}

}

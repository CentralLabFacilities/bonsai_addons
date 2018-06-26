package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

import de.unibi.citec.clf.btl.data.knowledgebase.Arena;
import de.unibi.citec.clf.btl.data.knowledgebase.Location;
import de.unibi.citec.clf.btl.data.knowledgebase.Room;
import de.unibi.citec.clf.btl.data.knowledgebase.Door;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import java.util.LinkedList;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 *
 * @author rfeldhans
 */
public class ArenaSerializer extends XomSerializer<Arena> {
    
    RoomSerializer rs = new RoomSerializer();
    LocationSerializer ls = new LocationSerializer();
    DoorSerializer ds = new DoorSerializer();
    
    @Override
    public Arena doFromElement(Element element) throws ParsingException, DeserializationException {
        Arena arena = new Arena();
        LinkedList<Location> places = new LinkedList();
        LinkedList<Room> rooms = new LinkedList();
        LinkedList<Door> doors = new LinkedList();
        
        Elements subElements = element.getChildElements();
        for (int i = 0; i < subElements.size(); i++) {
            if (subElements.get(i).getLocalName().equals(ls.getBaseTag())) {
                Location loc = ls.doFromElement(subElements.get(i));
                places.add(loc);
            } else if (subElements.get(i).getLocalName().equals(rs.getBaseTag())) {
                Room rm = rs.doFromElement(subElements.get(i));
                rooms.add(rm);
            } else if (subElements.get(i).getLocalName().equals(ds.getBaseTag())) {
                Door dr = ds.doFromElement(subElements.get(i));
                doors.add(dr);
            }
        }
        arena.setLocations(places);
        arena.setRooms(rooms);
        arena.setDoors(doors);
        return arena;
    }
    
    @Override
    public void doSanitizeElement(Element parent) {
    }
    
    @Override
    public void doFillInto(Arena data, Element parent) throws SerializationException {
        for (Location loc : data.getLocations()) {
            Element subNode = new Element(ls.getBaseTag());
            ls.fillInto(loc, subNode);
            parent.appendChild(subNode);
        }
        for (Room room : data.getRooms()) {
            Element subNode = new Element(rs.getBaseTag());
            rs.fillInto(room, subNode);
            parent.appendChild(subNode);
        }
        for (Door door : data.getDoors()) {
            Element subNode = new Element(ds.getBaseTag());
            ds.fillInto(door, subNode);
            parent.appendChild(subNode);
        }
    }
    
    @Override
    public Class<Arena> getDataType() {
        return Arena.class;
    }
    
    @Override
    public String getBaseTag() {
        return "ARENA";
    }
    
}

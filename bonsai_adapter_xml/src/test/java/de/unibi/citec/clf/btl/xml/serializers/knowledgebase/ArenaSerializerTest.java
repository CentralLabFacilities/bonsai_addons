/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.citec.clf.btl.xml.serializers.knowledgebase;

/*
 * #LICENSE%
 * btl-xml
 * ---
 * Copyright (C) 2009 - 2017 Frederic Siepmann
 * ---
 * This file is part of Bielefeld Sensor Actuator Interface (BonSAI).
 *
 * http://opensource.cit-ec.de/projects/bonsai
 *
 * This file may be licensed under the terms of of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by the
 * Excellence Cluster EXC 277 Cognitive Interaction Technology.
 * The Excellence Cluster EXC 277 is a grant of the Deutsche
 * Forschungsgemeinschaft (DFG) in the context of the German
 * Excellence Initiative.
 *
 * Contributors: Florian Lier, Frederic Siepmann, Leon Ziegler,
 * Matthias Schoepfer, Adriana-Victoria Dreyer, Agnes Swadzba,
 * Andreas Kipp, Birte Carlmeyer, Christian Ascheberg, Daniel Nacke,
 * Dennis Wigand, Günes Minareci, Hendrik ter Horst, Ingo Killmann,
 * Jan Pöppel, Lukas Kettenbach, Michael Zeunert, Patrick Renner,
 * Philipp Dresselhaus, Sebastian Meyer zu Borgsen, Soufian Jebbara,
 * Tobias Röhlig, Torben Toeniges, Viktor Losing, Viktor Richter
 * %LICENSE#
 */

import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.knowledgebase.Arena;
import de.unibi.citec.clf.btl.data.knowledgebase.Door;
import de.unibi.citec.clf.btl.data.knowledgebase.Location;
import de.unibi.citec.clf.btl.data.knowledgebase.Room;
import de.unibi.citec.clf.btl.data.map.Annotation;
import de.unibi.citec.clf.btl.data.map.Viewpoint;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * @author rfeldhans
 */
public class ArenaSerializerTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }


    //@Test TODO: Should work but does not. Maybe LinkedList is the problem?
    public void selfCompatibility() throws XomSerializer.SerializationException, XomSerializer.DeserializationException {
        Arena actual = new Arena();
        LinkedList<Room> rooms = new LinkedList();

        Room rooma = new Room();
        rooma.setName("rooma");
        rooma.setNumberOfDoors(5);
        Annotation a = new Annotation();
        a.setLabel("annotation");
        PrecisePolygon polygon = new PrecisePolygon();
        polygon.addPoint(0, 0, LengthUnit.METER);
        a.setPolygon(polygon);
        LinkedList<Viewpoint> viewpoints = new LinkedList();
        Viewpoint v = new Viewpoint();
        v.setLabel("vp");
        PositionData coordinates = new PositionData();
        coordinates.setX(1.2, LengthUnit.METER);
        coordinates.setY(3.4, LengthUnit.METER);
        coordinates.setYaw(5.6, AngleUnit.RADIAN);
       // v.setCoordinates(coordinates);
        viewpoints.add(v);
        a.setViewpoints(viewpoints);
        rooma.setAnnotation(a);
        rooms.add(rooma);

        Room roomb = new Room();
        roomb.setName("roomb");
        roomb.setNumberOfDoors(3);
        Annotation a2 = new Annotation();
        a2.setLabel("annotation2");
        PrecisePolygon polygon2 = new PrecisePolygon();
        polygon2.addPoint(7, 8, LengthUnit.METER);
        a2.setPolygon(polygon2);
        LinkedList<Viewpoint> viewpoints2 = new LinkedList();
        Viewpoint v2 = new Viewpoint();
        v2.setLabel("vp2");
        PositionData coordinates2 = new PositionData();
        coordinates2.setX(9.0, LengthUnit.METER);
        coordinates2.setY(1.2, LengthUnit.METER);
        coordinates2.setYaw(3.4, AngleUnit.RADIAN);
       // v2.setCoordinates(coordinates2);
        viewpoints2.add(v2);
        a2.setViewpoints(viewpoints2);
        roomb.setAnnotation(a2);
        rooms.add(roomb);
        actual.setRooms(rooms);

        LinkedList<Location> locs = new LinkedList();

        Location loca = new Location();
        loca.setIsBeacon(true);
        loca.setIsPlacement(false);
        loca.setName("loca");
        loca.setRoom("bedroom");
        loca.setAnnotation(a);
        locs.add(loca);

        Location locb = new Location();
        locb.setIsBeacon(false);
        locb.setIsPlacement(true);
        locb.setName("locb");
        locb.setRoom("bath");
        locb.setAnnotation(a2);
        locs.add(locb);
        actual.setLocations(locs);

        LinkedList<Door> doors = new LinkedList();
        Door doora = new Door();
        doora.setAnnotation(a);
        doora.setRoomOne("room1a");
        doora.setRoomTwo("room2a");
        doors.add(doora);

        Door doorb = new Door();
        doorb.setAnnotation(a2);
        doorb.setRoomOne("room1b");
        doorb.setRoomTwo("room2b");
        doors.add(doorb);
        actual.setDoors(doors);

        Document doc = XomTypeFactory.getInstance().createDocument(actual);
        logger.info(doc.toXML());

        Arena parsed = XomTypeFactory.getInstance().createType(doc, Arena.class);

        assertEquals(actual, parsed);

    }

}

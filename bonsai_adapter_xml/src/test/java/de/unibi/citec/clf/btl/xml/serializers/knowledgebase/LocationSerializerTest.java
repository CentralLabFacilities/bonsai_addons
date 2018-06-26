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
import de.unibi.citec.clf.btl.data.knowledgebase.Location;
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
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * @author rfeldhans
 */
public class LocationSerializerTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }


    @Test
    public void selfCompatibility() throws XomSerializer.SerializationException, XomSerializer.DeserializationException {
        Location actual = new Location();
        actual.setGenerator("unknown");
        actual.setIsBeacon(true);
        actual.setIsPlacement(false);
        actual.setName("boris");
        actual.setRoom("bath");

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
//        v.setCoordinates(coordinates);
        viewpoints.add(v);
        a.setViewpoints(viewpoints);
        actual.setAnnotation(a);

        Document doc = XomTypeFactory.getInstance().createDocument(actual);
        logger.info(doc.toXML());

        Location parsed = XomTypeFactory.getInstance().createType(doc, Location.class);

        assertEquals(actual, parsed);
    }

}

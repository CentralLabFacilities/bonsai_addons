/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.citec.clf.btl.xml.serializers.object;

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

import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.object.ObjectSetTable;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * @author semueller
 */
public class ObjectSetTableTest {

    private Logger logger = Logger.getLogger(getClass());

    @Test
    public void selfCompatibility() throws Exception {

        ObjectSetTable oss = new ObjectSetTable();

        oss.setName("testObject");
        oss.setMoved(true);
        oss.setGraspDifficulty(42);
        oss.setCurrentLocation(new PositionData());//test whether null positiondata breaks sth.
        oss.setOriginalLocation(new PositionData());
        Document doc = XomTypeFactory.getInstance().createDocument(oss);
        logger.info(doc.toXML());

        ObjectSetTable parsed = XomTypeFactory.getInstance().createType(doc,
                ObjectSetTable.class);

        assertEquals(oss.getGenerator(), parsed.getGenerator());
        assertEquals(oss.getTimestamp(), parsed.getTimestamp());
        assertEquals(oss.getName(), parsed.getName());
        assertEquals(oss.isMoved(), parsed.isMoved());
        assertEquals(oss.getGraspDifficulty(), parsed.getGraspDifficulty());


    }

    @Test
    public void fileCompatibility() throws Exception {

        Builder parser = new Builder();
        Document doc = parser.build(TestUtils
                .makeTestFileName("ObjectSetTable.xml"));

        ObjectSetTable parsed = XomTypeFactory.getInstance().createType(doc,
                ObjectSetTable.class);

        assertEquals("Thing", parsed.getName());
        assertEquals(666, parsed.getGraspDifficulty());
        assertEquals(true, parsed.isMoved());

    }

}

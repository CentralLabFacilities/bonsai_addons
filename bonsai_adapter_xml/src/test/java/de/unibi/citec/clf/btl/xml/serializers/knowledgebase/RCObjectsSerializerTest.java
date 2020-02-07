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

import de.unibi.citec.clf.btl.data.knowledgebase.RCObject;
import de.unibi.citec.clf.btl.data.knowledgebase.RCObjects;
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
public class RCObjectsSerializerTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }


    //@Test TODO: Should work but does not. Maybe LinkedList is the problem?
    public void selfCompatibility() throws XomSerializer.SerializationException, XomSerializer.DeserializationException {
        RCObjects actual = new RCObjects();
        LinkedList<RCObject> obj = new LinkedList();

        RCObject obja = new RCObject();
        obja.setCategory("Food");
        obja.setColor("blue");
        obja.setLocation("here");
        obja.setName("john");
        obja.setShape("pointy");
        obja.setSize(3);
        obja.setType("not my");
        obja.setWeight(4);
        obj.add(obja);

        RCObject objb = new RCObject();
        objb.setCategory("Candy");
        objb.setColor("red");
        objb.setLocation("there");
        objb.setName("boris");
        objb.setShape("sharp");
        objb.setSize(1);
        objb.setType("definetifly my");
        objb.setWeight(0);
        obj.add(objb);
        actual.setRCObjects(obj);


        Document doc = XomTypeFactory.getInstance().createDocument(actual);
        logger.info(doc.toXML());

        RCObjects parsed = XomTypeFactory.getInstance().createType(doc, RCObjects.class);

        assertEquals(actual, parsed);
    }


}

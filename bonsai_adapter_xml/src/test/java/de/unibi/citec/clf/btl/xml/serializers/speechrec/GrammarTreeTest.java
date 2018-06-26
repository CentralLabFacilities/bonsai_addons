/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.citec.clf.btl.xml.serializers.speechrec;

/*
 * #LICENSE%
 * btl-xml
 * ---
 * Copyright (C) 2009 - 2015 Frederic Siepmann
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

import de.unibi.citec.clf.btl.data.speechrec.GrammarTree;
import de.unibi.citec.clf.btl.data.speechrec.Utterance;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;

/**
 * @author lruegeme
 */
public class GrammarTreeTest {

    private static Logger logger = Logger.getLogger(UtteranceTest.class);

    @Before
    public void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void createTree() throws Exception {
        Builder parser = new Builder();
        Document doc = parser
                .build(TestUtils.makeTestFileName("UttDoubleNT.xml"));

        Utterance utt = XomTypeFactory.getInstance().createType(doc, Utterance.class);
        logger.info("\n#########\nDocument: " + doc.toXML());
        logger.info("\n#########\nUtterace: " + utt.getSimpleString());
        GrammarTree tree = utt.getGrammarTree();
        logger.info("\n#########\ntree: " + tree.toString());

        logger.info("\n#########\nsubsymbols: ");

        //assert(false);

    }


}

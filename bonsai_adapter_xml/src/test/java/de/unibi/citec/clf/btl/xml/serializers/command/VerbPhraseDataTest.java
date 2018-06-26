/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.citec.clf.btl.xml.serializers.command;

/*
 * #LICENSE%
 * btl-xml
 * ---
 * Copyright (C) 2009 - 2016 Frederic Siepmann
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

import de.unibi.citec.clf.btl.data.command.VerbPhraseData;
import de.unibi.citec.clf.btl.units.TimeUnit;
import de.unibi.citec.clf.btl.xml.XomTypeFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unibi.citec.clf.btl.xml.TestUtils;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author hneumann
 */
public class VerbPhraseDataTest {

    private Logger logger = Logger.getLogger(getClass());

    @BeforeClass
    public static void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void selfCompatibility() throws ParsingException,
            de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException,
            de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException {

        VerbPhraseData data = new VerbPhraseData();

        final long timestamp = System.currentTimeMillis();
        data.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        data.setVerb("verb");
        data.setVerbPhraseType("verbPhraseType");
        data.setVerbType("verbType");
        List<String> nominalPhrasesType = new ArrayList<>();
        List<String> nominalPhrases = new ArrayList<>();
        List<String> prepositions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            nominalPhrasesType.add("nominalPhrasesType" + i);
            prepositions.add("prepositions" + i);
            nominalPhrases.add("nominalPhrases" + i);
        }
        data.setNominalPhrases(nominalPhrases);
        data.setNominalPhrasesType(nominalPhrasesType);
        data.setPrepositions(prepositions);

        Document document = XomTypeFactory.getInstance().createDocument(data);
        logger.info(document.toXML());

        VerbPhraseData parsed = XomTypeFactory.getInstance().createType(document, VerbPhraseData.class);

        assertEquals(data.getVerb(), parsed.getVerb());
        assertEquals(data.getVerbPhraseType(), parsed.getVerbPhraseType());
        assertEquals(data.getVerbType(), parsed.getVerbType());
        assertEquals(data.getNominalPhrases(), parsed.getNominalPhrases());
        assertEquals(data.getNominalPhrasesType(), parsed.getNominalPhrasesType());
        assertEquals(data.getPrepositions(), parsed.getPrepositions());

    }

    @Test
    public void fileCompatibility() throws Exception {
        VerbPhraseData data = new VerbPhraseData();

        final long timestamp = System.currentTimeMillis();
        data.setTimestamp(timestamp, TimeUnit.MILLISECONDS);
        data.setVerb("verb");
        data.setVerbPhraseType("verbPhraseType");
        data.setVerbType("verbType");
        List<String> nominalPhrasesType = new ArrayList<>();
        List<String> nominalPhrases = new ArrayList<>();
        List<String> prepositions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            nominalPhrasesType.add("nominalPhrasesType" + i);
            prepositions.add("prepositions" + i);
            nominalPhrases.add("nominal" + i);
        }
        data.setNominalPhrases(nominalPhrases);
        data.setNominalPhrasesType(nominalPhrasesType);
        data.setPrepositions(prepositions);


        Builder parser = new Builder();
        Document document = parser.build(TestUtils.makeTestFileName("VerbPhraseData.xml"));
        VerbPhraseData parsed = XomTypeFactory.getInstance().createType(document, VerbPhraseData.class);

        assertEquals("test", parsed.getGenerator());
        assertEquals(data.getVerb(), parsed.getVerb());
        assertEquals(data.getVerbPhraseType(), parsed.getVerbPhraseType());
        assertEquals(data.getVerbType(), parsed.getVerbType());
        assertEquals(data.getNominalPhrases(), parsed.getNominalPhrases());
        assertEquals(data.getNominalPhrasesType(), parsed.getNominalPhrasesType());
        assertEquals(data.getPrepositions(), parsed.getPrepositions());

    }
}

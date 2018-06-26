
package de.unibi.citec.clf.btl.xml.serializers.command;



import de.unibi.citec.clf.btl.data.command.VerbPhraseData;
import de.unibi.citec.clf.btl.data.command.VerbPhraseDataList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 *
 * @author hneumann
 */
public class VerbPhraseDataListSerializer extends XomListSerializer<VerbPhraseData, VerbPhraseDataList> {
    
    @Override
    public XomSerializer<VerbPhraseData> getItemSerializer() {
        return new VerbPhraseDataSerializer();
    }

    @Override
    public VerbPhraseDataList getDefaultInstance() {
        return new VerbPhraseDataList();
    }
    
}

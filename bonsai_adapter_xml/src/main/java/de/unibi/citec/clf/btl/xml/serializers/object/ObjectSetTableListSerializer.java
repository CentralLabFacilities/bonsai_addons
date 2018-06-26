
package de.unibi.citec.clf.btl.xml.serializers.object;



import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.object.ObjectSetTable;
import de.unibi.citec.clf.btl.data.object.ObjectSetTableList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;
/**
 *
 * @author semueller
 */
public class ObjectSetTableListSerializer extends XomListSerializer<ObjectSetTable, List<ObjectSetTable>> {
    @Override
    public XomSerializer<ObjectSetTable> getItemSerializer() {
        return new ObjectSetTableSerializer();
    }

    @Override
    public List<ObjectSetTable> getDefaultInstance() {
        return new ObjectSetTableList(); 
    }
}
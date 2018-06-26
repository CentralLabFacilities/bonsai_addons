

package de.unibi.citec.clf.btl.xml.serializers.object;



import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.data.object.ObjectOrder;
import de.unibi.citec.clf.btl.data.object.ObjectOrderList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 *
 * @author cwitte
 */
public class ObjectOrderListSerializer extends XomListSerializer<ObjectOrder, List<ObjectOrder>> {
     @Override
    public XomSerializer<ObjectOrder> getItemSerializer() {
        return new ObjectOrderSerializer();
    }

    @Override
    public List<ObjectOrder> getDefaultInstance() {
        return new ObjectOrderList();
    }
}

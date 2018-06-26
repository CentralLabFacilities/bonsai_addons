package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import de.unibi.citec.clf.btl.data.vision3d.PlaneData;
import de.unibi.citec.clf.btl.data.vision3d.PlaneList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * This is only for convenience because bonsai does not support the
 * {@link ListSerializer} type so far.
 * 
 * @author lziegler
 */
public class PlaneListSerializer extends XomListSerializer<PlaneData, PlaneList> {

    @Override
    public XomSerializer<PlaneData> getItemSerializer() {
        return new PlaneDataSerializer();
    }
    @Override
    public PlaneList getDefaultInstance() {
        return new PlaneList();
    }
}

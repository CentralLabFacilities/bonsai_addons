package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import de.unibi.citec.clf.btl.data.vision3d.PlanePatch;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatchList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * This is only for convenience because bonsai does not support the
 * {@link ListSerializer} type so far.
 * 
 * @author lziegler
 */
public class PlanePatchListSerializer extends XomListSerializer<PlanePatch, PlanePatchList> {

    @Override
    public XomSerializer<PlanePatch> getItemSerializer() {
        return new PlanePatchSerializer();
    }
    @Override
    public PlanePatchList getDefaultInstance() {
        return new PlanePatchList();
    }
}

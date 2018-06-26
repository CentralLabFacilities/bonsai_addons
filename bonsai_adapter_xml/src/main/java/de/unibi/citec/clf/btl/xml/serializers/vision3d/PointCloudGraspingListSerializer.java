package de.unibi.citec.clf.btl.xml.serializers.vision3d;



import de.unibi.citec.clf.btl.data.vision3d.PointCloudGrasping;
import de.unibi.citec.clf.btl.data.vision3d.PointCloudGraspingList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * This is only for convenience because bonsai does not support the
 * {@link ListSerializer} type so far.
 * 
 * @author lziegler
 */
public class PointCloudGraspingListSerializer extends
        XomListSerializer<PointCloudGrasping, PointCloudGraspingList> {

    @Override
    public XomSerializer<PointCloudGrasping> getItemSerializer() {
        return new PointCloudGraspingSerializer();
    }

    @Override
    public PointCloudGraspingList getDefaultInstance() {
        return new PointCloudGraspingList();
    }
}

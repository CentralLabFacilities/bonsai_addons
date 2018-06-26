package de.unibi.citec.clf.btl.xml.serializers.object;



import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.data.object.ObjectLocationList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * Results of the object recognition. This class is meat so define the location
 * of the object in the detector's camera image! The given polygon describes the
 * objects's location in pixel coordinates! If you want to define an object in
 * world coordinates use {@link ObjectPositionSerializer}.
 * 
 * @author lziegler
 */
public class ObjectLocationListSerializer extends
        XomListSerializer<ObjectLocationData, ObjectLocationList> {

    @Override
    public XomSerializer<ObjectLocationData> getItemSerializer() {
        return new ObjectLocationSerializer();
    }
    @Override
    public ObjectLocationList getDefaultInstance() {
        return new ObjectLocationList();
    }
}

package de.unibi.citec.clf.btl.xml.serializers.object;



import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import de.unibi.citec.clf.btl.xml.XomListSerializer;
import de.unibi.citec.clf.btl.xml.XomSerializer;

/**
 * Results of the object recognition. This class is meat so define the location
 * of the object in the detector's camera image and contain shape information in
 * 3D! The given polygon describes the objects's location in pixel coordinates!
 * 
 * @author lziegler
 */
public class ObjectShapeListSerializer extends
        XomListSerializer<ObjectShapeData, ObjectShapeList> {

    @Override
    public XomSerializer<ObjectShapeData> getItemSerializer() {
        return new ObjectShapeSerializer();
    }
    @Override
    public ObjectShapeList getDefaultInstance() {
        return new ObjectShapeList();
    }
}

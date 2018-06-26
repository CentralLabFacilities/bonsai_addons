package de.unibi.citec.clf.btl.rst.serializers.object;



import java.util.List;

import rst.classification.ClassifiedRegion2DType.ClassifiedRegion2D;
import rst.classification.ClassifiedRegions2DType.ClassifiedRegions2D;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.data.object.ObjectLocationList;
import de.unibi.citec.clf.btl.rst.RstSerializer;

public class ObjectLocationListSerializer extends RstSerializer<ObjectLocationList, ClassifiedRegions2D> {

    private ObjectLocationSerializer objectSerializer = new ObjectLocationSerializer();

    @Override
    public ObjectLocationList deserialize(ClassifiedRegions2D hyp) {

        List<ClassifiedRegion2D> list = hyp.getRegionsList();
        ObjectLocationList shapes = new ObjectLocationList();
        for (ClassifiedRegion2D r : list) {
        	ObjectLocationData d = objectSerializer.deserialize(r);
            shapes.add(d);
        }

        return shapes;
    }

    @Override
    public void serialize(ObjectLocationList data, Builder<?> msg) {
    	ClassifiedRegions2D.Builder builder = (ClassifiedRegions2D.Builder) msg;

        for (ObjectLocationData d : data) {
            objectSerializer.serialize(d, builder.addRegionsBuilder());
        }
    }

    @Override
    public Class<ClassifiedRegions2D> getMessageType() {
        return ClassifiedRegions2D.class;
    }

    @Override
    public Class<ObjectLocationList> getDataType() {
        return ObjectLocationList.class;
    }
}

package de.unibi.citec.clf.btl.rst.serializers.object;



import java.util.List;

import rst.tracking.TrackedClassifiedRegion3DType.TrackedClassifiedRegion3D;
import rst.tracking.TrackedClassifiedRegions3DType.TrackedClassifiedRegions3D;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.object.ObjectShapeList;
import de.unibi.citec.clf.btl.rst.RstSerializer;

public class ObjectShapeListSerializer extends RstSerializer<ObjectShapeList, TrackedClassifiedRegions3D> {

    private ObjectShapeSerializer objectSerializer = new ObjectShapeSerializer();

    @Override
    public ObjectShapeList deserialize(TrackedClassifiedRegions3D hyp) {

        List<TrackedClassifiedRegion3D> list = hyp.getRegionList();
        ObjectShapeList shapes = new ObjectShapeList();
        for (TrackedClassifiedRegion3D r : list) {
            ObjectShapeData d = objectSerializer.deserialize(r);
            shapes.add(d);
            shapes.setFrameId(d.getFrameId());
        }

        return shapes;
    }

    @Override
    public void serialize(ObjectShapeList data, Builder<?> msg) {
    	TrackedClassifiedRegions3D.Builder builder = (TrackedClassifiedRegions3D.Builder) msg;

        for (ObjectShapeData d : data) {
            objectSerializer.serialize(d, builder.addRegionBuilder());
        }
    }

    @Override
    public Class<TrackedClassifiedRegions3D> getMessageType() {
        return TrackedClassifiedRegions3D.class;
    }

    @Override
    public Class<ObjectShapeList> getDataType() {
        return ObjectShapeList.class;
    }
}

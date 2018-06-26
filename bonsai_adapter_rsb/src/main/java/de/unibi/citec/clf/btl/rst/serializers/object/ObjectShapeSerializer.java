package de.unibi.citec.clf.btl.rst.serializers.object;



import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.BoundingBox3D;
import de.unibi.citec.clf.btl.data.object.ObjectShapeData;
import de.unibi.citec.clf.btl.data.object.ObjectData.Hypothesis;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.LengthUnit;
import de.unibi.citec.clf.btl.units.AngleUnit;
import javafx.geometry.BoundingBox;
import rst.classification.ClassificationResultType.ClassificationResult.ClassWithProbability;
import rst.geometry.BoundingBox3DFloatType.BoundingBox3DFloat;
import rst.geometry.PoseType.Pose;
import rst.geometry.TranslationType.Translation;
import rst.tracking.TrackedClassifiedRegion3DType.TrackedClassifiedRegion3D;

public class ObjectShapeSerializer extends RstSerializer<ObjectShapeData, TrackedClassifiedRegion3D> {

    private static final LengthUnit meter = LengthUnit.METER;

    @Override
    public ObjectShapeData deserialize(TrackedClassifiedRegion3D hyp) {
        
        ObjectShapeData l = new ObjectShapeData();
        BoundingBox3DFloat box = hyp.getRegion().getRegion();
        Pose boxPose = box.getTransformation();
        Translation boxTrans = boxPose.getTranslation();
        Point3D center = new Point3D(boxTrans.getX(), boxTrans.getY(), boxTrans.getZ(), meter);
        Point3D size = new Point3D(box.getHeight(), box.getWidth(), box.getDepth(), meter);
        center.setFrameId(boxTrans.getFrameId());
        Pose3D pose3D = new Pose3D(center, new Rotation3D(0.0, 0.0, 0.0, AngleUnit.RADIAN));
        BoundingBox3D boundingBox3D = new BoundingBox3D(pose3D, size);
        l.setFrameId(boxTrans.getFrameId());
        l.setId(Integer.toString(hyp.getInfo().getId()));

        List<ClassWithProbability> results = hyp.getRegion().getResult().getClassesList();
        for (ClassWithProbability c : results) {
            ObjectShapeData.Hypothesis h = new ObjectShapeData.Hypothesis();
            h.setClassLabel(c.getName().toStringUtf8());
            h.setReliability(c.getConfidence());
            l.addHypothesis(h);
        }

        return l;
    }

    @Override
    public void serialize(ObjectShapeData data, Builder<?> msg) {
    	TrackedClassifiedRegion3D.Builder builder = (TrackedClassifiedRegion3D.Builder) msg;
        Point3D center = data.getCenter();
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getTranslationBuilder().setX(center.getX(meter));
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getTranslationBuilder().setY(center.getY(meter));
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getTranslationBuilder().setZ(center.getZ(meter));
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getTranslationBuilder().setFrameId(center.getFrameId());
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getRotationBuilder().setQw(1.0);
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getRotationBuilder().setQx(0.0);
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getRotationBuilder().setQy(0.0);
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getRotationBuilder().setQz(0.0);
        builder.getRegionBuilder().getRegionBuilder().getTransformationBuilder().getRotationBuilder().setFrameId(data.getFrameId());
        builder.getRegionBuilder().getRegionBuilder().setDepth((float) data.getDepth(meter));
        builder.getRegionBuilder().getRegionBuilder().setWidth((float) data.getDepth(meter));
        builder.getRegionBuilder().getRegionBuilder().setHeight((float) data.getDepth(meter));
        builder.getInfoBuilder().setId(Integer.parseInt(data.getId()));

        for (Hypothesis h : data.getHypotheses()) {
            ClassWithProbability.Builder b = builder.getRegionBuilder().getResultBuilder().addClassesBuilder();
            b.setConfidence((float) h.getReliability());
            b.setName(ByteString.copyFromUtf8(h.getClassLabel()));
        }
    }

    @Override
    public Class<TrackedClassifiedRegion3D> getMessageType() {
        return TrackedClassifiedRegion3D.class;
    }

    @Override
    public Class<ObjectShapeData> getDataType() {
        return ObjectShapeData.class;
    }
}

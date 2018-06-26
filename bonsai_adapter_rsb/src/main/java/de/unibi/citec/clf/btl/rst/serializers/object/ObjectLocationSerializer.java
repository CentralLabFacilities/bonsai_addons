package de.unibi.citec.clf.btl.rst.serializers.object;



import java.util.List;

import rst.classification.ClassificationResultType.ClassificationResult.ClassWithProbability;
import rst.classification.ClassifiedRegion2DType.ClassifiedRegion2D;
import rst.geometry.BoundingBoxType.BoundingBox;
import rst.math.Vec2DIntType.Vec2DInt;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.data.object.ObjectLocationData;
import de.unibi.citec.clf.btl.data.object.ObjectData.Hypothesis;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.LengthUnit;

public class ObjectLocationSerializer extends RstSerializer<ObjectLocationData, ClassifiedRegion2D> {

    @Override
    public ObjectLocationData deserialize(ClassifiedRegion2D hyp) {

    	ObjectLocationData l = new ObjectLocationData();
        BoundingBox box = hyp.getRegion();
        Vec2DInt boxTL = box.getTopLeft();
        int height = box.getHeight();
		int width = box.getWidth();
        PrecisePolygon polygon = new PrecisePolygon();
        //Beware: lengthunit may be wrong! (see below also)
		polygon.addPoint(boxTL.getX(),         boxTL.getY(), LengthUnit.METER);
		polygon.addPoint(boxTL.getX() + width, boxTL.getY(), LengthUnit.METER);
		polygon.addPoint(boxTL.getX() + width, boxTL.getY() + height, LengthUnit.METER);
		polygon.addPoint(boxTL.getX(),         boxTL.getY() + height, LengthUnit.METER);
        l.setPolygon(polygon);

        List<ClassWithProbability> results = hyp.getResult().getClassesList();
        for (ClassWithProbability c : results) {
        	Hypothesis h = new Hypothesis();
            h.setClassLabel(c.getName().toStringUtf8());
            h.setReliability(c.getConfidence());
            l.addHypothesis(h);
        }

        return l;
    }

    @Override
    public void serialize(ObjectLocationData data, Builder<?> msg) {
    	ClassifiedRegion2D.Builder builder = (ClassifiedRegion2D.Builder) msg;
        PrecisePolygon polygon = data.getPolygon();
        
        if (polygon.getPointCount() > 2) {
            //Beware: lengthunit may be wrong! (see above also)
        	int x0 = (int)polygon.getMinX(LengthUnit.METER);
        	int y0 = (int)polygon.getMinX(LengthUnit.METER);
        	int x1 = (int)polygon.getMaxX(LengthUnit.METER);
        	int y1 = (int)polygon.getMaxX(LengthUnit.METER);
        	
        	int width = Math.abs(x1 - x0);
        	int height = Math.abs(y1 - y0);

        	builder.getRegionBuilder().getTopLeftBuilder().setX(x0);
        	builder.getRegionBuilder().getTopLeftBuilder().setY(y0);
        	builder.getRegionBuilder().setWidth(width);
        	builder.getRegionBuilder().setHeight(height);
        }

        for (Hypothesis h : data.getHypotheses()) {
            ClassWithProbability.Builder b = builder.getResultBuilder().addClassesBuilder();
            b.setConfidence((float) h.getReliability());
            b.setName(ByteString.copyFromUtf8(h.getClassLabel()));
        }
    }

    @Override
    public Class<ClassifiedRegion2D> getMessageType() {
        return ClassifiedRegion2D.class;
    }

    @Override
    public Class<ObjectLocationData> getDataType() {
        return ObjectLocationData.class;
    }
}

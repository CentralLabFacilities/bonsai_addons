package de.unibi.citec.clf.btl.rst.serializers.geometry;



import com.google.protobuf.GeneratedMessage.Builder;

import rst.geometry.PolygonalPatch3DType;
import rst.math.Vec2DFloatType.Vec2DFloat;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.data.geometry.PolygonalPatch3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.PrecisePolygon;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.LengthUnit;

/**
 * @author lziegler
 *
 */
public class PolygonalPatch3DSerializer extends
		RstSerializer<PolygonalPatch3D, PolygonalPatch3DType.PolygonalPatch3D> {

	Pose3DSerializer baseSerializer = new Pose3DSerializer();

	@Override
	public PolygonalPatch3D deserialize(PolygonalPatch3DType.PolygonalPatch3D msg)
			throws DeserializationException {

		Pose3D base = baseSerializer.deserialize(msg.getBase());
		PrecisePolygon poly = new PrecisePolygon();

		for (Vec2DFloat vec : msg.getBorderList()) {
			poly.addPoint(vec.getX(), vec.getY(), LengthUnit.METER);
		}

		PolygonalPatch3D p = new PolygonalPatch3D(base, poly);
		p.setFrameId(base.getFrameId());
		return p;
	}

	@Override
	public void serialize(PolygonalPatch3D data, Builder<?> abstractBuilder)
			throws SerializationException {
		PolygonalPatch3DType.PolygonalPatch3D.Builder builder = 
				(PolygonalPatch3DType.PolygonalPatch3D.Builder) abstractBuilder;

		baseSerializer.serialize(data.getBase(), builder.getBaseBuilder());

		for (Point2D p : data.getBorder()) {
			Vec2DFloat.Builder borderBuilder = builder.addBorderBuilder();
			borderBuilder.setX((float)p.getX(LengthUnit.METER));
			borderBuilder.setY((float)p.getY(LengthUnit.METER));
		}
	}

	@Override
	public Class<PolygonalPatch3DType.PolygonalPatch3D> getMessageType() {
		return PolygonalPatch3DType.PolygonalPatch3D.class;
	}

	@Override
	public Class<PolygonalPatch3D> getDataType() {
		return PolygonalPatch3D.class;
	}

}

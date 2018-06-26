package de.unibi.citec.clf.btl.rst.serializers.geometry;



import rst.geometry.TranslationType.Translation;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.LengthUnit;

public class Point3DSerializer extends RstSerializer<Point3D, Translation> {

	@Override
	public Class<Translation> getMessageType() {
		return Translation.class;
	}

	@Override
	public Point3D deserialize(Translation location) {
		double xMeter = location.getX();
		double yMeter = location.getY();
		double zMeter = location.getZ();

		Point3D p = new Point3D(xMeter, yMeter, zMeter, LengthUnit.METER);
		p.setFrameId(location.getFrameId());
		return p;
	}

	@Override
	public void serialize(Point3D data, Builder<?> abstractBuilder) {
		Translation.Builder builder = (Translation.Builder) abstractBuilder;
		builder.setX(data.getX(LengthUnit.METER));
		builder.setY(data.getY(LengthUnit.METER));
		builder.setZ(data.getZ(LengthUnit.METER));
		builder.setFrameId(data.getFrameId());
	}

	@Override
	public Class<Point3D> getDataType() {
		return Point3D.class;
	}

}

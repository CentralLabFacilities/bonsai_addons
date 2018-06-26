package de.unibi.citec.clf.btl.rst.serializers.geometry;



import javax.vecmath.Quat4d;

import rst.geometry.RotationType.Rotation;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.rst.RstSerializer;

public class Rotation3DSerializer extends
		RstSerializer<Rotation3D, Rotation> {
	
	@Override
	public Class<Rotation> getMessageType() {
		return Rotation.class;
	}

	@Override
	public Rotation3D deserialize(Rotation rotation) {
		Rotation3D r = new Rotation3D();
		double qx = rotation.getQx();
		double qy = rotation.getQy();
		double qz = rotation.getQz();
		double qw = rotation.getQw();

		r.setQuaternion(qx, qy, qz, qw);
		r.setFrameId(rotation.getFrameId());
		return r;
	}

	@Override
	public void serialize(Rotation3D data, Builder<?> msg) {
		Rotation.Builder builder = (Rotation.Builder) msg;
		Quat4d quat = data.getQuaternion();
		builder.setQx(quat.x);
		builder.setQy(quat.y);
		builder.setQz(quat.z);
		builder.setQw(quat.w);
		builder.setFrameId(data.getFrameId());
	}

	@Override
	public Class<Rotation3D> getDataType() {
		return Rotation3D.class;
	}

}

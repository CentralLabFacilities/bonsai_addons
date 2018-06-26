package de.unibi.citec.clf.btl.rst.serializers.geometry;



import com.google.protobuf.GeneratedMessage.Builder;

import rst.geometry.PoseType;
import rst.geometry.PoseType.Pose;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.rst.RstSerializer;

/**
 * @author lziegler
 *
 */
public class Pose3DSerializer extends RstSerializer<Pose3D, PoseType.Pose> {
	
	Point3DSerializer transSerializer = new Point3DSerializer();
	Rotation3DSerializer rotSerializer = new Rotation3DSerializer();

	@Override
	public Pose3D deserialize(Pose msg)
			throws RstSerializer.DeserializationException {
		
		Point3D translation = transSerializer.deserialize(msg.getTranslation());
		Rotation3D rotation = rotSerializer.deserialize(msg.getRotation());
		
		Pose3D pose = new Pose3D(translation, rotation);
		pose.setFrameId(translation.getFrameId());
		
		return pose;
	}

	@Override
	public void serialize(Pose3D data, Builder<?> abstractBuilder)
			throws RstSerializer.SerializationException {
		PoseType.Pose.Builder builder = (PoseType.Pose.Builder)abstractBuilder;
		
		transSerializer.serialize(data.getTranslation(), builder.getTranslationBuilder());
		rotSerializer.serialize(data.getRotation(), builder.getRotationBuilder());
	}

	@Override
	public Class<Pose> getMessageType() {
		return Pose.class;
	}

	@Override
	public Class<Pose3D> getDataType() {
		return Pose3D.class;
	}

}

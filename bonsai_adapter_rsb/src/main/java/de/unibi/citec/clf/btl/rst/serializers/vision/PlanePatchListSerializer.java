package de.unibi.citec.clf.btl.rst.serializers.vision;



import rst.geometry.PolygonalPatch3DSetType;
import rst.geometry.PolygonalPatch3DSetType.PolygonalPatch3DSet;
import rst.geometry.PolygonalPatch3DType;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.PolygonalPatch3D;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatch;
import de.unibi.citec.clf.btl.data.vision3d.PlanePatchList;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.rst.serializers.geometry.PolygonalPatch3DSerializer;

/**
 * @author lziegler
 *
 */
public class PlanePatchListSerializer extends
		RstSerializer<PlanePatchList, PolygonalPatch3DSetType.PolygonalPatch3DSet> {

	PolygonalPatch3DSerializer patchSerializer = new PolygonalPatch3DSerializer();

	@Override
	public PlanePatchList deserialize(PolygonalPatch3DSet msg) throws DeserializationException {
		PlanePatchList list = new PlanePatchList();
		for (PolygonalPatch3DType.PolygonalPatch3D p : msg.getPatchesList()) {
			PolygonalPatch3D patch = patchSerializer.deserialize(p);
			list.add(new PlanePatch(patch));
		}
		return list;
	}

	@Override
	public void serialize(PlanePatchList data, Builder<?> abstractBuilder)
			throws SerializationException {
		PolygonalPatch3DSetType.PolygonalPatch3DSet.Builder builder = (PolygonalPatch3DSetType.PolygonalPatch3DSet.Builder) abstractBuilder;
		for (PlanePatch p : data) {
			PolygonalPatch3DType.PolygonalPatch3D.Builder b = builder.addPatchesBuilder();
			patchSerializer.serialize(p, b);
		}
	}

	@Override
	public Class<PolygonalPatch3DSet> getMessageType() {
		return PolygonalPatch3DSet.class;
	}

	@Override
	public Class<PlanePatchList> getDataType() {
		return PlanePatchList.class;
	}

}

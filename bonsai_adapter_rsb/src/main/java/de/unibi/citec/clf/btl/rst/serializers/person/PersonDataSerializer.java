package de.unibi.citec.clf.btl.rst.serializers.person;



import javax.vecmath.Vector3d;

import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.converter.XOPConverter;
import rsb.converter.XOPsConverter;
import rst.geometry.BoundingBox3DFloatType.BoundingBox3DFloat;
import rst.hri.BodyType.Body;
import rst.hri.PersonHypothesesType;
import rst.hri.PersonHypothesisType.PersonHypothesis;

import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.data.navigation.PositionData.ReferenceFrame;
import de.unibi.citec.clf.btl.data.person.PersonData;
import de.unibi.citec.clf.btl.data.person.PersonAttribute;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.rst.serializers.geometry.Point3DSerializer;
import de.unibi.citec.clf.btl.rst.serializers.geometry.Rotation3DSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;

public class PersonDataSerializer extends RstSerializer<PersonData, PersonHypothesis> {

    public PersonDataSerializer() {
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new ProtocolBufferConverter<>(
                        PersonHypothesesType.PersonHypotheses.getDefaultInstance()));
    }

    @Override
    public PersonData deserialize(PersonHypothesis hyp) {

        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Rotation3D globalOrientation = new Rotation3DSerializer().deserialize(hyp.getBody().getOrientation());
        Point3D globalLocation = new Point3DSerializer().deserialize(hyp.getBody().getLocation());
        
        PositionData positionData = new PositionData();
        positionData.setFrameId(ReferenceFrame.GLOBAL);
        positionData.setX(globalLocation.getX(iLU), iLU);
        positionData.setY(globalLocation.getY(iLU), iLU);
        positionData.setYaw(globalOrientation.getYaw(iAU), iAU);
        positionData.setFrameId(globalLocation.getFrameId());
        
        PersonData personData = new PersonData();
        
        personData.setUuid(String.valueOf(hyp.getTrackingInfo().getId()));
        personData.setPosition(positionData);

        return personData;
    }

    @Override
    public void serialize(PersonData data, Builder<?> msg) {
        PersonHypothesis.Builder builder = (PersonHypothesis.Builder) msg;

        LengthUnit iLU = LengthUnit.METER;
        AngleUnit iAU = AngleUnit.RADIAN;

        Body.Builder bodyBuilder = builder.getBodyBuilder();

        PositionData position = data.getPosition();

        Rotation3D globalOrientation = new Rotation3D(new Vector3d(0, 0, 1), position.getYaw(iAU), iAU);
        globalOrientation.setFrameId(position.getFrameId());
        Point3D globalLocation = new Point3D(position.getX(iLU), position.getY(iLU), 0.0, iLU);
        globalLocation.setFrameId(position.getFrameId());
        
        Rotation3DSerializer rot = new Rotation3DSerializer();
        rot.serialize(globalOrientation, bodyBuilder.getOrientationBuilder());

        Point3DSerializer p = new Point3DSerializer();
        p.serialize(globalLocation, bodyBuilder.getLocationBuilder());

        builder.getTrackingInfoBuilder().setId(Integer.parseInt(data.getUuid()));
                
    }

    @Override
    public Class<PersonHypothesis> getMessageType() {
        return PersonHypothesis.class;
    }

    @Override
    public Class<PersonData> getDataType() {
        return PersonData.class;
    }
}

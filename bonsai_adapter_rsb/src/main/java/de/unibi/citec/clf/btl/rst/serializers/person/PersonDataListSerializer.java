

package de.unibi.citec.clf.btl.rst.serializers.person;



import rst.hri.PersonHypothesisType.PersonHypothesis;

import com.google.protobuf.GeneratedMessage;
import de.unibi.citec.clf.btl.data.geometry.Point2D;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.person.PersonData;

import rst.hri.PersonHypothesesType.PersonHypotheses;

import de.unibi.citec.clf.btl.data.person.PersonDataList;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.rst.serializers.geometry.Point3DSerializer;
import de.unibi.citec.clf.btl.rst.serializers.geometry.Rotation3DSerializer;
import de.unibi.citec.clf.btl.tools.MathTools;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import javax.vecmath.Vector3d;
import rst.hri.BodyType;

/**
 *
 * @author kharmening
 */
public class PersonDataListSerializer extends RstSerializer<PersonDataList, PersonHypotheses>{
    
    PersonDataSerializer personDataSerializer = new PersonDataSerializer();

    @Override
    public PersonDataList deserialize(PersonHypotheses msg) {
        PersonDataList list = new PersonDataList();
        
        for (int i = 0; i < msg.getPersonsCount(); i++) {
            PersonHypothesis hyp = msg.getPersons(i);
            list.add(personDataSerializer.deserialize(hyp));
        }
        return list;
    }

    @Override
    public void serialize(PersonDataList data, GeneratedMessage.Builder<?> msg) throws SerializationException {
        
        PersonHypotheses.Builder wholeBuilder = (PersonHypotheses.Builder) msg;

        for (PersonData person : data) {
            PersonHypothesis.Builder builder = wholeBuilder.addPersonsBuilder();

            BodyType.Body.Builder bodyBuilder = builder.getBodyBuilder();

            Rotation3DSerializer rot = new Rotation3DSerializer();
            Point3D point = new Point3D(person.getPosition().getX(LengthUnit.METER), 0.0, person.getPosition().getX(LengthUnit.METER), LengthUnit.METER);

            // TODO: give him the global orientation!!!
            //person.getGlobalOrientation(),bodyBuilder.getOrientationBuilder()
            //MathTools.cartesianToPolar(person.getPosition());//new Rotation3D();
            Point2D cart = new Point2D(person.getPosition().getX(LengthUnit.METER), person.getPosition().getY(LengthUnit.METER), LengthUnit.METER);
            Rotation3D rota = new Rotation3D(new Vector3d(0, 1, 0), MathTools.cartesianToPolar(cart).getAngle(AngleUnit.RADIAN), AngleUnit.RADIAN);
            rot.serialize(rota, bodyBuilder.getOrientationBuilder());

            Point3DSerializer p = new Point3DSerializer();
            p.serialize(point, bodyBuilder.getLocationBuilder());
            //person.getGlobalLocation()
            builder.getTrackingInfoBuilder().setId(Integer.parseInt(person.getUuid()));

        }
    }

    @Override
    public Class<PersonHypotheses> getMessageType() {
        return PersonHypotheses.class;
    }

    @Override
    public Class<PersonDataList> getDataType() {
        return PersonDataList.class;
    }

    
}

package de.unibi.citec.clf.bonsai.rsb;


import de.unibi.citec.clf.bonsai.core.exception.TransformException;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.apache.log4j.Logger;

import rct.TransformReceiver;
import rct.TransformerFactory;
import rct.TransformerFactory.TransformerFactoryException;
import rsb.InitializeException;
import de.unibi.citec.clf.bonsai.util.CoordinateTransformer;
import de.unibi.citec.clf.btl.Transform;
import de.unibi.citec.clf.btl.data.geometry.Point3D;
import de.unibi.citec.clf.btl.data.geometry.Pose3D;
import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
import de.unibi.citec.clf.btl.data.navigation.PositionData;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import java.util.logging.Level;
import rct.TransformerException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.hri.PersonHypothesesType;

public class RsbRctCoordinateTransformer extends CoordinateTransformer {

    /**
     * Instance of the TransformerReceiver that is used.
     */
    private static TransformReceiver transformReceiver = null;

    /**
     * The log.
     */
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * Constructor.
     *
     * @throws TransformerFactoryException
     */
    public RsbRctCoordinateTransformer() throws InitializeException, TransformerFactoryException {

//        DefaultConverterRepository.getDefaultConverterRepository()
//                .addConverter(new ProtocolBufferConverter<>(
//                        FrameTransformCollectionType.FrameTransformCollection.getDefaultInstance()));

        if (transformReceiver == null) {
            transformReceiver = TransformerFactory.getInstance().createTransformReceiver();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("interrupt");
            }
        }
    }



    @Override
    public Transform lookup(String from, String to, long time) throws TransformException {
        logger.debug("lookup \"" + from + "\" -> \"" + to + "\"");
        try {
            rct.Transform rct = transformReceiver.lookupTransform(to, from, time);
            Transform t = new Transform(rct.getTransform(), rct.getFrameParent(), rct.getFrameChild(), time);
            return t;
        } catch (TransformerException ex) {
            throw new de.unibi.citec.clf.bonsai.core.exception.TransformException(from, to, time);
        }

    }

}
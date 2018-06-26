//package de.unibi.citec.clf.bonsai.rsb;
//
//
//
//import de.unibi.citec.clf.bonsai.core.exception.TransformException;
//import static org.junit.Assert.*;
//
//import javax.media.j3d.Transform3D;
//import javax.vecmath.AxisAngle4d;
//import javax.vecmath.Vector3d;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import de.unibi.citec.clf.btl.data.common.Timestamp;
//import de.unibi.citec.clf.btl.data.geometry.Point3D;
//import de.unibi.citec.clf.btl.data.geometry.Pose3D;
//import de.unibi.citec.clf.btl.data.geometry.Rotation3D;
//import de.unibi.citec.clf.btl.data.navigation.PositionData;
//import de.unibi.citec.clf.btl.units.AngleUnit;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//import rct.Transform;
//import rct.TransformPublisher;
//import rct.TransformType;
//import rct.TransformerException;
//import rct.TransformerFactory;
//import rct.TransformerFactory.TransformerFactoryException;
//import rsb.InitializeException;
//
//public class RsbRctCoordinateTransformerTest {
//
//	private static RsbRctCoordinateTransformer transformer;
//
//	@BeforeClass
//	public static void setup() throws TransformerFactoryException, TransformerException, InitializeException, InterruptedException {
//		BasicConfigurator.configure();
//		transformer = new RsbRctCoordinateTransformer();
//		TransformPublisher publisher = TransformerFactory.getInstance().createTransformPublisher("bonsaitest");
//		Transform3D affine = new Transform3D();
//		affine.setRotation(new AxisAngle4d(new Vector3d(1, 0, 0), Math.PI/2.0));
//		affine.setTranslation(new Vector3d(0, 2, 0));
//		Transform transform = new Transform(affine, "foo", "bar", System.currentTimeMillis());
//		publisher.sendTransform(transform, TransformType.STATIC);
//		Thread.sleep(200);
//	}
//
//	@Test
//	public void testTransformPoint3DString() throws TransformException {
//
//		Point3D original = new Point3D(-1, -3, 1, LengthUnit.METER);
//		original.setFrameId("foo");
//		Point3D transformed = transformer.transform(original, "bar");
//
//		assertEquals(-1.0, transformed.getX(LengthUnit.METER), 0.0001);
//		assertEquals(1.0, transformed.getY(LengthUnit.METER), 0.0001);
//		assertEquals(5.0, transformed.getZ(LengthUnit.METER), 0.0001);
//	}
//
//	@Test
//	public void testTransformRotation3DString() throws TransformException {
//		Rotation3D original = new Rotation3D(1, 0, 0, 0, AngleUnit.RADIAN);
//		original.setFrameId("foo");
//		Rotation3D transformed = transformer.transform(original, "bar");
//		assertEqualsMatrix(new Rotation3D(1,0,0, -Math.PI/2.0, AngleUnit.RADIAN),transformed);
//	}
//
//	private void assertEqualsMatrix(Rotation3D expected, Rotation3D actual) {
//		assertEquals(expected.getMatrix().m00, actual.getMatrix().m00, 0.0001);
//		assertEquals(expected.getMatrix().m01, actual.getMatrix().m01, 0.0001);
//		assertEquals(expected.getMatrix().m02, actual.getMatrix().m02, 0.0001);
//		assertEquals(expected.getMatrix().m10, actual.getMatrix().m10, 0.0001);
//		assertEquals(expected.getMatrix().m11, actual.getMatrix().m11, 0.0001);
//		assertEquals(expected.getMatrix().m12, actual.getMatrix().m12, 0.0001);
//		assertEquals(expected.getMatrix().m20, actual.getMatrix().m20, 0.0001);
//		assertEquals(expected.getMatrix().m21, actual.getMatrix().m21, 0.0001);
//		assertEquals(expected.getMatrix().m22, actual.getMatrix().m22, 0.0001);
//	}
//
//	@Test
//	public void testTransformPose3DString() throws TransformException {
//		Point3D trans = new Point3D(-1, -3, 1, LengthUnit.METER);
//		trans.setFrameId("foo");
//		Rotation3D rot = new Rotation3D(1, 0, 0, 0, AngleUnit.RADIAN);
//		rot.setFrameId("foo");
//		Pose3D original = new Pose3D(trans,rot);
//		original.setFrameId("foo");
//		Pose3D transformed = transformer.transform(original, "bar");
//
//		assertEquals(-1.0, transformed.getTranslation().getX(LengthUnit.METER), 0.0001);
//		assertEquals(1.0, transformed.getTranslation().getY(LengthUnit.METER), 0.0001);
//		assertEquals(5.0, transformed.getTranslation().getZ(LengthUnit.METER), 0.0001);
//		assertEqualsMatrix(new Rotation3D(1,0,0, -Math.PI/2.0, AngleUnit.RADIAN), transformed.getRotation());
//	}
//
//	@Test
//	public void testTransformPositionDataString() throws TransformException {
//		PositionData original = new PositionData(2.0,-2.0,0.0,new Timestamp(),LengthUnit.METER,AngleUnit.RADIAN);
//		original.setFrameId("foo");
//		Pose3D transformed = transformer.transform(original, "bar");
//
//		assertEquals(2.0, transformed.getTranslation().getX(LengthUnit.METER), 0.0001);
//		assertEquals(0.0, transformed.getTranslation().getY(LengthUnit.METER), 0.0001);
//		assertEquals(4.0, transformed.getTranslation().getZ(LengthUnit.METER), 0.0001);
//		assertEqualsMatrix(new Rotation3D(1,0,0, -Math.PI/2.0, AngleUnit.RADIAN), transformed.getRotation());
//	}
//
//}

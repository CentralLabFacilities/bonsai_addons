//package de.unibi.citec.clf.btl.rst.data.geometry;
//
//
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Test;
//
//import rsb.Event;
//import de.unibi.citec.clf.btl.data.geometry.Point3D;
//import de.unibi.citec.clf.btl.rst.RstTypeFactory;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//
//public class RstPoint3DTest {
//
//	static {
//		BasicConfigurator.configure();
//	}
//
//	@Test
//	public void selfCompatibility() throws Exception {
//
//		Point3D p0 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
//		Point3D p1 = new Point3D(1, 2, 3, LengthUnit.MILLIMETER);
//		Point3D p2 = new Point3D(0.1, 0.2, 0.3, LengthUnit.MILLIMETER);
//
//		assertTrue("equals() not working", p0.equals(p1));
//		assertTrue("equals() not working", p1.equals(p0));
//
//		assertFalse("equals() not working", p1.equals(p2));
//
//		assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
//				p2.getX(LengthUnit.METER) * 1000, 0.00001);
//		assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
//				p2.getY(LengthUnit.METER) * 1000, 0.00001);
//		assertEquals("Conversion not working", p2.getZ(LengthUnit.MILLIMETER),
//				p2.getZ(LengthUnit.METER) * 1000, 0.00001);
//		assertEquals("Conversion not working", p2.getX(LengthUnit.MILLIMETER),
//				p2.getX(LengthUnit.CENTIMETER) * 10, 0.00001);
//		assertEquals("Conversion not working", p2.getY(LengthUnit.MILLIMETER),
//				p2.getY(LengthUnit.CENTIMETER) * 10, 0.00001);
//		assertEquals("Conversion not working", p2.getZ(LengthUnit.MILLIMETER),
//				p2.getZ(LengthUnit.CENTIMETER) * 10, 0.00001);
//
//		Event e = RstTypeFactory.getInstance().createEvent(p0);
//		Point3D p3 = RstTypeFactory.getInstance().createType(e, Point3D.class);
//
//		assertTrue("serializing not working", p0.equals(p3));
//
//	}
//
//}

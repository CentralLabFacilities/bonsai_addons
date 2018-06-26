//
//
//package de.unibi.citec.clf.btl.rst.data.vision;
//
//
//
//import de.unibi.citec.clf.btl.data.vision1d.LaserData;
//import de.unibi.citec.clf.btl.rst.serializers.vision.LaserDataSerializer;
//import de.unibi.citec.clf.btl.units.AngleUnit;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Assert;
//import org.junit.Test;
//
//import rst.vision.LaserScanType;
//
///**
// *
// * @author alangfel
// */
//public class LaserScanSerializerTest {
//    static {
//        BasicConfigurator.configure();
//    }
//
//    @Test
//    public void selfCompatibility() throws Exception {
//        LaserDataSerializer lasSerializer = new LaserDataSerializer();
//
//        LaserData data = new LaserData();
//        double[] scanValues = new double[361];
//
//        for(int index = 0; index < scanValues.length; index++) {
//            scanValues[index] = (double)index/10;
//        }
//
//        data.setScanValues(scanValues, LengthUnit.METER);
//        data.setScanAngle(185.5, AngleUnit.RADIAN);
//
//        LaserScanType.LaserScan.Builder builder = LaserScanType.LaserScan.newBuilder();
//        lasSerializer.serialize(data, builder);
//
//        LaserScanType.LaserScan scan = builder.build();
//
//        LaserData after = lasSerializer.deserialize(scan);
//
//        Assert.assertEquals("Num of laser points not equal!", data.getNumLaserPoints(), after.getNumLaserPoints());
//
//        Assert.assertEquals("Value of scanAngle not equal!", data.getScanAngle(AngleUnit.RADIAN), after.getScanAngle(AngleUnit.RADIAN), 0.0);
//
//        for (int index = 0; index < after.getNumLaserPoints(); index++) {
//            Assert.assertEquals("Value not equal!", (double)index/10, after.getScanValues(LengthUnit.METER)[index], 0.0001);
//        }
//
//    }
//}

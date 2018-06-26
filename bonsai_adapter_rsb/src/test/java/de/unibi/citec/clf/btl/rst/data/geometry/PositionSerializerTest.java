//
//
//package de.unibi.citec.clf.btl.rst.data.geometry;
//
//
//
//import de.unibi.citec.clf.btl.data.navigation.PositionData;
//import de.unibi.citec.clf.btl.rst.serializers.geometry.PositionSerializer;
//import de.unibi.citec.clf.btl.units.AngleUnit;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//import junit.framework.Assert;
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Test;
//import rst.geometry.PoseType;
//
///**
// *
// * @author alangfel
// */
//public class PositionSerializerTest {
//
//    static {
//        BasicConfigurator.configure();
//    }
//
//    @Test
//    public void selfCompatibility() throws Exception {
//        PositionSerializer posSerializer = new PositionSerializer();
//        PositionData data = new PositionData();
//        //fill data
//        double x = 1.0;
//        double y = 2.0;
//        double yaw = 3.14;
//        data.setX(x, LengthUnit.METER);
//        data.setY(y, LengthUnit.METER);
//        data.setYaw(yaw, AngleUnit.RADIAN);
//
//        PoseType.Pose.Builder builder = PoseType.Pose.newBuilder();
//        posSerializer.serialize(data, builder);
//        PoseType.Pose pose = builder.build();
//
//        PositionData after = posSerializer.deserialize(pose);
//
//        Assert.assertEquals("Yaw not equal!", yaw, after.getYaw(AngleUnit.RADIAN), 0.0001);
//        Assert.assertEquals("X not equal!", x, after.getX(LengthUnit.METER), 0.0001);
//        Assert.assertEquals("Y not equal!", y, after.getY(LengthUnit.METER), 0.0001);
//    }
//
//}

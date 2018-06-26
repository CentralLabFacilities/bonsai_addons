//package de.unibi.citec.clf.btl.rst.data.navigation;
//
//
//
//import de.unibi.citec.clf.btl.data.common.Timestamp;
//import de.unibi.citec.clf.btl.data.navigation.GlobalPlan;
//import de.unibi.citec.clf.btl.data.navigation.NavigationGoalData;
//import de.unibi.citec.clf.btl.data.navigation.PositionData;
//import de.unibi.citec.clf.btl.rst.serializers.navigation.GlobalPlanSerializer;
//import de.unibi.citec.clf.btl.units.AngleUnit;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//import de.unibi.citec.clf.btl.units.TimeUnit;
//import junit.framework.Assert;
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Test;
//import rst.navigation.PathType;
//
//
//
///**
// *
// * @author alangfel
// */
//public class GlobalPlanSerializerTest {
//
//    static {
//        BasicConfigurator.configure();
//    }
//
//    @Test
//    public void selfCompatibility() throws Exception {
//        GlobalPlanSerializer globalPlanSerializer = new GlobalPlanSerializer();
//        GlobalPlan data = new GlobalPlan();
//        //fill data
//        NavigationGoalData goal;
//        PositionData pos;
//        for (int i = 0; i < 10; i++) {
//            pos = new PositionData(i, i, i, new Timestamp(0, TimeUnit.DAYS), LengthUnit.METER, AngleUnit.RADIAN);
//            goal = new NavigationGoalData(pos);
//            data.add(i, goal);
//        }
//
//        PathType.Path.Builder builder = PathType.Path.newBuilder();
//        globalPlanSerializer.serialize(data, builder);
//        PathType.Path path = builder.build();
//
//        GlobalPlan after = globalPlanSerializer.deserialize(path);
//
//        //tests
//        NavigationGoalData goalAfter;
//        for (int i = 0; i < 10; i++) {
//            goal = data.get(i);
//            goalAfter = after.get(i);
//            Assert.assertEquals("X is not equal!", goal.getX(LengthUnit.METER), goalAfter.getX(LengthUnit.METER));
//            Assert.assertEquals("Y is not equal!", goal.getY(LengthUnit.METER), goalAfter.getY(LengthUnit.METER));
//            Assert.assertEquals("Yaw is not equal!", goal.getYaw(AngleUnit.RADIAN), goalAfter.getYaw(AngleUnit.RADIAN));
//        }
//    }
//
//}

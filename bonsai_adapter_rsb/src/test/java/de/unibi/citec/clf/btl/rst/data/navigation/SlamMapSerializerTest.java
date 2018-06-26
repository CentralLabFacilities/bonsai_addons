//package de.unibi.citec.clf.btl.rst.data.navigation;
//
//
//
//import de.unibi.citec.clf.btl.data.map.BinarySlamMap;
//import de.unibi.citec.clf.btl.rst.serializers.navigation.SlamMapSerializer;
//import de.unibi.citec.clf.btl.units.LengthUnit;
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Assert;
//import org.junit.Test;
//import rst.navigation.OccupancyGrid2DIntType;
//
//
//
///**
// *
// * @author alangfel
// */
//public class SlamMapSerializerTest {
//
//    static {
//        BasicConfigurator.configure();
//    }
//
//    @Test
//    public void selfCompatibility() throws Exception {
//        SlamMapSerializer slam = new SlamMapSerializer();
//
//        int height = 3;
//        int width = 3;
//        double resolution = 1;
//        int originX = 0;
//        int originY = 0;
//        float[] slamMap = new float[9];
//
//        for(int i = 0; i<9; i++) {
//            slamMap[i] = i/100;
//        }
//
//        BinarySlamMap map = new BinarySlamMap();
//        map.setHeight(height);
//        map.setWidth(width);
//        map.setOriginX(originX);
//        map.setOriginY(originY);
//        map.setResolution(resolution, LengthUnit.METER);
//        map.setSlamMap(slamMap);
//
//        OccupancyGrid2DIntType.OccupancyGrid2DInt.Builder builder = OccupancyGrid2DIntType.OccupancyGrid2DInt.newBuilder();
//
//        slam.serialize(map, builder);
//
//        OccupancyGrid2DIntType.OccupancyGrid2DInt grid = builder.build();
//
//        BinarySlamMap after = slam.deserialize(grid);
//
//        Assert.assertEquals("Height not equal!", map.getHeight(), after.getHeight());
//        Assert.assertEquals("Width not equal!", map.getHeight(), after.getWidth());
//        Assert.assertEquals("Resolution not equal!", (int)map.getResolution(LengthUnit.METER), (int)after.getResolution(LengthUnit.METER));
//        Assert.assertEquals("OriginX not equal!", map.getOriginX(), after.getOriginX());
//        Assert.assertEquals("OriginY not equal!", map.getOriginY(), after.getOriginY());
//
//        for(int i = 0; i<after.getHeight() * after.getWidth(); i++) {
//            float val = after.getDynamicGridMap().getLinearMap()[i];
////            System.out.println("i " + i + " = " + val);
//            Assert.assertEquals( map.getDynamicGridMap().getLinearMap()[i], after.getDynamicGridMap().getLinearMap()[i], 0.0);
//        }
//
//
//    }
//
//}

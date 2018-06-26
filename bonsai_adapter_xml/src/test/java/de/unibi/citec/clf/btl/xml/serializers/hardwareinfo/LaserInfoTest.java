package de.unibi.citec.clf.btl.xml.serializers.hardwareinfo;


import de.unibi.citec.clf.btl.data.hardwareinfo.LaserInfo.OutOfRangeException;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

public class LaserInfoTest {

    @Test
    public void testGetScanNum() throws OutOfRangeException {
        BasicConfigurator.configure();

//		assertEquals(0, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, -Math.PI / 2.0));
//		assertEquals(360, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, Math.PI / 2.0));
//		assertEquals(180, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, 0));
//		
//		assertEquals(0, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, -Math.PI / 2.0 + 0.0001));
//		assertEquals(360, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, Math.PI / 2.0 - 0.0001));
//		
//		double nextAngle = -Math.PI / 2.0 + (0.5 * Math.PI / 180.0);
//		assertEquals(1, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, nextAngle));
//		assertEquals(1, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, nextAngle - 0.0001));
//		
//		double prevAngle = Math.PI / 2.0 - (0.5 * Math.PI / 180.0);
//		assertEquals(359, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, prevAngle));
//		assertEquals(359, LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, prevAngle + 0.0001));
//		
//		try {
//			LaserInfo.calcScanIndex(ScannerModel.SICK_LMS200, Math.PI);
//			fail();
//		} catch (Exception e) {
//			// every thing ok
//		}
    }
}

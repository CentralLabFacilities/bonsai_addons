

package de.unibi.citec.clf.btl.rst.serializers.vision;



import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.btl.data.vision1d.LaserData;
import de.unibi.citec.clf.btl.rst.RstSerializer;
import de.unibi.citec.clf.btl.units.AngleUnit;
import de.unibi.citec.clf.btl.units.LengthUnit;
import rst.vision.LaserScanType;


/**
 *
 * @author alangfel
 */
public class LaserDataSerializer extends RstSerializer<LaserData, LaserScanType.LaserScan>{

    @Override
    public LaserData deserialize(LaserScanType.LaserScan msg) throws DeserializationException {
        LaserData laserData = new LaserData();
        
        
        double[] scanValues = new double[msg.getScanValuesCount()];
        int index = 0;
        for(float scan : msg.getScanValuesList()) {
            scanValues[index] = (double) scan;
            index++;
        }
        
        laserData.setScanValues(scanValues, LengthUnit.METER);
        laserData.setScanAngle((double)msg.getScanAngle(), AngleUnit.RADIAN);
        
        return laserData;
    }

    @Override
    public void serialize(LaserData data, GeneratedMessage.Builder<?> abstractBuilder) throws SerializationException {
        LaserScanType.LaserScan.Builder builder = (LaserScanType.LaserScan.Builder)abstractBuilder;
        
        double[] scanValues = new double[data.getNumLaserPoints()];
        scanValues = data.getScanValues(LengthUnit.METER);
        for(int index = 0; index < data.getNumLaserPoints(); index++) {
            builder.addScanValues((float)scanValues[index]);
        }
        
        builder.setScanAngle((float)data.getScanAngle(AngleUnit.RADIAN));
        
        
    }

    @Override
    public Class<LaserScanType.LaserScan> getMessageType() {
        return LaserScanType.LaserScan.class;
    }

    @Override
    public Class<LaserData> getDataType() {
        return LaserData.class;
    }
    
}

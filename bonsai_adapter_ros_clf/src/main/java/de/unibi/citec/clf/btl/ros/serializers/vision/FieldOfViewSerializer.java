package de.unibi.citec.clf.btl.ros.serializers.vision;

import de.unibi.citec.clf.btl.ros.RosSerializer;
import org.ros.message.MessageFactory;
import sensor_msgs.CameraInfo;
import de.unibi.citec.clf.btl.data.vision2d.CameraAttributes;


public class FieldOfViewSerializer extends RosSerializer<CameraAttributes, sensor_msgs.CameraInfo>{
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FieldOfViewSerializer.class);

    @Override
    public CameraInfo serialize(CameraAttributes data, MessageFactory fact) throws SerializationException {
        return null;
    }

    @Override
    public CameraAttributes deserialize(CameraInfo msg) throws DeserializationException {
        CameraAttributes cam = new CameraAttributes();
        int resX = msg.getWidth();
        int resY = msg.getHeight();

        double[] kinverse = inverse(msg.getK());

        double[] twoD1 = {0.0, resY/2, 1.0};
        double[] twoD2 = {resX, resY/2, 1.0};

        double x1 = mul(kinverse, twoD1)[0];
        double x2 = mul(kinverse, twoD2)[0];

        cam.setFovH(Math.atan(Math.abs(x1-x2)));

        double[] twoD3 = {resX/2, 0.0, 1.0};
        double[] twoD4 = {resX/2, resY, 1.0};

        double y1 = mul(kinverse, twoD3)[0];
        double y2 = mul(kinverse, twoD4)[0];

        cam.setFovV(Math.atan(Math.abs(y1-y2)));

        return cam;
    }

    @Override
    public Class<CameraInfo> getMessageType() {
        return CameraInfo.class;
    }

    @Override
    public Class<CameraAttributes> getDataType() {
        return CameraAttributes.class;
    }

    // matrix (3x3 matrix aka double[9]) inversion
    private double[] inverse(double[] mat){
        assert mat.length == 9;
        double[] inverse = new double[9];

        double a = mat[0];
        double b = mat[1];
        double c = mat[2];

        double d = mat[3];
        double e = mat[4];
        double f = mat[5];

        double g = mat[6];
        double h = mat[7];
        double i = mat[8];
        double det = a*e*i + b*f*g + c*d*h - c*e*g - b*d*i - a*f*g;

        inverse[0] = e*i - f*h;
        inverse[1] = c*h - b*i;
        inverse[2] = b*f - c*e;
        inverse[3] = f*g - d*i;
        inverse[4] = a*i - c*g;
        inverse[5] = d*c - a*f;
        inverse[6] = d*h - e*g;
        inverse[7] = b*g - a*h;
        inverse[8] = a*e - d*b;

        for(int j = 0; j < inverse.length; j++){
            inverse[j] = inverse[j] / det;
        }

        return inverse;
    }

    // matrix multiplication with vector
    private double[] mul(double[] mat, double[] vec){
        assert mat.length == 9;
        assert vec.length == 3;

        double[] result = new double[3];

        for(int i = 0; i < result.length; i++){
            double val = 0.0;
            for(int j = 0; j < result.length; j++){
                val += vec[j] * mat[result.length * i + j];
            }
            result[i] = val;
        }

        return result;
    }
}

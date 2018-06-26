package de.unibi.citec.clf.btl.rst.helper;



import rst.generic.KeyValuePairType;
import rst.generic.ValueType;

/**
 * Created by lruegeme on 02.06.16.
 */
public class DictHelper {

    public static KeyValuePairType.KeyValuePair.Builder doubleValue(String key, float value) {

        return KeyValuePairType.KeyValuePair.newBuilder().setKey(key).setValue(ValueType.Value.newBuilder().setDouble(value).setType(ValueType.Value.Type.DOUBLE));
    }

}

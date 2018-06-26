package de.unibi.citec.clf.btl.rst;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.btl.Type;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RstSerializerRepository {

    private static Logger logger = Logger.getLogger(RstSerializerRepository.class);

    private static Map<Class<? extends Type>, RstSerializer<? extends Type, ? extends GeneratedMessage>> serializers = new HashMap<>();

    static {
        Reflections reflections = new Reflections("de.unibi.citec.clf.btl.rst.serializers");

        Set<Class<? extends RstSerializer>> allClasses = reflections.getSubTypesOf(RstSerializer.class);
        for (Class<? extends RstSerializer> c : allClasses) {
            try {
                RstSerializer s = c.newInstance();
                Class<? extends Type> dataType = s.getDataType();
                addSerializer(dataType, s);
            } catch (InstantiationException | SecurityException | ExceptionInInitializerError | IllegalAccessException e) {
                logger.error("Can not instantiate class " + c.getSimpleName());
                logger.debug("Can not instantiate class " + c.getSimpleName(), e);
            }
        }
    }

    public static <T extends Type, M extends GeneratedMessage, S extends RstSerializer<T, M>> void addSerializer(
            Class<T> baseType, S rstType) {
        serializers.put(baseType, rstType);
    }

    public static <T extends Type> RstSerializer<T, ? extends GeneratedMessage> getRstSerializer(Class<T> baseType) {
        return (RstSerializer<T, ? extends GeneratedMessage>) serializers.get(baseType);

    }
}

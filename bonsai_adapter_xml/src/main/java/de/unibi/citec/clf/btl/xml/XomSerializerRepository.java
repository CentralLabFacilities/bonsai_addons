package de.unibi.citec.clf.btl.xml;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XomSerializerRepository {

    private static Logger logger = Logger.getLogger(XomSerializerRepository.class);

    private static Map<Class<? extends Type>, XomSerializer<? extends Type>> serializers = new HashMap<>();
    private static Map<Class<? extends Type>, XomSerializer<? extends List<?>>> listSerializers = new HashMap<>();

    static {

        Set<Class<? extends XomSerializer>> exceptions = new HashSet<>();

        // EXEPTIONS
        Reflections reflections = new Reflections("de.unibi.citec.clf.btl.xml.serializers");

        Set<Class<? extends XomSerializer>> allClasses = reflections.getSubTypesOf(XomSerializer.class);
        for (Class<? extends XomSerializer> c : allClasses) {
            try {
                if (!exceptions.contains(c)) {
                    XomSerializer s = c.newInstance();
                    Class<? extends Type> dataType = s.getDataType();
                    addSerializer(dataType, s);
                }
            } catch (InstantiationException | SecurityException | ExceptionInInitializerError | IllegalAccessException e) {
                logger.error("Can not instantiate class " + c.getSimpleName());
                logger.debug("Can not instantiate class " + c.getSimpleName(), e);
            }
        }

        Set<Class<? extends XomListSerializer>> allListClasses = reflections.getSubTypesOf(XomListSerializer.class);
        for (Class<? extends XomListSerializer> c : allListClasses) {
            try {
                if (!exceptions.contains(c)) {
                    XomSerializer s = c.newInstance();
                    Class<? extends Type> dataType = s.getDataType();
                    addSerializer(dataType, s);
                }
            } catch (InstantiationException | SecurityException | ExceptionInInitializerError | IllegalAccessException e) {
                logger.error("Can not instantiate class " + c.getSimpleName());
                logger.debug("Can not instantiate class " + c.getSimpleName(), e);
            }
        }
    }

    public static <T extends Type, S extends XomSerializer<T>> void addSerializer(Class<T> baseType, S serializer) {
        if (List.class.isAssignableFrom(baseType) && XomListSerializer.class.isAssignableFrom(serializer.getClass())) {
            XomListSerializer<T, ?> listSerializer = (XomListSerializer<T, ?>) serializer;
            Class<? extends Type> key = listSerializer.getItemSerializer().getDataType();
            logger.debug("Add list serializer " + serializer.getClass().getSimpleName() + " for data type "
                    + key.getSimpleName() + " and list class " + baseType.getSimpleName());
            listSerializers.put(key, listSerializer);
        } else {
            logger.debug("Add serializer " + serializer.getClass().getSimpleName() + " for data type "
                    + baseType.getName());
            serializers.put(baseType, serializer);
        }
    }

    public static <T extends Type, S extends Type> XomSerializer<T> getSerializer(Class<T> baseType) {
        XomSerializer<T> s;
        if (List.class.isAssignableFrom(baseType)) {
            if (List.class.equals(baseType)) {
                logger.warn("Can not choose specific list serializer from arbitrary list class definition. Use method getListSerializer(...) instead.");
                s = null;
            } else {
                List<S> l;
                try {
                    l = (List<S>) baseType.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    l = null;
                    logger.error("Can not auto detect serializer for type " + baseType.getSimpleName()
                            + ". Use method createTypeList() instead!", e);
                }
                if (l == null)
                    s = null;
                else {
                    Class<S> elemType = l.getElementType();
                    s = (XomSerializer<T>) listSerializers.get(elemType);
                }
            }
        } else {
            s = (XomSerializer<T>) serializers.get(baseType);
        }
        if (s == null) {
            logger.debug("No serializer for data type " + baseType.getSimpleName() + " found!");
        }
        return s;
    }

    public static <T extends Type> XomListSerializer<?, ? extends List<T>> getListSerializer(Class baseType) {
        XomListSerializer<?, ? extends List<T>> s = (XomListSerializer<?, ? extends List<T>>) listSerializers
                .get(baseType);
        if (s == null) {
            logger.debug("No list serializer for data type " + baseType.getSimpleName() + " found!");
        }
        return s;
    }
}

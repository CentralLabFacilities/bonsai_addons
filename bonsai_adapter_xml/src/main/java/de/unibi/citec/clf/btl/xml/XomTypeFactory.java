package de.unibi.citec.clf.btl.xml;


import nu.xom.Document;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;

import de.unibi.citec.clf.btl.List;
import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.xml.XomSerializer.DeserializationException;
import de.unibi.citec.clf.btl.xml.XomSerializer.SerializationException;

/**
 * @author lziegler, jwienke
 */
public class XomTypeFactory {

    /**
     * Indicates whether the parsing should try to parse older xml
     * representations without generating errors.
     */
    private static Logger logger = Logger.getLogger(XomTypeFactory.class);
    private static XomTypeFactory inst;

    public static XomTypeFactory getInstance() {
        if (inst == null) {
            inst = new XomTypeFactory();
        }
        return inst;
    }

    /**
     * Every type should be default constructible.
     */
    private XomTypeFactory() {
    }

    /**
     * Parses the contents of the given document into a new instance of type.
     * 
     * @param <T>
     *            The type of the new instance.
     * @param doc
     *            document to get the contents of this type from
     * @param type
     *            Class object of the new type.
     * @return The new instance of type.
     * @throws DeserializationException
     * @throws ParsingException
     *             When parsing failed.
     */
    @SuppressWarnings("unchecked")
    public <T extends Type, S extends Type> T createType(Document doc, Class<T> type) throws DeserializationException {

        XomSerializer<T> rstSerializer;

        // check weather the data is a list type
        try {
            if (List.class.equals(type)) {
                logger.warn("Can not create specific List from arbitrary class definition. Use method createTypeList(...) instead.");
                throw new DeserializationException("Invalid list type class given. Can not create specific "
                        + "List from arbitrary class definition. Use method createTypeList(...) instead.");

            } else if (List.class.isAssignableFrom(type)) {
                List<S> l;
                try {
                    l = (List<S>) type.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new XomSerializer.DeserializationException("Can not auto detect serializer for type "
                            + type.getSimpleName() + ". Use method createTypeList() instead!", e);
                }
                Class<S> elemType = l.getElementType();
                if (elemType == null) {
                    logger.warn("Method getElementType() returned null.");
                    throw new DeserializationException(
                            "Invalid list type class given. Method getElementType() returned null.");
                }

                XomListSerializer<?, ?> listSerializer = saveGetListSerializer(elemType);
                logger.debug("createType: Chose list serializer " + listSerializer.getClass() + " for list type "
                        + l.getClass().getSimpleName() + " and data type " + l.getElementType().getSimpleName());
                rstSerializer = (XomSerializer<T>) listSerializer;
            } else {
                rstSerializer = saveGetSerializer(type);
                logger.debug("createType: Chose serializer " + rstSerializer.getClass().getSimpleName()
                        + " for data type " + type.getSimpleName());
            }

        } catch (SerializationException e) {
            throw new DeserializationException(e.getMessage(), e.getCause());
        }

        return rstSerializer.deserialize(doc);
    }

    /**
     * Parses the contents of the given document into a new instance of type.
     * 
     * @param <T>
     *            The type of the new instance.
     * @param doc
     *            document to get the contents of this type from
     * @param type
     *            Class object of the new type.
     * @return The new instance of type.
     * @throws DeserializationException
     * @throws ParsingException
     *             When parsing failed.
     */
    public <T extends Type> List<T> createTypeList(Document doc, final Class<T> type) throws DeserializationException {

        final XomSerializer<T> rstSerializer = XomSerializerRepository.getSerializer(type);
        XomListSerializer<T, List<T>> listSerializer = new XomListSerializer<T, List<T>>() {
            @Override
            public XomSerializer<T> getItemSerializer() {
                return rstSerializer;
            }

            @Override
            public List<T> getDefaultInstance() {
                return new List<>(type);
            }
        };
        if (rstSerializer != null) {
            return listSerializer.deserialize(doc);
        } else {
            String error = "No serializer for data type " + type.getSimpleName() + " found!";
            logger.error(error);
            throw new XomSerializer.DeserializationException(error);
        }
    }

    /**
     * Utility method that creates a xom {@link Document} from this instance.
     * 
     * @return document containing xml representation of this instance
     * @param type
     *            class object from the desired type.
     * @throws SerializationException
     */
    public <T extends Type> Document createDocument(T data) throws SerializationException {

        XomSerializer<T> rstSerializer = getGenericSerializer(data);

        return rstSerializer.serialize(data);
    }

    /**
     * Getter for the base tag of a specific data type.
     * @param type The type to get a base tag for.
     * @return The base tag of the given data type.
     * @throws SerializationException
     */
    public String getBaseTag(final Class<? extends Type> type) throws SerializationException {
        XomSerializer<? extends Type> serializer = getGenericSerializer(type);
        return serializer.getBaseTag();
    }

    @SuppressWarnings("unchecked")
    private <T extends Type> XomSerializer<T> getGenericSerializer(T data) throws SerializationException {

        Class<T> dataType = (Class<T>) data.getClass();
        XomSerializer<T> rstSerializer;

        // check weather the data is a list type
        if (List.class.isAssignableFrom(dataType)) {
            List<?> l = (List<?>) data;

            XomListSerializer<?, ?> listSerializer = saveGetListSerializer(l.getElementType());
            logger.debug("createDocument: Chose list serializer " + listSerializer.getClass() + " for list type "
                    + l.getClass().getSimpleName() + " and data type " + l.getElementType().getSimpleName());
            rstSerializer = (XomSerializer<T>) listSerializer;
        } else {
            rstSerializer = saveGetSerializer(dataType);
            logger.debug("createDocument: Chose serializer " + rstSerializer.getClass().getSimpleName()
                    + " for data type " + dataType.getSimpleName());
        }

        return rstSerializer;
    }

    @SuppressWarnings("unchecked")
    private <T extends Type> XomSerializer<T> getGenericSerializer(Class<T> dataType) throws SerializationException {

        XomSerializer<T> rstSerializer;

        // check weather the data is a list type
        if (List.class.isAssignableFrom(dataType)) {
            List<?> l;
            try {
                l = (List<?>) dataType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new XomSerializer.SerializationException(
                        "Can not determine element type of given List data type.", e);
            }

            XomListSerializer<?, ?> listSerializer = saveGetListSerializer(l.getElementType());
            logger.debug("createDocument: Chose list serializer " + listSerializer.getClass() + " for list type "
                    + l.getClass().getSimpleName() + " and data type " + l.getElementType().getSimpleName());
            rstSerializer = (XomSerializer<T>) listSerializer;
        } else {
            rstSerializer = saveGetSerializer(dataType);
            logger.debug("createDocument: Chose serializer " + rstSerializer.getClass().getSimpleName()
                    + " for data type " + dataType.getSimpleName());
        }

        return rstSerializer;
    }

    private <T extends Type> XomSerializer<T> saveGetSerializer(Class<T> dataType) throws SerializationException {
        XomSerializer<T> rstSerializer;
        logger.debug("Get serializer for data type " + dataType);
        rstSerializer = XomSerializerRepository.getSerializer(dataType);
        if (rstSerializer == null) {
            String error = "No serializer for data type " + dataType.getSimpleName() + " found!";
            logger.error(error);
            throw new XomSerializer.SerializationException(error);
        }

        return rstSerializer;
    }

    private <T extends Type> XomListSerializer<?, ?> saveGetListSerializer(Class<T> listType)
            throws SerializationException {
        logger.debug("Get list serializer for data type " + listType.getSimpleName());
        XomListSerializer<? extends Type, ?> listSerializer = XomSerializerRepository.getListSerializer(listType);
        // try to create fallback
        if (listSerializer == null) {
            logger.debug("No list serializer for data type " + listType.getSimpleName() + " found. Trying fallback.");
            listSerializer = createFallbackListSerializer(listType);
        }
        // if fallback fails too:
        if (listSerializer == null) {
            String error = "No list serializer for data type " + listType.getSimpleName() + " found!";
            logger.error(error);
            throw new XomSerializer.SerializationException(error);
        }
        return listSerializer;
    }

    private <T extends Type> XomListSerializer<T, List<T>> createFallbackListSerializer(final Class<T> type)
            throws SerializationException {
        final XomSerializer<T> itemSerializer = XomSerializerRepository.getSerializer(type);
        if (itemSerializer == null) {
            String error = "No serializer for data type " + type.getSimpleName() + " found!";
            logger.error(error);
            throw new XomSerializer.SerializationException(error);
        }
        logger.debug("Creating fallback list serializer for data type " + type.getSimpleName());
        return new XomListSerializer<T, List<T>>() {
            @Override
            public XomSerializer<T> getItemSerializer() {
                return itemSerializer;
            }

            @Override
            public List<T> getDefaultInstance() {
                return new List<>(type);
            }
        };
    }
}

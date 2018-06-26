package de.unibi.citec.clf.btl.rst;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import rsb.Event;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;

import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.units.TimeUnit;

public abstract class RstSerializer<T extends Type, M extends GeneratedMessage> {

    private Logger logger = Logger.getLogger(getClass());

    public Event serialize(T data) throws SerializationException {

        Class<M> msgType = getMessageType();

        try {

            // as the protobuf builder implementation is static and the
            // newBuilder() method as well, we need to use reflection to
            // receive a builder instance.
            Method newBuilderMethod = msgType.getMethod("newBuilder", new Class[] {});
            GeneratedMessage.Builder<?> abstractBuilder = (GeneratedMessage.Builder<?>) newBuilderMethod.invoke(null);

            serialize(data, abstractBuilder);

            Event event = new Event(getMessageType(), abstractBuilder.build());
            event.getMetaData().setUserInfo("generator", data.getGenerator());
            event.getMetaData().setUserTime("created", data.getTimestamp().getCreated(TimeUnit.MICROSECONDS));
            event.getMetaData().setUserTime("updated", data.getTimestamp().getUpdated(TimeUnit.MICROSECONDS));
            return event;

        } catch (SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
            throw new SerializationException("Can not serialize data with type " + data.getClass().getSimpleName()
                    + " and msg type " + msgType.getSimpleName(), e);
        }
    }

    public T deserialize(Event event) throws DeserializationException {
        try {
            @SuppressWarnings("unchecked")
            M msg = (M) event.getData();
            String gen = "unknown";
            if (event.getMetaData().hasUserInfo("generator")) {
                gen = event.getMetaData().getUserInfo("generator");
            }
            long createdMicro = 0;
            long updatedMicro = 0;
            if (event.getMetaData().hasUserTime("created")) {
                createdMicro = event.getMetaData().getUserTime("created");
            }
            if (event.getMetaData().hasUserTime("updated")) {
                updatedMicro = event.getMetaData().getUserTime("updated");
            }

            T t = deserialize(msg);
            t.setGenerator(gen);
            t.setTimestamp(new Timestamp(createdMicro, updatedMicro, TimeUnit.MICROSECONDS));
            return t;

        } catch (ClassCastException e) {
            String msg = "Class cast error. The given event does not contain "
                    + "data that can be cast to the message type defined. Event type: "
                    + event.getType().getSimpleName();
            logger.fatal(msg);
            logger.debug(msg, e);
            throw new DeserializationException(msg, e);
        }
    }

    public abstract T deserialize(M msg) throws DeserializationException;

    public abstract void serialize(T data, Builder<?> abstractBuilder) throws SerializationException;

    public abstract Class<M> getMessageType();

    public abstract Class<T> getDataType();

    public static class SerializationException extends Exception {
        private static final long serialVersionUID = -132203105128523928L;

        public SerializationException() {
            super();
        }

        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public SerializationException(String message) {
            super(message);
        }

        public SerializationException(Throwable cause) {
            super(cause);
        }
    }

    public static class DeserializationException extends Exception {
        private static final long serialVersionUID = 7350863188260012099L;

        public DeserializationException() {
            super();
        }

        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DeserializationException(String message) {
            super(message);
        }

        public DeserializationException(Throwable cause) {
            super(cause);
        }
    }
}

package de.unibi.citec.clf.btl.rst;



import org.apache.log4j.Logger;

import rsb.Event;

import com.google.protobuf.GeneratedMessage;

import de.unibi.citec.clf.btl.Type;
import de.unibi.citec.clf.btl.rst.RstSerializer.DeserializationException;
import de.unibi.citec.clf.btl.rst.RstSerializer.SerializationException;

/**
 * @author lziegler
 */
public class RstTypeFactory {

	private static Logger logger = Logger.getLogger(RstTypeFactory.class);
	private static RstTypeFactory inst;

	public static RstTypeFactory getInstance() {
		if (inst == null) {
			inst = new RstTypeFactory();
		}
		return inst;
	}

	/**
	 * Singleton Pattern.
	 */
	private RstTypeFactory() {

	}

	public <T extends Type> T createType(Event event, Class<T> dataType)
			throws DeserializationException {
		RstSerializer<T, ? extends GeneratedMessage> rstSerializer = RstSerializerRepository.getRstSerializer(dataType);
		if (rstSerializer != null) {
			return rstSerializer.deserialize(event);
		} else {
			String error = "No serializer for data type "
					+ dataType.getSimpleName() + " found!";
			logger.error(error);
			throw new DeserializationException(error);
		}
	}
        
        public <T extends Type, M extends GeneratedMessage> T createType(M msg, Class<T> dataType)
			throws DeserializationException {
		RstSerializer<T, M> rstSerializer = (RstSerializer<T,M>) RstSerializerRepository.getRstSerializer(dataType);
		if (rstSerializer != null) {
			return rstSerializer.deserialize(msg);
		} else {
			String error = "No serializer for data type "
					+ dataType.getSimpleName() + " found!";
			logger.error(error);
			throw new DeserializationException(error);
		}
	}

	public final <T extends Type> Event createEvent(T data)
			throws SerializationException {

		@SuppressWarnings("unchecked")
		Class<T> dataType = (Class<T>) data.getClass();
		RstSerializer<T, ? extends GeneratedMessage> rstSerializer = RstSerializerRepository.getRstSerializer(dataType);

		if (rstSerializer != null) {
			return rstSerializer.serialize(data);
		} else {
			String error = "No serializer for data type "
					+ dataType.getSimpleName() + " found!";
			logger.error(error);
			throw new SerializationException(error);
		}

	}
}

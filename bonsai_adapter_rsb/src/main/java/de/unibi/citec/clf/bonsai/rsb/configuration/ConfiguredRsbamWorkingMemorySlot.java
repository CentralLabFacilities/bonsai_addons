package de.unibi.citec.clf.bonsai.rsb.configuration;



import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.core.object.MemorySlot;
import de.unibi.citec.clf.bonsai.rsb.slots.RsbWorkingMemorySlot;

public class ConfiguredRsbamWorkingMemorySlot {

	private Class<?> dataType;
	private Class<? extends MemorySlot<?>> slotClass;

	public ConfiguredRsbamWorkingMemorySlot(Class<?> dataType,
			Class<? extends MemorySlot<?>> slotClass) {
		super();
		this.dataType = dataType;
		this.slotClass = slotClass;
	}

	/**
	 * @param dataType
	 *            requested configuration
	 * @return <code>true</code> if this configuration object is suitable for
	 *         the requested data type
	 */
	public boolean isSuitableFor(Class<?> dataType) {
		return this.dataType.isAssignableFrom(dataType);
	}

	public Class<? extends MemorySlot<?>> getSlotClass() {
		return slotClass;
	}

	/**
	 * Create a new specific actuator instance.
	 * 
	 * @return new instance
	 * @throws CoreObjectCreationException
	 *             problem creating the new instance
	 */
	@SuppressWarnings("unchecked")
	public <T> RsbWorkingMemorySlot<T> createInstance() {
		try {
			// create the slot
			return (RsbWorkingMemorySlot<T>) slotClass.newInstance();

		} catch (IllegalAccessException | InstantiationException e) {
			throw new CoreObjectCreationException("Can not create instance of "
					+ slotClass + ". Probabily it has no "
					+ "visible default constructor.s", e);
		}
    }
}

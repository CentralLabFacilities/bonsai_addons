package de.unibi.citec.clf.bonsai.rsb.slots;





import de.unibi.citec.clf.bonsai.rsb.RsbamWorkingMemory;
import de.unibi.citec.clf.bonsai.core.object.MemorySlot;

public interface RsbWorkingMemorySlot<T> extends MemorySlot<T> {
	
	/**
	 * Initialize the slot.
	 * @param memory The host memory
	 * @param slot The name of the slot
	 * @param dataType The data type handled by this slot
	 */
	void initialize(RsbamWorkingMemory memory, String slot, Class<? extends T> dataType);
}

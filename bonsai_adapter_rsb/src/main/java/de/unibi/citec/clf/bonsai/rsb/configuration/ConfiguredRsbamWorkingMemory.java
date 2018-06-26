package de.unibi.citec.clf.bonsai.rsb.configuration;



import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.core.object.SlotToConfigure;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemoryToConfigure;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemory;
import de.unibi.citec.clf.bonsai.rsb.RsbamWorkingMemory;
import de.unibi.citec.clf.bonsai.rsb.slots.RsbWorkingMemorySlot;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import rsb.InitializeException;

/**
 * A Working Memory implementation using the RSB Active Memory.
 *
 * @author lziegler
 */
public class ConfiguredRsbamWorkingMemory extends ConfiguredRsbWorkingMemory {

    private static Logger logger = Logger
            .getLogger(ConfiguredRsbamWorkingMemory.class);
    private Set<ConfiguredRsbamWorkingMemorySlot> slots = new HashSet<>();

    /**
     * Check if memory to configure can be created from this instance.
     *
     * @param memoryToConfigure requested configuration
     * @return <code>true</code> if this configuration object is suitable for
     * the requested memory
     */
    public static boolean isSuitableFor(
            WorkingMemoryToConfigure memoryToConfigure) {

        boolean suitable = memoryToConfigure.getMemoryClass().equals(
                RsbamWorkingMemory.class);

        Set<SlotToConfigure> slots = memoryToConfigure.getSlots();
        for (SlotToConfigure slot : slots) {
            if (!RsbWorkingMemorySlot.class
                    .isAssignableFrom(slot.getSlotClass())) {
                logger.warn("The configured slot \"" + slot + "\" can not "
                        + "be used by RsbWorkingMemory because it does not "
                        + "implement the RsbWorkingMemory Interface");
                suitable = false;
            }
        }

        return suitable;
    }

    /**
     * Create a new specific working memory instance.
     *
     * @return new instance
     * @throws CoreObjectCreationException problem creating the new instance
     */
    @Override
    public WorkingMemory createInstance() throws CoreObjectCreationException {
        try {
            logger.debug("Create RsbWorkingMemory instance with " + slots.size() + " memory slots.");
            return new RsbamWorkingMemory( slots);
        } catch (InitializeException ex) {
            throw new CoreObjectCreationException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getActiveMemoriesToInitialize() {
        return new String[]{getMemoryName()};
    }

    @Override
    public void configureMemorySlots(Set<SlotToConfigure> slots) {
        logger.debug("Configure " + slots.size() + " memory slots.");
        for (SlotToConfigure slot : slots) {
            logger.debug("Adding slot: " + slot);
            if (RsbWorkingMemorySlot.class.isAssignableFrom(slot.getSlotClass())) {
                this.slots.add(new ConfiguredRsbamWorkingMemorySlot(slot
                        .getDataTypeClass(), slot.getSlotClass()));
            } else {
                logger.error("Trying to configure slot \"" + slot + "\", "
                        + "which can not be used by RsbWorkingMemory because "
                        + "it does not implement the RsbWorkingMemory "
                        + "Interface");
            }
        }
    }
}

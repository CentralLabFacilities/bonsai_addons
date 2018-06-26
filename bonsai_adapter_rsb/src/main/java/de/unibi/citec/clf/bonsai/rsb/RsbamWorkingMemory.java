package de.unibi.citec.clf.bonsai.rsb;



import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.CommunicationException;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.core.exception.CoreObjectCreationException;
import de.unibi.citec.clf.bonsai.core.exception.InitializationException;
import de.unibi.citec.clf.bonsai.core.object.MemorySlot;
import de.unibi.citec.clf.bonsai.core.object.WorkingMemory;
import de.unibi.citec.clf.bonsai.rsb.configuration.ConfiguredRsbamWorkingMemorySlot;
import de.unibi.citec.clf.bonsai.rsb.slots.RsbWorkingMemorySlot;
import de.unibi.citec.clf.rsbam.MemoryInterface;
import de.unibi.citec.clf.rsbam.comm.rsb.RsbMemoryInterface;
import de.unibi.citec.clf.rsbam.comm.rsb.RsbMemoryRecord;
import de.unibi.citec.clf.rsbam.model.MemoryException;
import de.unibi.citec.clf.rsbam.model.MemoryRecord;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import rsb.InitializeException;
import rsb.transport.XOMXOP;
import rsb.transport.XOP;

public class RsbamWorkingMemory extends RsbNode implements WorkingMemory {

    /**
     * Option for publisher based sensors defining the memory name.
     */
    public static final String OPTION_MEMORY_NAME = "memoryName";
    /**
     * Instance of the active memory that is used.
     */
    private static MemoryInterface memory = null;
    /**
     * Name of the active memory.
     */
    private String memoryName = "";
    /**
     * Lock for synchronized memory access.
     */
    private static final Object memoryLock = new Object();
    /**
     * The log.
     */
    private Logger logger = Logger.getLogger(this.getClass());
    private Set<ConfiguredRsbamWorkingMemorySlot> slots;

    /**
     * Constructor.
     *
     * @param slots slot implementations to use
     */
    public RsbamWorkingMemory(Set<ConfiguredRsbamWorkingMemorySlot> slots) throws InitializeException {
        this.slots = slots;  
    }

    @Override
    public <T> MemorySlot<T> getSlot(String xpathPrefix, Class<T> dataType)
            throws CommunicationException, IllegalArgumentException,
            CoreObjectCreationException {

        // find suitable slot
        for (ConfiguredRsbamWorkingMemorySlot confSlot : slots) {
            if (confSlot.isSuitableFor(dataType)) {
                logger.debug("Creating \""
                        + confSlot.getSlotClass().getSimpleName()
                        + "\" for slot: " + xpathPrefix);
                RsbWorkingMemorySlot<T> slot = confSlot.createInstance();
                slot.initialize(this, xpathPrefix, dataType);

                return slot;
            }
        }

        logger.fatal("SLOTIMPL: ");
        slots.forEach(it -> {
            logger.fatal(" - " + it.getSlotClass());
        });

        throw new CoreObjectCreationException(
                "Can not create MemorySlot for data type: "
                + dataType.getSimpleName() + ". No applicable slot "
                + "implementation found in configuration.");
    }

    public void memorize(String slot, String baseTag, XOMXOP data) throws CommunicationException {
        try {
            synchronized (memoryLock) {
            	forgetUnlocked(slot, baseTag);
                memory.insert(new RsbMemoryRecord(new XOP(data)));
            }
        } catch (MemoryException ex) {
            throw new CommunicationException("MemoryException while inserting to rsbam. Maybe a timeout occured because memory is not running?", ex);
        }
    }
    
    private void forgetUnlocked(String slot, String baseTag) throws CommunicationException {
        try {
            memory.remove((slot + "/" + baseTag).replaceAll("//", "/"));
        } catch (MemoryException ex) {
            throw new CommunicationException("MemoryException while removing from rsbam. Maybe a timeout occured because memory is not running?", ex);
        }
    }

    public void forget(String slot, String baseTag) throws CommunicationException {
        synchronized (memoryLock) {
            forgetUnlocked(slot, baseTag);
        }
}

    public Set<MemoryRecord> recall(String slot) throws CommunicationException {
        Set<MemoryRecord> result;
        try {
            synchronized (memoryLock) {
                result = memory.query(slot);
            }
        } catch (MemoryException ex) {
            throw new CommunicationException(ex);
        }
        return result;
    }
    

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        memoryName = conf.requestValue(OPTION_MEMORY_NAME);
    }

    @Override
    public void startNode() throws InitializeException {
        if (memory == null) {
            memory = new RsbMemoryInterface(memoryName);
        }
    }

    @Override
    public void destroyNode() {
        //todo
    }
}

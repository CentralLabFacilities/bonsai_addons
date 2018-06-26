package de.unibi.citec.clf.btl.xml.serializers.status;



import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import de.unibi.citec.clf.btl.xml.XomSerializer;
import de.unibi.citec.clf.btl.data.status.RobotState;
import de.unibi.citec.clf.btl.data.status.RobotState.RobotStates;
/**
 * 
 * @author fsiepman
 */
public class RobotStateSerializer extends XomSerializer<RobotState> {

	@Override
	public Class<RobotState> getDataType() {
		return RobotState.class;
	}

	@Override
	public String getBaseTag() {
		return "ROBOTSTATE";
	}
	
	/**
	 * Constructs a {@link RobotStateSerializer} object from a given XOM {@link Element}.
	 * 
	 * @param objectElement
	 *            The XOM {@link Element} to construct an object from.
	 * @return The {@link RobotStateSerializer} object containing all the information
	 *         given by the {@link Element} object.
	 */
	@Override
	public RobotState doFromElement(Element element)
			throws ParsingException, DeserializationException {

		// Getting current state from element
		Element stateElement = element
				.getFirstChildElement(RobotState.CURRENT_STATE_ELEMENT_NAME);

		// Getting previous state from element
		Element previousStateElement = element
				.getFirstChildElement(RobotState.PREVIOUS_STATE_ELEMENT_NAME);

		// Getting state target
		Element stateTarget = element
				.getFirstChildElement(RobotState.STATE_TARGET_ELEMENT_NAME);

		if (stateElement == null) {
			throw new ParsingException("Unable to find element with name "
					+ RobotState.CURRENT_STATE_ELEMENT_NAME + ".");
		} else if (previousStateElement == null) {
			throw new ParsingException("Unable to find element with name "
					+ RobotState.PREVIOUS_STATE_ELEMENT_NAME + ".");
		} else if (stateTarget == null) {
			throw new ParsingException("Unable to find element with name "
					+ RobotState.STATE_TARGET_ELEMENT_NAME + ".");
		}

		String stateName = stateElement.getValue();
		if ((stateName == null) || stateName.trim().isEmpty()) {
			throw new ParsingException("No state found in element with name "
					+ RobotState.CURRENT_STATE_ELEMENT_NAME + ".");
		}

		String previousStateName = previousStateElement.getValue();
		if ((stateName == null) || stateName.trim().isEmpty()) {
			throw new ParsingException("No state found in element with name "
					+ RobotState.PREVIOUS_STATE_ELEMENT_NAME + ".");
		}

		String targetName = stateTarget.getValue();
		if ((targetName == null) || targetName.trim().isEmpty()) {
			throw new ParsingException("No state found in element with name "
					+ RobotState.STATE_TARGET_ELEMENT_NAME + ".");
		}

		RobotState rs = new RobotState();
		
		try {
			RobotStates current = RobotStates.valueOf(stateName);
			RobotStates previous = RobotStates.valueOf(previousStateName);
			rs.setCurrenState(current);
			rs.setPreviousState(previous);
			rs.setTargetName(targetName);

		} catch (IllegalArgumentException e) {
			throw new ParsingException("Unable to extract valid RobotState.");
		} catch (NullPointerException e) {
			throw new ParsingException(
					"Unable to extract valid RobotState. Don't you dare creating NPE's...");
		}
		return rs;
	}

	@Override
	public void doSanitizeElement(Element parent) {

		Nodes nodes = parent.query("CURRENTSTATE");
		for (int i = 0; i < nodes.size(); i++) {
			parent.removeChild(nodes.get(i));
		}

		nodes = parent.query("PREVIOUSSTATE");
		for (int i = 0; i < nodes.size(); i++) {
			parent.removeChild(nodes.get(i));
		}

		nodes = parent.query("STATETARGET");
		for (int i = 0; i < nodes.size(); i++) {
			parent.removeChild(nodes.get(i));
		}
	}

	/**
	 * Serializes the {@link RobotStateSerializer} object into a given XOM
	 * {@link Element}.
	 * 
	 * @param parent
	 *            The {@link Element} to serialize the object into. The given
	 *            {@link Element} object should have the base tag defined by
	 *            this class. (see {@link #getClass().getSimpleName()})
	 * @see #getClass().getSimpleName()
	 */
	@Override
	public void doFillInto(RobotState data, Element parent) throws SerializationException {	
		sanitizeElement(parent);

		Element stateElement = new Element(RobotState.CURRENT_STATE_ELEMENT_NAME);
		Element previousElement = new Element(RobotState.PREVIOUS_STATE_ELEMENT_NAME);
		Element targetElement = new Element(RobotState.STATE_TARGET_ELEMENT_NAME);
		stateElement.appendChild(data.getCurrenState().name());
		previousElement.appendChild(data.getPreviousState().name());
		targetElement.appendChild(data.getTargetName());
		parent.appendChild(stateElement);
		parent.appendChild(previousElement);
		parent.appendChild(targetElement);
		
	}
}

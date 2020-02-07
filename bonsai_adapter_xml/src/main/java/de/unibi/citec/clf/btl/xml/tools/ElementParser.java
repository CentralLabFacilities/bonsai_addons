package de.unibi.citec.clf.btl.xml.tools;



import de.unibi.citec.clf.btl.xml.UnexpectedElementFormatException;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

/**
 * Utility functions parsing XML elements
 * 
 * @author lschilli
 */
public class ElementParser {

	public static Element getFirstChildElement(final Node parentNode,
			final String childName) {

		Nodes nodes = parentNode.query(childName);
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof Element) {
				return (Element) nodes.get(i);
			}
		}

		throw new IllegalArgumentException("No Element " + childName
				+ " found in:\n" + parentNode.toXML() + "\nbase tag: "
				+ parentNode.toString());

	}

	public static String getAttributeValue(Element elem, String name) {
		String result = elem.getAttributeValue(name);
		if (result == null) {
			throw new UnexpectedElementFormatException("Element '"
					+ elem.getLocalName() + "' has no attribute '" + name + "'");
		}
		return result;
	}

	public static String getTagValue(Element elem) {
		if (elem.getChildCount() > 0 && elem.getChild(0) instanceof Text) {
			return elem.getChild(0).getValue();
		} else {
			throw new UnexpectedElementFormatException("Element '"
					+ elem.getLocalName() + "' has no text");
		}

	}

	private static String msgPrefix(Element elem, String name) {
		return "Element '" + elem.getLocalName() + "': Attribute '" + name
				+ "': value '" + getAttributeValue(elem, name) + "'";
	}

	public static boolean getBooleanAttributeValueUnchecked(Element elem,
			String name) {
		return "1".equals(elem.getAttributeValue(name));
	}

	public static boolean getBooleanAttributeValue(Element elem, String name) {
		String val = getAttributeValue(elem, name);
		boolean result = "1".equals(val) || "yes".equalsIgnoreCase(val)
				|| "true".equalsIgnoreCase(val);
		if (!"0".equals(val) && !"no".equalsIgnoreCase(val)
				&& !"false".equalsIgnoreCase(val) && !result) {
			throw new UnexpectedElementFormatException(msgPrefix(elem, name)
					+ "' is not 0, 1, yes, no, true or false");
		}
		return result;
	}

	public static int getIntAttributeValue(Element elem, String name) {
		try {
			return Integer.parseInt(getAttributeValue(elem, name));
		} catch (NumberFormatException e) {
			throw new UnexpectedElementFormatException(msgPrefix(elem, name)
					+ " is no int", e);
		}
	}

	public static long getLongAttributeValue(Element elem, String name) {
		try {
			return Long.parseLong(getAttributeValue(elem, name));
		} catch (NumberFormatException e) {
			throw new UnexpectedElementFormatException(msgPrefix(elem, name)
					+ " is no long", e);
		}
	}

	public static double getDoubleAttributeValue(Element elem, String name) {
		try {
			return Double.parseDouble(getAttributeValue(elem, name));
		} catch (NumberFormatException e) {
			throw new UnexpectedElementFormatException(msgPrefix(elem, name)
					+ " is no double", e);
		}
	}

}

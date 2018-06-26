package de.unibi.citec.clf.btl.xml;



/**
 * Indicates an unexpected value to parse an attribute or text from.
 * 
 * @author jwienke
 * @author unknown others
 */
public class UnexpectedElementFormatException extends RuntimeException {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 1L;

	public UnexpectedElementFormatException() {
	}

	public UnexpectedElementFormatException(String message) {
		super(message);
	}

	public UnexpectedElementFormatException(Throwable cause) {
		super(cause);
	}

	public UnexpectedElementFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}

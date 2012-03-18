package it.atcetera.jgett;

/**
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public class ShareNotFoundException extends Exception {

	/**
	 * Serialization unique identifier
	 */
	private static final long serialVersionUID = 5535338288142689495L;

	/**
	 * Used to generate a failure when a Ge.tt share is not found
	 * @param message A {@link String} containing a message for the user
	 * @param source An {@link Exception} that had generated this exception
	 */
	public ShareNotFoundException(String message, Throwable source) {
		super(message, source);
	}

	/**
	 * Used to generate a failure when a Ge.tt share is not found
	 * @param message A {@link String} containing a message for the user
	 */
	public ShareNotFoundException(String message) {
		super(message);
	}

	/**
	 * Used to generate a failure when a Ge.tt share is not found
	 * @param source An {@link Exception} that had generated this exception
	 */
	public ShareNotFoundException(Throwable source) {
		super(source);
	}
}

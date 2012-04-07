package it.atcetera.jgett;

/**
 * Exception used to signal a missing Ge.tt file
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public class FileNotFoundException extends Exception {

	/**
	 * Serialization unique identifier
	 */
	private static final long serialVersionUID = 6977179071580910376L;
	
	/**
	 * Used to generate a failure when a Ge.tt file is not found
	 * @param message A {@link String} containing a message for the user
	 * @param source An {@link Exception} that had generated this exception
	 */
	public FileNotFoundException(String message, Throwable source) {
		super(message, source);
	}

	/**
	 * Used to generate a failure when a Ge.tt file is not found
	 * @param message A {@link String} containing a message for the user
	 */
	public FileNotFoundException(String message) {
		super(message);
	}

	/**
	 * Used to generate a failure when a Ge.tt file is not found
	 * @param source An {@link Exception} that had generated this exception
	 */
	public FileNotFoundException(Throwable source) {
		super(source);
	}
	

}

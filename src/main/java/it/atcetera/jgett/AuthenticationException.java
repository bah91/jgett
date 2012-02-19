package it.atcetera.jgett;

/**
 * Exception raised on Ge.tt authentication failure
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public class AuthenticationException extends Exception {

	/**
	 * Serialization unique identifier
	 */
	private static final long serialVersionUID = -3130410769389498583L;

	/**
	 * Used to generate an authentication failure exception
	 * @param message A {@link String} containing a message for the user
	 * @param source An {@link Exception} that had generated this exception
	 */
	public AuthenticationException(String message, Throwable source) {
		super(message, source);
	}

	/**
	 * Used to generate an authentication failure exception
	 * @param message A {@link String} containing a message for the user
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
	 * Used to generate an authentication failure exception
	 * @param source An {@link Exception} that had generated this exception
	 */
	public AuthenticationException(Throwable source) {
		super(source);
	}
	
	

}

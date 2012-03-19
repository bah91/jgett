package it.atcetera.jgett;

/**
 * Ge.tt file upload status
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public enum ReadyState {
	
	/**
	 * File or Share is ready to use
	 */
	READY("ready"),

	/**
	 * File or Share has been declared but non uploaded yet
	 */
	REMOTE("remote"),

	/**
	 * File or Share has been removed
	 */
	REMOVED("removed"),

	/**
	 * File or Share is currently uploading
	 */
	UPLOADING("uploading"),
	
	/**
	 * File or Share has been uploaded
	 */
	UPLOADED("uploaded");
	
	/**
	 * Internal String representation of ready state value
	 */
	private final String value;
	
	/**
	 * Get the String representation of the Ready State value
	 * @return A {@link String} containing the Ready State value as obtained from Ge.tt API
	 */
	public String getValue(){
		return this.value;
	}
	
	
	@Override
	public String toString(){
		return this.getValue();
	}
	
	/**
	 * Default constructor
	 * @param value A {@link String} containing the Ready State value as obtained from Ge.tt API
	 */
	ReadyState(String value){
		this.value = value;
	}
	

}

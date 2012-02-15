package it.atcetera.jgett;

/**
 * Interface used to display information on user storage 
 * such as how many bytes is currently using and how many bytes are available on its space
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public interface StorageInfo {
	
	/**
	 * How many bytes are used by this user
	 * 
	 * @return A long that represents how many bytes are used by this user
	 */
	public long getUsedSpace();
	
	/**
	 * How many bytes are available for this user
	 * 
	 * @return A long that represents how many bytes are available for this user
	 */
	public long getLimitSpace();
	
	/**
	 * How many extra bytes has been granted to this user (they are already included into the limit space) as extra bonus space
	 * 
	 * @return A long that represents how many bytes has been granted for this user as extra bonus space
	 * @see StorageInfo#getLimitSpace()
	 */
	public long getExtraSpace();

}

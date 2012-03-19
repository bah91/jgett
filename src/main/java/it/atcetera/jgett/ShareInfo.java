package it.atcetera.jgett;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Interface used to display info on Ge.tt shares
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public interface ShareInfo {
	
	/**
	 * The unique name assigned from Ge.tt to this share
	 * @return A {@link String} containing the unique name assigned to this share
	 */
	public String getShareName();
	
	/**
	 * The title of this share 
	 * @return A {@link String} that represent the share title or <code>null</code> if this share has no title set
	 */
	public String getTitle();
	
	/**
	 * The date when this share has been created
	 * @return A {@link Date} that contains the sahre creation date
	 */
	public Date getCreationDate();
	
	/**
	 * A list of files associated to this share
	 * @return A {@link List} of {@link FileInfo} objects that states the files belonging to this share
	 */
	public List<FileInfo> getFiles();
	
	/**
	 * Where to find info about this Share on Ge.tt
	 * @return An {@link URL} that contains the Ge.tt service URI where this share is available
	 */
	public URL getUrl();	
	
	/**
	 * The status of this Share on Ge.tt 
	 * @return A {@link ReadyState} enum instance that states the status of this share (if it is deleted, for example)
	 */
	public ReadyState getReadyState();

}

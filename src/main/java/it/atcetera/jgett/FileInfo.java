package it.atcetera.jgett;

import java.net.URL;
import java.util.Date;

/**
 * Interface used to display info on Ge.tt uploaded files
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public interface FileInfo {
	
	/**
	 * The name of the file saved into Ge.tt system
	 * @return A {@link String} that contains the name of the file
	 */
	public String getFileName();
	
	/**
	 * The unique identifier of this file into Ge.tt system
	 * @return A {@link String} containing the file unique id
	 */
	public String getFileId();
	
	/**
	 * How many times this file has been downloaded
	 * @return An int containing the number of downloads of this file
	 */
	public int getNumberOfDownloads();
	
	/**
	 * The Ge.tt share which this file belong to
	 * @return A {@link ShareInfo} instance with the Ge.tt share which this file belong to
	 */
	public ShareInfo getShare();
	
	/**
	 * Upload status of this file
	 * @return A {@link ReadyState} enumeration that states the upload status of this file
	 */
	public ReadyState getReadyState();
	
	/**
	 * When this file has been created
	 * @return A {@link Date} that contains the creation date of this file
	 */
	public Date getCreationDate();
	
	/**
	 * Where to download this file
	 * @return An {@link URL} that contains the Ge.tt service URI where this file is available
	 */
	public URL getUrl();

}

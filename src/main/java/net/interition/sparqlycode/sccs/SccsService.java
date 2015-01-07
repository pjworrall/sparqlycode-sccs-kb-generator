package net.interition.sparqlycode.sccs;

import java.io.File;
import java.net.URI;

/**
 * @author pjworrall
 *
 */
public interface SccsService {
	
	/**
	 * 
	 * Creates a URI for an artefact using preset values for Global and Project parts
	 * 
	 * @return a string representations of the URI
	 */
	public String createArtefactUriAsString();
	
	/**
	 * 
	 * Creates a URI for an artefact using values provided in arguments
	 * 
	 * @param globalName
	 * @param projectName
	 * @return a string representations of the URI
	 */
	public String createArtefactUriAsString(String globalName, String projectName);
	
	/**
	 * @return a URI type
	 */
	public URI createArtefactUri();
	
	/**
	 * Creates a URI for an artefact using values provided in arguments
	 * 
	 * @param globalName
	 * @param projectName
	 * @return a URI type
	 */
	public URI createArtefactUri(String globalName, String projectName);
	
	public void publishSCforHead(File out) throws Exception;
	
	public void publishSCforBranch(String branchName, File out) throws Exception;
	
	public void publishSCforTag(String tagName, File out) throws Exception;

}

package net.interition.sparqlycode.sccs;

import java.io.File;
import java.net.URI;

/**
 * @author pjworrall
 *
 */
public interface SccsService {
	
	public void publishSCforHead(File out) throws Exception;
	
	public void publishSCforBranch(String branchName, File out) throws Exception;
	
	public void publishSCforTag(String tagName, File out) throws Exception;

}

package net.interition.sparqlycode.sccs;

import java.io.File;

/**
 * @author Paul Worrall
 *
 */
public interface SccsService {
	
	public void publishSCforHead(File out) throws Exception;
	
	public void publishSCforBranch(String branchName, File out) throws Exception;
	
	public void publishSCforTag(File out, String startTag, String endTag) throws Exception;

}

package net.interition.sparqlycode.sccs;

import java.io.File;
import java.util.List;

/**
 * @author Paul Worrall
 *
 */
public interface SccsService {
	
	public void publishSCforHead(File out, final List<String> sourceFolders) throws Exception;
	
	public void publishSCforBranch(File out, String branchName, final List<String> sourceFolders) throws Exception;
	
	public void publishSCforTag(File out, String startTag, String endTag, final List<String> sourceFolders) throws Exception;

}

package net.interition.sparqlycode.sccs.git;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.interition.sparqlycode.sccs.SccsService;

import org.junit.Test;

public class SccsServiceForGitTest {
	

	/**
	 * The technique used to test the publish should be to:
	 * Add the Sparqlycode generator to this projects build
	 * Use the Sparqlycode testsuite framework
	 * Use the git repo of the Sparqlycode testsuite for the SPARQL tests 
	 * @throws Exception 
	 */
	@Test
	public void testPublishSCforHead() throws Exception {
		
		SccsService service = new SccsServiceForGitImpl("net.interition.sparqlycode.testsuite",
				"/Users/pjworrall/Documents/Java2RDF/sparqlycode/sparqlycode-test-suite");
		
		String gitTtlLoc  = System.getProperty("sccs-ttl-loc");
		String gitTtlName = System.getProperty("sccs-ttl-name");
		
		String fullFileName = gitTtlLoc + File.separator + gitTtlName;	
		
		File file = new File(fullFileName);

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}
		
		service.publishSCforHead(file);
		
		// here we would need a test to check the existence of the rdf file and the integrity of its contents
		
		fail("Not yet implemented");
	}

	@Test
	public void testPublishSCforBranch() {
		fail("Not yet implemented");
	}

	@Test
	public void testPublishSCforTag() {
		fail("Not yet implemented");
	}

}

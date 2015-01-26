package net.interition.sparqlycode.sccs.git;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.interition.sparqlycode.sccs.SccsService;
import net.interition.sparqlycode.sccs.sparql.SparqlycodeBaseTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SccsServiceForGitTest extends SparqlycodeBaseTest {

	String baseDir;
	
	@Before
	public void setUp() throws Exception {
		
		this.baseDir = System.getProperty("baseDir");
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 * git log --pretty=oneline HEAD^^^^^..HEAD
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPublishSCforHead() throws Exception {

		File file = new File(baseDir + "/testPublishSCforHead.ttl");

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		SccsService service = new SccsServiceForGitImpl("sccs.git",
				baseDir);

		List<String> sourceFolders = new ArrayList<String>();

		sourceFolders.add(baseDir + "/src/main/java");

		service.publishSCforHead(file, sourceFolders, 5);
		
		assertTrue("SCCS KB failed equivelance test for git log --pretty=oneline HEAD^^^^^..HEAD",
				 sparqlyCodeTest("PublishSCForHeadTest"));
		
		// delete file ?

	}

	@Test
	public void testPublishSCforBranch() {
		fail("Not yet implemented");
	}
	
	/*
	 * 
	 * git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2
	 * 
	 * 
	 */

	@Test
	public void testPublishSCforTag() throws Exception {

		SccsService service = new SccsServiceForGitImpl("sccs.git", baseDir);

		String startTag = "refs/tags/0.0.2";
		String endTag = "refs/tags/0.0.1";

		File file = new File(baseDir + "/testPublishSCforTag.ttl");

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		service.publishSCforTag(file, startTag, endTag, new ArrayList<String>());

		// run any sparql tests
		
		assertTrue("SCCS KB failed equivelance test for git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2",
				 sparqlyCodeTest("PublishSCForTagTest"));

		// delete file ?


	}

}

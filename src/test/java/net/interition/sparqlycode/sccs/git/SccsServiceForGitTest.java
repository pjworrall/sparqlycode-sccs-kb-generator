package net.interition.sparqlycode.sccs.git;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.interition.sparqlycode.sccs.SccsService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SccsServiceForGitTest {

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

		fail("Not yet implemented");
	}

	@Test
	public void testPublishSCforBranch() {
		fail("Not yet implemented");
	}

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

		// delete file

		// assert
		fail("Not yet implemented");

	}

}

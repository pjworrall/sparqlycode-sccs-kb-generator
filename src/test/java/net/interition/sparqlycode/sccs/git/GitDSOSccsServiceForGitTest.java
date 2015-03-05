package net.interition.sparqlycode.sccs.git;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.interition.sparqlycode.sccs.SccsService;
import net.interition.sparqlycode.sccs.sparql.SparqlycodeBaseTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class GitDSOSccsServiceForGitTest extends SparqlycodeBaseTest {

	String baseDir;

	@Before
	public void setUp() throws Exception {

		baseDir = System.getProperty("baseDir");

		model = ModelFactory.createDefaultModel();
	}

	@After
	public void tearDown() throws Exception {
		model.close();
	}


	/*
	 * 
	 * git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2
	 */

	@Test
	public void testPublishSCforTag() throws Exception {

		SccsService service = new GitDSOSccsServiceForGitImpl("sccs.git", baseDir);

		String startTag = "refs/tags/0.0.2";
		String endTag = "refs/tags/0.0.1";

		// create a file for the KB output
		String fileName = "/gito4git.ttl";
		File file = new File(baseDir + fileName);

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		service.publishSCforTag(file, startTag, endTag, new ArrayList<String>());

		assertTrue(
				"SCCS KB failed equivelance test for git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2",
				sparqlyCodeTest("GITOBasicModelTest"));

	}

}

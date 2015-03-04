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

public class SccsServiceForGitTest extends SparqlycodeBaseTest {

	String baseDir;

	@Before
	public void setUp() throws Exception {

		baseDir = System.getProperty("baseDir");

		model = ModelFactory.createDefaultModel();
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
		// create a file for the KB output
		String fileName = "/testPublishSCforHead.ttl";

		File file = new File(baseDir + "/src/test/resources/" + fileName);

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		// create the knowledge model
		SccsService service = new W3CPROVOSccsServiceForGitImpl("sccs.git", baseDir);

		List<String> sourceFolders = new ArrayList<String>();
		sourceFolders.add(baseDir + "/src/main/java");
		service.publishSCforHead(file, sourceFolders, 5);

		// instantiate a Jena model so we can run a SPARQL query to check output

		URL kb = this.getClass().getResource(fileName);

		if (kb == null)
			throw new IOException(
					"Sparqlycode KB not found. Have you produced it yet?");

		// clear out any existing model and build an empty one
		model.close();
		model = ModelFactory.createDefaultModel();
		model.read(kb.toString());

		assertTrue(
				"SCCS KB failed equivelance test for git log --pretty=oneline HEAD^^^^^..HEAD",
				sparqlyCodeTest("PublishSCForHeadTest"));

		// delete file ?

	}

/*	@Test
	public void testPublishSCforBranch() {
		fail("Not yet implemented");
	}*/

	/*
	 * 
	 * git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2
	 */

	@Test
	public void testPublishSCforTag() throws Exception {

		SccsService service = new W3CPROVOSccsServiceForGitImpl("sccs.git", baseDir);

		String startTag = "refs/tags/0.0.2";
		String endTag = "refs/tags/0.0.1";

		// create a file for the KB output
		String fileName = "/testPublishSCforTag.ttl";
		File file = new File(baseDir + "/target/classes/" + fileName);

		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}

		service.publishSCforTag(file, startTag, endTag, new ArrayList<String>());

		// instantiate a Jena model so we can run a SPARQL query to check output

		URL kb = this.getClass().getResource(fileName);

		if (kb == null)
			throw new IOException(
					"Sparqlycode KB not found. Have you produced it yet?");

		// clear out any existing model and build an empty one
		model.close();
		model = ModelFactory.createDefaultModel();
		model.read(kb.toString());

		// run any sparql tests

		assertTrue(
				"SCCS KB failed equivelance test for git log --pretty=oneline refs/tags/0.0.1..refs/tags/0.0.2",
				sparqlyCodeTest("PublishSCForTagTest"));

		// delete file ?

	}

}

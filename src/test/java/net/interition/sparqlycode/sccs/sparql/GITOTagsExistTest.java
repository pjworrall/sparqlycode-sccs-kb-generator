 /**
 * 
 */
package net.interition.sparqlycode.sccs.sparql;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul Worrall, Interition Ltd
 *
 */
public class GITOTagsExistTest extends SparqlycodeBaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void gitOAuthorTest() throws Exception {
		
		assertTrue("GITOTagsExistTest didn't encounter any tags",
				 sparqlyCodeTest("GITOTagsExistTest"));
	}

}

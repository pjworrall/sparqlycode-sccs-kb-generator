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
public class GITOAuthorTest extends SparqlycodeBaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void gitOAuthorTest() throws Exception {
		
		assertTrue("GITOAuthorTest didn't encounter git:author relationships for commits",
				 sparqlyCodeTest("GITOCommitAuthorTest"));
	}

}

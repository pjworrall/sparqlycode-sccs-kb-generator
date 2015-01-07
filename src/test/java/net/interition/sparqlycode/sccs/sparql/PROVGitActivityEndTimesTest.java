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
public class PROVGitActivityEndTimesTest extends SparqlycodeBaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void uriLineNumberForFieldTest() throws Exception {
		
		assertTrue("PROVGitActivityEndTimesTest didn't encounter any instances of prov:endedAtTime prov:Activity",
				 sparqlyCodeTest("PROVGitActivityEndTimesTest"));
	}

}

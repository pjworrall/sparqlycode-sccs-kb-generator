
 
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
public class PROVGitActivityHasEntitiesTest extends SparqlycodeBaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void uriLineNumberForFieldTest() throws Exception {
		
		assertTrue("PROVGitActivityHasEntitiesTest didn't encounter any related prov:Entity for any commits",
				 sparqlyCodeTest("PROVGitActivityHasEntitiesTest"));
	}

}

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
public class PROVGitActivityAuthorTypeTest extends SparqlycodeBaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void uriLineNumberForFieldTest() throws Exception {
		
		assertFalse("PROVGitActivitAuthoryTypeTest didn't find any objects for prov:wasAssociatedWith that were prov:Person type",
				 sparqlyCodeTest("PROVGitActivityAuthorTypeTest"));
	}

}

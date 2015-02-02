package net.interition.sparqlycode.sccs.plugin;

import static org.junit.Assert.*;
import java.io.File;

import net.interition.sparqlycode.sccs.SccsMojo;

import org.junit.Test;

public class SccsMojoTest {  

	@Test
	public void getGitDirTest() {

		SccsMojo mojo = new SccsMojo();
		
		File gitFolder = mojo.getGitDir(new File("/Users/pjworrall/Documents/sparqlycode/sources/jena/jena-parent"));
		
		assertEquals(gitFolder.toString(), "/Users/pjworrall/Documents/sparqlycode/sources/jena");
		
		
	}

}

package net.interition.sparqlycode.sccs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.interition.sparqlycode.sccs.git.SccsServiceForGitImpl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Runs SCCS KB Publisher for containing project
 * 
 * The more contemporary Java 5 annotation options for maven-plugin-plugin won't
 * work with Maven 2 apparently so using the old Javadoc method.
 * 
 * @goal screpo
 */
public class SccsMojo extends AbstractMojo {

	/**
	 * Configuration parameters and default values
	 */

	/**
	 * 
	 * An informational message
	 * 
	 * @parameter expression="${screpo.message}"
	 *            default-value="Sparqlycode publishing SCCS KB"
	 */
	private Object message;

	/**
	 * Name of the output RDF file
	 * 
	 * @parameter expression="${screpo.outputfile}" default-value="unspecified"
	 */
	private Object outputfile;

	/**
	 * Any unique identifier to add into the minted Uri's
	 * 
	 * @parameter expression="${screpo.identifier}" default-value="unspecified"
	 */
	private Object identifier;

	/**
	 * The place where commits should start to be processed
	 * 
	 * @parameter expression="${screpo.startTag}" default-value="HEAD"
	 */
	private Object startTag;

	/**
	 * The predecessor commit where processing should stop (It processing from
	 * most recent backwards)
	 * 
	 * @parameter expression="${screpo.endTag}" default-value="HEAD^^^"
	 */
	private Object endTag;

	/** @parameter default-value="${project}" */
	private org.apache.maven.project.MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		getLog().debug(
				"Parameters - outputfile= " + outputfile
						+ " , uri identifier= " + identifier + " , message= "
						+ message + " , startTag= " + startTag + " , endTag= "
						+ endTag + ", sccs dir= " + project.getBasedir());

		getLog().info(message.toString());

		// this is the identifier to appear in the minted Uri
		String id = identifier.toString();
		// the root of the project where .git will exist
		String directory = project.getBasedir().toString();
		String filename = outputfile.toString();
		String start = startTag.toString();
		String end = endTag.toString();

		SccsService service;
		try {
			service = new SccsServiceForGitImpl(id, directory);
		} catch (Exception e) {
			throw new MojoExecutionException("Srarting the SCCS KB service failed",e);
		}

		File file = new File(filename);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			getLog().error("Error opening file for output.", e);
			return;
		}
		
		// we need the source code roots relative to the project base directory
		List<String> roots = this.getSourceFolderRoots();
		
		try {
			getLog().info(
					"calling  service.publishSCforTag() with " + file + " , "
							+ start + " , " + end);
			service.publishSCforTag(file, start, end, roots);
		} catch (Exception e) {
			getLog().error("Error encountered publishing SC", e);
			return;
		}

	}
	
	/**
	 * 
	 * The SCCS will have file paths relative to the base of the project.
	 * Java will resolve packages and source code files relative to source code folders.
	 * The SCCS API needs to know the source code roots relative to the project base.
	 * Source code roots include project and test folder roots
	 * This works them out and returns them in a list (as there can be more than one source code root)
	 * 
	 * @return List<String>
	 */
	
	private List<String> getSourceFolderRoots() {
		@SuppressWarnings("rawtypes")
		
		// create a mask using the absolute folder with a trailing /
		
		// Build for project source code roots
		
		String mask = project.getBasedir().toString() + "/";
		List<String> roots = new ArrayList<String>();
		for(Object sourceAbsoluteRoot : project.getCompileSourceRoots() ) {			
			String relativeSourceRoot = sourceAbsoluteRoot.toString().replace(mask,"");
			getLog().debug("project source root: " + sourceAbsoluteRoot.toString() + " , relative root: " + relativeSourceRoot);
			roots.add(relativeSourceRoot);
		}
		
		// .. add test source code roots
		
		for(Object sourceAbsoluteRoot : project.getTestCompileSourceRoots()) {			
			String relativeSourceRoot = sourceAbsoluteRoot.toString().replace(mask,"");
			getLog().debug("test source root: " + sourceAbsoluteRoot.toString() + " , relative root: " + relativeSourceRoot);
			roots.add(relativeSourceRoot);
		}
			
		return roots;
	}
}
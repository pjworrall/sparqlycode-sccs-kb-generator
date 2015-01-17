package net.interition.sparqlycode.sccs;

import java.io.File;
import java.io.IOException;

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
	 * @parameter expression="${screpo.identifer}" default-value="unspecified"
	 */
	private Object identifer;

	/**
	 * The place where commits should start to be processed
	 * 
	 * @parameter expression="${screpo.identifer}" default-value="HEAD"
	 */
	private Object startTag;

	/**
	 * The predecessor commit where processing should stop (It processing from
	 * most recent backwards)
	 * 
	 * @parameter expression="${screpo.identifer}" default-value="HEAD^^^"
	 */
	private Object endTag;

	/** @parameter default-value="${project}" */
	private org.apache.maven.project.MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		getLog().debug(
				"Parameters - outputfile= " + outputfile
						+ " , uri identifier= " + identifer + " , message= "
						+ message + " , startTag= " + startTag + " , endTag= "
						+ endTag + ", sccs dir= " + project.getBasedir());

		getLog().info(message.toString());

		// this is the identifier to appear in the minted Uri
		String id = identifer.toString();
		// the root of the project where .git will exist
		String directory = project.getBasedir().toString();
		String filename = outputfile.toString();
		String start = startTag.toString();
		String end = endTag.toString();

		SccsService service = new SccsServiceForGitImpl(id, directory);

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

		try {
			getLog().info(
					"calling  service.publishSCforTag() with " + file + " , "
							+ start + " , " + end);
			service.publishSCforTag(file, start, end);
		} catch (Exception e) {
			getLog().error("Error encountered publishing SC", e);
			return;
		}

	}
}
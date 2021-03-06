package net.interition.sparqlycode.sccs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.interition.sparqlycode.sccs.git.GitDSOSccsServiceForGitImpl;
import net.interition.sparqlycode.sccs.git.W3CPROVOSccsServiceForGitImpl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Runs SCCS KB Publisher for containing project
 * 
 * The more contemporary Java 5 annotation options for maven-plugin-plugin won't
 * work with Maven 2 apparently so using the old Javadoc method.
 * 
 * @goal screpo
 * @phase generate-sources
 * @aggregator true
 * @requiresDirectInvocation false
 * 
 */
public class SccsMojo extends AbstractMojo {

	/**
	 * Configuration parameters and default values
	 */
	/**
	 * @parameter expression="${reactorProjects}"
	 * @readonly
	 */
	private List<MavenProject> reactorProjects;

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

	/**
	 * The Ontology to use: W3C PROV or Interitions GITO
	 * 
	 * @parameter expression="${screpo.ontology}" default-value="GITO"
	 */
	private Object ontology;

	/** @parameter default-value="${project}" */
	private org.apache.maven.project.MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		String gitDir = getGitDir(project.getBasedir()).toString();

		getLog().debug(getParameterReport());
		getLog().debug("gitDir = " + gitDir);

		getLog().info(message.toString());

		// this is the identifier to appear in the minted Uri
		String id = identifier.toString();
		// the root of the project where .git will exist
		String directory = gitDir;
		String filename = outputfile.toString();
		String start = startTag.toString();
		String end = endTag.toString();
		String ont = ontology.toString();

		SccsService service;

		try {
			if ("GITO".equalsIgnoreCase(ont)) {
				service = new GitDSOSccsServiceForGitImpl(id, directory); 
			} else if ("PROVO".equalsIgnoreCase(ont)) {
				service = new W3CPROVOSccsServiceForGitImpl(id, directory);
			} else {
				throw new MojoExecutionException("Unrecognised Ontology");
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Starting an " + ont
					+ " SCCS KB service failed", e);
		}

		File file = new File(filename);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			throw new MojoExecutionException("Starting an " + ont
					+ " SCCS KB service failed", e);
		}

		// we need the source code roots relative to the git base directory
		List<String> roots = this.getSourceFolderRoots(gitDir);

		getLog().debug(roots.toString());

		try {
			getLog().info(
					"processing " + file + ", from " + start + ", to " + end);

			service.publishSCforTag(file, start, end, roots);
		} catch (Exception e) {
			throw new MojoExecutionException(ont + " SCCS KB service failed", e);
		}

	}

	/**
	 * For both single and multi module projects determine the source folder
	 * routes This is needed for the SCCS API.
	 * 
	 * 
	 * @return List<String>
	 */

	private List<String> getSourceFolderRoots(String gitDir) {

		// if the project is multi module then iterate through and concatenate
		// the list
		// else just process this project

		List<String> sourceFolders = new ArrayList<String>();

		if ("POM".equalsIgnoreCase(project.getPackaging())) {
			getLog().debug(
					"Multi module project, packaging: "
							+ project.getPackaging()
							+ "No modules in reactor: "
							+ reactorProjects.size());

			for (MavenProject module : reactorProjects) {
				getLog().info(
						"module: " + module.getName() + " baseDir: "
								+ module.getBasedir().toString());
				sourceFolders.addAll(getSourceFolderRootsForProject(gitDir,
						module));
			}
		} else {
			getLog().debug(
					"Not a multi module project, packaging: "
							+ project.getPackaging());
			sourceFolders = getSourceFolderRootsForProject(gitDir, project);
		}

		return sourceFolders;

	}

	/**
	 * 
	 * The SCCS will have file paths relative to the base of the project. Java
	 * will resolve packages and source code files relative to source code
	 * folders. The SCCS API needs to know the source code roots relative to the
	 * project base. Source code roots include project and test folder roots
	 * This works them out and returns them in a list (as there can be more than
	 * one source code root)
	 * 
	 * @return List<String>
	 */

	private List<String> getSourceFolderRootsForProject(String gitDir,
			MavenProject module) {

		// create a mask using the absolute folder with a trailing /
		String mask = gitDir + "/";

		getLog().debug("getting source file roots... mask: " + mask);

		// Build for project source code roots

		List<String> roots = new ArrayList<String>();
		for (Object sourceAbsoluteRoot : module.getCompileSourceRoots()) {
			String relativeSourceRoot = sourceAbsoluteRoot.toString().replace(
					mask, "");
			getLog().debug(
					"module source root: " + sourceAbsoluteRoot.toString()
							+ " , relative root: " + relativeSourceRoot);
			roots.add(relativeSourceRoot);
		}

		// .. add test source code roots

		for (Object sourceAbsoluteRoot : module.getTestCompileSourceRoots()) {
			String relativeSourceRoot = sourceAbsoluteRoot.toString().replace(
					mask, "");
			getLog().debug(
					"test source root: " + sourceAbsoluteRoot.toString()
							+ " , relative root: " + relativeSourceRoot);
			roots.add(relativeSourceRoot);
		}

		return roots;

	}

	/**
	 * 
	 * Determines the .git location using the projects base dir and then
	 * navigating up the folder hierarchy until a .git folder is found.
	 * 
	 * @param project
	 * @return
	 */
	public File getGitDir(File baseDir) {

		if (baseDir == null) {
			throw new RuntimeException(
					"Could not determine projects directory location");
		}

		while (!containsGitFolder(baseDir)) {
			if ((baseDir = baseDir.getParentFile()) == null) {
				throw new RuntimeException(
						"No .git folder found in folder hierarchy");
			}
		}

		return baseDir;

	}

	private boolean containsGitFolder(final File directory) {

		boolean gitFolder = false;

		if (!directory.isDirectory()) {
			throw new RuntimeException(
					"Maven baseDir unexpectedly not a directory");
		} else {
			for (File file : directory.listFiles()) {
				if (".git".equals(file.getName())) {
					gitFolder = true;
					break;
				}
			}
		}

		return gitFolder;
	}

	private String getParameterReport() {
		StringBuffer msg = new StringBuffer();
		msg.append("mojo parameters..\n");
		msg.append("outputfile= " + outputfile + "\n");
		msg.append("uri identifier= " + identifier + "\n");
		msg.append("message= " + message + "\n");
		msg.append("startTag= " + startTag + "\n");
		msg.append("endTag= " + endTag + "\n");
		msg.append("ontology = " + ontology + "\n");

		return msg.toString();

	}

}
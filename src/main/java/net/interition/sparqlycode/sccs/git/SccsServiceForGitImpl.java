package net.interition.sparqlycode.sccs.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import net.interition.sparlycode.model.PROVO;
import net.interition.sparqlycode.sccs.SccsService;

/**
 * @author pjworrall
 * 
 *         This class needs to be immutable so the arguments should be set by
 *         the constructor and not from setters
 * 
 *         I think that some behaviour needs to be abstracted and put into a
 *         superclass before we build the subversion publisher
 * 
 */
public class SccsServiceForGitImpl implements SccsService {

	private final Log logger = LogFactory.getLog(SccsServiceForGitImpl.class);

	private String prefix;
	private String sccsProjectRootFolder;

	public SccsServiceForGitImpl(String project, String folder) {
		this.sccsProjectRootFolder = folder;
		buildPrefix("http://www.interition.net/sccs", "git", project);
	}

	// create an empty Jena Model
	private Model model = ModelFactory.createDefaultModel();

	public String createArtefactUriAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String createArtefactUriAsString(String globalName,
			String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI createArtefactUri() {
		// TODO Auto-generated method stub
		return null;
	}

	public URI createArtefactUri(String globalName, String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void publishSCforHead(File out) throws Exception {
		try {
			generateRDF(out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	public void publishSCforBranch(String branchName, File out) {
		// TODO Auto-generated method stub

	}

	public void publishSCforTag(String tagName, File out) {
		// TODO Auto-generated method stub

	}

	private void generateRDF(File out) throws Exception {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = builder
					.setGitDir(new File(this.sccsProjectRootFolder + "/.git"))
					.readEnvironment() // scan environment GIT_* variable
					.findGitDir() // scan up the file system tree
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

		Git git = new Git(repository);
		Iterable<RevCommit> log;
		try {
			log = git.log().call();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

		for (RevCommit commit : log) {
			// create an RDF Resource for a Commit
			Resource commitResource = model.createResource(prefix + "commit/"
					+ commit.getName());

			// add a property (nothing appears in the model until a property is
			// applied)
			commitResource.addProperty(RDFS.label,
					model.createTypedLiteral(commit.getName()));

			// declare the commit as a prov:Activity type
			commitResource.addProperty(RDF.type, PROVO.Activity);

			// create an association with each controlled artefact (file)
			RevTree tree = commit.getTree();

			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			while (treeWalk.next()) {
				
				// make each file a prov:Entity
				Resource fileResource = model.createResource(prefix + treeWalk.getPathString());
				fileResource.addProperty(RDF.type, PROVO.Entity);
				
				// relate the prov:Entity to the commit
				commitResource.addProperty(PROVO.used, fileResource);
				
				DateTime dt = new DateTime(commit.getCommitTime() * 1000);
				
				DateTimeFormatter formater = new DateTimeFormatterBuilder()
				 .appendYear(4,4)
				 .appendLiteral('-')
			     .appendMonthOfYear(2)
			     .appendLiteral('-')
			     .appendDayOfMonth(2)
			     .appendLiteral('T')
			     .appendHourOfDay(2)
			     .appendLiteral(':')
			     .appendMinuteOfHour(2)
			     .appendLiteral(':')
			     .appendSecondOfMinute(2)
			     .toFormatter();
				
				String date = dt.toString(formater);
								
				// need to find out how to specify type like time..this isn't correct
				commitResource.addProperty(PROVO.endedAtTime, model.createTypedLiteral(date, "xsd:dateTime"));
				
			}

		}

		repository.close();

		writeRdf(model, out);

	}

	/**
	 * 
	 * Builds the prefix URI for all subjects
	 * 
	 * @return
	 */
	private String buildPrefix(String global, String sccs, String project) {

		return this.prefix = global + "/" + sccs + "/" + project.replace('.', '/') + "/";

	}

	/**
	 * 
	 * Writes out the RDF as Turtle
	 * 
	 * This is a common activity resulting in duplication. SHould find some
	 * utility pattern for this and factor it.
	 * 
	 * @param model
	 * @throws Exception
	 */
	private void writeRdf(Model model, File out) throws Exception {
		try {

			FileWriter fw = new FileWriter(out.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			model.write(bw, "TURTLE");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error writing out turtle file ");
		}
	}

}

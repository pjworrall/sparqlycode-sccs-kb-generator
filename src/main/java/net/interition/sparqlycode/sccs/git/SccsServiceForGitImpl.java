package net.interition.sparqlycode.sccs.git;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;

import net.interition.sparlycode.model.PROVO;
import net.interition.sparqlycode.sccs.SccsService;

/**
 * 
 * @author Paul Worrall
 * 
 */
public class SccsServiceForGitImpl extends RDFServices implements SccsService {

	private final Log logger = LogFactory.getLog(SccsServiceForGitImpl.class);

	private String commitPrefix = "http://www.interition.net/sccs/";
	private String filePrefix = "file://www.interition.net/sparqlycode/";
	private final DateTimeFormatter formater = new DateTimeFormatterBuilder()
			.appendYear(4, 4).appendLiteral('-').appendMonthOfYear(2)
			.appendLiteral('-').appendDayOfMonth(2).appendLiteral('T')
			.appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2)
			.appendLiteral(':').appendSecondOfMinute(2)
			.appendTimeZoneOffset(null, true, 3, 3).toFormatter();

	// create an empty Jena Model
	private Model model = ModelFactory.createDefaultModel();

	private Repository repository = null;

	public SccsServiceForGitImpl(String projectIdentifier, String folder)
			throws Exception {

		// instantiate the git repository
		FileRepositoryBuilder builder = new FileRepositoryBuilder();

		try {
			repository = builder.setGitDir(new File(folder + "/.git")).build();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		// build RDF prefix's
		buildPrefix("git", projectIdentifier);

	}

	/*
	 * Produces SC SCCS KB from the current Git HEAD back X commits
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.interition.sparqlycode.sccs.SccsService#publishSCforHead(java.io.
	 * File)
	 */
	public void publishSCforHead(File out, final List<String> sourceFolders,
			int depth) throws Exception {

		RevWalk walk = new RevWalk(repository);
		ObjectId from = repository.resolve("HEAD");
		
		if(from == null) {
			throw new Exception("HEAD did not exist");
		}
		
		String sDepth = from.getName() + '~' + String.valueOf(depth);
		
		ObjectId to = repository.resolve(sDepth);

		if(to == null) {
			throw new Exception(sDepth +  " did not exist");
		}
		
		logger.debug("walk from: HEAD (" + from.getName() + ") to " + sDepth);

		walk.markStart(walk.parseCommit(from));
		walk.markUninteresting(walk.parseCommit(to));

		for (RevCommit commit : walk) {
			// create an RDF Resource for a Commit
			Resource commitResource = model.createResource(commitPrefix
					+ commit.getName(), PROVO.Activity);

			// add a property (nothing appears in the model until a property is
			// applied)
			commitResource.addProperty(RDFS.label, commit.getShortMessage());

			// create the relationships between the commit and the files
			relateCommitsToArtifacts(commit, commitResource, sourceFolders);

		}

		walk.dispose();

		writeRdf(model, out);

	}

	/*
	 * Publishes SC SCCS KB for all Git commits from the head of a branch to
	 * when it was branched
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.interition.sparqlycode.sccs.SccsService#publishSCforBranch(java.lang
	 * .String, java.io.File)
	 */
	public void publishSCforBranch(File out, String branchName,
			final List<String> sourceFolders) {
		// TODO Auto-generated method stub

	}

	public void publishSCforTag(File out, String startTag, String endTag,
			final List<String> sourceFolders) throws Exception {

		// to use the tag ranges there is going to have to be some checks that
		// they are valid!!!

		RevWalk walk = new RevWalk(repository);

		Ref from = repository.getRef(startTag);
		if(from == null) {
			throw new Exception(startTag + " did not exist");
		}
		
		Ref to = repository.getRef(endTag);
		
		if(to == null) {
			throw new Exception(endTag + " did not exist");
		}

		logger.debug("walk from: " + startTag + " , to " + endTag);

		walk.markStart(walk.parseCommit(from.getObjectId()));
		walk.markUninteresting(walk.parseCommit(to.getObjectId()));

		for (RevCommit commit : walk) {
			// create an RDF Resource for a Commit
			Resource commitResource = model.createResource(commitPrefix
					+ commit.getName(), PROVO.Activity);

			// add a property (nothing appears in the model until a property is
			// applied)
			commitResource.addProperty(RDFS.label, commit.getShortMessage());

			// create the relationships between the commit and the files
			relateCommitsToArtifacts(commit, commitResource, sourceFolders);

		}

		walk.dispose();

		writeRdf(model, out);

	}

	private void relateCommitsToArtifacts(final RevCommit commit,
			final Resource commitResource, List<String> sourceFolders)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {

		TreeWalk walk = new TreeWalk(repository);
		RevTree tree = commit.getTree();
		walk.setRecursive(true);

		walk.addTree(tree);

		while (walk.next()) {

			// remove the source folder prefix's from Java file paths
			String path = walk.getPathString();
			if (path.endsWith(".java")) {
				path = removeSourceRootFromPath(sourceFolders, path);
			}

			// make each file a prov:Entity
			Resource fileResource = model.createResource(filePrefix + path,
					PROVO.Entity);

			// relate the prov:Entity to the commit
			commitResource.addProperty(PROVO.used, fileResource);

			DateTime dt = new DateTime(commit.getAuthorIdent().getWhen());

			String date = dt.toString(formater);

			commitResource.addProperty(PROVO.endedAtTime,
					model.createTypedLiteral(date, "xsd:dateTime"));

			String authorEmail = commit.getAuthorIdent().getEmailAddress();
			Resource author = model.createResource("mailto:" + authorEmail,
					PROVO.Person);
			commitResource.addProperty(PROVO.wasAssociatedWith, author);

			// Add some foaf properties to the author
			author.addProperty(FOAF.name, commit.getAuthorIdent().getName());
			author.addProperty(FOAF.mbox, authorEmail);

			// get the parent commit and associate with prov:wasInformedBy
			for (RevCommit parent : commit.getParents()) {

				Resource parentResource = model.createResource(commitPrefix
						+ parent.getName(), PROVO.Activity);

				commitResource.addProperty(PROVO.wasInformedBy, parentResource);

			}

		}
	}

	/**
	 * 
	 * Builds the commitPrefix URI for all subjects
	 * 
	 * @return
	 */
	private void buildPrefix(String sccs, String project) {

		// not using any default prefixes to make sure we can merge easily
		// model.setNsPrefix("", "http://default/?");

		// prefix for commits
		this.commitPrefix = this.commitPrefix + sccs + "/id/" + project + "/";

		// prefix for files
		this.filePrefix = this.filePrefix + project + "/";

		model.setNsPrefix("sccs", commitPrefix);

		// general prefix setting

		model.setNsPrefix("foaf", FOAF.getURI());
		model.setNsPrefix("prov", PROVO.getURI());

	}

	/**
	 * 
	 * Absolute file names for Java files need the source folder prefix removed
	 * This is so file Uri here will be the same as file Uri minted from Java
	 * code itself The latter has no idea where the source folder is other than
	 * it HAS to reflect the package and Class name
	 * 
	 * @return String
	 */
	private String removeSourceRootFromPath(final List<String> roots,
			String path) {

		// for each root does the path start with it?
		for (String root : roots) {
			if (path.startsWith(root)) {
				logger.debug("with source root: " + path);
				path = path.replaceFirst(root, "");
				if (path.startsWith("/"))
					path = path.substring(1);
				logger.debug("without source root: " + path);
				break;
			}
		}

		return path;
	}
	
	public void close() {
		repository.close();
	}

}

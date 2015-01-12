package net.interition.sparqlycode.sccs.git;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private String prefix;
	private String sccsProjectRootFolder;

	private final DateTimeFormatter formater = new DateTimeFormatterBuilder()
			.appendYear(4, 4).appendLiteral('-').appendMonthOfYear(2)
			.appendLiteral('-').appendDayOfMonth(2).appendLiteral('T')
			.appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2)
			.appendLiteral(':').appendSecondOfMinute(2)
			.appendTimeZoneOffset(null, true, 3, 3).toFormatter();

	public SccsServiceForGitImpl(String project, String folder) {
		this.sccsProjectRootFolder = folder;
		buildPrefix("http://www.interition.net/sccs", "git", project);
	}

	// create an empty Jena Model
	private Model model = ModelFactory.createDefaultModel();

	/*
	 * Produces SC SCCS KB from the current Git HEAD to the last HEAD ??? ph*# or something dunno
	 * 
	 * (non-Javadoc)
	 * @see net.interition.sparqlycode.sccs.SccsService#publishSCforHead(java.io.File)
	 */
	public void publishSCforHead(File out) throws Exception {
		try {
			generateRDF(out,"HEAD^","HEAD");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

	}
	/*
	 * Publishes SC SCCS KB for all Git commit between two Tags
	 * 
	 * (non-Javadoc)
	 * @see net.interition.sparqlycode.sccs.SccsService#publishSCforBranch(java.lang.String, java.io.File)
	 */
	public void publishSCforBranch(String branchName, File out) {
		// TODO Auto-generated method stub

	}

	public void publishSCforTag(File out, String startTag, String endTag) throws Exception {
		try {
			generateRDF(out,startTag,endTag);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	private void generateRDF(File out, String startTag, String endTag) throws Exception {
		
		// to use the tag ranges there is going to have to be some checks that they are valid
		// git log --pretty=oneline refs/tags/jena-2.11.0..refs/tags/jena-2.11.2
		 //startTag = "refs/tags/jena-2.11.2" ;
		// endTag = "refs/tags/jena-2.11.1" ;
		
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = builder.setGitDir(
					new File(this.sccsProjectRootFolder + "/.git")).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

		RevWalk walk = new RevWalk(repository);
		
		Ref from = repository.getRef(startTag);
		Ref to = repository.getRef(endTag);

		logger.debug("walk from: " + startTag + " , to " + endTag);
		
		walk.markStart(walk.parseCommit(from.getObjectId()));
		walk.markUninteresting(walk.parseCommit(to.getObjectId()));

		for (RevCommit commit : walk) {
			// create an RDF Resource for a Commit
			Resource commitResource = model.createResource(prefix + "commit/"
					+ commit.getName(), PROVO.Activity);

			// add a property (nothing appears in the model until a property is
			// applied)
			commitResource.addProperty(RDFS.label, commit.getShortMessage());

			// create an association with each controlled artefact (file)
			RevTree tree = commit.getTree();

			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			
			while (treeWalk.next()) {

				// make each file a prov:Entity
				Resource fileResource = model.createResource(
						prefix + treeWalk.getPathString(), PROVO.Entity);

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

					Resource parentResource = model.createResource(prefix
							+ "commit/" + parent.getName(), PROVO.Activity);

					commitResource.addProperty(PROVO.wasInformedBy,
							parentResource);
					
				}

			}

		}

		walk.dispose();

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

		return this.prefix = global + "/" + sccs + "/"
				+ project.replace('.', '/') + "/";

	}

}

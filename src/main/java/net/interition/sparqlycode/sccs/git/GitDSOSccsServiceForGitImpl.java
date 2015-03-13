package net.interition.sparqlycode.sccs.git;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import com.hp.hpl.jena.rdf.model.Resource;

import net.interition.sparqlycode.sccs.SccsService;

public class GitDSOSccsServiceForGitImpl extends RDFServices implements
		SccsService {

	private final Log logger = LogFactory
			.getLog(GitDSOSccsServiceForGitImpl.class);

	public GitDSOSccsServiceForGitImpl(String projectIdentifier, String folder)
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

	public void publishSCforHead(File out, List<String> sourceFolders, int depth)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void publishSCforBranch(File out, String branchName,
			List<String> sourceFolders) throws Exception {
		// TODO Auto-generated method stub

	}

	public void publishSCforTag(File out, String startTag, String endTag,
			List<String> sourceFolders) throws Exception {

		RevWalk walk = new RevWalk(repository);

		Ref from = repository.getRef(startTag);
		Ref to = repository.getRef(endTag);
		if (from == null) {
			throw new Exception(startTag + " did not exist");
		} else if (to == null) {
			throw new Exception(endTag + " did not exist");
		}

		logger.debug("walk from: " + startTag + " , to " + endTag);

		// scope the walk to between the two tag refs
		walk.markStart(walk.parseCommit(from.getObjectId()));
		walk.markUninteresting(walk.parseCommit(to.getObjectId()));

		GitDSOFactoryImpl factory = new GitDSOFactoryImpl(
				new URL(commitPrefix), new URL(filePrefix));

		// process the tags themselves
		createTags(factory, from, to);

		// process the commits
		for (RevCommit commit : walk) {

			// create model instances for the commit and its parents
			Resource commitResource = factory.createACommit(model, commit);

			// create an author
			factory.createAnAuthor(model, commitResource, commit
					.getAuthorIdent().getEmailAddress(), commit
					.getAuthorIdent().getName());

			// for one or more parents create model info about the differences
			for (RevCommit parent : commit.getParents()) {
				/*
				 * Necessary to make a deep copy of the parent object for
				 * getTree() to return something other than null
				 */
				parent = walk.parseCommit(parent.getId());

				ObjectReader reader = repository.newObjectReader();

				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				ObjectId oldTree = parent.getTree();
				oldTreeIter.reset(reader, oldTree);

				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				ObjectId newTree = commit.getTree();
				newTreeIter.reset(reader, newTree);

				DiffFormatter diffFormatter = new DiffFormatter(
						DisabledOutputStream.INSTANCE);
				diffFormatter.setRepository(repository);
				List<DiffEntry> entries = diffFormatter.scan(oldTreeIter,
						newTreeIter);

				Resource parentResource = factory.createParentCommit(model,
						parent);

				for (DiffEntry entry : entries) {
					factory.createADiff(model, commitResource, parentResource,
							entry);
				}

			}

		}

		walk.dispose();

		writeRdf(model, out);

	}

	private void createTags(GitDSOFactoryImpl factory, Ref from, Ref to) {

		RevWalk walk = new RevWalk(repository);

		// ugly but hey can't think of anything else at the moment

		for (Ref ref : new Ref[] { from, to } ) {
			RevObject object;
			try {
				object = walk.parseAny(ref.getObjectId());
			} catch (MissingObjectException e) {
				logger.warn("Could not process tag. Ignored.", e);
				return;
			} catch (IOException e) {
				logger.warn("Could not process tag. Ignored.", e);
				return;
			}

			if (object instanceof RevTag) {
				logger.debug("calling factory.createATag() for tag: " + object.getName());
				
				factory.createATag(model, (RevTag) object);
			} else if (object instanceof RevCommit) {
				logger.warn("Unannotated tag not supported yet. Ignored.");
			} else {
				logger.warn("tag type undeterminable. Ignored.");
			}
		}

	}
}

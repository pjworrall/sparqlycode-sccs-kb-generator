package net.interition.sparqlycode.sccs.git;

import java.net.URL;

import net.interition.sparlycode.model.GITO;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author Paul Worrall, Interition Limited
 * 
 *         This is a service class. The driver for creating this class was the
 *         need to have testable public methods. Methods declared on a higher
 *         level service class would have required to be private: hence making
 *         unit testing difficult.
 * 
 */
public class GitDSOFactoryImpl {

	// this commit uriPrefix needs to be parameterised
	private String uriPrefix = "http://www.interition.net/sccs/git/id/";
	private String filePrefix = "file://www.interition.net/sccs/git/id/";
	
	
	GitDSOFactoryImpl(URL uriPrefix, URL filePrefix) {
		this.uriPrefix = uriPrefix.toString();
		this.filePrefix = filePrefix.toString();
	}
	

	public Resource createACommit(Model model, RevCommit commit) {
		Resource resource = model.createResource(uriPrefix + commit.getName(),
				GITO.Commit);

		resource.addProperty(RDFS.label, commit.getShortMessage());

		resource.addProperty(GITO.tree, createATree(model, commit.getTree()));

		return resource;
	}

	public Resource createATree(Model model, RevTree tree) {
		Resource resource = model.createResource(uriPrefix + tree.getName(),
				GITO.Tree);
		return resource;
	}

	public Resource createParentCommit(Model model, RevCommit parent) {

		Resource commitResource = model.createResource(
				uriPrefix + parent.getName(), GITO.Commit);

		Resource parentResource = model.createResource(
				uriPrefix + parent.getName(), GITO.Commit);

		commitResource.addProperty(GITO.parent, parentResource);

		return commitResource;
	}

	public Resource createADiff(Model model, Resource commit1,
			Resource commit2, DiffEntry diff) {

		// use an anonymous node for a diff resource

		Resource diffBnode = model.createResource();

		commit1.addProperty(GITO.difference, diffBnode);
		commit2.addProperty(GITO.difference, diffBnode);

		// add properties to the diff like the change type and the file name

		ChangeType change = diff.getChangeType();
		Property property = getChangeTypeProperty(change);
		if (change != null) {
			diffBnode.addProperty(property, model.createTypedLiteral(true));
		}

		/*
		 * todo: this file resource is NOT going to be the same as what is
		 * published by in CODE KB its uriPrefix will also need considering. using
		 * the default uriPrefix for now.
		 */
		Resource fileResource = model.createResource(
				filePrefix + diff.getNewPath(), GITO.File);

		diffBnode.addProperty(GITO.file, fileResource);

		return diffBnode;
	}

	public Property getChangeTypeProperty(ChangeType change) {

		Property property;

		switch (change) {
		case ADD:
			property = GITO.isAdded;
			break;

		case MODIFY:
			property = GITO.isModified;
			break;

		case DELETE:
			property = GITO.isDeleted;
			break;

		case RENAME:
			property = GITO.isRenamed;
			break;

		case COPY:
			property = GITO.isCopied;
			break;
		default:
			property = null;
			break;
		}

		return property;

	}

	public Resource createAnAuthor(Model model, Resource commit, String email, String name ) {
		
		Resource author = model.createResource("mailto:" + email,
				GITO.Author);
		
		commit.addProperty(GITO.author, author);

		author.addProperty(FOAF.name, name);
		author.addProperty(FOAF.mbox, email);
		
		return author;
		
	}
	
	public Resource createATag(Model model, RevTag tag) {
		
		String tagId = tag.getId().name();
		String comment = ((RevTag) tag).getShortMessage();
		String commitId = ((RevTag) tag).getObject().name();
		
		
		// this resource is expected to already exist.
		Resource commitResource = model.createResource(uriPrefix + commitId,
				GITO.Commit);
		
		Resource tagResource = model.createResource(uriPrefix + tagId,
				GITO.Tag);
		
		commitResource.addProperty(GITO.tag,tagResource);
		
		tagResource.addProperty(GITO.commit, commitResource);
		tagResource.addProperty(GITO.message, comment);
			
		return tagResource;
	}
}

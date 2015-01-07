package net.interition.sparqlycode.sccs.git;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.lib.TreeEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class JGitExplorer {
	
	public static String PREFIX = "http://www.interition.net/git/";
	
	public static String PROJECT_PREFIX = "http://www.interition.net/git/"; // with Git should this be the Origin URL?
	
	public static void main(String[] arg) throws IOException, NoHeadException, GitAPIException {
		
		String groupId = "net.interition.sparqlycode.testsuite".replace('.', '/') + "/";
		
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/pjworrall/Documents/Java2RDF/sparqlycode/sparqlycode-test-suite/.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		
		System.out.println("Test..");
		
		//ObjectId head = repository.resolve("HEAD");
		
		//Ref HEAD = repository.getRef("refs/heads/master");
		
		
		Git git = new Git(repository);
		Iterable<RevCommit> log = git.log().call();
		
		
		for (RevCommit commit : log) {
			
			System.out.println("----------");
			
			String activity = "<" + JGitExplorer.PREFIX + groupId + commit.getName() + ">";
			
			System.out.println(activity + " a prov:Activity .");
			
			DateTime dt = new DateTime(commit.getAuthorIdent().getWhen());
			
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
		     .appendTimeZoneOffset(null, true, 3, 3)
		     .toFormatter();
			
			String date = dt.toString(formater);
			
			System.out.println("commit time in seconds since epoch: " + commit.getCommitTime());
			System.out.println(activity + " prov:generatedAtTime " + "\"" + date + "\"^^xsd:dateTime");
			
			System.out.println(commit.getFullMessage());
			System.out.println(commit.getAuthorIdent().getName());
			
			String agent = "<mailto:" + commit.getAuthorIdent().getEmailAddress() + ">";
			
			System.out.println(agent  + " a prov:Agent ." );
			
			System.out.println(activity + " prov:wasAssociatedWith " +  agent + " ." );
			
			RevTree tree = commit.getTree();
			
			System.out.println("Head tree: " + tree);
			
			TreeWalk treeWalk = new TreeWalk(repository);
	        treeWalk.addTree(tree);
	        treeWalk.setRecursive(true);
	        while (treeWalk.next()) {
	            //System.out.println("found: " + treeWalk.getPathString());
	        }
					
			// debug
			//System.exit(1);
			
			//System.out.println(commit.getCommitterIdent().getName());
			//System.out.println(commit.getCommitterIdent().getEmailAddress());
			
			
			
		}
		
		repository.close();
		
		
	}

}

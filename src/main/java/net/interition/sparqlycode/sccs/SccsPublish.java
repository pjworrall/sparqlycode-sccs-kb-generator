package net.interition.sparqlycode.sccs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.interition.sparqlycode.sccs.git.SccsServiceForGitImpl;

/*
 * This is to launch the SCCS KB Publisher
 * 
 * it needs to process command line argument:
 * the type of SCCS - Git or Svn (Only Git supported at the moment)
 * the Tag where the commits should be processed from
 * the Tag where the commits should stop being processed
 * 
 * the project identifier (eg. group and artifact id of the project )
 * the directory where the sccs repository exist (only local repo's at the moment)
 * a filename for the output ttl file
 * 
 */

public class SccsPublish {
	
	private static final Log log = LogFactory.getLog(SccsPublish.class);

	public static void main(String[] args) {

		SccsPublish publisher = new SccsPublish();

		String sccs = "";
		String startTag = "";
		String endTag = "";
		String identifier = "IDENTIFIER-NOT-PROVIDED";
		String directory = ".";
		String filename = "";
		String sourceroots = "";

		if (args.length == 7) {
			sccs = args[0];
			endTag = args[1];
			startTag = args[2];
			identifier = args[3];
			directory = args[4];
			filename = args[5] + ".ttl";
			sourceroots = args[6];
		} else {
			System.out
					.println("usage: java -jar x.jar sccs-type range-start range-end uri-id sccs-dir outputfile \"sourceroot1:2:3..\"");
			System.exit(1);
		}

		if (sccs.equalsIgnoreCase("GIT")) {
			publisher.gitPublisher(startTag, endTag, identifier, directory, filename, getSourceRoots(sourceroots));
		} else {
			System.out.println("sccs type: only support GIT currently");
			System.exit(1);
		}

	}

	private void gitPublisher(String startTag, String endTag, String identifier, String directory,
			String filename, List<String> sourceRoots) {

		SccsService service;
		try {
			service = new SccsServiceForGitImpl(identifier, directory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		File file = new File(filename);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("Error opening file for output.");
			System.exit(2);
		}

		try {
			service.publishSCforTag(file, startTag, endTag, sourceRoots);
		} catch (Exception e) {
			throw new RuntimeException("Error encountered publishing SC",e);
		}

	}
	
	private static List<String> getSourceRoots(String sourceRoots) {
		
		log.debug("Processing sourceroots: " + sourceRoots);
		
		String[] roots = sourceRoots.split(":");
		
		@SuppressWarnings("unchecked")
		List<String> rootsList = new ArrayList<String>(Arrays.asList(roots));
		
		log.debug("source roots list content:" + rootsList) ;
		
		return rootsList;
		
	}

}

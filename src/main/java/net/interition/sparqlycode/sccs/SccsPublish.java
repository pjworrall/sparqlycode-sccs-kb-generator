package net.interition.sparqlycode.sccs;

import java.io.File;
import java.io.IOException;

import net.interition.sparqlycode.sccs.git.SccsServiceForGitImpl;

/*
 * This is to launch the SCCS KB Publisher
 * 
 * it needs to process command line argument:
 * the type of SCCS - Git or Svn (Only Git supported at the moment)
 * the project identifier (eg. group and artifact id of the project )
 * the directory where the sccs repository exist (only local repo's at the moment)
 * a filename for the output ttl file
 * 
 */

public class SccsPublish {

	public static void main(String[] args) {

		SccsPublish publisher = new SccsPublish();

		String sccs = "";
		String identifier = "IDENTIFIER-NOT-PROVIDED";
		String directory = ".";
		String filename = "";

		if (args.length == 4) {
			sccs = args[0];
			identifier = args[1];
			directory = args[2];
			filename = args[3] + ".ttl";
		} else {
			System.out
					.println("usage: SccsPublish sccs identifier directory-path filename");
			System.exit(1);
		}

		if (sccs.equalsIgnoreCase("GIT")) {
			publisher.gitPublisher(identifier, directory, filename);
		} else {
			System.out.println("sccs type: only support GIT currently");
			System.exit(1);
		}

	}

	private void gitPublisher(String identifier, String directory,
			String filename) {

		SccsService service = new SccsServiceForGitImpl(identifier, directory);

		File file = new File(filename);
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("Error opening file for output.");
			e.printStackTrace();
			System.exit(2);
		}

		try {
			service.publishSCforHead(file);
		} catch (Exception e) {
			System.out.println("Error encountered publishing SC for HEAD");
			e.printStackTrace();
			System.exit(2);
		}

	}

}

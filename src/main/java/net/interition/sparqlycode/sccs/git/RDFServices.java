package net.interition.sparqlycode.sccs.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.interition.sparlycode.model.PROVO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public abstract class RDFServices {

	private final Log logger = LogFactory.getLog(RDFServices.class);

	// create an empty Jena Model
	@SuppressWarnings("unused")
	protected Model model = ModelFactory.createDefaultModel();

	protected Repository repository = null;

	protected String commitPrefix = "http://www.interition.net/sccs/";
	protected String filePrefix = "file://www.interition.net/sparqlycode/";
	protected final DateTimeFormatter formater = new DateTimeFormatterBuilder()
			.appendYear(4, 4).appendLiteral('-').appendMonthOfYear(2)
			.appendLiteral('-').appendDayOfMonth(2).appendLiteral('T')
			.appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2)
			.appendLiteral(':').appendSecondOfMinute(2)
			.appendTimeZoneOffset(null, true, 3, 3).toFormatter();


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
	protected void writeRdf(Model model, File out) throws Exception {

		logger.debug("Writing out " + model.size() + " statements.");

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

	/**
	 * 
	 * Builds the commitPrefix URI for all subjects
	 * 
	 * @return
	 */
	protected void buildPrefix(String sccs, String project) {

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
}

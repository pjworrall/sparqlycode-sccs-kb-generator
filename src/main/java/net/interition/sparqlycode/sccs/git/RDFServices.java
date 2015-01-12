package net.interition.sparqlycode.sccs.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class RDFServices {
	
	// create an empty Jena Model
	@SuppressWarnings("unused")
	protected Model model = ModelFactory.createDefaultModel();
	
	private final Log logger = LogFactory.getLog(RDFServices.class);
	
	
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
}

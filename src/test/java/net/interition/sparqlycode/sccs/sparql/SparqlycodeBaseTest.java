/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
package net.interition.sparqlycode.sccs.sparql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * 
 * @author Paul Worrall, Interition Ltd.
 * 
 */
public class SparqlycodeBaseTest {
	
	protected transient final Log log = LogFactory.getLog(getClass());

	protected Model model;

	private String sparqlyQueryExt = ".rq";
	private String sparqlyQueryFolder = "/sparqlycode/";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.model = ModelFactory.createDefaultModel();
		
		// TODO: this, and the POM config, needs migratign to the testsuite as well
		String sccsTtleName = System.getProperty("sccs-ttl-name");
		
		URL kb = this.getClass().getResource(sccsTtleName);
		
		if(kb == null) throw new IOException("Sparqlycode KB not found. Have you produced it yet?");
		
		model.read(kb.toString());

	}
	

	private boolean ask(String query) {

 	    Query sparqlQuery = QueryFactory.create(query) ;
 	
		QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, model) ;
		boolean result = qexec.execAsk() ;
		qexec.close() ;
		
		return result;
		}
	

	private String getQuery(String name) throws Exception {
		
		String filename = sparqlyQueryFolder + name + sparqlyQueryExt;

		URL url = this.getClass().getResource(filename);
		
		if(url == null) { 
			throw new FileNotFoundException("sparql query file [" + filename + "] not found on classpath" );
		}

		String query = null ;
		FileInputStream inputStream = new FileInputStream(url.getPath());
		try {
			query = IOUtils.toString(inputStream);
		} finally {
			inputStream.close();
		}
		
		if(query == null) log.debug("SPARQL Query " + name + "not found or empty");
		
		return query;
		
	}
	
	/*
	 * the exception on this needs to be specialised to Sparqlycode
	 */
	public boolean sparqlyCodeTest(String name) throws Exception {
		String query = getQuery(name);
		return ask(query);
		
	}

}
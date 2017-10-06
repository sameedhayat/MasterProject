/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;

/**
 * Helper class for Sparql utilities. Has 2 functionalitis:
 * 
 * 1) Escaping characters for Sparql Queries.
 * 2) Print all triples of given Repository.
 */
public class SparqlUtils {

	/**
	 * See http://www.w3.org/TR/rdf-sparql-query/#grammarEscapes
	 * @param name
	 * @return
	 */
	private static final ArrayList<Character> SPARQL_ESCAPE_SEARCH_REPLACEMENTS = new ArrayList<>();

        /*
	private static final Map SPARQL_ESCAPE_SEARCH_REPLACEMENTS = ImmutableMap.builder()
		.put("\t", "\\t")
		.put("\n", "\\n")
		.put("\r", "\\r")
		.put("\b", "\\b")
		.put("\f", "\\f")
		.put("\"", "\\\"")
                .put("'" , "\\'")
		.put("\\", "\\\\")
		.build();
        */
        
        /* 
         * http://www.onemusicapi.com/blog/2014/10/08/escaping-sparql-java/
         */
	public static String escape(String string) throws UnsupportedEncodingException {
		
            fill();
            
            StringBuilder bufOutput = new StringBuilder(string);
            for (int i = 0; i < bufOutput.length(); i++) {
                //String replacement = (String) SPARQL_ESCAPE_SEARCH_REPLACEMENTS.get("" + bufOutput.charAt(i));
                // if(replacement!=null) {
                if( SPARQL_ESCAPE_SEARCH_REPLACEMENTS.contains(bufOutput.charAt(i))) {
                    String replacement = URLEncoder.encode( Character.toString(bufOutput.charAt(i)), "UTF-8");
                    bufOutput.deleteCharAt(i);
                    bufOutput.insert(i, replacement);
                    // advance past the replacement
                    i += (replacement.length() - 1);
                }
            }
            return bufOutput.toString();
	}

    private static void fill() {	
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\t');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\n');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\r');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\b');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\f');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\"');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\'');
        SPARQL_ESCAPE_SEARCH_REPLACEMENTS.add('\\');
    }
    
    
    /**
     * Prints all triples of given Repository. 
     * 
     * Can be used for development & testing.
     * 
     * @param rep
     * 
     * @throws org.eclipse.rdf4j.repository.RepositoryException
     * @throws org.eclipse.rdf4j.query.MalformedQueryException
     * @throws org.eclipse.rdf4j.query.QueryEvaluationException
     */
    public static void printAllTriplesOfRepository(SailRepository rep) 
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        
        RepositoryConnection conn = rep.getConnection();
        
        String query = "SELECT $s $p $o WHERE { $s $p $o }";        
        
        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
        TupleQueryResult result = tupleQuery.evaluate();

            while (result.hasNext()) {
                
                BindingSet bs = result.next();
                
                Value sVal   = bs.getValue("s");
                Value pVal   = bs.getValue("p");
                Value oVal   = bs.getValue("o");

                System.out.println(sVal.stringValue() + " " +
                        pVal.stringValue() + " " + oVal.stringValue() );                
            }                   
    }
}

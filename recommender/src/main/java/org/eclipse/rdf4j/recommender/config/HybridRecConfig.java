package org.eclipse.rdf4j.recommender.config;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.GraphBasedDataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;

public class HybridRecConfig extends CrossDomainRecConfig {
	 /*--------------*
		 * Constructors *
		 *--------------*/
	
	
	private RecGraphOrientation graphOrientation = RecGraphOrientation.DIRECTED; //default
	
	//Path to doc2vec model file
	private String doc2VecInputPath = "";
	
	//Path to rdf2vec model file
	private String rdf2VecInputPath = "";
	
	//Path to doc2vec csv file
	private String doc2VecOutputPath = "";
		
	//Path to rdf2vec csv file
	private String rdf2VecOutputPath = "";
		
	
	//Whether to compute Doc2Vec or its precomputed
	private Boolean computeDoc2Vec = false;
	
	//Whether to compute Rdf2Vec or its precomputed
	private Boolean computeRdf2Vec = false;
	
	      
	        public HybridRecConfig (String configName) {
	            super(configName);
	        }
	        
	        public HybridRecConfig (HybridRecConfig config) {
	                super(config);
	        }
	        
	        
		    public RecGraphOrientation getGraphOrientation() {
	                return graphOrientation;
	        }
	
	        public void setGraphOrientation(RecGraphOrientation graphOrientation) {
	                this.graphOrientation = graphOrientation;
	        }    
	        
	    	public String getDoc2VecOutputPath() {
                return doc2VecOutputPath;
	        }
	    	public String getRdf2VecOutputPath() {
                return rdf2VecOutputPath;
	        }
	    	
	    	public String getDoc2VecInputPath() {
                return doc2VecInputPath;
	        }
	    	public String getRdf2VecInputPath() {
                return rdf2VecInputPath;
	        }
	    	
	    	public Boolean getComputeDoc2Vec() {
                return computeDoc2Vec;
	        }
	    	public Boolean getComputeRdf2Vec() {
                return computeRdf2Vec;
	        }
	        
	        //Path to doc2vec csv file
	    	public void doc2VecOutputPath(String path) {
                this.doc2VecOutputPath = path;
	        }
	        
	        //Path to rdf2vec csv file
	        public void rdf2VecOutputPath(String path) {
                this.rdf2VecOutputPath = path;
	        }
	        
	        //Path to doc2vec model file
	    	public void doc2VecInputPath(String path) {
                this.doc2VecInputPath = path;
	        }
	        
	        //Path to rdf2vec model file
	        public void rdf2VecInputPath(String path) {
                this.rdf2VecInputPath = path;
	        }
	        
	        
	        //Whether to compute Doc2Vec or its precomputed
	        public void computeDoc2Vec() {
                this.computeDoc2Vec = true;
	        }
	        
	       //Whether to compute Rdf2Vec or its precomputed
	        public void computerRdf2Vec() {
	        	this.computeRdf2Vec = true;
	        }
	        
	        @Override
	        public DataManager validateConfiguration() throws RecommenderException{
	                super.validateConfiguration();
	                
	                if (getRecStorage() != RecStorage.EXTERNAL_GRAPH) {
	                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
	                }
	                
	                return new GraphBasedDataManager(this);
	        }
}

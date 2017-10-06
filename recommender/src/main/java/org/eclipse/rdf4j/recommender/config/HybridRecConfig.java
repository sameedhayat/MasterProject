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
	       
	        @Override
	        public DataManager validateConfiguration() throws RecommenderException{
	                super.validateConfiguration();
	                
	                if (getRecStorage() != RecStorage.EXTERNAL_GRAPH) {
	                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
	                }
	                
	                return new GraphBasedDataManager(this);
	        }
}

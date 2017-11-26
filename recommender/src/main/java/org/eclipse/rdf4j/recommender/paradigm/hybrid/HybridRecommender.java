package org.eclipse.rdf4j.recommender.paradigm.hybrid;

import java.util.Set;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.paradigm.AbstractRecommender;
import org.eclipse.rdf4j.repository.sail.SailRepository;

public class HybridRecommender extends AbstractRecommender {
	   
	/*-------------*
		 * Constructor *
		 *-------------*/
	        
	public HybridRecommender(SailRepository sailRep, DataManager dataManager)
	                        throws RecommenderException {
	            
	                //Calling superclass' constructor.
	                super(sailRep, dataManager);
	        }
	
	@Override
	public double predictRating(String userURI, String itemURI) throws RecommenderException {
		return getDataManager().getResRelativeImportance(userURI, itemURI);
	}

	
	@Override
    public RatedResource[] getTopRecommendations(String userURI, int size, 
                    Set<String> candidatesURI) 
                    throws RecommenderException {
			System.out.println(candidatesURI);
            RatedResource[] topK = super.getTopRecommendations(userURI, size, candidatesURI);                 
            RatedResource[] normalizedTopK = new RatedResource[topK.length];
            double maxScore = topK[0].getRating(); //Highest score
            
            //We normalize based on this max value                
            for (int i = 0; i < topK.length; i++) {
                    if(topK[i] == null) {
                            normalizedTopK[i] = null;
                    } else {
                            normalizedTopK[i] = 
                                    new RatedResource(topK[i].getResource(), 
                                            topK[i].getRating() / maxScore);                                                     
                    }
            }                                              
            return normalizedTopK;
    }

}

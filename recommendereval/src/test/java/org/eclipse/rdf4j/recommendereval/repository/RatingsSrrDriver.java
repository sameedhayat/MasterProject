/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.repository;

import java.util.Set;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.sail.Sail;

/**
 * SRR stands for Sail Recommender Repository.
 * This is a driver recommender repository designed to provide fixed answers 
 * based on the "fixed-ratings.ttl" dataset.
 */
public class RatingsSrrDriver extends SailRecommenderRepository {         

	/*--------------*
	 * Constructors *
	 *--------------*/
        
	/**
	 * Creates a new repository object that operates on the supplied Sail.
	 * 
	 * @param sail
	 *        A Sail object.
	 */
	public RatingsSrrDriver(Sail sail) {
		super(sail);
	}

	/*---------*
	 * Methods *
	 *---------*/      
        /**
         * Contrarily to a SailRecommenderRepository this repository doesn't
         * use the configuration (it doesn't require a recommender object to
         * be instantiated since no algorithm is required.)
         * 
         * @param recConfig
         * @throws RecommenderException 
         */
        @Override
        public void loadRecConfiguration(RecConfig recConfig)
                        throws RecommenderException {
                if (recConfig == null) 
                        throw new RecommenderException("CANNOT LOAD A NULL CONFIGURATION");
        }
        
        /**
         * Method for getting the predicted rating for a given user, item.
         * 
         * The predicted ratings are fixed within this repository. It is for
         * test purposes.
         * It predicts 1 for user1, 2 for user2, etc. If the user is not among
         * those from the dataset it returns -1.
         * @param userURI
         * @param itemURI
         * @return 
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public double predictRating(String userURI, String itemURI) 
                        throws RecommenderException {
                if (userURI.equals("http://example.org/fixed#User1"))
                    return 1.0;
                if (userURI.equals("http://example.org/fixed#User2"))
                    return 2.0;
                if (userURI.equals("http://example.org/fixed#User3"))
                    return 3.0;
                if (userURI.equals("http://example.org/fixed#User4"))
                    return 4.0;                
                return -1.0;
        }
        
        /**
         * Returns fixed top-k Recommendations 
         * It only supports two items in the top-k list.
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public RatedResource[] getTopRecommendations(String userURI, int size, 
                        Set<String> candidatesURIs) throws RecommenderException {
                
                if (size==2)            
                        return getTop2Recommendations(userURI);
                return null;
        }
        
        /**
         * Returns fixed top-2 Recommendations.
         * @throws org.eclipse.rdf4j.recommender.exception.RecommenderException 
         */
        public RatedResource[] getTop2Recommendations(String userURI) throws RecommenderException {
                RatedResource[] top2Rec = new RatedResource[2];
                RatedResource res1 = null;
                RatedResource res2 = null;
                
                //Perfect match in one fold. Precision = 0 in all other folds
                if (userURI.equals("http://example.org/fixed#User1")){
                        res1 = new RatedResource("http://example.org/fixed#Item5", 5.0);
                        res2 = new RatedResource("http://example.org/fixed#Item6", 5.0);
                }
                //1/2 matches in two folds. Precision = 0 in one fold
                if (userURI.equals("http://example.org/fixed#User2")){
                        res1 = new RatedResource("http://example.org/fixed#Item4", 5.0);
                        res2 = new RatedResource("http://example.org/fixed#Item1", 5.0);
                }
                //1/2 matches in non adjacent folds. Precision = 0 in one fold
                if (userURI.equals("http://example.org/fixed#User3")){
                        res1 = new RatedResource("http://example.org/fixed#Item1", 5.0);
                        res2 = new RatedResource("http://example.org/fixed#Item5", 5.0);
                }
                //Precision 0
                if (userURI.equals("http://example.org/fixed#User4")){
                        res1 = new RatedResource("http://example.org/fixed#Item5", 5.0);
                        res2 = new RatedResource("http://example.org/fixed#Item6", 5.0);
                }
                top2Rec[0] = res1;
                top2Rec[1] = res2;
                return top2Rec;
        }
}

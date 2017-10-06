/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.crosskfold;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.repository.RatingsAndNullsSrrDriver;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This is a stub class to have control on the way that folds are created and
 * isolate better the behavior of the evaluator.
 */
public class CusFoldsRatingsAndNullsEvalStub extends CrossKFoldEvaluator {

    final static Logger LOGGER = Logger.getLogger(CusFoldsRatingsAndNullsEvalStub.class);
    
    public CusFoldsRatingsAndNullsEvalStub(SailRecEvaluatorRepository evaluatorRepository) {
        super(evaluatorRepository);
    }  
    
    /**
     * For test purposes the method has been fixed to return a 
 RatingsAndNullsSrrDriver.
     *  
     * @return SailRecommenderRepository
     */
    @Override
    public SailRecommenderRepository loadSail() {
        return new RatingsAndNullsSrrDriver(new MemoryStore());
    }
    
    /** 
     * For test purposes. To get some control on how the split of folds is done.
     * 
     * @param numberOfFolds
     * @return 
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException 
     */
    @Override
    public Folds splitRatingsKFold(int numberOfFolds) 
                    throws EvaluatorException {
        
            //I will make my own folds:
            ArrayList<ArrayList<ArrayList<Rating>>> myUserFolds = new ArrayList<>();
            
            //For each user we add one list:
            ArrayList<ArrayList<Rating>> foldsOfUser1 = new ArrayList<>();
            ArrayList<ArrayList<Rating>> foldsOfUser2 = new ArrayList<>();
            ArrayList<ArrayList<Rating>> foldsOfUser3 = new ArrayList<>();
            ArrayList<ArrayList<Rating>> foldsOfUser4 = new ArrayList<>();
            
            //For each user we add three folds:
            ArrayList<Rating> fold1OfUser1 = new ArrayList<>();
            ArrayList<Rating> fold2OfUser1 = new ArrayList<>();
            ArrayList<Rating> fold3OfUser1 = new ArrayList<>();
            
            ArrayList<Rating> fold1OfUser2 = new ArrayList<>();
            ArrayList<Rating> fold2OfUser2 = new ArrayList<>();
            ArrayList<Rating> fold3OfUser2 = new ArrayList<>();
            
            ArrayList<Rating> fold1OfUser3 = new ArrayList<>();
            ArrayList<Rating> fold2OfUser3 = new ArrayList<>();
            ArrayList<Rating> fold3OfUser3 = new ArrayList<>();
            
            ArrayList<Rating> fold1OfUser4 = new ArrayList<>();
            ArrayList<Rating> fold2OfUser4 = new ArrayList<>();
            ArrayList<Rating> fold3OfUser4 = new ArrayList<>();
            
            //Ensamble everything:
            foldsOfUser1.add(fold1OfUser1);
            foldsOfUser1.add(fold2OfUser1);
            foldsOfUser1.add(fold3OfUser1);
            
            foldsOfUser2.add(fold1OfUser2);
            foldsOfUser2.add(fold2OfUser2);
            foldsOfUser2.add(fold3OfUser2);
            
            foldsOfUser3.add(fold1OfUser3);
            foldsOfUser3.add(fold2OfUser3);
            foldsOfUser3.add(fold3OfUser3);
            
            foldsOfUser4.add(fold1OfUser4);
            foldsOfUser4.add(fold2OfUser4);
            foldsOfUser4.add(fold3OfUser4);
            
            myUserFolds.add(foldsOfUser1);
            myUserFolds.add(foldsOfUser2);
            myUserFolds.add(foldsOfUser3);
            myUserFolds.add(foldsOfUser4);
            
            //Here we insert the Ratings into the three folds:
            fold1OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item1", 3.0));
            fold1OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item2", 1.0));            
            fold1OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item3", 4.0));
            fold1OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item4", 3.0));
            fold1OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item5", 4.0));
            fold1OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item6", 2.0));           
            fold1OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item1", 1.0));
            fold1OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item2", 5.0));
            
            fold2OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item3", 2.0));
            fold2OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item4", 3.0));            
            fold2OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item5", 5.0));
            fold2OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item6", 3.0));
            fold2OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item1", 3.0));
            fold2OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item2", 3.0));           
            fold2OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item3", 5.0));
            fold2OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item4", 2.0));
            
            fold3OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item5", 3.0));
            fold3OfUser1.add(new Rating("http://example.org/fixed#User1", "http://example.org/fixed#Item6", 4.0));            
            fold3OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item1", 4.0));
            fold3OfUser2.add(new Rating("http://example.org/fixed#User2", "http://example.org/fixed#Item2", 3.0));
            fold3OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item3", 1.0));
            fold3OfUser3.add(new Rating("http://example.org/fixed#User3", "http://example.org/fixed#Item4", 5.0));           
            fold3OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item5", 1.0));
            fold3OfUser4.add(new Rating("http://example.org/fixed#User4", "http://example.org/fixed#Item6", 1.0));
            
            return new Folds(myUserFolds);
    }
}
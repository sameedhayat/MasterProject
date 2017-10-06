/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.crosskfold;

import org.apache.log4j.Logger;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.repository.LikesAndNullsSrrDriver;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This is a stub class which conducts a standard data split.
 * The only difference is that it returns a specific kind of repository
 LikesAndNullsSrrDriver.
 */
public class StdFoldsLikesAndNullsEvalStub extends CrossKFoldEvaluator {

    final static Logger LOGGER = Logger.getLogger(StdFoldsLikesAndNullsEvalStub.class);
    
    public StdFoldsLikesAndNullsEvalStub(SailRecEvaluatorRepository evaluatorRepository) {
        super(evaluatorRepository);
    }  
    
    /**
     * For test purposes the method has been fixed to return a 
 LikesAndNullsSrrDriver.
     *  
     * @return SailRecommenderRepository
     */
    @Override
    public SailRecommenderRepository loadSail() {
        return new LikesAndNullsSrrDriver(new MemoryStore());
    }
}
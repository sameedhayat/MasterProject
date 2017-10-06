/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.crosskfold;

import org.apache.log4j.Logger;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.repository.LikesSrrDriver;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This is a stub class which conducts a standard data split.
 * The only difference is that it returns a specific kind of repository
 LikesSrrDriver.
 */
public class StdFoldsLikesEvalStub extends CrossKFoldEvaluator {

    final static Logger LOGGER = Logger.getLogger(StdFoldsLikesEvalStub.class);
    
    public StdFoldsLikesEvalStub(SailRecEvaluatorRepository evaluatorRepository) {
        super(evaluatorRepository);
    }  
    
    /**
     * For test purposes the method has been fixed to return a 
 LikesSrrDriver.
     *  
     * @return SailRecommenderRepository
     */
    @Override
    public SailRecommenderRepository loadSail() {
        return new LikesSrrDriver(new MemoryStore());
    }
}
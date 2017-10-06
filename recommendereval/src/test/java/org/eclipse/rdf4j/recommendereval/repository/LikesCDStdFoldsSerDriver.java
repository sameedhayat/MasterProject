/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.repository;

import java.util.ArrayList;
import org.eclipse.rdf4j.recommendereval.config.EvalConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.evaluator.Evaluator;
import org.eclipse.rdf4j.recommendereval.evaluator.crosskfold.StdFoldsLikesEvalStub;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.sail.Sail;

/**
 * SER stands for Sail Evaluation Repository.
 * The repository is fixed to evaluate a 
 * @org.eclipse.rdf4j.recommender.repository.SrrLikesDriver.
 * 
 * This evaluator should be only used for test purposes.
 */
public class LikesCDStdFoldsSerDriver extends SailRecEvaluatorRepository {

    /*--------*
     * Fields *
     *--------*/
    
    /**
     * Configuration of the evaluation.
     */
    private EvalConfig evalConfig = null;

    private Evaluator evaluator = null;

    /**
     * Set of recommendation configurations. Allows multiple recommenders to be 
     * evaluated at once.
     */
    private ArrayList<RecConfig> recConfigList = null;

    /*--------------*
     * Constructors *
     *--------------*/
    
    /**
     * Creates a new repository object that operates on the supplied Sail.
     *
     * @param sail A Sail object.
     */
    public LikesCDStdFoldsSerDriver(Sail sail) {
        super(sail, new ArrayList<RecConfig>());
        this.recConfigList = EvalTestRepositoryInstantiator.getRecConfigListLikesCDFixed();
    }

    /*---------*
     * Methods *
     *---------*/
    
    @Override
    /* The only difference: it uses a special kind of evaluator
     * StdFoldsLikesEvalStub, that creates a SrrLikesDriver,
     * recommender repository instead of creating one based on the 
     * configuration.
     */
    public void loadEvalConfiguration(EvalConfig evalConfig)
            throws EvaluatorException {

        System.out.println("Loading configuration...");
        long startTime = System.currentTimeMillis();
        
        if (evalConfig == null) {
            throw new EvaluatorException("CANNOT LOAD A NULL CONFIGURATION");
        }        
        if ( recConfigList.isEmpty() ) {
            throw new EvaluatorException("RECOMMENDER CONFIGURATION LIST EMPTY");
        }
        evalConfig.setRecommenderConfigurations(recConfigList);
                
        // if you change checkGraphPattern(), change below too
        evalConfig.setGraphPattern(recConfigList.get(0).getPosGraphPattern());
        evalConfig.addEvalEntity(EvalEntity.USER, recConfigList.get(0).getRecEntity(RecEntity.USER));
        evalConfig.addEvalEntity(EvalEntity.RATING, recConfigList.get(0).getRecEntity(RecEntity.RATING));
        evalConfig.addEvalEntity(EvalEntity.POS_ITEM, recConfigList.get(0).getRecEntity(RecEntity.POS_ITEM));
        
        boolean success = evalConfig.validateConfiguration();

        if (success) {

            this.evalConfig = evalConfig;

            if( evalConfig instanceof CrossKFoldEvalConfig ) {
                evaluator = new StdFoldsLikesEvalStub(this);
            }
            else {
                throw new EvaluatorException("EVALUATOR CONFIGURATION ERROR. PLEASE USE ONE OF THE VALID CONFIGURATION CLASSES");
            }
        } else {
            throw new EvaluatorException("EVALUATOR CONFIGURATION VALIDATION UNSUCCESSFUL");
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("LOADING... COMPLETED");
        System.out.println("Total time spent for loading configuration: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
    }

    /** 
     * Evaluates all Recommender Configurations and handles output
     * 
     * No difference with respect to evaluate() of superclass, except that
     * "evaluator" and "evalConfig" from superclass are not accessible anymore
     * and the local fields have to be used instead.
     * 
     * @throws EvaluatorException
     */
    @Override
    public void evaluate()
            throws EvaluatorException {
        
        System.out.println("Evaluator starting...");
        long startTime = System.currentTimeMillis();
        
        evaluator.preprocess(recConfigList);
        evaluator.evaluate(recConfigList);
        evaluator.writeOutput(evalConfig.getOutputMethod(),evalConfig.getEvalMetrics());
        // remove files in native store case
        removeFiles(evaluator.getNativeStoreCounter());
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("EVALUATION... COMPLETED");
        System.out.println("Total time spent for evaluation: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
        System.out.println("");
    }   

    /**
     * Gets the inner configuration.
     *
     * @return
     */
    @Override
    public EvalConfig getCurrentLoadedConfiguration() {
        return this.evalConfig;
    }
    
    /**
     * For testing
     * @return evaluator
     */
    @Override
    public Evaluator getEvaluator() {
        return this.evaluator;
    }            
}

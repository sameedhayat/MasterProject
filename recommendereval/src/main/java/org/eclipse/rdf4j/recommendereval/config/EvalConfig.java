/* 
 * Victor Anthony Arrascue Ayala
 * & Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.config;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;

/**
 * Interface of a generic configuration of an evaluator of a recommender system.
 */
public interface EvalConfig {
    
    /**
     * Checks whether all parameters have been configured correctly for the
     * given configuration.
     *
     * @return
     * @throws EvaluatorException
     */
    public boolean validateConfiguration()
            throws EvaluatorException;
    
    /**
     * Gets Evaluator Entity Map
     * @return 
     */
    public HashMap<EvalEntity,String> getEvalEntityMap();
    
    /**
     * Adds Evaluator Entity to Evaluator Entity Map.
     * @param entity
     * @param varName
     * @throws EvaluatorException 
     */
    public void addEvalEntity(EvalEntity entity, String varName) throws EvaluatorException;
    
    /**
     * Gets graph pattern of the triples.
     * @return graphPattern
     */
    public String getGraphPattern();
    
    /**
     * Sets graph pattern.
     * @param graphPattern 
     */
    public void setGraphPattern(String graphPattern);
    
    /**
     * Gets graph pattern of the triples, for features of items.
     * @return graphPattern
     */
    public String getFeatureGraphPattern();
    
    /**
     * Sets graph pattern for features of items.
     * @param featureGraphPattern
     */
    public void setFeatureGraphPattern(String featureGraphPattern);

    /** 
     * Gets isReproducible variable.
     * 
     * If isReproducible variable is true, Evaluator will produce same results 
     * everytime program runs(deterministic if recommenders are also deterministic).
     * 
     * If isReproducible variable is false, Evaluator will work different results 
     * everytime program runs and work in more random way when creating test sets.
     * 
     * @return boolean isReproducible
     */
    public boolean isReproducible();
    
    /**  
     * Sets reproducible boolean
     * 
     * If isReproducible variable is true, Evaluator will produce same results 
     * everytime program runs(Evaluator will run deterministic if recommenders are also deterministic).
     * 
     * If isReproducible variable is false, Evaluator will work different results 
     * everytime program runs and work in more random way when creating test sets.
     *
     * @param isReproducible false for random, true for reproducible
     */
    public void setIsReproducible(boolean isReproducible);

    /**
     * Can be called multiple times by a configuration instance to add Ranking Evaluator Metrics
     * that has to be tested.
     *
     * @param rankingMetric
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    public void addEvalMetric(RankingEvalMetric rankingMetric) throws EvaluatorException;

    /**
     * Can be called multiple times by a configuration instance to add Prediction Evaluator Metrics
     * that has to be tested.
     *
     * @param predictionMetric
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    public void addEvalMetric(PredictionEvalMetric predictionMetric) throws EvaluatorException;

    /**
     * Can be called multiple times by a configuration instance to add Global Evaluator Metrics
     * that has to be tested.
     *
     * @param globalMetric
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    public void addEvalMetric(GlobalEvalMetric globalMetric) throws EvaluatorException;

    /**
     * Gets for Evaluation Metrics list of Evaluator.
     *
     * @return
     */
    public ArrayList<GenericEvalMetric> getEvalMetrics();

    /**
     * Adds topK size to be evaluated along with all ranking metrics.
     *
     * @param size
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException if size is out of range
     */
    public void addRankingMetricTopKSize(Integer size) throws EvaluatorException;
    
    /**
     * Getter method for topKSize list of Evaluator.
     *
     * @return
     */
    public ArrayList<Integer> getTopKSizesToBeEvaluated();

    /**
     * Gets the output method that should be used to report the results when
     * the evaluation is completed.
     *
     * @return EvalOutput
     */
    public EvalOutput getOutputMethod();

    /**
     * 
     * Configure how to report the results of the evaluation.
     *
     * @param outputMethod
     * @param filePath
     */
    public void setOutputMethod(EvalOutput outputMethod, String filePath);
    
    /**
     * Gets the path of the output file, in case this is selected as output
     * method.
     *
     * @return String
     */
    public String getOutputPath();

    /**
     * Sets the user selection type.
     * 
     * @param selectionType
     */
    public void selectSpecificUsersForEvaluation(EvalUserSelectionWrapper selectionType);
    
    /**
     * Gets the user selection type.
     * 
     * @return 
     */
    public EvalUserSelectionWrapper getUserSelectionCriterion();
    
    /**
     * Gets Evaluation Storage type
     * @return 
     */
    public EvalStorage getStorage();
    
    /**
     * Sets Evaluation Storage type
     * @param storage type
     */
    public void setStorage(EvalStorage storage);
    
    /**
     * Sets Recommender Configurations List
     * @param recConfigList 
     */
    public void setRecommenderConfigurations(ArrayList<RecConfig> recConfigList);
}
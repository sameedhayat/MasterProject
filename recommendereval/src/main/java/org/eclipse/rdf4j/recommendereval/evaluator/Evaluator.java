/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommendereval.datamanager.EvalDataManager;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;

/**
 * Evaluates each given Recommender Configuration for each Evaluation Metric
 * by creating Recommenders and getting their recommendations.
 * 
 * Also responsible for preprocessing part(using EvalDataManager).
 */
public interface Evaluator {
    
    /**
     * Adds an Evaluation Result
     * @param evalResult 
     */
    public void addEvalResult(EvaluationResult evalResult);        
    
    /**
     * Gets RecEvaluatorRepository object
     * @return 
     */
    public SailRecEvaluatorRepository getEvaluatorRepository();
    
    /**
     * Calculates averageRating and stores ratings in a List
     * 
     * @param configurations
     * @throws EvaluatorException
     */
    public void preprocess(List<RecConfig> configurations) throws EvaluatorException;

    /**
     * Creates recommenders using configurations and evaluates them.
     * 
     * @param configurations
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    public void evaluate(List<RecConfig> configurations)
            throws EvaluatorException;
    
    /**
     * Gets results of evaluation
     * @return 
     */
    public ArrayList<EvaluationResult> getEvalResults();
    
    /**
     * Gets result of evaluation for a recommender configuration with given index
     * @param configurationIndex of rec configuration
     * @return 
     */
    public EvaluationResult getEvalResultByIndex(int configurationIndex);
    
    /**
     * Gets an Evaluation Result with given configuration name. Returns the first result
     * that matches given configuration name. 
     * 
     * @param configurationName
     * @return 
     */
    public EvaluationResult getEvalResultByName(String configurationName);
    
    /**
     * Writes output considering outputMethod
     * @param outputMethod 
     * @param evalMetrics 
     */
    public void writeOutput(EvalOutput outputMethod,  ArrayList<GenericEvalMetric> evalMetrics);
    

    /**
     * Evaluates performance of the recommender with respect to Prediction Evaluation 
     * Metric.
     * 
     * @param metric Evaluation Metric
     * @param datasetTestSet Ratings of the items in the test set, from input file
     * @param recommenderRatings Ratings of the items in the test set obtained from recommender
     * @param userAverages User averages for the training set
     * @return 
     */
    public Double evaluatePredictionMetric(EvalMetric metric, ArrayList<Rating> datasetTestSet,
            ArrayList<Double> recommenderRatings, ArrayList<Double> userAverages );
    
    /**
     * Evaluates performance of the recommender with respect to Ranking Evaluation 
     * Metric.
     * 
     * @param metric Evaluation Metric
     * @param datasetTestSet Ratings of the items in the test set, from input file
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages for the training set
     * @param ignoreUsers Users to be ignored are the ones that are true
     * @param topKSize For Precision and Recall calculations. 0 will be used for others
     * @param folds For creating training set ratings, when needed
     * @param foldIndex For creating training set ratings, when needed
     * @return 
     */
    public Double evaluateRankingMetric(EvalMetric metric, ArrayList<Rating> datasetTestSet,
            ArrayList<RatedResource[]> topRecommendations, ArrayList<Double> userAverages, 
            ArrayList<Boolean> ignoreUsers, int topKSize, Folds folds, int foldIndex);
    
    /**
     * Evaluates performance of the recommender with respect to Global Evaluation 
     * Metric.
     * 
     * @param metric Evaluation Metric
     * @param datasetTestSet Ratings of the items in the test set, from input file
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages for the training set
     * @param ignoreUsers Users to be ignored are the ones that are true
     * @param topKSize For Precision and Recall calculations. 0 will be used for others
     * @return 
     */
    public Double evaluateGlobalMetric(EvalMetric metric, ArrayList<Rating> datasetTestSet,
            ArrayList<RatedResource[]> topRecommendations, ArrayList<Double> userAverages, 
            ArrayList<Boolean> ignoreUsers, int topKSize);
                        
    /**
     * For testing EvalDataManager 
     * @return EvalDataManager
     */
    public EvalDataManager getDataManager();
    
    /**
     * Increases Native Store Counter when another folder for Native Store is used.
     */
    public void incrementNativeStoreCounter();
    
    /**
     * Get Native Store File Counter
     * 
     * Will be used for deleting native store folders.
     * 
     * @return 
     */
    public int getNativeStoreCounter();
    
    /**
     * For testing.
     * 
     * Sets Users array list for Data Manager 
     * @param users 
     */
    public void setUsers(ArrayList<String> users); 
    
    /**
     * For testing.
     * 
     * Sets Items array list for Data Manager 
     * @param items 
     */
    public void setItems(ArrayList<String> items);    
    
    /**
     * For testing.
     * 
     * Sets ItemFeatures array list for Data Manager 
     * @param itemFeatures 
     */
    public void setItemFeatures(ArrayList<ArrayList<String>> itemFeatures);      
    
    /**
     * For Testing.
     * 
     * Gets 2 Dimensional Ratings ArrayList(ratings for each user) for Data Manager 
     * @param usersRatings
     */
    public void setUsersRatings(ArrayList<ArrayList<Rating>> usersRatings);
}

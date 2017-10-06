/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.crosskfold;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.evaluator.AbstractEvaluator;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResultsForFold;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.javatuples.Pair;

public class CrossKFoldEvaluator extends AbstractEvaluator {

    final static Logger LOGGER = Logger.getLogger(CrossKFoldEvaluator.class);
    
    public CrossKFoldEvaluator(SailRecEvaluatorRepository evaluatorRepository) {
        super(evaluatorRepository);
    }

    @Override
    public void evaluate(List<RecConfig> configurations) throws EvaluatorException {

        System.out.println("Evaluator evaluate starting... It will evaluate each recommender...");
        long startTime = System.currentTimeMillis();        
        
        int numberOfFolds = ( (CrossKFoldEvalConfig) getEvaluatorRepository().getCurrentLoadedConfiguration() ).getNumberOfFolds();
        
        // Get folds for CrossKFold Validation  
        Folds folds = splitRatingsKFold(numberOfFolds);
        
        logUsersToIgnore();
        
        // evaluate each configuration
        for ( int i = 0 ; i < configurations.size() ; i++ ) {
      
            long startTimeForConfig = System.currentTimeMillis();   
            
            System.gc();
            evaluateRecommender(configurations.get(i), folds);
            
            long stopTimeForConfig = System.currentTimeMillis();
            long elapsedTimeForConfig = stopTimeForConfig - startTimeForConfig;
            System.out.println("EVALUATING RECOMMENDER " + (i+1) + "... COMPLETED");
            System.out.println("Time spent for evaluating Recommender Configuration " + (i+1) + " : " + (elapsedTimeForConfig) 
                + "ms (ca. " + (elapsedTimeForConfig / 1000) + " secs).");
        }
                
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("EVALUATOR EVALUATE... COMPLETED");
        System.out.println("Total time spent for evaluate: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
    }
    
    /**
     * Method that evaluates a Recommender using given Recommender Configuration
     * 
     * @param recConfig Recommender Configuration
     * @param folds Three Dimensional Ratings List obtained from Data Manager
     * @throws EvaluatorException 
     */
    
    public void evaluateRecommender(RecConfig recConfig, Folds folds) 
            throws EvaluatorException {
        
        // the list that will be used for storing metric results for each fold
        ArrayList<ArrayList<Double>> evalMetricsResults = new ArrayList<>();
        
        try {     
            if( folds.getFoldsOfAllUsers().isEmpty() ) {
                throw new EvaluatorException("K FOLD LISTS ARE EMPTY");
            }
            
            // for each fold, calculate metrics
            for (int i = 0; i < folds.getNumberOfFolds() ; i++) {
                
                long startTimeForFold = System.currentTimeMillis();   

                ArrayList<Boolean> ignoreUsers = getDataManager().getUsersToIgnore();   
                
                ArrayList<Rating> testSet = createTestSet(ignoreUsers, folds, i);
                
                SailRecommenderRepository sRecommender = createRecommender(testSet,recConfig);
                
                evalMetricsResults.add(evaluateMetrics(sRecommender, folds,
                        testSet, i));             
                
                // shutdown recommender for file deletion (native store case)
                if( getNativeStoreCounter() > 1 )
                    sRecommender.shutDown();
                
                long stopTimeForFold = System.currentTimeMillis();
                long elapsedTimeForFold = stopTimeForFold - startTimeForFold;
                System.out.println("FOLD " + (i+1) + "... COMPLETED");
                System.out.println("Time spent for all computations of Fold " + (i+1) + " : " + (elapsedTimeForFold) 
                    + "ms (ca. " + (elapsedTimeForFold / 1000) + " secs).");
            }
        } catch (RepositoryException | RecommenderException ex) {
            throw new EvaluatorException(ex);
        }
        
        calculateResultsForRecConfig(evalMetricsResults,recConfig);
    }

    /**
     * Creates Recommender object using Evaluator Repository.
     * 
     * Loads sail, initializes Recommender. Prepares triples for the training set.
     * Then loads configuration of the recommender.
     * 
     * @param testSet
     * @param recConfig
     * @return
     * @throws RecommenderException
     * @throws RepositoryException 
     */
    public SailRecommenderRepository createRecommender(ArrayList<Rating> testSet,
                RecConfig recConfig) throws RecommenderException, RepositoryException {
        
        long startTimeCreatingRec = System.currentTimeMillis();   
        
        SailRecommenderRepository sRecommender = loadSail();
        sRecommender.initialize();                
        SailRepositoryConnection conn = sRecommender.getConnection();
        conn.add(getEvaluatorRepository().getConnection().getStatements(null, null, null, true));
        getDataManager().removeStatements(sRecommender,testSet);       
        sRecommender.loadRecConfiguration(recConfig); 
        
        long stopTimeCreatingRec = System.currentTimeMillis();
        long elapsedTimeCreatingRec = stopTimeCreatingRec - startTimeCreatingRec;
        System.out.println("Time spent for creating Recommender: " + (elapsedTimeCreatingRec) 
        + "ms (ca. " + (elapsedTimeCreatingRec / 1000) + " secs)."); 
        
        return sRecommender;
    }
    
    /**
     * Loads sail considering EvalStorage type and returns the sail.
     * 
     * Available storage types: MEMORY, NATIVE
     * 
     * @return SailRecommenderRepository
     */
    public SailRecommenderRepository loadSail() {
        
        if( getEvaluatorRepository().getCurrentLoadedConfiguration().getStorage() == EvalStorage.MEMORY) {
            return new SailRecommenderRepository(new MemoryStore());
        }
        else {
            // String indexes = "spoc,posc,cspo,cpos,cops,cosp,opsc,ospc";
            String indexes = "spoc,posc,opsc,ospc";
            int counter = getNativeStoreCounter();            
            incrementNativeStoreCounter();
            return new SailRecommenderRepository(new NativeStore(new File(Thread.currentThread()
                            .getContextClassLoader().getResource("").getPath() + counter + "/"),indexes));   
        }
    }

    /**
     * Gets predictions and top k lists from the recommender considering training
     * set / test set.
     * @param sRecommender
     * @param folds
     * @param testSet
     * @param foldIndex
     * @return 
     */
    public ArrayList<Double> evaluateMetrics(SailRecommenderRepository sRecommender, 
            Folds folds, ArrayList<Rating> testSet, int foldIndex) 
            throws EvaluatorException, RecommenderException {
        
        ArrayList<GenericEvalMetric> evalMetrics = 
            getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalMetrics();
        ArrayList<Double> userAverages = getDataManager().calculateAllUsersAverages(folds,foldIndex);   
        ArrayList<Double> recommenderRatings = new ArrayList<>();    
        ArrayList<Boolean> ignoreUsers = getDataManager().getUsersToIgnore();

        long startTimeGetRating = System.currentTimeMillis();       
     
        // check if a prediction metric is in metrics list, if yes, predict ratings
        if( hasPredictionMetric(evalMetrics) ) {
            for ( int j = 0; j < testSet.size(); j++ ) {
                
                Double recResult = sRecommender.predictRating(
                        testSet.get(j).getUserURI(),
                        testSet.get(j).getItemURI());
                
                recommenderRatings.add(recResult);
            }
        }
        
        /*
        //In case of need of debugging the training set.
        System.out.println("-------------------TRAINING SET---------------");
        IndexBasedDataManager datMan = (IndexBasedDataManager)sRecommender.getRecommender().getDataManager();
        IndexBasedStorage stor = (IndexBasedStorage) sRecommender.getRecommender().getDataManager().getStorage();
                
        Set<Integer> allUsersInd = stor.getAllUserIndexes();
        for (Integer oneUserIn: allUsersInd) {
                System.out.println("USER: " + stor.getURI(oneUserIn));
                Set<RatedResource> items = datMan.getRatedResources(stor.getURI(oneUserIn));
                System.out.println("RATINGS: ");
                for (RatedResource item: items) {
                        System.out.println("---------------" + item);
                }
                System.out.println("NEIGHBORS: ");
                Set<RatedResource> neis = datMan.getNeighbors(stor.getURI(oneUserIn));
                for (RatedResource nei: neis) {
                        System.out.println("°°°°°°°°°°°°°°°" + nei);
                }
        }
        */

        // get top recommendations
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        // second top recommendations list for diversity and novelty metrics
        ArrayList<RatedResource[]> topRecommendationsDivNov = new ArrayList<>();

        // Get top recommendations for each user
        if( hasRankingOrGlobalMetric(evalMetrics) ) {

            ArrayList<String> users = getDataManager().getUsers();

            for( int userIndex = 0 ; userIndex < users.size() ; userIndex++ ) {

                // if user has less ratings than number of k folds
                // do not include that user to Precision, Recall, etc. calculations
                if( ignoreUsers.get(userIndex) == true ) {          
                    RatedResource[] tempRatedResources = {};
                    topRecommendations.add(tempRatedResources);
                    topRecommendationsDivNov.add(tempRatedResources);
                    continue;
                }

                Pair<HashSet<String>,Integer> candidatesResult = getCandidates(folds, userIndex, foldIndex, false);
                HashSet<String> candidates = candidatesResult.getValue0();
                int candidatesSize = candidatesResult.getValue1();
                
                if( candidates.isEmpty() ) {
                    RatedResource[] tempRatedResources = {};
                    topRecommendations.add(tempRatedResources);
                }
                else {

                    // TODO - dont do this part if there are no ranking metrics other than 
                    // diversity and novelty
                    RatedResource[] tempRatedResources = sRecommender.getTopRecommendations(users.get(userIndex),
                            candidatesSize, candidates);
                    topRecommendations.add(tempRatedResources);   
                        
                    if( getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.RAT_ITEM) != null 
                            && hasDiversityOrNovelty(evalMetrics) ) {
                        
                        Pair<HashSet<String>,Integer> candidatesResultDivNov = getCandidates(folds, userIndex, foldIndex, true);
                        HashSet<String> candidatesDivNov = candidatesResultDivNov.getValue0();
                        int candidatesSizeDivNov = candidatesResultDivNov.getValue1();
                        
                        RatedResource[] tempRatedResourcesDivNov = sRecommender.getTopRecommendations(users.get(userIndex),
                                candidatesSizeDivNov, candidatesDivNov);
                        topRecommendationsDivNov.add(tempRatedResourcesDivNov); 
                    }
                }              
            }

            // true if error exist
            boolean topRecommendationsEmptyFlag = true;
            for(RatedResource[] resource : topRecommendations) {
                if(resource != null && resource.length > 0) {
                    topRecommendationsEmptyFlag = false;
                    break;
                }
            }

            if(topRecommendationsEmptyFlag) {
                throw new EvaluatorException("ALL FOLDS ARE EMPTY FOR ALL USERS, CHECK NUMBER OF TEST SETS");
            }
        }

        long stopTimeGetRating = System.currentTimeMillis();
        long elapsedTimeGetRating = stopTimeGetRating - startTimeGetRating;
        System.out.println("Time spent for getting ratings from Recommender: "
                + (elapsedTimeGetRating) + "ms (ca. " + (elapsedTimeGetRating / 1000) + " secs).");   
        
        long startTimeCalculate = System.currentTimeMillis();   
                
        ArrayList<Double> evalMetricsResults = new ArrayList<>();
        
        // With recommendations, calculate the performance of the recommender according to different metrics
        for ( int i = 0 ; i < evalMetrics.size() ; i++ ) {
            int tempTopKSize = 0;

            Double res = 0.0;
            if( evalMetrics.get(i) instanceof RankingEvalMetric ) {
                tempTopKSize = ((RankingEvalMetric) evalMetrics.get(i)).getTopKSize();
                
                // diversity and novelty are evaluated with different candidate set and
                // therefore there are different recommendations for them
                if( ( evalMetrics.get(i).getMetric() == EvalMetric.DIVERSITY || 
                        evalMetrics.get(i).getMetric() == EvalMetric.NOVELTY ) &&
                        getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.RAT_ITEM) != null ) {    
                    res = evaluateRankingMetric(evalMetrics.get(i).getMetric(), testSet, 
                        topRecommendationsDivNov, userAverages, ignoreUsers, tempTopKSize, folds, foldIndex);
                }
                else {                                
                    res = evaluateRankingMetric(evalMetrics.get(i).getMetric(), testSet, 
                        topRecommendations, userAverages, ignoreUsers, tempTopKSize, folds, foldIndex);
                }
            }
            if( evalMetrics.get(i) instanceof PredictionEvalMetric ) {
                res = evaluatePredictionMetric(evalMetrics.get(i).getMetric(), 
                        testSet, recommenderRatings, userAverages);
            }
            if( evalMetrics.get(i) instanceof GlobalEvalMetric ) {
                res = evaluateGlobalMetric(evalMetrics.get(i).getMetric(), testSet, 
                        topRecommendations, userAverages, ignoreUsers, tempTopKSize);
            }
              
            evalMetricsResults.add(res);                    
        }
        
        long stopTimeCalculate = System.currentTimeMillis();
        long elapsedTimeCalculate = stopTimeCalculate - startTimeCalculate;
        System.out.println("Time spent for calculating with respect to metrics: " + (elapsedTimeCalculate) 
        + "ms (ca. " + (elapsedTimeCalculate / 1000) + " secs).");      
        
        return evalMetricsResults;
    }
    
    /**
     * Creates set of candidates to be given to the Recommender.
     */
    public Pair<HashSet<String>,Integer> getCandidates(Folds folds, int userIndex, int foldIndex, boolean isDivNov) {
        
        // TODO candidates sizes?
        
        HashSet<String> candidates = new HashSet<>();
        int candidatesSize = 0;
        ArrayList<Rating> ratingsOfUserForCurrentFold = folds.getRatingsOfFold(userIndex, foldIndex);
                
        if( isDivNov ) {

            //create candidates list by removing training set
            candidates.addAll(getDataManager().getItems());

            ArrayList<Rating> ratingsOfUserTraining = new ArrayList<>();
            for( int tempFoldIndex = 0 ; tempFoldIndex < folds.getNumberOfFolds() ; tempFoldIndex++ ) {
                if( tempFoldIndex == foldIndex )
                    continue;
                ratingsOfUserTraining.addAll(folds.getRatingsOfFold(userIndex, tempFoldIndex));                                     
            }

            ratingsOfUserTraining.stream().forEach((removeRating) -> {
                candidates.remove(removeRating.getItemURI());
            });
            
            ArrayList<Integer> topKSizes = getEvaluatorRepository().getCurrentLoadedConfiguration().getTopKSizesToBeEvaluated();
            candidatesSize = topKSizes.get(topKSizes.size() - 1);    
            
            return new Pair<>(candidates,candidatesSize);
        }
        
        // candidates for top K list
        for( int p = 0 ; p < ratingsOfUserForCurrentFold.size() ; p++ ) {
            candidates.add(ratingsOfUserForCurrentFold.get(p).getItemURI());              
        }
        
        // 'Likes' graph pattern
        if( getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.POS_ITEM) != null && 
                getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.NEG_ITEM) == null) {


            //create candidates list by removing training set
            candidates.addAll(getDataManager().getItems());

            ArrayList<Rating> ratingsOfUserTraining = new ArrayList<>();
            for( int tempFoldIndex = 0 ; tempFoldIndex < folds.getNumberOfFolds() ; tempFoldIndex++ ) {
                if( tempFoldIndex == foldIndex )
                    continue;
                ratingsOfUserTraining.addAll(folds.getRatingsOfFold(userIndex, tempFoldIndex));                                     
            }

            ratingsOfUserTraining.stream().forEach((removeRating) -> {
                candidates.remove(removeRating.getItemURI());
            });
            candidatesSize = candidates.size();
        }
        else {                                 
            ArrayList<Integer> topKSizes = getEvaluatorRepository().getCurrentLoadedConfiguration().getTopKSizesToBeEvaluated();
            candidatesSize = topKSizes.get(topKSizes.size() - 1);          
        }
        
        return new Pair<>(candidates,candidatesSize);
    }
    
    /**
     * Checks if there exist a Prediction Metric in the metrics list.
     * 
     * @return True if one of the evaluation metrics is MAE or RMSE, otherwise False
     */
    private boolean hasPredictionMetric(ArrayList<GenericEvalMetric> metrics) {
        
        return metrics.stream().anyMatch((metricWrapper) -> ( 
                   metricWrapper.getMetric() == EvalMetric.MAE 
                || metricWrapper.getMetric() == EvalMetric.RMSE
                || metricWrapper.getMetric() == EvalMetric.AUC));
    }

    /**
     * Checks if there exist a Ranking or Global Metric in the metrics list.
     * By this, it checks necessity of calculating top recommendations for each user in the test set.
     *  
     * @param metrics
     * @return True if one of the evaluation metrics is PRE, REC, FMEASURE, MRR,
     * NDCG, DIVERSITY, NOVELTY or COVERAGE, otherwise False
     */
    public boolean hasRankingOrGlobalMetric(ArrayList<GenericEvalMetric> metrics) {
        
        return metrics.stream().anyMatch((metricWrapper) -> ( 
                   metricWrapper.getMetric() == EvalMetric.PRE
                || metricWrapper.getMetric() == EvalMetric.REC
                || metricWrapper.getMetric() == EvalMetric.F_MEASURE
                || metricWrapper.getMetric() == EvalMetric.MRR
                || metricWrapper.getMetric() == EvalMetric.NDCG
                || metricWrapper.getMetric() == EvalMetric.DIVERSITY
                || metricWrapper.getMetric() == EvalMetric.NOVELTY
                || metricWrapper.getMetric() == EvalMetric.ACC
                || metricWrapper.getMetric() == EvalMetric.COVERAGE ));
    }

    /**
     * Checks if there exist Diversity Metric or Novelty Metric in the metrics list.
     * 
     * @return True if one of the evaluation metrics is DIVERSITY or NOVELTY, otherwise False
     */
    private boolean hasDiversityOrNovelty(ArrayList<GenericEvalMetric> metrics) {
        
        return metrics.stream().anyMatch((metricWrapper) -> ( 
                   metricWrapper.getMetric() == EvalMetric.DIVERSITY 
                || metricWrapper.getMetric() == EvalMetric.NOVELTY) );
    }
    
    /**
     * Creates result object using the results for each metric for each fold.
     * 
     * Calculates F-Measure.
     * 
     * Takes the averages and adds it as a result object.
     * 
     * @param evalMetricLists folds - metrics (outer to inner)
     * @param recConfig 
     */
    public void calculateResultsForRecConfig(ArrayList<ArrayList<Double>> evalMetricLists, 
            RecConfig recConfig) {
        
        EvaluationResult evResult = new EvaluationResult(recConfig.getConfigurationName());
        
        ArrayList<GenericEvalMetric> metrics = 
            getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalMetrics();
        
        // for each fold
        for( int i = 0 ; i < evalMetricLists.size() ; i++ ) {   
            
            EvaluationResultsForFold erFold = new EvaluationResultsForFold(); 
       
            for( int j = 0 ; j < metrics.size() ; j++ ) {             
                
                if( metrics.get(j).getMetric() == EvalMetric.F_MEASURE ) {
                    if( metrics.get(j-2).getMetric() == EvalMetric.PRE && metrics.get(j-1).getMetric() == EvalMetric.REC ) {

                        Double precisionValue = evalMetricLists.get(i).get(j-2);
                        Double recallValue    = evalMetricLists.get(i).get(j-1);
                        erFold.addResult(metrics.get(j),calculateFMeasure(precisionValue, recallValue));  
                        continue;
                    }            
                }                       
                
                erFold.addResult(metrics.get(j),evalMetricLists.get(i).get(j));  
            }
            evResult.addFoldResults(erFold);
        }  
        
        ArrayList<EvaluationResultsForFold> resultsArray = evResult.getAllFoldResults();
        
        EvaluationResultsForFold resultsAverages = new EvaluationResultsForFold();
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {        
                        
            double metricsEvalTotal = 0.0;
            
            for( int j = 0 ; j < resultsArray.size() ; j++ ) {                
                metricsEvalTotal += resultsArray.get(j).getMetricScores().get(i);            
            }
            
            resultsAverages.addResult(metrics.get(i), metricsEvalTotal/evalMetricLists.size());
        }   
        evResult.setOverallPerformance(resultsAverages);
        addEvalResult(evResult);
    }

    /**
     * Writes ignored Users' UserURIs to the log file.
     */
    private void logUsersToIgnore() {                 
        
        ArrayList<Boolean> ignoreUsers = getDataManager().getUsersToIgnore();
        ArrayList<String> users = getDataManager().getUsers();
        ArrayList<String> ignoredUsers = new ArrayList<>();
        
        for( int i = 0 ; i < ignoreUsers.size() ; i++ ) {
            if( ignoreUsers.get(i) == true ) {
                ignoredUsers.add(users.get(i));
            }
        }
        
        if( ignoredUsers.isEmpty() ) {
            LOGGER.info("All of the users have number of ratings more than fold size.");    
            LOGGER.info("");            
        }
        else {
            LOGGER.warn( ignoredUsers.size() + " number of users have number of ratings less than fold size. The list of users: ");    
            for( int i = 0 ; i < ignoredUsers.size() ; i++ ) {
		LOGGER.info(ignoredUsers.get(i) + "\n"); 
            } 
            LOGGER.warn("These users are ignored and not included into Cross K Fold Validation calculation.");                    
            LOGGER.info("");                    
        }
    }

    public ArrayList<Rating> createTestSet(ArrayList<Boolean> ignoreUsers, Folds folds, int i) {
               
        ArrayList<Rating> testSet = new ArrayList<>();   
                
        // include ratings of users who are not ignored to the test set
        for( int j = 0 ; j < ignoreUsers.size() ; j++ ) {     
            if( ignoreUsers.get(j) == false ) {
                testSet.addAll(folds.getRatingsOfFold(j, i));
            }
        }
        
        return testSet;
    }
}
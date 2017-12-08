/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommendereval.datamanager.EvalDataManager;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResultsForFold;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;
import org.eclipse.rdf4j.recommendereval.util.RatingPredictionPair;
import org.eclipse.rdf4j.recommendereval.util.RatingPredictionPairComparator;

public abstract class AbstractEvaluator implements Evaluator {
    
    private SailRecEvaluatorRepository evaluatorRepository = null;

    private EvalDataManager dataManager = null;    
    
    private ArrayList<EvaluationResult> recommendersResults;

    private int nativeStoreCounter = 1;
    
    public AbstractEvaluator(SailRecEvaluatorRepository evaluatorRepository) {
        this.evaluatorRepository = evaluatorRepository;
        recommendersResults = new ArrayList<>();
        dataManager = new EvalDataManager(evaluatorRepository.getCurrentLoadedConfiguration(),
                    evaluatorRepository.getCurrentLoadedConfiguration().getEvalEntityMap()); 
    }

    @Override
    public void preprocess(List<RecConfig> configurations) throws EvaluatorException {
        
        System.out.println("Evaluator preprocess starting... Evaluator will fetch, store and sort ratings...");
        long startTime = System.currentTimeMillis();
        
        dataManager.fetchRatings(getEvaluatorRepository(),configurations); 
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("EVALUATOR PREPROCESS... COMPLETED");
        System.out.println("Total time spent for preprocess: " + (elapsedTime) 
                + "ms (ca. " + (elapsedTime / 1000) + " secs).");
    }

    @Override
    public void writeOutput(EvalOutput outputMethod, ArrayList<GenericEvalMetric> evalMetrics) {            
        
        if( outputMethod == EvalOutput.EXTERNAL_FILE ) {
            writeToFile(evalMetrics);
        }        
        else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    /** 
     * Returns Folds with respect to the given number of folds. 
     * 
     * @param numberOfFolds
     * @return 
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException 
     */
    public Folds splitRatingsKFold(int numberOfFolds) 
            throws EvaluatorException {
        return dataManager.splitRatingsKFold(numberOfFolds,
                getEvaluatorRepository().getCurrentLoadedConfiguration().isReproducible());
    }
     
    @Override
    public Double evaluatePredictionMetric(EvalMetric metric, ArrayList<Rating> testSet,
            ArrayList<Double> recommenderRatings, ArrayList<Double> userAverages ) {
        
        System.gc();
        
        // It doesn't matter if it is "ratings" or "likes"(binary) or 
        // Cross Domain Recommender.  
        
        if( null != metric ) switch (metric) {
            case MAE:
                return calculateMAE(testSet, recommenderRatings);
            case RMSE:
                return calculateRMSE(testSet, recommenderRatings);
            case AUC:
                return calculateAUC(testSet, recommenderRatings, userAverages);
            default:
                break;
        }
        
        return 0.0;
    }
     
    @Override
    public Double evaluateRankingMetric(EvalMetric metric, ArrayList<Rating> datasetTestSet,
            ArrayList<RatedResource[]> topRecommendations, ArrayList<Double> userAverages, 
            ArrayList<Boolean> ignoreUsers, int topKSize, Folds folds, int foldIndex) {
        
        System.gc();
        
        // It doesn't matter if it is "ratings" or "likes"(binary) or 
        // Cross Domain Recommender.  
        
        if( null != metric ) switch (metric) {
            case PRE:
                return calculatePrecision(topRecommendations, datasetTestSet, userAverages, ignoreUsers, topKSize);
            case REC:
                return calculateRecall(topRecommendations, datasetTestSet, userAverages, ignoreUsers, topKSize);
            case ACC:
                return calculateAccuracy(topRecommendations, datasetTestSet, userAverages, ignoreUsers, topKSize, folds);
            case MRR:
                return calculateMRR(topRecommendations, datasetTestSet, userAverages, ignoreUsers);
            case NDCG:
                return calculateNDCG(topRecommendations, userAverages, ignoreUsers, topKSize, folds, foldIndex);
            case DIVERSITY:
                return calculateDiversity(topRecommendations, ignoreUsers, topKSize);
            case NOVELTY:
                return calculateNovelty(topRecommendations, ignoreUsers, topKSize, folds, foldIndex);
            default:
                break;
        }
        
        return 0.0;
    }
     
    @Override
    public Double evaluateGlobalMetric(EvalMetric metric, ArrayList<Rating> datasetTestSet,
            ArrayList<RatedResource[]> topRecommendations, ArrayList<Double> userAverages, 
            ArrayList<Boolean> ignoreUsers, int topKSize) {
        
        System.gc();
        
        // It doesn't matter if it is "ratings" or "likes"(binary) or 
        // Cross Domain Recommender.  
        
        if( null != metric ) switch (metric) {
            case COVERAGE:
                return calculateCoverage(topRecommendations, ignoreUsers);
            default:
                break;
        }
        
        return 0.0;
    }

    /**
     * Calculates Normalized Discounted Cumulative Gain value of the Recommender.
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages
     * @param ignoreUsers Users to be ignored marked true
     * @param topKSize
     * @param folds
     * @param foldIndex
     * @return 
     */
    public Double calculateNDCG(ArrayList<RatedResource[]> topRecommendations, 
            ArrayList<Double> userAverages, ArrayList<Boolean> ignoreUsers, 
            int topKSize, Folds folds, int foldIndex) {
        
        Double sum = 0.0;
        double totalSize = 0.0;        
        
        // for every user
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            // Get actual ratings of "i"th user
        
            // Get topRecommendations list as Ratings list
            // Cannot get this from test set since some recommended items might not 
            // be in test set.
            ArrayList<Rating> tempActRatings  = convertRatings(topRecommendations.get(i),i);
            ArrayList<Double> listForIdcgRelevance = new ArrayList<>();
            
            totalSize++;
            
            if( tempActRatings.isEmpty() ) {
                continue;
            }        
            
            Double dcg = 0.0;            
                
            for( int j = 0 ; j < tempActRatings.size() ; j++ ) { 
                
                double tempRelevance = getNDCGRelevance( userAverages.get(i), 
                        tempActRatings.get(j), folds, foldIndex, i);
                double log = ( Math.log(j+2) / Math.log(2) );
                dcg  += tempRelevance / log; // log2(j+1) in formula
                listForIdcgRelevance.add(tempRelevance);
            }          
            Collections.sort(listForIdcgRelevance);
            Collections.reverse(listForIdcgRelevance);
            
            Double idcg = 0.0;
            
            for( int j = 0 ; j < listForIdcgRelevance.size() ; j++ ) { 
                idcg += listForIdcgRelevance.get(j) 
                        / ( Math.log(j+2) / Math.log(2) );
            }
            
            if( idcg == 0.0) { }
            else {
                // ndcg = dcg / idcg
                // add it to sum since we will have the average ndcg of all users
                sum += dcg / idcg ; 
            }            
        }
        
        if( totalSize == 0) {
            return 0.0;
        }
        
        Double result = sum / totalSize;
        
        return result;
    }

    /**
     * Calculates Precision value of the Recommender.
     * 
     * For likes(binary) ratings:
     * 
     * The rating that recommender gives is not important since users' ratings 
     * will be used. If a rating is not found in the user's ratings, method will 
     * create a rating with 0.0. 
     * 
     * (For all users) User Average will be 1.0. At the following code:
     * currRating.getRatingValue() >= avg
     * it will be 1.0 >= 1.0 for true positives and 
     * avg > currRating.getRatingValue()
     * 1.0 > 0.0 for false positives
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages from the training set
     * @param ignoreUsers User to be ignored are marked true
     * @param topKSize 
     * 
     * @return 
     */
    private Double calculatePrecision(ArrayList<RatedResource[]> topRecommendations, ArrayList<Rating> datasetTestSet,
            ArrayList<Double> userAverages, ArrayList<Boolean> ignoreUsers, int topKSize) {
        
        ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();
        
        Double sum = 0.0;
        double totalSize = 0.0;        
        
        // for every user
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            Double avg = userAverages.get(i);
            // Top K List of the "i"th user
            RatedResource[] recommendations = topRecommendations.get(i);
            
            totalSize++;
            System.out.println("---------------Evaluator Recommender---------------");
            System.out.println("---------------user evaluator Recommender---------------" + dataManager.getUsers().get(i));
            System.out.println("---------------topkRecommendation---------------");
            for(RatedResource r: recommendations) {
            	System.out.println(r.getResource().toString());
            }
            double truePositive  = 0.0;
            double falsePositive = 0.0;                 
                    
            for( int j = 0 ; j < topKSize ; j++ ) {
                
                if( j >= recommendations.length ) {
                    falsePositive++;
                    continue;
                }
                if(  recommendations[j] == null ) {
                    falsePositive++;
                    continue;
                }
                
                // if the rating is not in the user's ratings, a rating with 0.0
                // will be created, it will be a falsePositive
                Rating currRating = findRatingWithItemUriSafe(usersRatings.get(i), 
                        recommendations[j].getResource(),dataManager.getUsers().get(i));     
                
                if( !datasetTestSet.contains(currRating) ) {
                    falsePositive++;
                    continue;
                }
                
                // true positive
                if( currRating.getRatingValue() >= avg) {
                    truePositive++;
                }
                // false positive
                else if( currRating.getRatingValue() < avg) {
                    falsePositive++;
                }
            }          
            
            if ((truePositive + falsePositive) == 0.0) {
                // no need to add 0
            } 
            else {
                sum += truePositive / (truePositive + falsePositive);
            }
        }
        
        if( totalSize == 0) {
            return 0.0;
        }
        
        Double result = sum / totalSize;
        
        return result;
    }
    
    
    /**
     * Calculates Recall value of the Recommender.
     *      
     * For likes(binary) ratings:
     * 
     * The rating that recommender gives is not important since users' ratings 
     * will be used. If a rating is not found in the user's ratings, method will 
     * create a rating with 0.0. 
     * 
     * (For all users) User Average will be 1.0. At the following code:
     * currRating.getRatingValue() >= avg
     * it will be 1.0 >= 1.0 for true positives 
     * For false negatives it will never get into 'continue' line when comparing
     * averages. 
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages from the training set
     * @param ignoreUsers User to be ignored are marked true
     * @param topKSize 
     * 
     * @return 
     */
    private Double calculateRecall(ArrayList<RatedResource[]> topRecommendations, ArrayList<Rating> datasetTestSet,
            ArrayList<Double> userAverages, ArrayList<Boolean> ignoreUsers, int topKSize) {
        
        ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();
        Double sum = 0.0;
        double totalSize = 0.0;
        
        // for every user
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            Double avg = userAverages.get(i);
            // Top K List of the "i"th user
            RatedResource[] recommendations = topRecommendations.get(i);
                
            totalSize++;
            
            double truePositive  = 0.0;
            double falseNegative = 0.0;
            
            for( int j = 0 ; j < topKSize ; j++ ) {
                
                if( j >= recommendations.length ) {
                    break;
                }
                
                if(  recommendations[j] == null ) {
                    continue;
                }
                
                // if the rating is not in the user's ratings, a rating with 0.0
                // will be created
                Rating currRating = findRatingWithItemUriSafe(usersRatings.get(i), 
                        recommendations[j].getResource(),dataManager.getUsers().get(i));   
                
                if( !datasetTestSet.contains(currRating) ) {
                    continue;
                }
                
                // true positive
                if( currRating.getRatingValue() >= avg) {
                    truePositive++;
                }
            }          
            
            for( int j = 0 ; j < datasetTestSet.size() ; j++ ) {
                
                // TODO can be improved by changing this list to a 2d one
                if( !datasetTestSet.get(j).getUserURI().equals( dataManager.getUsers().get(i) ) ) {
                    continue;
                }
                
                if( datasetTestSet.get(j).getRatingValue() < avg ) {
                    continue;
                }
                
                boolean falseNegativeFlag = false;
                
                for (RatedResource recommendation : recommendations) {
                    if (recommendation == null) {
                        continue;
                    }
                    // item recommended
                    if (datasetTestSet.get(j).getItemURI().equals(recommendation.getResource())) {
                        falseNegativeFlag = true;
                        break;
                    }
                }
                // a relevant item not recommended
                if( !falseNegativeFlag ) {
                    falseNegative++;
                }
            }
            
            if ((truePositive + falseNegative) == 0.0) {
                // no need to add 0
            } 
            else {
                sum += truePositive / (truePositive + falseNegative);
            }
        }
        
        if( totalSize == 0) {
            return 0.0;
        }
        
        Double result = sum / totalSize;
        
        return result;
    }
    
    // TODO discuss TN for binary ratings
    /**
     * Calculates Accuracy value of the Recommender.
     *      
     * For likes(binary) ratings:
     * 
     * The rating that recommender gives is not important since users' ratings 
     * will be used. If a rating is not found in the user's ratings, method will 
     * create a rating with 0.0. 
     * 
     * (For all users) User Average will be 1.0. At the following code:
     * currRating.getRatingValue() >= avg
     * it will be 1.0 >= 1.0 for true positives 
     * For false negatives it will never get into 'continue' line when comparing
     * averages. 
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages from the training set
     * @param ignoreUsers User to be ignored are marked true
     * @param topKSize 
     * 
     * @return 
     */
    private Double calculateAccuracy(ArrayList<RatedResource[]> topRecommendations, ArrayList<Rating> datasetTestSet,
            ArrayList<Double> userAverages, ArrayList<Boolean> ignoreUsers, int topKSize, Folds folds) {
        
        ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();
        Double sum = 0.0;
        double totalSize = 0.0;
        
        // for every user
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            Double avg = userAverages.get(i);
            // Top K List of the "i"th user
            RatedResource[] recommendations = topRecommendations.get(i);
                
            totalSize++;
            
            double truePositive  = 0.0;
            double falsePositive = 0.0;     
            double falseNegative = 0.0;
            double trueNegative  = 0.0;
            
            for( int j = 0 ; j < topKSize ; j++ ) {
                
                if( j >= recommendations.length ) {
                    falsePositive++;
                    continue;
                }
                if(  recommendations[j] == null ) {
                    falsePositive++;
                    continue;
                }
                
                // if the rating is not in the user's ratings, a rating with 0.0
                // will be created, it will be a falsePositive
                Rating currRating = findRatingWithItemUriSafe(usersRatings.get(i), 
                        recommendations[j].getResource(),dataManager.getUsers().get(i));     
                
                if( !datasetTestSet.contains(currRating) ) {
                    falsePositive++;
                    continue;
                }
                
                // true positive
                if( currRating.getRatingValue() >= avg) {
                    truePositive++;
                }
                // false positive
                else if( currRating.getRatingValue() < avg) {
                    falsePositive++;
                }
            }          
            
            for( int j = 0 ; j < datasetTestSet.size() ; j++ ) {
                
                // TODO can be improved by changing this list to a 2d one
                if( !datasetTestSet.get(j).getUserURI().equals( dataManager.getUsers().get(i) ) ) {
                    continue;
                }
                
                boolean foundFlag = false;
                
                for (RatedResource recommendation : recommendations) {
                    if (recommendation == null) {
                        continue;
                    }
                    // item recommended
                    if (datasetTestSet.get(j).getItemURI().equals(recommendation.getResource())) {
                        foundFlag = true;
                        break;
                    }
                }
                
                // an item not recommended
                if( !foundFlag ) {
                    if(datasetTestSet.get(j).getRatingValue() < avg) {
                        trueNegative++;
                    }
                    else {
                        falseNegative++;
                    }
                }
            }
            
            // positive feedback case
            if( getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.POS_ITEM) != null && 
                    getEvaluatorRepository().getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.NEG_ITEM) == null) {
            
                // unrated items of the user 
                List<String> otherCandidates = new ArrayList<>();
            
                //create candidates list by removing training set
                otherCandidates.addAll(getDataManager().getItems());

                ArrayList<Rating> ratingsOfUserTraining = new ArrayList<>();
                for( int tempFoldIndex = 0 ; tempFoldIndex < folds.getNumberOfFolds() ; tempFoldIndex++ ) {
                    ratingsOfUserTraining.addAll(folds.getRatingsOfFold(i, tempFoldIndex));                                     
                }

                ratingsOfUserTraining.stream().forEach((removeRating) -> {
                    otherCandidates.remove(removeRating.getItemURI());
                });
                
                
                for( int j = 0 ; j < otherCandidates.size() ; j++ ) {
                    boolean foundFlag = false;
                    for (RatedResource recommendation : recommendations) {
                        if (recommendation == null) {
                            continue;
                        }
                        // item recommended
                        if (otherCandidates.get(j).equals(recommendation.getResource())) {
                            foundFlag = true;
                            break;
                        }
                    }
                    
                    // an item not recommended
                    if( !foundFlag ) {
                        trueNegative++;
                    }
                }
            }
            
            
            double divisor = truePositive + trueNegative + falsePositive + falseNegative;
            
            if ( divisor != 0.0 ) {
                sum += (truePositive + trueNegative) / divisor;
            }
        }
        
        if( totalSize == 0) {
            return 0.0;
        }
        
        Double result = sum / totalSize;
        
        return result;
    }
    
    /**
     * Calculates MRR value of the Recommender.
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param userAverages User averages for the training set
     * @param ignoreUsers User to be ignored are marked true
     * 
     * @return 
     */
    private Double calculateMRR(ArrayList<RatedResource[]> topRecommendations, ArrayList<Rating> datasetTestSet,
            ArrayList<Double> userAverages, ArrayList<Boolean> ignoreUsers) {    
        
        Double sum = 0.0;
        double totalSize = 0.0;
        
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            Double avg = userAverages.get(i);
            RatedResource[] recommendations = topRecommendations.get(i);
            
            if( recommendations.length == 0 ) {
                continue;
            } 
            
            double rank = 1.0;
            totalSize++;
            
            for (RatedResource recommendation : recommendations) {
                
                if( recommendation == null ) {
                    rank++;
                    continue;                
                }
                
                // if the rating is not in the user's ratings, a rating with 0.0
                // will be created
                Rating currRating = findRatingWithItemUriSafe(dataManager.getUsersRatings().get(i), 
                        recommendation.getResource(),dataManager.getUsers().get(i));   
                
                if( !datasetTestSet.contains(currRating) ) {
                    rank++;
                    continue;
                }
                
                if( currRating.getRatingValue() >= avg ) {
                    sum += 1/rank;
                    break;
                }
                rank++;
            }            
        }
        
        if( totalSize == 0.0 ) {
            return 0.0;
        }
        
        Double result = sum / totalSize;   
        
        return result;
    }

    /**
     * Calculates AUC value of the Recommender.
     * 
     * Idea taken from: 
     * http://stats.stackexchange.com/questions/105501/understanding-roc-curve
     * 
     * @param actualRatings Actual ratings of the items in the test set
     * @param recommenderRatings Ratings of the items in the test set obtained from recommender
     * @return 
     */
    private Double calculateAUC(ArrayList<Rating> actualRatings, 
            ArrayList<Double> recommenderRatings, ArrayList<Double> userAverages) {
        
        Double area = 0.0;
        double aboveAvg = 0.0;
        double belowAvg = 0.0;
        
        if( actualRatings.isEmpty() ) {
            return 0.5;
        }
        
        ArrayList<RatingPredictionPair> ratingPairs = new ArrayList<>();
        ArrayList<Boolean> relevanceList = new ArrayList<>();
        
        for( int i = 0 ; i < actualRatings.size() ; i++ ) { 
            ratingPairs.add(new RatingPredictionPair(actualRatings.get(i), 
                    recommenderRatings.get(i))  );
        }
        
        Collections.sort( ratingPairs, new RatingPredictionPairComparator() );
        
        for( int i = 0 ; i < ratingPairs.size() ; i++ ) {
            
            // calculate number of items that are positive(above average) or 
            // negative(below average)                     
            
            if(  ratingPairs.get(i).rating == null ) {
                belowAvg++;
                relevanceList.add(Boolean.FALSE);
                continue;
            }   
            
            int indexOfUser = dataManager.getUsers().indexOf(ratingPairs.get(i).rating.getUserURI());
            double averageOfUser = userAverages.get(indexOfUser);
            
            if( ratingPairs.get(i).rating.getRatingValue() >= averageOfUser ) {
                aboveAvg++;
                relevanceList.add(Boolean.TRUE);
            }
            else if( ratingPairs.get(i).rating.getRatingValue() < averageOfUser ) {
                belowAvg++;
                relevanceList.add(Boolean.FALSE);
            }
        }

        // get points on the graph
        ArrayList<Double> xAxis = new ArrayList<>();
        ArrayList<Double> yAxis = new ArrayList<>();                
        Double prevX = 0.0;
        Double prevY = 0.0;
            
        
        for( int i = 0 ; i < ratingPairs.size() ; i++ ) {

            if( relevanceList.get(i) ) {
                prevY += 1 / aboveAvg; 
            }
            else {
                prevX += 1 / belowAvg;
            }
            xAxis.add(prevX);
            yAxis.add(prevY);
        }   
            
        // calculate the area using the points
        prevX = 0.0;
        prevY = 0.0;

        for( int j = 0 ; j < xAxis.size() ; j++ ) {

            // dont calculate the area, move to the next
            if( Math.abs( yAxis.get(j) - prevY ) <= 0.000001 ) {
                // movement to right hand side direction, change X
                prevX = xAxis.get(j);
            }
            // calculate the area
            else {
                if( aboveAvg != 0) {
                    area += ( 1 / aboveAvg ) * ( 1 - xAxis.get(j) );
                }
                prevY = yAxis.get(j);
            }
        }
        
        if( area > 1.0 ) {
            area = 1.0;
        }
        
        return area;
    }
    
    /**
     * 
     * Calculates Diversity value of the Recommender. ILD formula from RecSySHandbook 
     * is used.
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param ignoreUsers User to be ignored are marked true
     * @param topKSize
     * @return 
     */
    private Double calculateDiversity(ArrayList<RatedResource[]> topRecommendations, 
            ArrayList<Boolean> ignoreUsers, int topKSize) {
        
        Double sum = 0.0;
        double totalSize = 0.0;
        
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            RatedResource[] recommendations = topRecommendations.get(i);
            
            if( recommendations.length == 0 ) {
                continue;
            } 
            
            totalSize++;
            ArrayList<String> tempItems = new ArrayList<>();
            
            for (RatedResource recommendation : recommendations) {
                
                if( recommendation == null ) {
                    continue;                
                }
                tempItems.add(recommendation.getResource());
            }            
            
            for( int p = 0 ; p < tempItems.size() ; p++ ) {
                
                for( int r = 0 ; r < tempItems.size() ; r++ ) {
                    
                    // TODO discuss 
                    if( r == p ) {
                        continue;
                    }   
                    
                    Double score = getItemDistance(tempItems.get(p), tempItems.get(r));                    
                    
                    double interRes = score / 
                            ( topKSize * ( topKSize - 1 ));
                    
                    sum += interRes;
                }
            }
        }
        
        if( totalSize == 0.0 ) {
            return 0.0;
        }
        
        Double result = sum / totalSize;   
        
        return result;
    }

    /**
     * Calculates Novelty value of the Recommender. Unexp formula from RecSySHandbook 
     * is used.
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param ignoreUsers User to be ignored are marked true
     * 
     * @return 
     */
    private Double calculateNovelty(ArrayList<RatedResource[]> topRecommendations, 
            ArrayList<Boolean> ignoreUsers, int topKSize, Folds folds, int foldIndex) {
        
        Double sum = 0.0;
        double totalSize = 0.0;
        
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            RatedResource[] recommendations = topRecommendations.get(i);
            
            if( recommendations.length == 0 ) {
                continue;
            } 
            
            totalSize++;
            ArrayList<String> recItems = new ArrayList<>();
            
            for (RatedResource recommendation : recommendations) {
                
                if( recommendation == null ) {
                    continue;                
                }
                recItems.add(recommendation.getResource());
            }      
            
            // Create i th user's item list
            ArrayList<String> usersItems = new ArrayList<>();
            
            for( int k = 0 ; k < folds.getNumberOfFolds() ; k++ ) {
                
                if( k == foldIndex )
                    continue;
                
                ArrayList<Rating> ratingsForItemStrings = folds.getRatingsOfFold(i, k);
                for( int m = 0 ; m < ratingsForItemStrings.size() ; m++ )  {
                    usersItems.add(ratingsForItemStrings.get(m).getItemURI());
                }
            }
            
            // Performance can be improved
            for( int p = 0 ; p < recItems.size() ; p++ ) {
                
                for( int r = 0 ; r < usersItems.size() ; r++ ) {   
                    
                    // distance = 0.0
                    if( recItems.get(p).equals(usersItems.get(r)) ) {
                        continue;
                    }
                    
                    double score = getItemDistance(recItems.get(p), usersItems.get(r));
                    double interRes = score / 
                            ( topKSize * ( usersItems.size() ));
                    sum += interRes;
                }
            }
        }
        
        if( totalSize == 0.0 ) {
            return 0.0;
        }
        
        Double result = sum / totalSize;   
        
        return result;
    }

    /**
     * Calculates Coverage value of the Recommender.
     * 
     * @param topRecommendations Top recommendations that are obtained from recommender
     * @param ignoreUsers User to be ignored are marked true
     * 
     * @return 
     */
    private Double calculateCoverage(ArrayList<RatedResource[]> topRecommendations, 
            ArrayList<Boolean> ignoreUsers) {
        
        ArrayList<Integer> itemCount = new ArrayList<>();
        
        // fill novelty arraylist
        dataManager.getItems().stream().forEach((_item) -> {
            itemCount.add(0);
        }); 
        
        for( int i = 0 ; i < dataManager.getUsers().size() ; i++ ) {
            
            if( ignoreUsers.get(i) == true ) {
                continue;
            }
            
            RatedResource[] recommendations = topRecommendations.get(i);
            
            if( recommendations.length == 0 ) {
                continue;
            } 
            
            for (RatedResource recommendation : recommendations) {
                
                if( recommendation == null ) {
                    continue;                
                }
                
                int index = dataManager.getItems().indexOf(recommendation.getResource());
                int value = itemCount.get(index);
                itemCount.set(index, (value+1));
            }            
        }
        
        Double zeroItems = 0.0;
        
        for( int i = 0 ; i < itemCount.size() ; i++ ) {        
            if( itemCount.get(i) == 0 ) {
                zeroItems++;
            }
        }
        
        if( itemCount.isEmpty() ) 
            return 0.0;
        
        
        return 1 - ( zeroItems / itemCount.size() );
    }
    
    /**
     * Calculates MAE value of the Recommender.
     * 
     * @param actualRatings Actual ratings of the items in the test set
     * @param recommenderRatings Ratings of the items in the test set obtained from recommender
     * @return 
     */
    private Double calculateMAE(ArrayList<Rating> actualRatings, ArrayList<Double> recommenderRatings) {
        
        Double totalError = 0.0;
        int count = 0;         
        
        for (int i = 0; i < actualRatings.size() ; i++ ) {
            count++;
            if( recommenderRatings.get(i) == null ) {
                totalError += actualRatings.get(i).getRatingValue();
                continue;
            }
            totalError += Math.abs(recommenderRatings.get(i) - actualRatings.get(i).getRatingValue());
        }
        
        if( count == 0 ) {
            return 0.0;
        }
        
        Double result = (Double) (totalError/count);
        
        return Math.sqrt(result);         
    }

    /**
     * Calculates RMSE value of the Recommender.
     * 
     * @param actualRatings Actual ratings of the items in the test set
     * @param recommenderRatings Ratings of the items in the test set obtained from recommender
     * @return 
     */
    private Double calculateRMSE(ArrayList<Rating> actualRatings, ArrayList<Double> recommenderRatings) {  
        
        Double totalError = 0.0;
        int count = 0;

        for (int i = 0; i < actualRatings.size() ; i++ ) {
            count++;
            if( recommenderRatings.get(i) == null ) {
                totalError += actualRatings.get(i).getRatingValue() * actualRatings.get(i).getRatingValue();
                continue;
            }
            Double error = Math.abs(recommenderRatings.get(i) - actualRatings.get(i).getRatingValue());
            totalError += error*error;
        }
        
        if( totalError == 0.0 || count == 0.0 ) 
            return 0.0;
        
        Double result = (Double) (totalError/count);
        
        return Math.sqrt(result);        
    }

    /**
     * Calculates F-Measure value for given Precision and Recall values.
     * 
     * @param precision
     * @param recall
     * @return 
     */
    public Double calculateFMeasure(Double precision, Double recall) {  
                            
        if( ( precision + recall ) == 0.0 ) {
            return 0.0;
        }
        else {
            Double fmeasure = 2 * ( precision * recall ) 
                                / ( precision + recall );
            return fmeasure;
        }
    }
    
    /**
     * Computes item distance.
     * 
     * @param itemUri1
     * @param itemUri2
     * @return 
     */
    public double getItemDistance(String itemUri1, String itemUri2) {
        
        ArrayList<String> itemFeatures1 = new ArrayList<>();
        ArrayList<String> itemFeatures2 = new ArrayList<>();
        
        int featuresSize1 = 0;
        int featuresSize2 = 0;
        
        int itemIndex1 = getDataManager().getItems().indexOf(itemUri1);
        int itemIndex2 = getDataManager().getItems().indexOf(itemUri2);
        
        if( itemIndex1 == -1 || itemIndex2 == -1 ) {
            return 1.0;
        }
        if( itemIndex1 == itemIndex2 ) {
            return 0.0;
        }
        
        itemFeatures1.addAll(getDataManager().getItemFeatures().get(itemIndex1));
        itemFeatures2.addAll(getDataManager().getItemFeatures().get(itemIndex2));
        
        featuresSize1 = itemFeatures1.size();
        featuresSize2 = itemFeatures2.size();
        
        // Make lists have the same size 
        if( itemFeatures1.size() > itemFeatures2.size() ) {
            int difference = itemFeatures1.size() - itemFeatures2.size();
            
            for( int i = 0 ; i < difference ; i++ ) {
                itemFeatures2.add("zero");
            }
        }
        else if( itemFeatures1.size() < itemFeatures2.size() ) {
            int difference = itemFeatures2.size() - itemFeatures1.size();
            
            for( int i = 0 ; i < difference ; i++ ) {
                itemFeatures1.add("zero");
            }
        }
       
        double sum = 0.0;
        
        for( int i = 0 ; i < itemFeatures1.size() ; i++ ) {
            if( itemFeatures2.contains(itemFeatures1.get(i)) ) {
                sum++;
            }
        }
        
        double similarity = 0.0;
        
        if( !itemFeatures1.isEmpty() ) {
            similarity = sum / ( Math.sqrt( (double)featuresSize1) * Math.sqrt( (double)featuresSize2 ) );
        }        
    
        double distance = 1.0 - similarity;
        
        return distance;
    }
    
    /**
     * Generates relevance value for NDCG metric. 
     * 
     * Scores:
     * 0.0 non-relevant
     * 1.0 unknown
     * 2.0 relevant
     * 
     * @param userAverage
     * @param rating
     * @param folds
     * @param foldIndex
     * @param userIndex
     * @return 
     */
    public double getNDCGRelevance(Double userAverage, Rating rating, Folds folds, 
            int foldIndex, int userIndex) {
        
        if( rating == null || ( rating.getUserURI().equals("") && rating.getItemURI().equals("") 
                && rating.getRatingValue() == 0.0) ) {
            return 0.0;
        }
        
        for( int i = 0 ; i < folds.getNumberOfFolds() ; i++ ) {
            
            ArrayList<Rating> ratingsOfFold_i = folds.getRatingsOfFold(userIndex, i);
            
            for( Rating toCompare : ratingsOfFold_i ) {
                if( toCompare.getUserURI().equals(rating.getUserURI()) &&
                        toCompare.getItemURI().equals(rating.getItemURI()) ) {
                    
                    // in the current test set
                    if( i == foldIndex ) {
                        // relevant
                        if( rating.getRatingValue() >= userAverage ) {
                            return 2.0;
                        }
                        else {
                            return 0.0;
                        }
                    }
                    // in the current training set
                    else {
                        return 0.0;
                    }
                }
            }            
        }
        
        // not found = unknown
        return 1.0;
    }
    
    /**
     * Helper method for writing output to a file.
     */
    private void writeToFile(ArrayList<GenericEvalMetric> evalMetrics) {
        
        try {
            
            FileOutputStream fos = new FileOutputStream(
                    getEvaluatorRepository().getCurrentLoadedConfiguration().getOutputPath());
            OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");
            
            try (BufferedWriter bw = new BufferedWriter(w)) {
                //bw.write("sep=,");
                //bw.write('\n');
                
                bw.write("Name of the Configuration");
                bw.write(',');
                
                for( int i = 0 ; i < evalMetrics.size() ; i++ ) {
                    bw.write(evalMetrics.get(i).toString());
                    if( i != evalMetrics.size() - 1 ) {
                        bw.write(',');
                    }
                    else {
                        bw.write('\n');
                    }
                }
                
                for( int i = 0 ; i < recommendersResults.size() ; i++ ) {
                    
                    EvaluationResult recommenderResult = recommendersResults.get(i);
                    
                    EvaluationResultsForFold overallResult = recommenderResult.getOverallPerformance();
                    ArrayList<EvaluationResultsForFold> foldResults = recommenderResult.getAllFoldResults();
                    
                    for( int j = 0 ; j < foldResults.size() ; j++ ) {
                        
                        EvaluationResultsForFold foldResult = foldResults.get(j);
                        
                        bw.write(recommenderResult.getRecommenderConfigurationName() + "-Fold-" +(j+1) );
                        bw.write(',');
                    
                        for( int k = 0 ; k < foldResult.getMetricScores().size() ; k++ ) {
                            bw.write(foldResult.getMetricScores().get(k).toString());
                            if( k != foldResult.getMetricScores().size() - 1 ) {
                                bw.write(',');
                            }
                            else {
                                bw.write('\n');
                            }
                        }
                    }
                    
                    bw.write(recommenderResult.getRecommenderConfigurationName() + "-Overall" );
                    bw.write(',');

                    for( int k = 0 ; k < overallResult.getMetricScores().size() ; k++ ) {
                        bw.write(overallResult.getMetricScores().get(k).toString());
                        if( k != overallResult.getMetricScores().size() - 1 ) {
                            bw.write(',');
                        }
                        else {
                            bw.write('\n');
                        }
                    }
                }
                
                bw.flush();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AbstractEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public EvalDataManager getDataManager() {
        return this.dataManager;
    }

    /**
     * Sorts User's Ratings list with respect to rating values.
     * 
     * Ratings are sorted decreasingly.
     * 
     * Example: 
     * 
     * User1 ratings: 1.7, 3.0, 2.1
     * 
     * Result:
     * 
     * User1 ratings: 3.0, 2.1, 1.7
     */
    private ArrayList<Rating> sortDecreasing(ArrayList<Rating> usersRatings) {
        
        // like(binary) case, return all ratings, no need to sort
        if( evaluatorRepository.getCurrentLoadedConfiguration().getEvalEntityMap().get(EvalEntity.RAT_ITEM) == null) {
            return usersRatings;
        }
        
        ArrayList<Rating> tempRatings = new ArrayList<>(usersRatings);

        Collections.sort(tempRatings, (Rating o1, Rating o2) -> {
            if(o1.getRatingValue() > o2.getRatingValue()) return -1;
            if(o1.getRatingValue() < o2.getRatingValue()) return 1;
            return 0;
        });
        
        return tempRatings;
    }
    
    /**
     * Converts RatedResource array to an Arraylist with Ratings objects, while
     * getting actual(or we can call it "real") ratings of the User.
     * 
     * UserUri is not needed since topRecommendations contains all recommendations 
     * for all users. 
     *   
     * @param topRecommendations Recommendations for All Users
     * @return 
     */
    private ArrayList<Rating> convertRatings(RatedResource[] topRecommendations, int index) {
        
        // Top K Recommendations list of "i"th User. 
        ArrayList<Rating> result = new ArrayList<>();

        for (RatedResource topRecommendation : topRecommendations) {
            if (topRecommendation == null) {
                result.add(new Rating("", "", 0.0));
                continue;
            }
            // Since "i" represents index in the both Users list and Top K 
            // Recommendations List, we can use "i" for other lists too
            Rating rating = findRatingWithItemUriSafe(dataManager.getUsersRatings().get(index), topRecommendation.getResource(), dataManager.getUsers().get(index));
            result.add(rating);                
        }
            
        return result;
    }
    
    /**
     * Searches rating with given itemURI.
     * 
     * If rating is found, rating object with the given itemURI is returned.
     * 
     * If rating cannot be found, returns null.
     * 
     * @param ratingsOfUser
     * @param itemURI
     * @return 
     */
    private Rating findRatingWithItemUri(ArrayList<Rating> ratingsOfUser, String itemURI) {
                            
        for( int i = 0 ; i < ratingsOfUser.size() ; i++) {               
            if(ratingsOfUser.get(i).getItemURI().equals(itemURI)) {
                return ratingsOfUser.get(i);
            }
        }
        
        return null;
    }
    
    /**
     * Searches rating with given itemURI. "Safe" because it does not return a 
     * null rating object. Instead it returns a rating object with 0.0 rating.
     * 
     * If it is found, rating object with the given itemURI is returned.
     * 
     * If it cannot be found, a rating object with given itemURI and userURI is
     * returned.
     * 
     * @param ratingsOfUser
     * @param itemURI
     * @return 
     */
    private Rating findRatingWithItemUriSafe(ArrayList<Rating> ratingsOfUser, 
            String itemURI, String userURI) {        
        
        for( int i = 0 ; i < ratingsOfUser.size() ; i++) {               
            if(ratingsOfUser.get(i).getItemURI().equals(itemURI)) {
                return ratingsOfUser.get(i);
            }
        }   
        
        return new Rating(userURI, itemURI, 0.0);
    }
        
    @Override
    public void addEvalResult(EvaluationResult evalResult) {
        recommendersResults.add(evalResult);
    }
    
    @Override
    public void setUsers(ArrayList<String> users) {
        dataManager.setUsers(users);
    }
    
    @Override
    public void setItems(ArrayList<String> items) {
        dataManager.setItems(items);
    }
    
    @Override
    public void setItemFeatures(ArrayList<ArrayList<String>> itemFeatures) {
        dataManager.setItemFeatures(itemFeatures);
    }
    
    @Override
    public void setUsersRatings(ArrayList<ArrayList<Rating>> usersRatings) {
        dataManager.setUsersRatings(usersRatings);
    }  
    
    @Override
    public SailRecEvaluatorRepository getEvaluatorRepository() {
        return this.evaluatorRepository;
    }    
    
    @Override
    public ArrayList<EvaluationResult> getEvalResults() {
        return this.recommendersResults;
    } 
    
    @Override
    public EvaluationResult getEvalResultByIndex(int index) {
        return this.recommendersResults.get(index);
    }
    
    @Override
    public EvaluationResult getEvalResultByName(String configurationName) {
        
        for( int i = 0 ; i < recommendersResults.size() ; i++ ) {
            if( recommendersResults.get(i).getRecommenderConfigurationName().equals(configurationName) ) {
                return recommendersResults.get(i);
            }
        }
        
        return null;
    }
    
    @Override
    public void incrementNativeStoreCounter() {
        this.nativeStoreCounter++;
    }
    
    @Override
    public int getNativeStoreCounter() {
        return this.nativeStoreCounter;
    }
    
    /**
     * For testing. Do not use for other purposes.
     * @param dataManager
     */
    public AbstractEvaluator(EvalDataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * For testing. Do not use for other purposes.
     * @param evaluatorRepository
     */
    public void setEvaluatorRepository(SailRecEvaluatorRepository evaluatorRepository) {
        this.evaluatorRepository = evaluatorRepository;
    }
    
    /**
     * For testing.
     */
    public void printRecommendersResults() {
        
        for( int i = 0 ; i < recommendersResults.size() ; i++ ) {
            EvaluationResult resultToPrint = recommendersResults.get(i);
            System.out.println("index " + i + " conf name " + resultToPrint.getRecommenderConfigurationName());
            
            ArrayList<EvaluationResultsForFold> foldResults = resultToPrint.getAllFoldResults();
            EvaluationResultsForFold overallPerformance = resultToPrint.getOverallPerformance();
            
            for( int j = 0 ; j < foldResults.size() ; j++ ) {
                ArrayList<Double> scores = foldResults.get(j).getMetricScores();
                ArrayList<GenericEvalMetric> metrics = foldResults.get(j).getMetrics();
                for( int k = 0 ; k < metrics.size() ; k++ ) {
                    System.out.println( "Fold: " + j + " Metric: " + metrics.get(k) + " Score: " + scores.get(k));
                }
            }
            
            ArrayList<Double> scores = overallPerformance.getMetricScores();
            ArrayList<GenericEvalMetric> metrics = overallPerformance.getMetrics();
            for( int k = 0 ; k < metrics.size() ; k++ ) {
                System.out.println( "Overall Performance: Metric: " + metrics.get(k) + " Score: " + scores.get(k));
            }
        }
    }
}

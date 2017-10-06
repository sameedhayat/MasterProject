/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.evaluator.crosskfold.CrossKFoldEvaluatorTest;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.repository.RatingsSDCusFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.RatingsSrrDriver;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This test has been designed to verify all the metrics for the following cases:
 * - Ratings are used
 * - It is single domain
 * Folds a created by means of a stub class to isolate the behavior of the 
 * evaluator.
 */
public class RatingsSDCusFoldsMetricsTest {
    /**
     * Allowed error.
     */
    private double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
   
    @Test    
    public void testResultsOfFixedSRRWithRatings() {
            System.out.println("");
            System.out.println("testFixedRepo starting...");
            
            RatingsSrrDriver fixedRep = new RatingsSrrDriver(new MemoryStore());
            fixedRep.loadRecConfiguration(EvalTestRepositoryInstantiator.getRecConfigListRatingsSDFixed().get(0));
            
            Assert.assertEquals(new Double(1.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User1", null)));
            Assert.assertEquals(new Double(2.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User2", null)));
            Assert.assertEquals(new Double(3.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User3", null)));
            Assert.assertEquals(new Double(4.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User4", null)));   
            
            RatedResource[] actualTop2RecUser1 = fixedRep.getTopRecommendations("http://example.org/fixed#User1", 2, null) ;
            RatedResource user1Res1 = new RatedResource("http://example.org/fixed#Item5", 5.0);
            RatedResource user1Res2 = new RatedResource("http://example.org/fixed#Item6", 5.0);           
            Assert.assertEquals(user1Res1, actualTop2RecUser1[0]);
            Assert.assertEquals(user1Res2, actualTop2RecUser1[1]);
            
            RatedResource[] actualTop2RecUser2 = fixedRep.getTopRecommendations("http://example.org/fixed#User2", 2, null) ;
            RatedResource user2Res2 = new RatedResource("http://example.org/fixed#Item1", 5.0);
            RatedResource user2Res1 = new RatedResource("http://example.org/fixed#Item4", 5.0);           
            Assert.assertEquals(user2Res1, actualTop2RecUser2[0]);
            Assert.assertEquals(user2Res2, actualTop2RecUser2[1]);
            
            RatedResource[] actualTop2RecUser3 = fixedRep.getTopRecommendations("http://example.org/fixed#User3", 2, null) ;
            RatedResource user3Res1 = new RatedResource("http://example.org/fixed#Item1", 5.0);
            RatedResource user3Res2 = new RatedResource("http://example.org/fixed#Item5", 5.0);           
            Assert.assertEquals(user3Res1, actualTop2RecUser3[0]);
            Assert.assertEquals(user3Res2, actualTop2RecUser3[1]);
            
            RatedResource[] actualTop2RecUser4 = fixedRep.getTopRecommendations("http://example.org/fixed#User4", 2, null) ;
            RatedResource user4Res1 = new RatedResource("http://example.org/fixed#Item5", 5.0);
            RatedResource user4Res2 = new RatedResource("http://example.org/fixed#Item6", 5.0);           
            Assert.assertEquals(user4Res1, actualTop2RecUser4[0]);
            Assert.assertEquals(user4Res2, actualTop2RecUser4[1]);      
            
    }
    
    /**
     * Tests calculateMae() method
     */
    @Test
    public void testMae() {
     
        System.out.println("");
        System.out.println("testMae starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
                evalConfig.addRankingMetricTopKSize(1);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                //Training set: fold1-2 - Test set: fold3
                double errorUser1F3 = (Math.abs(3-1) + Math.abs(4-1));
                double errorUser2F3 = (Math.abs(4-2) + Math.abs(3-2));
                double errorUser3F3 = (Math.abs(1-3) + Math.abs(5-3));
                double errorUser4F3 = (Math.abs(1-4) + Math.abs(1-4));
                double errorF3 = Math.sqrt((errorUser1F3 + errorUser2F3 + errorUser3F3 + errorUser4F3) / 8);

                //Training set: fold1-3 - Test set: fold2
                double errorUser1F2 = (Math.abs(2-1) + Math.abs(3-1));
                double errorUser2F2 = (Math.abs(5-2) + Math.abs(3-2));
                double errorUser3F2 = (Math.abs(3-3) + Math.abs(3-3));
                double errorUser4F2 = (Math.abs(5-4) + Math.abs(2-4));
                double errorF2 =  Math.sqrt((errorUser1F2 + errorUser2F2 + errorUser3F2 + errorUser4F2) / 8);

                //Training set: fold2-3 - Test set: fold1
                double errorUser1F1 = (Math.abs(3-1) + Math.abs(1-1));
                double errorUser2F1 = (Math.abs(4-2) + Math.abs(3-2));
                double errorUser3F1 = (Math.abs(4-3) + Math.abs(2-3));
                double errorUser4F1 = (Math.abs(1-4) + Math.abs(5-4));
                double errorF1 =  Math.sqrt((errorUser1F1 + errorUser2F1 + errorUser3F1 + errorUser4F1) / 8);

                double expectedMAE = (errorF1 + errorF2 + errorF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
            
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateRmse() method
     */
    @Test
    public void testRmse() {
     
        System.out.println("");
        System.out.println("testRmse starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
                evalConfig.addRankingMetricTopKSize(1);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                //Training set: fold1-2 - Test set: fold3
                double errorUser1F3 = (Math.pow((3-1),2) + Math.pow((4-1),2));
                double errorUser2F3 = (Math.pow((4-2),2) + Math.pow((3-2),2));
                double errorUser3F3 = (Math.pow((1-3),2) + Math.pow((5-3),2));
                double errorUser4F3 = (Math.pow((1-4),2) + Math.pow((1-4),2));
                double errorF3 =  Math.sqrt((errorUser1F3 + errorUser2F3 + errorUser3F3 + errorUser4F3) / 8);

                //Training set: fold1-3 - Test set: fold2
                double errorUser1F2 = (Math.pow((2-1),2) + Math.pow((3-1),2));
                double errorUser2F2 = (Math.pow((5-2),2) + Math.pow((3-2),2));
                double errorUser3F2 = (Math.pow((3-3),2) + Math.pow((3-3),2));
                double errorUser4F2 = (Math.pow((5-4),2) + Math.pow((2-4),2));
                double errorF2 =  Math.sqrt((errorUser1F2 + errorUser2F2 + errorUser3F2 + errorUser4F2) / 8);

                //Training set: fold2-3 - Test set: fold1
                double errorUser1F1 = (Math.pow((3-1),2) + Math.pow((1-1),2));
                double errorUser2F1 = (Math.pow((4-2),2) + Math.pow((3-2),2));
                double errorUser3F1 = (Math.pow((4-3),2) + Math.pow((2-3),2));
                double errorUser4F1 = (Math.pow((1-4),2) + Math.pow((5-4),2));
                double errorF1 =  Math.sqrt((errorUser1F1 + errorUser2F1 + errorUser3F1 + errorUser4F1) / 8);

                double expectedRMSE = (errorF1 + errorF2 + errorF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);

        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculatePrecision() method
     */
    @Test
    public void testPrecision() {
     
        System.out.println("");
        System.out.println("testPrecision starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                //Training set: fold1-2 - Test set: fold3
                double preUser1F3 = 2.0 / (2.0 + 0.0);
                double preUser2F3 = 1.0 / (1.0 + 1.0);
                double preUser3F3 = 0.0;
                double preUser4F3 = 0.0;

                double precisionF3 = (preUser1F3 + preUser2F3 + preUser3F3 + preUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double preUser1F2 = 0.0;
                double preUser2F2 = 0.0;
                double preUser3F2 = 1.0 / (1.0 + 1.0);
                double preUser4F2 = 0.0;   
                double precisionF2 = (preUser1F2 + preUser2F2 + preUser3F2 + preUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double preUser1F1 = 0.0;
                double preUser2F1 = 0.0;
                double preUser3F1 = 1.0 / (1.0 + 1.0);
                double preUser4F1 = 0.0;
                double precisionF1 = (preUser1F1 + preUser2F1 + preUser3F1 + preUser4F1)/4;

                double expectedPrecision = (precisionF1 + precisionF2 + precisionF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getPrecisionScore(2));
                System.out.println(result.getFoldResult(1).getPrecisionScore(2));
                System.out.println(result.getFoldResult(2).getPrecisionScore(2));
                Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(2), DELTA);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateRecall() method
     */
    @Test
    public void testRecall() {
     
        System.out.println("");
        System.out.println("testRecall starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();
                
                System.out.println("------------------------------------------------------------");

                //Training set: fold1-2 - Test set: fold3
                double recUser1F3 = 2.0 / (2.0 + 0.0);
                double recUser2F3 = 1.0 / (1.0 + 0.0);
                double recUser3F3 = 0.0 / (0.0 + 1.0);
                double recUser4F3 = 0.0;

                double recallF3 = (recUser1F3 + recUser2F3 + recUser3F3 + recUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double recUser1F2 = 0.0;
                double recUser2F2 = 0.0;
                double recUser3F2 = 1.0 / (1.0 + 1.0);
                double recUser4F2 = 0.0 / (0.0 + 2.0);

                double recallF2 = (recUser1F2 + recUser2F2 + recUser3F2 + recUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double recUser1F1 = 0.0;
                double recUser2F1 = 0.0;
                double recUser3F1 = 1.0 / (1.0 + 0.0);
                double recUser4F1 = 0.0;
                double recallF1 = (recUser1F1 + recUser2F1 + recUser3F1 + recUser4F1)/4;
                
                System.out.println("recallF1 " + recallF1);
                System.out.println("recallF2 " + recallF2);
                System.out.println("recallF3 " + recallF3);

                double expectedRecall = (recallF1 + recallF2 + recallF3) / 3;
                System.out.println("expectedRecall " + expectedRecall);

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getRecallScore(2));
                System.out.println(result.getFoldResult(1).getRecallScore(2));
                System.out.println(result.getFoldResult(2).getRecallScore(2));
                System.out.println(result.getOverallPerformance().getRecallScore(2));
                Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(2), DELTA);   
            
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateFMeasure() method
     */
    @Test
    public void testFMeasure() {
     
        System.out.println("");
        System.out.println("testFMeasure starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                System.out.println("------------------------------------------------------------");

                //Training set: fold1-2 - Test set: fold3
                double preUser1F3 = 2.0 / (2.0 + 0.0);
                double preUser2F3 = 1.0 / (1.0 + 1.0);
                double preUser3F3 = 0.0;
                double preUser4F3 = 0.0;

                double precisionF3 = (preUser1F3 + preUser2F3 + preUser3F3 + preUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double preUser1F2 = 0.0;
                double preUser2F2 = 0.0;
                double preUser3F2 = 1.0 / (1.0 + 1.0);
                double preUser4F2 = 0.0;   
                double precisionF2 = (preUser1F2 + preUser2F2 + preUser3F2 + preUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double preUser1F1 = 0.0;
                double preUser2F1 = 0.0;
                double preUser3F1 = 1.0 / (1.0 + 1.0);
                double preUser4F1 = 0.0;
                double precisionF1 = (preUser1F1 + preUser2F1 + preUser3F1 + preUser4F1)/4;

                //Training set: fold1-2 - Test set: fold3
                double recUser1F3 = 2.0 / (2.0 + 0.0);
                double recUser2F3 = 1.0 / (1.0 + 0.0);
                double recUser3F3 = 0.0 / (0.0 + 1.0);
                double recUser4F3 = 0.0;

                double recallF3 = (recUser1F3 + recUser2F3 + recUser3F3 + recUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double recUser1F2 = 0.0;
                double recUser2F2 = 0.0;
                double recUser3F2 = 1.0 / (1.0 + 1.0);
                double recUser4F2 = 0.0 / (0.0 + 2.0);

                double recallF2 = (recUser1F2 + recUser2F2 + recUser3F2 + recUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double recUser1F1 = 0.0;
                double recUser2F1 = 0.0;
                double recUser3F1 = 1.0 / (1.0 + 0.0);
                double recUser4F1 = 0.0;
                double recallF1 = (recUser1F1 + recUser2F1 + recUser3F1 + recUser4F1)/4;
                
                double fMeasure1 = 2 / ( (1/precisionF1) + (1/recallF1) );
                double fMeasure2 = 2 / ( (1/precisionF2) + (1/recallF2) );
                double fMeasure3 = 2 / ( (1/precisionF3) + (1/recallF3) );

                double expectedFMeasure = ( fMeasure1 + fMeasure2 + fMeasure3 ) / 3 ;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getPrecisionScore(2));
                System.out.println(result.getFoldResult(1).getPrecisionScore(2));
                System.out.println(result.getFoldResult(2).getPrecisionScore(2));

                System.out.println(result.getFoldResult(0).getRecallScore(2));
                System.out.println(result.getFoldResult(1).getRecallScore(2));
                System.out.println(result.getFoldResult(2).getRecallScore(2));

                System.out.println(result.getFoldResult(0).getFMeasureScore(2));
                System.out.println(result.getFoldResult(1).getFMeasureScore(2));
                System.out.println(result.getFoldResult(2).getFMeasureScore(2));
               
                Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(2), DELTA);   
            
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateAccuracy() method
     */
    @Test
    public void testAccuracy() {
     
        System.out.println("");
        System.out.println("testAccuracy starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.ACC));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();
                
                System.out.println("------------------------------------------------------------");

                //Training set: fold1-2 - Test set: fold3
                double accUser1F3 = 2.0 / ( 2.0 + 0.0 + 0.0 + 0.0 );
                double accUser2F3 = 2.0 / ( 1.0 + 1.0 + 1.0 + 0.0 );
                double accUser3F3 = 1.0 / ( 0.0 + 1.0 + 2.0 + 1.0 );
                double accUser4F3 = 0.0 / ( 0.0 + 0.0 + 2.0 + 0.0 );

                double accuracyF3 = (accUser1F3 + accUser2F3 + accUser3F3 + accUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double accUser1F2 = 1.0 / ( 0.0 + 1.0 + 2.0 + 1.0 );
                double accUser2F2 = 1.0 / ( 0.0 + 1.0 + 2.0 + 1.0 );
                double accUser3F2 = 1.0 / ( 1.0 + 0.0 + 1.0 + 1.0 );
                double accUser4F2 = 0.0 / ( 0.0 + 0.0 + 2.0 + 2.0 );

                double accuracyF2 = (accUser1F2 + accUser2F2 + accUser3F2 + accUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double accUser1F1 = 1.0 / ( 0.0 + 1.0 + 2.0 + 1.0 );
                double accUser2F1 = 0.0 / ( 0.0 + 0.0 + 2.0 + 1.0 );
                double accUser3F1 = 2.0 / ( 1.0 + 1.0 + 1.0 + 0.0 );
                double accUser4F1 = 1.0 / ( 0.0 + 1.0 + 2.0 + 1.0 );
                double accuracyF1 = (accUser1F1 + accUser2F1 + accUser3F1 + accUser4F1)/4;
                
                System.out.println("accuracyF1 " + accuracyF1);
                System.out.println("accuracyF2 " + accuracyF2);
                System.out.println("accuracyF3 " + accuracyF3);

                double expectedAccuracy = (accuracyF1 + accuracyF2 + accuracyF3) / 3;
                System.out.println("testAccuracy " + expectedAccuracy);

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getAccuracyScore(2));
                System.out.println(result.getFoldResult(1).getAccuracyScore(2));
                System.out.println(result.getFoldResult(2).getAccuracyScore(2));
                System.out.println(result.getOverallPerformance().getAccuracyScore(2));
                Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(2), DELTA);   
            
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateMRR() method
     */
    @Test
    public void testMRR() {
     
        System.out.println("");
        System.out.println("testMRR starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                //Training set: fold1-2 - Test set: fold3
                double mrrUser1F3 = 1.0;
                double mrrUser2F3 = 0.5;
                double mrrUser3F3 = 0.0;
                double mrrUser4F3 = 0.0;

                double mrrF3 = (mrrUser1F3 + mrrUser2F3 + mrrUser3F3 + mrrUser4F3) / 4;

                //Training set: fold1-3 - Test set: fold2
                double mrrUser1F2 = 0.0;
                double mrrUser2F2 = 0.0;
                double mrrUser3F2 = 1.0;
                double mrrUser4F2 = 0.0;   
                double mrrF2 = (mrrUser1F2 + mrrUser2F2 + mrrUser3F2 + mrrUser4F2) / 4;

                //Training set: fold2-3 - Test set: fold1
                double mrrUser1F1 = 0.0;
                double mrrUser2F1 = 0.0;
                double mrrUser3F1 = 0.5;
                double mrrUser4F1 = 0.0;
                double mrrF1 = (mrrUser1F1 + mrrUser2F1 + mrrUser3F1 + mrrUser4F1)/4;

                double expectedMRR = (mrrF1 + mrrF2 + mrrF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getMRRScore(2));
                System.out.println(result.getFoldResult(1).getMRRScore(2));
                System.out.println(result.getFoldResult(2).getMRRScore(2));
                Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(2), DELTA);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateNDCG() method
     */
    @Test
    public void testNDCG() {
     
        System.out.println("");
        System.out.println("testNDCG starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.NDCG));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();
                
                //relevance scores are as follows:
                //0 = non relevant
                //1 = relevant
                
                //DCG_2 is trivial for each user.
                //For each of the two elements in the top-2 list assign 1 if it
                //is relevant, 0 otherwise.
                //sum those values and that's it
                // i  |   rel_i   |   log_2(i+1)   |   rel_i / log_2(i+1)
                // 1  |   [0|1|2] |     1          |       [0|1|2]
                // 2  |   [0|1|2] |     1.58496250 |       [0|0.63092975385|1.26185950771706]
                                
                //Training set: fold1-2 - Test set: fold3
                double ndcgUser1F3 = 3.26185950771706   / 3.26185950771706;
                double ndcgUser2F3 = 1.2618595071429148 / 2.0;
                double ndcgUser3F3 = 0.0;
                double ndcgUser4F3 = 0.0;

                double ndcgF3 = (ndcgUser1F3 + ndcgUser2F3 + ndcgUser3F3 + ndcgUser4F3) / 4;

                //Training set: fold1-3 - Test set: fold2
                double ndcgUser1F2 = 0.0;
                double ndcgUser2F2 = 0.0;
                double ndcgUser3F2 = 2.0 / 2.0;
                double ndcgUser4F2 = 0.0;   
                double ndcgF2 = (ndcgUser1F2 + ndcgUser2F2 + ndcgUser3F2 + ndcgUser4F2) / 4;

                //Training set: fold2-3 - Test set: fold1
                double ndcgUser1F1 = 0.0;
                double ndcgUser2F1 = 0.0;
                double ndcgUser3F1 = 1.26185950771706 / 2.0;
                double ndcgUser4F1 = 0.0;
                double ndcgF1 = (ndcgUser1F1 + ndcgUser2F1 + ndcgUser3F1 + ndcgUser4F1)/4;

                double expectedNDCG = (ndcgF1 + ndcgF2 + ndcgF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getNDCGScore(2));
                System.out.println(result.getFoldResult(1).getNDCGScore(2));
                System.out.println(result.getFoldResult(2).getNDCGScore(2));
                Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(2), DELTA);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * Tests calculateAUC() method
     */
    @Test
    public void testAUC() {
     
        System.out.println("");
        System.out.println("testAUC starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.AUC));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();   
                /*
                
                //AUC is computed for each user.
                //auc(C)= 1/PN sum_(j=1)^N(s_j-j)
                //(s_j-j) is the number of positives before the jth negative 
                //in Z.
                                
                //Training set: fold1-2 - Test set: fold3                
                //Elements in the test set (fold3)
                //For user1
                // Item   rat  Class  Predicted score
                //(Item5, 3.0) P      1.0
                //(Item6, 4.0) P      1.0
                //2 samples in test set: 2 pos, 0 neg.
                double coordinateXUser1F3 = 0.0;
                double coordinateYUser1F3 = (1/2) + (1/2);
                double aucUser1F3 = 0.0; //no neg.
                
                //For user2                
                //(Item1, 4.0) P      2.0 
                //(Item2, 3.0) N      2.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser2F3 = 1.0;
                double coordinateYUser2F3 = 1.0;
                double aucUser2F3 = (1/(1.0*1.0))*(1.0); 
                
                //For user3
                //(Item3, 1.0) N      3.0 
                //(Item4, 5.0) P      3.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser3F3 = 1.0;
                double coordinateYUser3F3 = 1.0;
                double aucUser3F3 = (1/(1.0*1.0))*(0.0);
                
                //For user4
                //(Item5, 1.0) N      4.0 
                //(Item6, 1.0) N      4.0               
                //2 samples in test set: 0 pos, 2 neg.
                double coordinateXUser4F3 = (1/2) + (1/2);
                double coordinateYUser4F3 = 0.0;
                double aucUser4F3 = (1/(1.0*1.0))*(0.0 + 0.0);
                
                double aucF3 = (aucUser1F3 + aucUser2F3 + aucUser3F3 + aucUser4F3) / 4;
                
                //Training set: fold1-3 - Test set: fold2
                //Elements in the test set (fold2)
                //For user1
                // Item   rat  Class  Predicted score
                //(Item3, 2.0) N      1.0
                //(Item4, 3.0) P      1.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser1F2 = 1.0;
                double coordinateYUser1F2 = 1.0;
                double aucUser1F2 = (1/(1.0*1.0))*(0.0);
                
                //For user2                
                //(Item5, 5.0) P      2.0
                //(Item6, 3.0) N      2.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser2F2 = 1.0;
                double coordinateYUser2F2 = 1.0;
                double aucUser2F2 = (1/(1.0*1.0))*(1.0);
                
                //For user3
                //(Item1, 3.0) P      3.0 
                //(Item2, 3.0) P      3.0
                //2 samples in test set: 2 pos, 0 neg.
                double coordinateXUser3F2 = 0.0;
                double coordinateYUser3F2 = (1/2) + (1/2);
                double aucUser3F2 = 0.0;
                
                //For user4
                //(Item3, 5.0) P      4.0 
                //(Item4, 2.0) P      4.0
                //2 samples in test set: 0 pos, 2 neg.
                double coordinateXUser4F2 = 0.0;
                double coordinateYUser4F2 = (1/2) + (1/2);
                double aucUser4F2 = 0.0;
                
                double aucF2 = (aucUser1F2 + aucUser2F2 + aucUser3F2 + aucUser4F2) / 4;

                
                //Training set: fold2-3 - Test set: fold1
                //Elements in the test set (fold1)
                //For user1
                // Item   rat  Class  Predicted score
                //(Item1, 3.0) P      1.0
                //(Item2, 1.0) N      1.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser1F1 = 1.0;
                double coordinateYUser1F1 = 1.0;
                double aucUser1F1 = (1/(1.0*1.0))*(1.0);
                
                //For user2                
                //(Item3, 4.0) P      2.0 
                //(Item4, 3.0) N      2.0
                //2 samples in test set: 1 pos, 1 neg.
                double coordinateXUser2F1 = 1.0;
                double coordinateYUser2F1 = 1.0;
                double aucUser2F1 = (1/(1.0*1.0))*(1.0);
                
                //For user3
                //(Item5, 4.0) P      3.0 
                //(Item6, 2.0) N      3.0
                //2 samples in test set: 2 pos, 0 neg.
                double coordinateXUser3F1 = 1.0;
                double coordinateYUser3F1 = 1.0;
                double aucUser3F1 = (1/(1.0*1.0))*(1.0);
                
                //For user4
                //(Item1, 1.0) N      4.0 
                //(Item2, 5.0) P      4.0
                //2 samples in test set: 0 pos, 2 neg.
                double coordinateXUser4F1 = 1.0;
                double coordinateYUser4F1 = 1.0;
                double aucUser4F1 = (1/(1.0*1.0))*(0.0);
                
                double aucF1 = (aucUser1F1 + aucUser2F1 + aucUser3F1 + aucUser4F1) / 4;
                double expectedAUC = (aucF1 + aucF2 + aucF3) / 3;*/
                
                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                double aucF1 = (0.5 * 0.75) + (0.25 * 0.5) + (0.25 * 0.25);
                double aucF2 = 5.0 / 6.0 * 1;
                double aucF3 = (0.25 * 0.5);
                double expectedAUC = (aucF1 + aucF2 + aucF3) / 3;
                
                System.out.println(result.getFoldResult(0).getAUCScore());
                System.out.println(result.getFoldResult(1).getAUCScore());
                System.out.println(result.getFoldResult(2).getAUCScore());
                Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
                System.out.println("testAUC ended with an error!");
        }
    }
    
    /**
     * Tests calculateCoverage() method
     */   
    @Test
    public void testCoverage() {
     
        System.out.println("");
        System.out.println("testCoverage starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new GlobalEvalMetric(EvalMetric.COVERAGE));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();
                
                Set<String> allItems = new HashSet<>();
                allItems.add("I1");
                allItems.add("I2");
                allItems.add("I3");
                allItems.add("I4");
                allItems.add("I5");
                allItems.add("I6");
                
                //Training set: fold1-2 - Test set: fold3
                Set<String> itemsRecommendedForUser1F3 = new HashSet<>();
                Set<String> itemsRecommendedForUser2F3 = new HashSet<>();
                Set<String> itemsRecommendedForUser3F3 = new HashSet<>();
                Set<String> itemsRecommendedForUser4F3 = new HashSet<>();
                
                itemsRecommendedForUser1F3.add("I5");
                itemsRecommendedForUser1F3.add("I6");
                itemsRecommendedForUser2F3.add("I1");
                itemsRecommendedForUser2F3.add("I4");
                itemsRecommendedForUser3F3.add("I1");
                itemsRecommendedForUser3F3.add("I5");  
                itemsRecommendedForUser4F3.add("I5");
                itemsRecommendedForUser4F3.add("I6");
                //I2, I3 missing
                
                Set<String> allRecommendationsF3 = new HashSet<>();
                allRecommendationsF3.addAll(itemsRecommendedForUser1F3);
                allRecommendationsF3.addAll(itemsRecommendedForUser2F3);
                allRecommendationsF3.addAll(itemsRecommendedForUser3F3);
                allRecommendationsF3.addAll(itemsRecommendedForUser4F3);
                                               
                double coverageF3 = allRecommendationsF3.size() / (double) allItems.size();

                //For these folds nothing chanages because the recommendations 
                //are fixed.
                //Training set: fold1-3 - Test set: fold2
                //Training set: fold2-3 - Test set: fold1

                double coverageF2 = coverageF3;
                double coverageF1 = coverageF3;
                
                double expectedCoverage = (coverageF1 + coverageF2 + coverageF3) / 3; //should be 4/6

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println("COVERAGE-F1" + result.getFoldResult(0).getCoverageScore());
                System.out.println("COVERAGE-F2" + result.getFoldResult(1).getCoverageScore());
                System.out.println("COVERAGE-F3" + result.getFoldResult(2).getCoverageScore());
                
                // Since in this case the metrics return the same result for each 
                // fold then we can assess this results individually
                System.out.println(result.getFoldResult(0).getCoverageScore());
                System.out.println(result.getFoldResult(1).getCoverageScore());
                System.out.println(result.getFoldResult(2).getCoverageScore());
                Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
                
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }               
    
    /**
     * TODO
     * Tests calculateDiversity() method
     */
    @Test
    public void testDiversity() {
     
        System.out.println("");
        System.out.println("testDiversity starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY));
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);
                evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
                evalConfig.setFeatureGraphPattern("?item <http://example.org/fixed#hasGenre> ?genre ");

                recEvalRepository.loadEvalConfiguration(evalConfig);
                recEvalRepository.evaluate();

                //Training set: fold1-2 - Test set: fold3
                double divUser1F3 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double divUser2F3 = ( (1.0 - 0.5) + (1.0 - 0.5) ) / (2.0 * 1.0);
                double divUser3F3 = ( (1.0 - 0.0) + (1.0 - 0.0) ) / (2.0 * 1.0);
                double divUser4F3 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double diversityF3 = (divUser1F3 + divUser2F3 + divUser3F3 + divUser4F3)/4;

                //Training set: fold1-3 - Test set: fold2
                double divUser1F2 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double divUser2F2 = ( (1.0 - 0.5) + (1.0 - 0.5) ) / (2.0 * 1.0);
                double divUser3F2 = ( (1.0 - 0.0) + (1.0 - 0.0) ) / (2.0 * 1.0);
                double divUser4F2 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double diversityF2 = (divUser1F2 + divUser2F2 + divUser3F2 + divUser4F2)/4;

                //Training set: fold2-3 - Test set: fold1
                double divUser1F1 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double divUser2F1 = ( (1.0 - 0.5) + (1.0 - 0.5) ) / (2.0 * 1.0);
                double divUser3F1 = ( (1.0 - 0.0) + (1.0 - 0.0) ) / (2.0 * 1.0);
                double divUser4F1 = ( (1.0 - 0.40824829046386) + (1.0 - 0.40824829046386) ) / (2.0 * 1.0);
                double diversityF1 = (divUser1F1 + divUser2F1 + divUser3F1 + divUser4F1)/4;
        

                double expectedDiversity = (diversityF1 + diversityF2 + diversityF3) / 3;

                EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                        "ratingsSDConfig");

                System.out.println(result.getFoldResult(0).getDiversityScore(2));
                System.out.println(result.getFoldResult(1).getDiversityScore(2));
                System.out.println(result.getFoldResult(2).getDiversityScore(2));
                System.out.println("ExpDiv1 " + diversityF1);
                System.out.println("ExpDiv2 " + diversityF2);
                System.out.println("ExpDiv3 " + diversityF3);
                Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(2), DELTA);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }
    }
    
    /**
     * TODO
     * Tests calculateNovelty() method
     */
    @Test
    public void testNovelty() {
     
        System.out.println("");
        System.out.println("testNovelty starting...");
        
        RatingsSDCusFoldsSerDriver recEvalRepository
                = EvalTestRepositoryInstantiator.createRatingsSDCusFoldsSer();
        
                
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {       
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.NOVELTY));
            evalConfig.addRankingMetricTopKSize(2);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(3);
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
            evalConfig.setFeatureGraphPattern("?item <http://example.org/fixed#hasGenre> ?genre ");

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.evaluate();

            // sum of distance scores / ( number of recommended items * number of items of the user )
            //Training set: fold1-2 - Test set: fold3
            double novUser1F3 = ( 1.0 + 0.5 + 0.29289321881345 + 0.5 + 0.18350341907227 + 0.18350341907227 + 0.42264973081037 + 0.18350341907227 ) 
                    / (2.0 * 4.0); //(I5,I1)(I5,I2)(I5,I3)(I5,I4)(I6,I1)(I6,I2)(I6,I3)(I6,I4)
            double novUser2F3 = ( 0.29289321881345 + 0.0 + 0.5 + 0.18350341907227 + 1.0 + 0.5 + 1.0 + 0.18350341907227 ) 
                    / (2.0 * 4.0); //(I4,I3)(I4,I4)(I4,I5)(I4,I6)(I1,I3)(I1,I4)(I1,I5)(I1,I6)
            double novUser3F3 = ( 1.0 + 0.18350341907227 + 0.0 + 0.5 + 0.0 + 0.59175170953613 + 1.0 + 0.5 ) 
                    / (2.0 * 4.0); //(I1,I5)(I1,I6)(I1,I1)(I1,I2)(I5,I5)(I5,I6)(I5,I1)(I5,I2)
            double novUser4F3 = ( 1.0 + 0.5 + 0.29289321881345 + 0.5 + 0.18350341907227 + 0.18350341907227 + 0.42264973081037 + 0.18350341907227 ) 
                    / (2.0 * 4.0); //(I5,I1)(I5,I2)(I5,I3)(I5,I4)(I6,I1)(I6,I5)(I6,I3)(I6,I4)
            double noveltyF3 = (novUser1F3 + novUser2F3 + novUser3F3 + novUser4F3)/4;

            //Training set: fold1-3 - Test set: fold2
            double novUser1F2 = ( 1.0 + 0.5 + 0.0 + 0.59175170953613 + 0.18350341907227 + 0.18350341907227 + 0.59175170953613 + 0.0 ) 
                    / (2.0 * 4.0); //(I5,I1)(I5,I2)(I5,I5)(I5,I6)(I6,I1)(I6,I2)(I6,I5)(I6,I6)
            double novUser2F2 = ( 0.29289321881345 + 0.0 + 0.5 + 0.5 + 1.0 + 0.5 + 0.0 + 0.5 ) 
                    / (2.0 * 4.0); //(I4,I3)(I4,I4)(I4,I1)(I4,I2)(I1,I3)(I1,I4)(I1,I1)(I1,I2)
            double novUser3F2 = ( 1.0 + 0.18350341907227 + 1.0 + 0.5 + 0.0 + 0.59175170953613 + 0.29289321881345 + 0.5 ) 
                    / (2.0 * 4.0); //(I1,I5)(I1,I6)(I1,I3)(I1,I4)(I5,I5)(I5,I6)(I5,I3)(I5,I4)
            double novUser4F2 = ( 1.0 + 0.5 + 0.0 + 0.59175170953613 + 0.18350341907227 + 0.18350341907227 + 0.59175170953613 + 0.0 ) 
                    / (2.0 * 4.0); //(I5,I1)(I5,I2)(I5,I5)(I5,I6)(I6,I1)(I6,I2)(I6,I5)(I6,I6)
            double noveltyF2 = (novUser1F2 + novUser2F2 + novUser3F2 + novUser4F2)/4;

            //Training set: fold2-3 - Test set: fold1
            double novUser1F1 = ( 0.29289321881345 + 0.5 + 0.0 + 0.59175170953613 + 0.42264973081037 + 0.18350341907227 + 0.59175170953613 + 0.0 ) 
                    / (2.0 * 4.0); //(I5,I3)(I5,I4)(I5,I5)(I5,I6)(I6,I3)(I6,I4)(I6,I5)(I6,I6)
            double novUser2F1 = ( 0.5 + 0.18350341907227 + 0.5 + 0.5 + 1.0 + 0.18350341907227 + 0.0 + 0.5 ) 
                    / (2.0 * 4.0); //(I4,I5)(I4,I6)(I4,I1)(I4,I2)(I1,I5)(I1,I6)(I1,I1)(I1,I2)
            double novUser3F1 = ( 0.0 + 0.5 + 1.0 + 0.5 + 1.0 + 0.5 + 0.29289321881345 + 0.5 ) 
                    / (2.0 * 4.0); //(I1,I1)(I1,I2)(I1,I3)(I1,I4)(I5,I1)(I5,I2)(I5,I3)(I5,I4)
            double novUser4F1 = ( 0.29289321881345 + 0.5 + 0.0 + 0.59175170953613 + 0.42264973081037 + 0.18350341907227 + 0.59175170953613 + 0.0 ) 
                    / (2.0 * 4.0); //(I5,I3)(I5,I4)(I5,I5)(I5,I6)(I6,I3)(I6,I4)(I6,I5)(I6,I6)
            double noveltyF1 = (novUser1F1 + novUser2F1 + novUser3F1 + novUser4F1)/4;


            double expectedNovelty = (noveltyF1 + noveltyF2 + noveltyF3) / 3;

            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName(
                    "ratingsSDConfig");

            System.out.println(result.getFoldResult(0).getNoveltyScore(2));
            System.out.println(result.getFoldResult(1).getNoveltyScore(2));
            System.out.println(result.getFoldResult(2).getNoveltyScore(2));
            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(2), DELTA);
        } catch (EvaluatorException ex) {
            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
    }
}



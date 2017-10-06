/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.evaluator.crosskfold.CrossKFoldEvaluatorTest;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.repository.LikesCDStdFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesSrrDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesSDStdFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This test has been designed to verify all the metrics for the following cases:
 * - Likes are used
 * - It is cross domain
 * - Folds a created by a standard data manager.
 */
public class LikesCDStdFoldsMetricsTest {
    /**
     * Allowed error.
     */
    private final double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    
    /**
     * Tests the results returned by the fixed recommender repository which
     * is going to be assessed.
     */
    @Test    
    public void testResultsOfFixedCrossDomainSRRWithLikes() {
            System.out.println("");
            System.out.println("testResultsOfFixedCrossDomainSRRWithLikes starting...");
            
            // Note: this is not a mistake. We use LikesSrrDriver as 
            // the recommender, because the source domain is completely ignored
            // and the recommendations would be the same.
            
            LikesSrrDriver fixedRep = new LikesSrrDriver(new MemoryStore());
            fixedRep.loadRecConfiguration(EvalTestRepositoryInstantiator.getRecConfigListLikesSDFixed().get(0));
            
            Assert.assertEquals(new Double(1.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User1", null)));
            Assert.assertEquals(new Double(0.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User2", null)));
            Assert.assertEquals(new Double(0.5), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User3", null)));
            Assert.assertEquals(new Double(0.2), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User4", null)));
            
            
            RatedResource[] actualTop2RecUser1 = fixedRep.getTopRecommendations("http://example.org/fixed#User1", 2, null) ;
            RatedResource user1Res1 = new RatedResource("http://example.org/fixed#Item5", 1.0);
            RatedResource user1Res2 = new RatedResource("http://example.org/fixed#Item6", 1.0);           
            Assert.assertEquals(user1Res1, actualTop2RecUser1[0]);
            Assert.assertEquals(user1Res2, actualTop2RecUser1[1]);
            
            RatedResource[] actualTop2RecUser2 = fixedRep.getTopRecommendations("http://example.org/fixed#User2", 2, null) ;
            RatedResource user2Res2 = new RatedResource("http://example.org/fixed#Item4", 1.0);
            RatedResource user2Res1 = new RatedResource("http://example.org/fixed#Item1", 1.0);           
            Assert.assertEquals(user2Res2, actualTop2RecUser2[0]);
            Assert.assertEquals(user2Res1, actualTop2RecUser2[1]);
            
            RatedResource[] actualTop2RecUser3 = fixedRep.getTopRecommendations("http://example.org/fixed#User3", 2, null) ;
            RatedResource user3Res1 = new RatedResource("http://example.org/fixed#Item1", 1.0);
            RatedResource user3Res2 = new RatedResource("http://example.org/fixed#Item5", 1.0);           
            Assert.assertEquals(user3Res1, actualTop2RecUser3[0]);
            Assert.assertEquals(user3Res2, actualTop2RecUser3[1]);
            
            RatedResource[] actualTop2RecUser4 = fixedRep.getTopRecommendations("http://example.org/fixed#User4", 2, null) ;
            RatedResource user4Res1 = new RatedResource("http://example.org/fixed#Item5", 1.0);
            RatedResource user4Res2 = new RatedResource("http://example.org/fixed#Item6", 1.0);           
            Assert.assertEquals(user4Res1, actualTop2RecUser4[0]);
            Assert.assertEquals(user4Res2, actualTop2RecUser4[1]); 
            
            System.out.println("testResultsOfFixedCrossDomainSRRWithLikes COMPLETED");
    }
        
    /**
     * To verify if the evaluation of a cross-domain recommender is done 
     * correctly, we create two small datasets, on with items in a single domain
     * (d1) and one with two domains (d2). The target domain of d2 is the same
     * as d1's single domain and also the ratings for this domain are identical.
     * In this way the folds are created in the same way. Here we delegate the
     * task of creating the folds to a standard data manager and not a stub.
     * Both evaluators evaluate then a LikesSrrDriver. 
     * The results of the metrics should be identical.
     */
    @Test
    public void testAllMetrics() {
     
        System.out.println("");
        System.out.println("testAllMetrics starting...");
        
        LikesSDStdFoldsSerDriver recSDEvalRepository
                = EvalTestRepositoryInstantiator.createLikesSDStdFoldsSer();
        
        LikesCDStdFoldsSerDriver recCDEvalRepository
                = EvalTestRepositoryInstantiator.createLikesCDStdFoldsSer();
        
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
                
        try {  
                //Same evaluation confiugration for both
                evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
                evalConfig.setStorage(EvalStorage.MEMORY);
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
                evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.AUC));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.ACC));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.NDCG));              
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY));
                evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
                evalConfig.setFeatureGraphPattern("?item <http://example.org/fixed#hasGenre> ?genre");
                
                evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.NOVELTY));
                evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
                evalConfig.setFeatureGraphPattern("?item <http://example.org/fixed#hasGenre> ?genre");
                evalConfig.addEvalMetric( new GlobalEvalMetric(EvalMetric.COVERAGE));
                                                              
                evalConfig.addRankingMetricTopKSize(2);
                evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
                evalConfig.setIsReproducible(true);
                evalConfig.setNumberOfFolds(3);
                
                //single domain
                recSDEvalRepository.loadEvalConfiguration(evalConfig);
                recSDEvalRepository.evaluate();
                
                //cross domain
                recCDEvalRepository.loadEvalConfiguration(evalConfig);
                recCDEvalRepository.evaluate();
                
                EvaluationResult sDresult = recSDEvalRepository.getEvaluator().getEvalResultByName(
                        "likesSDConfig");
                EvaluationResult cDresult = recCDEvalRepository.getEvaluator().getEvalResultByName(
                        "likesCDConfig");
                
                System.out.println(sDresult.getOverallPerformance().getMAEScore());
                System.out.println(sDresult.getOverallPerformance().getRMSEScore());
                System.out.println(sDresult.getOverallPerformance().getAUCScore());
                System.out.println(sDresult.getOverallPerformance().getPrecisionScore(2));
                System.out.println(sDresult.getOverallPerformance().getRecallScore(2));
                System.out.println(sDresult.getOverallPerformance().getFMeasureScore(2));
                System.out.println(sDresult.getOverallPerformance().getMRRScore(2));
                System.out.println(sDresult.getOverallPerformance().getNDCGScore(2));
                System.out.println(sDresult.getOverallPerformance().getDiversityScore(2));
                System.out.println(sDresult.getOverallPerformance().getNoveltyScore(2));
                System.out.println(sDresult.getOverallPerformance().getCoverageScore());
                
                Assert.assertEquals(sDresult.getOverallPerformance().getMAEScore(), cDresult.getOverallPerformance().getMAEScore(), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getMAEScore() > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getRMSEScore(), cDresult.getOverallPerformance().getRMSEScore(), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getRMSEScore() > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getAUCScore(), cDresult.getOverallPerformance().getAUCScore(), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getAUCScore() > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getPrecisionScore(2), cDresult.getOverallPerformance().getPrecisionScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getPrecisionScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getRecallScore(2), cDresult.getOverallPerformance().getRecallScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getRecallScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getFMeasureScore(2), cDresult.getOverallPerformance().getFMeasureScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getFMeasureScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getAccuracyScore(2), cDresult.getOverallPerformance().getAccuracyScore(2), DELTA);               
                Assert.assertTrue(sDresult.getOverallPerformance().getAccuracyScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getMRRScore(2), cDresult.getOverallPerformance().getMRRScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getMRRScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getNDCGScore(2), cDresult.getOverallPerformance().getNDCGScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getNDCGScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getDiversityScore(2), cDresult.getOverallPerformance().getDiversityScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getDiversityScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getNoveltyScore(2), cDresult.getOverallPerformance().getNoveltyScore(2), DELTA);
                Assert.assertTrue(sDresult.getOverallPerformance().getNoveltyScore(2) > 0);
                Assert.assertEquals(sDresult.getOverallPerformance().getCoverageScore(), cDresult.getOverallPerformance().getCoverageScore(), DELTA);                         
                Assert.assertTrue(sDresult.getOverallPerformance().getCoverageScore() > 0);
        } catch (EvaluatorException ex) {
                Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail();
        }        
        System.out.println("testAllMetrics COMPLETED");
    }    
}



/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.repository;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;

/**
 * Test Class for SailRecEvaluatorRepository Class
 */
public class SailRecEvaluatorRepositoryTest {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread()
            .getContextClassLoader();

    /**
     * Allowed error.
     */
    private final double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    
    /**
     * Test if the Evaluator evaluates 2 Recommenders.
     * 
     * Same graph patterns case
     * 
     */
    @Test
    public void testSailRecEvaluator1() throws EvaluatorException {

        System.out.println("");
        System.out.println("testSailRecEvaluator1 starting...");
        
        
        try {
            ArrayList<RecConfig> recConfigList = new ArrayList();

            VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");            
            configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration1.preprocessBeforeRecommending(true);
            configuration1.setSimMetric(RecSimMetric.COSINE);
            configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration1.setDecimalPlaces(3);
            configuration1.setNeighborhoodSize(4);
        
            VsmCfRecConfig configuration2 = new VsmCfRecConfig("config2");

            configuration2.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.RATING, "?rating");
            
            
            configuration2.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration2.preprocessBeforeRecommending(true);
            configuration2.setSimMetric(RecSimMetric.COSINE);
            configuration2.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration2.setDecimalPlaces(3);
            configuration2.setNeighborhoodSize(4);

            recConfigList.add(configuration1);
            recConfigList.add(configuration2);
        
            SailRecEvaluatorRepository recEvalRepository
                    = EvalTestRepositoryInstantiator.createTestRepository1(recConfigList);

            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
            evalConfig.addRankingMetricTopKSize(1);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.evaluate();
            
            EvaluationResult result1 = recEvalRepository.getEvaluator().getEvalResultByName("config1");
            EvaluationResult result2 = recEvalRepository.getEvaluator().getEvalResultByName("config2");
                
            Double expectedMAE       = 1.1818481288744074;             
            Double expectedRMSE      = 1.7161880660968483;            
            Double expectedMRR       = 0.5; 
            Double expectedPrecision = 0.5;
            Double expectedRecall    = 0.5;         
            Double expectedFMeasure  = 0.5;             
            
            Assert.assertEquals(expectedMAE      , result1.getOverallPerformance().getMAEScore(), DELTA);
            Assert.assertEquals(expectedRMSE     , result1.getOverallPerformance().getRMSEScore(), DELTA);
            Assert.assertEquals(expectedMRR      , result1.getOverallPerformance().getMRRScore(1), DELTA);
            Assert.assertEquals(expectedPrecision, result1.getOverallPerformance().getPrecisionScore(1), DELTA);
            Assert.assertEquals(expectedRecall   , result1.getOverallPerformance().getRecallScore(1), DELTA);
            Assert.assertEquals(expectedFMeasure , result1.getOverallPerformance().getFMeasureScore(1), DELTA);
            
            Assert.assertEquals(expectedMAE      , result2.getOverallPerformance().getMAEScore(), DELTA);
            Assert.assertEquals(expectedRMSE     , result2.getOverallPerformance().getRMSEScore(), DELTA);
            Assert.assertEquals(expectedMRR      , result2.getOverallPerformance().getMRRScore(1), DELTA);
            Assert.assertEquals(expectedPrecision, result2.getOverallPerformance().getPrecisionScore(1), DELTA);
            Assert.assertEquals(expectedRecall   , result2.getOverallPerformance().getRecallScore(1), DELTA);
            Assert.assertEquals(expectedFMeasure , result2.getOverallPerformance().getFMeasureScore(1), DELTA);
            
        } catch (RecommenderException | EvaluatorException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testSailRecEvaluator1 COMPLETED");
    }    
}

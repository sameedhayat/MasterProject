/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator.crosskfold;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.evaluator.Evaluator;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResultsForFold;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.javatuples.Pair;

/**
 * Test class for CrossKFoldEvaluator Class
 *
 * Configurations that are tested in the test cases. 1) Non-CD with ratings 2)
 * Non-CD with likes 3) CD with ratings 4) CD with likes.
 */
public class CrossKFoldEvaluatorTest {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread()
            .getContextClassLoader();

    /**
     * Allowed error.
     */
    private final double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    private final double DELTA_3 = EvalTestRepositoryInstantiator.DELTA_3;

    
    /**
     * Tests results for each metric.(Dataset from the book).
     *
     * Non-CD with ratings
     */
    @Test
    public void testRecKFoldHybrid() {

        System.out.println("");
        System.out.println("testRecKFoldHybrid starting...");

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepositoryHybrid(
                        EvalTestRepositoryInstantiator.getRecConfigListHybrid());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
            evalConfig.addRankingMetricTopKSize(10);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?subject");
            evalConfig.setFeatureGraphPattern("?o <http://purl.org/dc/terms/subject> ?subject ");

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.evaluate();

            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");

//            Double expectedPrecision = 0.5;
//            Double expectedRecall = 0.5;
//            Double expectedFMeasure = 0.5;
//            Double expectedMRR = 0.5;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.777525512860841;
//            Double expectedNDCG = 0.5;
//            Double expectedAccuracy = 0.5;
//            Double expectedMAE = 1.1818481288744074;
//            Double expectedRMSE = 1.7161880660968483;
//            Double expectedAUC = 0.5;
//            Double expectedCoverage = 0.68;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);

        } catch (EvaluatorException ex) {
            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        System.out.println("testRecKFold1 COMPLETED");
    }

//   
//    /**
//     * Tests results for each metric.(Dataset from the book).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold1() {
//
//        System.out.println("");
//        System.out.println("testRecKFold1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository1(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.5;
//            Double expectedRecall = 0.5;
//            Double expectedFMeasure = 0.5;
//            Double expectedMRR = 0.5;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.777525512860841;
//            Double expectedNDCG = 0.5;
//            Double expectedAccuracy = 0.5;
//            Double expectedMAE = 1.1818481288744074;
//            Double expectedRMSE = 1.7161880660968483;
//            Double expectedAUC = 0.5;
//            Double expectedCoverage = 0.68;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold1 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric with different topKSize.(Dataset from the
//     * book).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold2() {
//
//        System.out.println("");
//        System.out.println("testRecKFold2 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository1(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(2);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.25;
//            Double expectedRecall = 0.5;
//            Double expectedFMeasure = 0.3333333333333333;
//            Double expectedMRR = 0.5;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.3887627564304205;
//            Double expectedNDCG = 0.5;
//            Double expectedAccuracy = 0.25;
//            Double expectedMAE = 1.1818481288744074;
//            Double expectedRMSE = 1.7161880660968483;
//            Double expectedAUC = 0.5;
//            Double expectedCoverage = 0.68;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(2), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(2), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(2), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(2), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(2), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(2), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(2), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(2), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold2 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric with different topKSize.(Dataset from the
//     * book, different order).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold3() {
//
//        System.out.println("");
//        System.out.println("testRecKFold3 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository7(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(2);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.25;
//            Double expectedRecall = 0.5;
//            Double expectedFMeasure = 0.3333333333333333;
//            Double expectedMRR = 0.5;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.3887627564304205;
//            Double expectedNDCG = 0.5;
//            Double expectedAccuracy = 0.25;
//            Double expectedMAE = 1.1818481288744074;
//            Double expectedRMSE = 1.7161880660968483;
//            Double expectedAUC = 0.5;
//            Double expectedCoverage = 0.68;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(2), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(2), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(2), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(2), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(2), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(2), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(2), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(2), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold3 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric.(Dataset with bad distribution).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold4() {
//
//        System.out.println("");
//        System.out.println("testRecKFold4 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.6666666666666666;
//            Double expectedRecall = 0.5999999999999999;
//            Double expectedFMeasure = 0.6285714285714286;
//            Double expectedMRR = 0.6666666666666666;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7046109212700495;
//            Double expectedNDCG = 0.6666666666666666;
//            Double expectedAccuracy = 0.5999999999999999;
//            Double expectedMAE = 0.9599943331492353;
//            Double expectedRMSE = 1.1143024642417965;
//            Double expectedAUC = 0.7666666666666666;
//            Double expectedCoverage = 0.375;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold4 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric when Precision is missing.(Dataset with bad
//     * distribution).
//     *
//     * It should calculate Precision by itself because of F-Measure Metric.
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold5() {
//
//        System.out.println("");
//        System.out.println("testRecKFold5 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.6666666666666666;
//            Double expectedRecall = 0.5999999999999999;
//            Double expectedFMeasure = 0.6285714285714286;
//            Double expectedMRR = 0.6666666666666666;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7046109212700495;
//            Double expectedNDCG = 0.6666666666666666;
//            Double expectedAccuracy = 0.5999999999999999;
//            Double expectedMAE = 0.9599943331492353;
//            Double expectedRMSE = 1.1143024642417965;
//            Double expectedAUC = 0.7666666666666666;
//            Double expectedCoverage = 0.375;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold5 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric when Recall is missing.(Dataset with bad
//     * distribution).
//     *
//     * It should calculate Recall by itself because of F-Measure Metric.
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold6() {
//
//        System.out.println("");
//        System.out.println("testRecKFold6 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.6666666666666666;
//            Double expectedRecall = 0.5999999999999999;
//            Double expectedFMeasure = 0.6285714285714286;
//            Double expectedMRR = 0.6666666666666666;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7046109212700495;
//            Double expectedNDCG = 0.6666666666666666;
//            Double expectedAccuracy = 0.5999999999999999;
//            Double expectedMAE = 0.9599943331492353;
//            Double expectedRMSE = 1.1143024642417965;
//            Double expectedAUC = 0.7666666666666666;
//            Double expectedCoverage = 0.375;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold6 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric when Precision & Recall is missing.(Dataset
//     * with bad distribution).
//     *
//     * It should calculate Precision & Recall by itself because of F-Measure
//     * Metric.
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold7() {
//
//        System.out.println("");
//        System.out.println("testRecKFold7 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.6666666666666666;
//            Double expectedRecall = 0.5999999999999999;
//            Double expectedFMeasure = 0.6285714285714286;
//            Double expectedMRR = 0.6666666666666666;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7046109212700495;
//            Double expectedNDCG = 0.6666666666666666;
//            Double expectedAccuracy = 0.5999999999999999;
//            Double expectedMAE = 0.9599943331492353;
//            Double expectedRMSE = 1.1143024642417965;
//            Double expectedAUC = 0.7666666666666666;
//            Double expectedCoverage = 0.375;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold7 COMPLETED");
//    }
//
//    /**
//     * Test if getTopK feature works correctly in CrossKFold
//     * Evaluator.(Auto-generated dataset, InputCreator is used).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold8() {
//
//        System.out.println("");
//        System.out.println("testRecKFold8 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository2(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(5);
//            evalConfig.addRankingMetricTopKSize(7);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 10000000, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            System.out.println(result.getOverallPerformance().getPrecisionScore(5));
//            System.out.println(result.getOverallPerformance().getRecallScore(5));
//            System.out.println(result.getOverallPerformance().getFMeasureScore(5));
//            System.out.println(result.getOverallPerformance().getMRRScore(5));
//            System.out.println(result.getOverallPerformance().getDiversityScore(5));
//            System.out.println(result.getOverallPerformance().getNoveltyScore(5));
//            System.out.println(result.getOverallPerformance().getNDCGScore(5));
//            System.out.println(result.getOverallPerformance().getAccuracyScore(5));
//            System.out.println(result.getOverallPerformance().getPrecisionScore(7));
//            System.out.println(result.getOverallPerformance().getRecallScore(7));
//            System.out.println(result.getOverallPerformance().getFMeasureScore(7));
//            System.out.println(result.getOverallPerformance().getMRRScore(7));
//            System.out.println(result.getOverallPerformance().getDiversityScore(7));
//            System.out.println(result.getOverallPerformance().getNoveltyScore(7));
//            System.out.println(result.getOverallPerformance().getNDCGScore(7));
//            System.out.println(result.getOverallPerformance().getAccuracyScore(7));
//            System.out.println(result.getOverallPerformance().getMAEScore());
//            System.out.println(result.getOverallPerformance().getRMSEScore());
//            System.out.println(result.getOverallPerformance().getAUCScore());
//            System.out.println(result.getOverallPerformance().getCoverageScore());
//
//            Double expectedPrecision5 = 0.2;
//            Double expectedRecall5 = 0.7535999999999999;
//            Double expectedFMeasure5 = 0.31604598279967633;
//            Double expectedMRR5 = 0.6256;
//            Double expectedDiversity5 = 1.8985482005889263;
//            Double expectedNovelty5 = 1.2652503030375095;
//            Double expectedNDCG5 = 0.6592656450128646;
//            Double expectedAccuracy5 = 0.2;
//            Double expectedPrecision7 = 0.14285714285714332;
//            Double expectedRecall7 = 0.7536;
//            Double expectedFMeasure7 = 0.24014583458688757;
//            Double expectedMRR7 = 0.6256;
//            Double expectedDiversity7 = 0.9040420002805943;
//            Double expectedNovelty7 = 0.9037502164561093;
//            Double expectedNDCG7 = 0.6592656450128646;
//            Double expectedAccuracy7 = 0.14285714285714332;
//            Double expectedMAE = 1.6001817599141634;
//            Double expectedRMSE = 2.879828098755133;
//            Double expectedAUC = 0.4932134165314716;
//            Double expectedCoverage = 0.9949999999999999;
//
//            Assert.assertEquals(expectedPrecision5, result.getOverallPerformance().getPrecisionScore(5), DELTA_3);
//            Assert.assertEquals(expectedRecall5, result.getOverallPerformance().getRecallScore(5), DELTA_3);
//            Assert.assertEquals(expectedFMeasure5, result.getOverallPerformance().getFMeasureScore(5), DELTA_3);
//            Assert.assertEquals(expectedMRR5, result.getOverallPerformance().getMRRScore(5), DELTA_3);
//            Assert.assertEquals(expectedDiversity5, result.getOverallPerformance().getDiversityScore(5), DELTA_3);
//            Assert.assertEquals(expectedNovelty5, result.getOverallPerformance().getNoveltyScore(5), DELTA_3);
//            Assert.assertEquals(expectedNDCG5, result.getOverallPerformance().getNDCGScore(5), DELTA_3);
//            Assert.assertEquals(expectedAccuracy5, result.getOverallPerformance().getAccuracyScore(5), DELTA_3);
//            Assert.assertEquals(expectedPrecision7, result.getOverallPerformance().getPrecisionScore(7), DELTA_3);
//            Assert.assertEquals(expectedRecall7, result.getOverallPerformance().getRecallScore(7), DELTA_3);
//            Assert.assertEquals(expectedFMeasure7, result.getOverallPerformance().getFMeasureScore(7), DELTA_3);
//            Assert.assertEquals(expectedMRR7, result.getOverallPerformance().getMRRScore(7), DELTA_3);
//            Assert.assertEquals(expectedDiversity7, result.getOverallPerformance().getDiversityScore(7), DELTA_3);
//            Assert.assertEquals(expectedNovelty7, result.getOverallPerformance().getNoveltyScore(7), DELTA_3);
//            Assert.assertEquals(expectedNDCG7, result.getOverallPerformance().getNDCGScore(7), DELTA_3);
//            Assert.assertEquals(expectedAccuracy7, result.getOverallPerformance().getAccuracyScore(7), DELTA_3);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA_3);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA_3);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA_3);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA_3);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold8 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric when NativeStore is used.(Dataset with bad
//     * distribution).
//     *
//     * Non-CD with ratings
//     */
//    @Test
//    public void testRecKFold9() {
//
//        System.out.println("");
//        System.out.println("testRecKFold9 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.NATIVE);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.6666666666666666;
//            Double expectedRecall = 0.5999999999999999;
//            Double expectedFMeasure = 0.6285714285714286;
//            Double expectedMRR = 0.6666666666666666;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7046109212700495;
//            Double expectedNDCG = 0.6666666666666666;
//            Double expectedAccuracy = 0.6;
//            Double expectedMAE = 0.9599943331492353;
//            Double expectedRMSE = 1.1143024642417965;
//            Double expectedAUC = 0.7666666666666666;
//            Double expectedCoverage = 0.375;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFold9 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric.(Dataset from the book + CD target domain).
//     *
//     * CD with ratings
//     *
//     * Will be improved in the future since CD with ratings is not stable yet at
//     * the Recommender.
//     */
//    @Test
//    public void testRecKFoldCD1() {
//
//        System.out.println("");
//        System.out.println("testRecKFoldCD1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository4(
//                        EvalTestRepositoryInstantiator.getRecConfigList3());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutputCD.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 0.5;
//            Double expectedRecall = 0.5;
//            Double expectedFMeasure = 0.5;
//            Double expectedMRR = 0.5;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.7775255128608;
//            Double expectedNDCG = 0.5;
//            Double expectedAccuracy = 0.5;
//            Double expectedMAE = 1.721340765024062;
//            Double expectedRMSE = 3.2045949342986;
//            Double expectedAUC = 0.5333333333333333;
//            Double expectedCoverage = 0.44;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFoldCD1 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric.(Dataset with "Likes" relation).
//     *
//     * Non-CD with likes
//     */
//    @Test
//    public void testRecKFoldLikes1() {
//
//        System.out.println("");
//        System.out.println("testRecKFoldLikes1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository6(
//                        EvalTestRepositoryInstantiator.getRecConfigList2());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 9, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            // TODO likes - recommender: non-deterministic
//            Double expectedMAE = 0.7447789548843693;
//            Double expectedRMSE = 0.5605759323667919;
//            Double expectedMRR = 0.4148148148148148;
//            Double expectedPrecision = 0.1211111111111111;
//            Double expectedRecall = 0.13333333333333336;
//            Double expectedFMeasure = 0.1111111111111111;
//
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//
//            System.out.println(result.getOverallPerformance().getPrecisionScore(1));
//            System.out.println(result.getOverallPerformance().getRecallScore(1));
//            System.out.println(result.getOverallPerformance().getFMeasureScore(1));
//            System.out.println(result.getOverallPerformance().getMRRScore(1));
//            System.out.println(result.getOverallPerformance().getDiversityScore(1));
//            System.out.println(result.getOverallPerformance().getNoveltyScore(1));
//            System.out.println(result.getOverallPerformance().getNDCGScore(1));
//            System.out.println(result.getOverallPerformance().getAccuracyScore(1));
//            System.out.println(result.getOverallPerformance().getMAEScore());
//            System.out.println(result.getOverallPerformance().getRMSEScore());
//            System.out.println(result.getOverallPerformance().getAUCScore());
//            System.out.println(result.getOverallPerformance().getCoverageScore());
//            // Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            // Assert.assertEquals(expectedFMeasure , result.getOverallPerformance().getFMeasureScore(1), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//        System.out.println("testRecKFoldLikes1 COMPLETED");
//    }
//
//    /**
//     * Tests results for each metric.
//     *
//     * CD with likes
//     */
//    @Test
//    public void testRecKFoldCDLikes1() {
//
//        System.out.println("");
//        System.out.println("testRecKFoldCDLikes1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository5(
//                        EvalTestRepositoryInstantiator.getRecConfigList4());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.MEMORY);
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//            evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 9, 0));
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            recEvalRepository.evaluate();
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            Double expectedPrecision = 1.0;
//            Double expectedRecall = 1.0;
//            Double expectedFMeasure = 1.0;
//            Double expectedMRR = 1.0;
//            Double expectedDiversity = 0.0;
//            Double expectedNovelty = 0.777525512860841;
//            Double expectedNDCG = 1.0;
//            Double expectedAccuracy = 1.0;
//            Double expectedMAE = 1.0;
//            Double expectedRMSE = 1.0;
//            Double expectedAUC = 1.0;
//            Double expectedCoverage = 0.2;
//
//            Assert.assertEquals(expectedPrecision, result.getOverallPerformance().getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall, result.getOverallPerformance().getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure, result.getOverallPerformance().getFMeasureScore(1), DELTA);
//            Assert.assertEquals(expectedMRR, result.getOverallPerformance().getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedDiversity, result.getOverallPerformance().getDiversityScore(1), DELTA);
//            Assert.assertEquals(expectedNovelty, result.getOverallPerformance().getNoveltyScore(1), DELTA);
//            Assert.assertEquals(expectedNDCG, result.getOverallPerformance().getNDCGScore(1), DELTA);
//            Assert.assertEquals(expectedAccuracy, result.getOverallPerformance().getAccuracyScore(1), DELTA);
//            Assert.assertEquals(expectedMAE, result.getOverallPerformance().getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE, result.getOverallPerformance().getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedAUC, result.getOverallPerformance().getAUCScore(), DELTA);
//            Assert.assertEquals(expectedCoverage, result.getOverallPerformance().getCoverageScore(), DELTA);
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testRecKFoldCDLikes1 COMPLETED");
//    }
//
//    /**
//     * Test for calculateResultsForRecConfig method.
//     *
//     */
//    @Test
//    public void testCalculateResults() {
//
//        System.out.println("");
//        System.out.println("testCalculateResults starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository3(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        try {
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//            evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//            evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//            evalConfig.addRankingMetricTopKSize(1);
//            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 6, 0));
//            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//            evalConfig.setStorage(EvalStorage.NATIVE);
//            evalConfig.setIsReproducible(true);
//            evalConfig.setNumberOfFolds(5);
//            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//            recEvalRepository.loadEvalConfiguration(evalConfig);
//            ArrayList<ArrayList<Double>> evalMetricResults = new ArrayList<>();
//
//            ArrayList<Double> fold1 = new ArrayList<>();
//            ArrayList<Double> fold2 = new ArrayList<>();
//            ArrayList<Double> fold3 = new ArrayList<>();
//
//            fold1.add(0.25); // MAE
//            fold1.add(0.5); // RMSE
//            fold1.add(0.9);  // MRR
//            fold1.add(1.0);  // PREC
//            fold1.add(0.5); // REC
//            fold1.add(0.0);  // F-MEASURE will be calculated next, that is why it is 0.0
//
//            fold2.add(0.75); // MAE
//            fold2.add(0.20); // RMSE
//            fold2.add(0.8);  // MRR
//            fold2.add(0.0);  // PREC
//            fold2.add(0.3); // REC
//            fold2.add(0.0);  // F-MEASURE will be calculated next, that is why it is 0.0
//
//            fold3.add(0.25); // MAE
//            fold3.add(0.80); // RMSE
//            fold3.add(0.7);  // MRR
//            fold3.add(0.2);  // PREC
//            fold3.add(0.9); // REC
//            fold3.add(0.0);  // F-MEASURE will be calculated next, that is why it is 0.0
//
//            evalMetricResults.add(fold1);
//            evalMetricResults.add(fold2);
//            evalMetricResults.add(fold3);
//
//            ((CrossKFoldEvaluator) recEvalRepository.getEvaluator())
//                    .calculateResultsForRecConfig(evalMetricResults,
//                            EvalTestRepositoryInstantiator.getRecConfigList1().get(0));
//
//            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");
//
//            EvaluationResultsForFold resFold1 = result.getFoldResult(0);
//            EvaluationResultsForFold resFoldAvg = result.getOverallPerformance();
//
//            // Check if EvaluationResultsForFold object is created correctly && fmeasure calculated correctly
//            Double expectedMAE1 = 0.25;
//            Double expectedRMSE1 = 0.5;
//            Double expectedMRR1 = 0.9;
//            Double expectedPrecision1 = 1.0;
//            Double expectedRecall1 = 0.5;
//            Double expectedFMeasure1 = 0.66666666666666;
//
//            Assert.assertEquals(expectedMAE1, resFold1.getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSE1, resFold1.getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedMRR1, resFold1.getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedPrecision1, resFold1.getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecall1, resFold1.getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasure1, resFold1.getFMeasureScore(1), DELTA);
//
//            Assert.assertEquals(EvalMetric.MAE, resFold1.getMetrics().get(0).getMetric());
//            Assert.assertEquals(EvalMetric.RMSE, resFold1.getMetrics().get(1).getMetric());
//            Assert.assertEquals(EvalMetric.MRR, resFold1.getMetrics().get(2).getMetric());
//            Assert.assertEquals(EvalMetric.PRE, resFold1.getMetrics().get(3).getMetric());
//            Assert.assertEquals(EvalMetric.REC, resFold1.getMetrics().get(4).getMetric());
//            Assert.assertEquals(EvalMetric.F_MEASURE, resFold1.getMetrics().get(5).getMetric());
//
//            // Check if results averages are calculated correctly
//            Double expectedMAEavg = 0.25;
//            Double expectedRMSEavg = 0.5;
//            Double expectedMRRavg = 0.9;
//            Double expectedPrecisionavg = 1.0;
//            Double expectedRecallavg = 0.5;
//            Double expectedFMeasureavg = 0.6666666666666666;
//
//            Assert.assertEquals(expectedMAEavg, resFold1.getMAEScore(), DELTA);
//            Assert.assertEquals(expectedRMSEavg, resFold1.getRMSEScore(), DELTA);
//            Assert.assertEquals(expectedMRRavg, resFold1.getMRRScore(1), DELTA);
//            Assert.assertEquals(expectedPrecisionavg, resFold1.getPrecisionScore(1), DELTA);
//            Assert.assertEquals(expectedRecallavg, resFold1.getRecallScore(1), DELTA);
//            Assert.assertEquals(expectedFMeasureavg, resFold1.getFMeasureScore(1), DELTA);
//
//            Assert.assertEquals(EvalMetric.MAE, resFoldAvg.getMetrics().get(0).getMetric());
//            Assert.assertEquals(EvalMetric.RMSE, resFoldAvg.getMetrics().get(1).getMetric());
//            Assert.assertEquals(EvalMetric.MRR, resFoldAvg.getMetrics().get(2).getMetric());
//            Assert.assertEquals(EvalMetric.PRE, resFoldAvg.getMetrics().get(3).getMetric());
//            Assert.assertEquals(EvalMetric.REC, resFoldAvg.getMetrics().get(4).getMetric());
//            Assert.assertEquals(EvalMetric.F_MEASURE, resFoldAvg.getMetrics().get(5).getMetric());
//
//        } catch (EvaluatorException ex) {
//            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        }
//
//        System.out.println("testCalculateResults COMPLETED");
//    }
//    
//    /**
//     * Tests getCandidates() method.
//     *
//     * Ratings feedback case.
//     * 
//     */
//    @Test
//    public void testGetCandidates1() {
//        
//        System.out.println("");
//        System.out.println("testGetCandidates1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository1(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//       
//        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//        evalConfig.setStorage(EvalStorage.MEMORY);
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//        evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//        evalConfig.addRankingMetricTopKSize(1);
//        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//        evalConfig.setIsReproducible(true);
//        evalConfig.setNumberOfFolds(5);
//        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//        evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//        recEvalRepository.loadEvalConfiguration(evalConfig);
//        CrossKFoldEvaluator cKFoldEval = (CrossKFoldEvaluator) recEvalRepository.getEvaluator();
//        
//        ArrayList<String> items = new ArrayList<>();
//        
//        items.add("<http://example.org/users#Item1>");
//        items.add("<http://example.org/users#Item2>");
//        items.add("<http://example.org/users#Item3>");
//        items.add("<http://example.org/users#Item4>");
//        items.add("<http://example.org/users#Item5>");
//        items.add("<http://example.org/users#Item6>");
//        
//        cKFoldEval.setItems(items);
//        
//        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
//             
//        ArrayList<Rating> jackRatings0   = new ArrayList<>();
//        ArrayList<Rating> jackRatings1   = new ArrayList<>();
//        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
//        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
//        
//        String item1 = "<http://example.org/users#Item1>";
//        String item2 = "<http://example.org/users#Item2>";
//        String item3 = "<http://example.org/users#Item3>";
//        String item4 = "<http://example.org/users#Item4>";
//        String item5 = "<http://example.org/users#Item5>";
//        String item6 = "<http://example.org/users#Item6>";
//        
//        Rating rating1  = new Rating("<http://example.org/users#Jack>", item1 , 3.0);
//        Rating rating2  = new Rating("<http://example.org/users#Jack>", item2 , 2.0);
//        Rating rating3  = new Rating("<http://example.org/users#Jack>", item3 , 3.0);
//        Rating rating4  = new Rating("<http://example.org/users#Jack>", item4 , 0.0);
//        Rating rating5  = new Rating("<http://example.org/users#Jack>", item5 , 1.0);
//        Rating rating6  = new Rating("<http://example.org/users#Jack>", item6 , 2.0);
//        
//        Rating rating7  = new Rating("<http://example.org/users#Carlos>", item1 , 3.0);
//        Rating rating8  = new Rating("<http://example.org/users#Carlos>", item2 , 0.0);
//        Rating rating9  = new Rating("<http://example.org/users#Carlos>", item3 , 1.0);
//        Rating rating10 = new Rating("<http://example.org/users#Carlos>", item4 , 1.0);
//        Rating rating11 = new Rating("<http://example.org/users#Carlos>", item5 , 2.0);
//        Rating rating12 = new Rating("<http://example.org/users#Carlos>", item6 , 3.0);
//        
//        jackRatings0.add(rating1);
//        jackRatings0.add(rating2);
//        jackRatings1.add(rating4);
//        jackRatings1.add(rating5);
//        
//        carlosRatings0.add(rating7);
//        carlosRatings0.add(rating8);
//        carlosRatings1.add(rating10);
//        carlosRatings1.add(rating11);
//        
//        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
//        jackFolds.add(jackRatings0);
//        jackFolds.add(jackRatings1);        
//        
//        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
//        carlosFolds.add(carlosRatings0);
//        carlosFolds.add(carlosRatings1);        
//        
//        foldsList.add(jackFolds);
//        foldsList.add(carlosFolds);
//        Folds folds = new Folds(foldsList);
//        
//        Pair<HashSet<String>,Integer> candidates1 = cKFoldEval.getCandidates(folds, 0, 0, false);
//        // for Diversity and Novelty 
//        Pair<HashSet<String>,Integer> candidates2 = cKFoldEval.getCandidates(folds, 0, 0, true);
//        
//        int topKSize1 = candidates1.getValue1();
//        int topKSize2 = candidates2.getValue1();
//        
//        Assert.assertTrue( candidates1.getValue0().contains(item1) );
//        Assert.assertTrue( candidates1.getValue0().contains(item2) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item3) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item4) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item5) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item6) );
//        Assert.assertEquals( 2, candidates1.getValue0().size());
//        Assert.assertEquals( 1, topKSize1 );
//        
//        Assert.assertEquals( 1, topKSize2);
//        Assert.assertEquals( 4, candidates2.getValue0().size());
//        Assert.assertTrue( candidates2.getValue0().contains(item1) );
//        Assert.assertTrue( candidates2.getValue0().contains(item2) );
//        Assert.assertTrue( candidates2.getValue0().contains(item3) );
//        Assert.assertTrue( !candidates2.getValue0().contains(item4) );
//        Assert.assertTrue( !candidates2.getValue0().contains(item5) );
//        Assert.assertTrue( candidates2.getValue0().contains(item6) );
//        
//        System.out.println("testGetCandidates1 COMPLETED");
//    }
//    
//    /**
//     * Tests getCandidates() method.
//     *
//     * Positive feedback case.
//     * 
//     */
//    @Test
//    public void testGetCandidates2() {
//        
//        System.out.println("");
//        System.out.println("testGetCandidates2 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository6(
//                        EvalTestRepositoryInstantiator.getRecConfigList2());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//        
//        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//        evalConfig.setStorage(EvalStorage.MEMORY);
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//        evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//        evalConfig.addRankingMetricTopKSize(1);
//        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 9, 0));
//        evalConfig.setIsReproducible(true);
//        evalConfig.setNumberOfFolds(5);
//        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//        evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//        recEvalRepository.loadEvalConfiguration(evalConfig);
//        CrossKFoldEvaluator cKFoldEval = (CrossKFoldEvaluator) recEvalRepository.getEvaluator();
//        
//        ArrayList<String> items = new ArrayList<>();
//        
//        items.add("<http://example.org/users#Item1>");
//        items.add("<http://example.org/users#Item2>");
//        items.add("<http://example.org/users#Item3>");
//        items.add("<http://example.org/users#Item4>");
//        items.add("<http://example.org/users#Item5>");
//        items.add("<http://example.org/users#Item6>");
//        
//        cKFoldEval.setItems(items);
//        
//        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
//             
//        ArrayList<Rating> jackRatings0   = new ArrayList<>();
//        ArrayList<Rating> jackRatings1   = new ArrayList<>();
//        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
//        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
//        
//        String item1 = "<http://example.org/users#Item1>";
//        String item2 = "<http://example.org/users#Item2>";
//        String item3 = "<http://example.org/users#Item3>";
//        String item4 = "<http://example.org/users#Item4>";
//        String item5 = "<http://example.org/users#Item5>";
//        String item6 = "<http://example.org/users#Item6>";
//        
//        Rating rating1  = new Rating("<http://example.org/users#Jack>", item1 , 3.0);
//        Rating rating2  = new Rating("<http://example.org/users#Jack>", item2 , 2.0);
//        Rating rating3  = new Rating("<http://example.org/users#Jack>", item3 , 3.0);
//        Rating rating4  = new Rating("<http://example.org/users#Jack>", item4 , 0.0);
//        Rating rating5  = new Rating("<http://example.org/users#Jack>", item5 , 1.0);
//        Rating rating6  = new Rating("<http://example.org/users#Jack>", item6 , 2.0);
//        
//        Rating rating7  = new Rating("<http://example.org/users#Carlos>", item1 , 3.0);
//        Rating rating8  = new Rating("<http://example.org/users#Carlos>", item2 , 0.0);
//        Rating rating9  = new Rating("<http://example.org/users#Carlos>", item3 , 1.0);
//        Rating rating10 = new Rating("<http://example.org/users#Carlos>", item4 , 1.0);
//        Rating rating11 = new Rating("<http://example.org/users#Carlos>", item5 , 2.0);
//        Rating rating12 = new Rating("<http://example.org/users#Carlos>", item6 , 3.0);
//        
//        jackRatings0.add(rating1);
//        jackRatings0.add(rating2);
//        jackRatings1.add(rating4);
//        jackRatings1.add(rating5);
//        
//        carlosRatings0.add(rating7);
//        carlosRatings0.add(rating8);
//        carlosRatings1.add(rating10);
//        carlosRatings1.add(rating11);
//        
//        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
//        jackFolds.add(jackRatings0);
//        jackFolds.add(jackRatings1);        
//        
//        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
//        carlosFolds.add(carlosRatings0);
//        carlosFolds.add(carlosRatings1);        
//        
//        foldsList.add(jackFolds);
//        foldsList.add(carlosFolds);
//        Folds folds = new Folds(foldsList);
//        
//        Pair<HashSet<String>,Integer> candidates1 = cKFoldEval.getCandidates(folds, 0, 0, false);
//        // for Diversity and Novelty 
//        Pair<HashSet<String>,Integer> candidates2 = cKFoldEval.getCandidates(folds, 0, 0, true);
//        
//        Assert.assertTrue( candidates1.getValue0().contains(item1) );
//        Assert.assertTrue( candidates1.getValue0().contains(item2) );
//        Assert.assertTrue( candidates1.getValue0().contains(item3) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item4) );
//        Assert.assertTrue( !candidates1.getValue0().contains(item5) );
//        Assert.assertTrue( candidates1.getValue0().contains(item6) );
//        Assert.assertEquals( 4 , candidates1.getValue0().size());
//        
//        Assert.assertTrue( candidates2.getValue0().contains(item1) );
//        Assert.assertTrue( candidates2.getValue0().contains(item2) );
//        Assert.assertTrue( candidates2.getValue0().contains(item3) );
//        Assert.assertTrue( !candidates2.getValue0().contains(item4) );
//        Assert.assertTrue( !candidates2.getValue0().contains(item5) );
//        Assert.assertTrue( candidates2.getValue0().contains(item6) );
//        Assert.assertEquals( 4, candidates2.getValue0().size());
//        
//        System.out.println("testGetCandidates2 COMPLETED");
//    }
//    
//    /**
//     * Tests createTestSet() method.     * 
//     */
//    @Test
//    public void testCreateTestSet1() {
//        
//        System.out.println("");
//        System.out.println("testCreateTestSet1 starting...");
//
//        SailRecEvaluatorRepository recEvalRepository
//                = EvalTestRepositoryInstantiator.createTestRepository1(
//                        EvalTestRepositoryInstantiator.getRecConfigList1());
//
//        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
//
//       
//        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
//        evalConfig.setStorage(EvalStorage.MEMORY);
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.PRE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.REC));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.F_MEASURE));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.MRR));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.DIVERSITY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NOVELTY));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.NDCG));
//        evalConfig.addEvalMetric(new RankingEvalMetric(EvalMetric.ACC));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.MAE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.RMSE));
//        evalConfig.addEvalMetric(new PredictionEvalMetric(EvalMetric.AUC));
//        evalConfig.addEvalMetric(new GlobalEvalMetric(EvalMetric.COVERAGE));
//        evalConfig.addRankingMetricTopKSize(1);
//        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
//        evalConfig.setIsReproducible(true);
//        evalConfig.setNumberOfFolds(5);
//        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
//        evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");
//
//        recEvalRepository.loadEvalConfiguration(evalConfig);
//        CrossKFoldEvaluator cKFoldEval = (CrossKFoldEvaluator) recEvalRepository.getEvaluator();
//        
//        ArrayList<String> items = new ArrayList<>();
//        
//        items.add("<http://example.org/users#Item1>");
//        items.add("<http://example.org/users#Item2>");
//        items.add("<http://example.org/users#Item3>");
//        items.add("<http://example.org/users#Item4>");
//        items.add("<http://example.org/users#Item5>");
//        items.add("<http://example.org/users#Item6>");
//        
//        cKFoldEval.setItems(items);
//        
//        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
//        ignoreUsers.add(Boolean.FALSE);
//        ignoreUsers.add(Boolean.FALSE);
//        
//        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
//             
//        ArrayList<Rating> jackRatings0   = new ArrayList<>();
//        ArrayList<Rating> jackRatings1   = new ArrayList<>();
//        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
//        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
//        
//        String item1 = "<http://example.org/users#Item1>";
//        String item2 = "<http://example.org/users#Item2>";
//        String item3 = "<http://example.org/users#Item3>";
//        String item4 = "<http://example.org/users#Item4>";
//        String item5 = "<http://example.org/users#Item5>";
//        String item6 = "<http://example.org/users#Item6>";
//        
//        Rating rating1  = new Rating("<http://example.org/users#Jack>", item1 , 3.0);
//        Rating rating2  = new Rating("<http://example.org/users#Jack>", item2 , 2.0);
//        Rating rating3  = new Rating("<http://example.org/users#Jack>", item3 , 3.0);
//        Rating rating4  = new Rating("<http://example.org/users#Jack>", item4 , 0.0);
//        Rating rating5  = new Rating("<http://example.org/users#Jack>", item5 , 1.0);
//        Rating rating6  = new Rating("<http://example.org/users#Jack>", item6 , 2.0);
//        
//        Rating rating7  = new Rating("<http://example.org/users#Carlos>", item1 , 3.0);
//        Rating rating8  = new Rating("<http://example.org/users#Carlos>", item2 , 0.0);
//        Rating rating9  = new Rating("<http://example.org/users#Carlos>", item3 , 1.0);
//        Rating rating10 = new Rating("<http://example.org/users#Carlos>", item4 , 1.0);
//        Rating rating11 = new Rating("<http://example.org/users#Carlos>", item5 , 2.0);
//        Rating rating12 = new Rating("<http://example.org/users#Carlos>", item6 , 3.0);
//        
//        jackRatings0.add(rating1);
//        jackRatings0.add(rating2);
//        jackRatings1.add(rating4);
//        jackRatings1.add(rating5);
//        
//        carlosRatings0.add(rating7);
//        carlosRatings0.add(rating8);
//        carlosRatings1.add(rating10);
//        carlosRatings1.add(rating11);
//        
//        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
//        jackFolds.add(jackRatings0);
//        jackFolds.add(jackRatings1);        
//        
//        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
//        carlosFolds.add(carlosRatings0);
//        carlosFolds.add(carlosRatings1);        
//        
//        foldsList.add(jackFolds);
//        foldsList.add(carlosFolds);
//        Folds folds = new Folds(foldsList);
//        
//        ArrayList<Rating> testSet = cKFoldEval.createTestSet(ignoreUsers, folds, 0);
//        
//        Assert.assertEquals( 4, testSet.size());
//        Assert.assertTrue( testSet.contains(rating1) );
//        Assert.assertTrue( testSet.contains(rating2) );
//        Assert.assertTrue( testSet.contains(rating7) );
//        Assert.assertTrue( testSet.contains(rating8) );
//        
//        
//        System.out.println("testCreateTestSet1 COMPLETED");
//    }
}

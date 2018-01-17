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
            evalConfig.addRankingMetricTopKSize(20);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM, 5, 0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(6);
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?subject");
            evalConfig.setFeatureGraphPattern("?o <http://purl.org/dc/terms/subject> ?subject ");

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.evaluate();

            EvaluationResult result = recEvalRepository.getEvaluator().getEvalResultByName("config1");

        } catch (EvaluatorException ex) {
            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        System.out.println("testRecKFold1 COMPLETED");
    }

}

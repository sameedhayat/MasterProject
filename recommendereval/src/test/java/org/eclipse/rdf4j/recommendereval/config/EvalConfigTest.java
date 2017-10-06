/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.config;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommender.config.LinkAnalysisRecConfig;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.output.model.EvaluationResult;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepositoryTest;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.junit.Assert;

/**
 * Test class for EvalConfigTest Class
 */
public class EvalConfigTest {

    /**
     * Allowed error.
     */
    private final double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    
    /**
     * Test if the EvaluatorException is thrown. Empty set of Recommender
     * Configurations; RecConfig list is given.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation1() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation1 starting...");
        
        ArrayList<RecConfig> configList = new ArrayList<>();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation1 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Empty Evaluation
     * Configuration.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation2() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation2 starting...");

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvalExceptionOnValidation2 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. No output method provided.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation3() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation3 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation3 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Output file name is null.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation4() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation4 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, null);
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation4 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Output file name is empty.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation5() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation5 starting...");
        
        
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation5 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. No Evaluation Metric provided.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation6() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation6 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation6 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. An Evaluation Metric provided
     * multiple times.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation7() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation7 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());
        
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation7 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Number of test sets is 1.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation8() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation8 starting...");
       

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation8 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Number of test sets is a
     * negative number.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation9() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation9 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(-1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation9 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Number of test sets is higher
     * than ratings size.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation10() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation10 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(500);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation10 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Number of test sets is above 5,
     * all folds should have invalid number of ratings for all users. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation11() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation11 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(6);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation11 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. No topKSize provided. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation12() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation12 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation12 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is NOT thrown. No topKSize provided.
     * (No Precision, Recall or F-Measure case) 
     *
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    @Test
    public void testRecEvalExceptionOnValidation13() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation13 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation13 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Non-positive topKSize provided. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation14() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation14 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(-2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation14 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. No storage type is provided. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation15() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation15 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.F_MEASURE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MRR));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation15 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Wrong EvalMetric provided for
     * RankingEvalMetric. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation16() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation16 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation16 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Wrong EvalMetric provided for
     * PredictionEvalMetric. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation17() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation17 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.REC));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation17 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Wrong EvalMetric provided for
     * GlobalEvalMetric. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation18() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation18 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new GlobalEvalMetric(EvalMetric.COVERAGE));
        evalConfig.addEvalMetric( new GlobalEvalMetric(EvalMetric.REC));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation18 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. FeatureGraphPattern is not provided 
     * while DIVERSITY metric is used. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation19() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation19 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation19 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is not thrown. FeatureGraphPattern is not provided 
     * while DIVERSITY metric is not used. 
     */
    @Test
    public void testRecEvalExceptionOnValidation20() {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation20 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation20 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is thrown. Feature Entity is not provided 
     * while DIVERSITY metric is used. 
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionOnValidation21() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation21 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.setFeatureGraphPattern("Dummy Pattern");
        // evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feature");
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation21 COMPLETED");
    }

    /**
     * Test if the EvaluatorException is not thrown. Feature Entity is not provided 
     * while DIVERSITY metric is not used. 
     */
    @Test
    public void testRecEvalExceptionOnValidation22() {

        System.out.println("");
        System.out.println("testRecEvalExceptionOnValidation22 starting...");
        

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
            
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE));
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionOnValidation22 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. No Graph Pattern provided
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionGraphPattern1() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionGraphPattern1 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

        try {
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration.setRecEntity(RecEntity.RATING, "?rating");
        } catch (RecommenderException ex) {
            Logger.getLogger(EvalConfigTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration.setDecimalPlaces(3);
        configuration.setNeighborhoodSize(4);

        recConfigList.add(configuration);
        
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
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionGraphPattern1 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. No USER Entity provided.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionGraphPattern2() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionGraphPattern2 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

        try {
            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration.setRecEntity(RecEntity.RATING, "?rating");
        } catch (RecommenderException ex) {
            Logger.getLogger(EvalConfigTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration.setDecimalPlaces(3);
        configuration.setNeighborhoodSize(4);

        recConfigList.add(configuration);
        
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
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionGraphPattern2 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. No RAT_ITEM Entity provided.
     *
     * @throws EvaluatorException
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvalExceptionGraphPattern3() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvalExceptionGraphPattern3 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

        try {
            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RATING, "?rating");
        } catch (RecommenderException ex) {
            Logger.getLogger(EvalConfigTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration.preprocessBeforeRecommending(true);
        //configuration.setRatingsNormStrategy(RecRatingsNormalization.MEAN_CENTERING);
        configuration.setSimMetric(RecSimMetric.COSINE);
        configuration.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration.setDecimalPlaces(3);
        configuration.setNeighborhoodSize(4);

        recConfigList.add(configuration);
        
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
        evalConfig.addRankingMetricTopKSize(2);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testRecEvalExceptionGraphPattern3 COMPLETED");
    }   
    
    
    
    /**
     * Test graph patterns of multiple recommender configurations.
     * 
     * Same graph patterns case with different strings. 
     * 
     * Rating Feedback Case
     * 
     * 4 Different Pattern Strings:
     * Normal-Extra Spaces-Extra New Line-Extra Dot at the end
     *
     */
    @Test
    public void testRecEvaluatorGraphPattern11() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern11 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config2");

        try {
            configuration2.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode    . "
                    + "?intermNode       <http://example.org/movies#ratedMovie> ?movie ."
                    + "  ?intermNode  <http://example.org/movies#hasRating> ?rating"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration2.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration2.preprocessBeforeRecommending(true);
        configuration2.setSimMetric(RecSimMetric.COSINE);
        configuration2.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration2.setDecimalPlaces(3);
        configuration2.setNeighborhoodSize(4);
        
        
        VsmCfRecConfig configuration3 = new VsmCfRecConfig("config3");

        try {
            configuration3.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie . \n"
                    + "?intermNode <http://example.org/movies#hasRating> ?rating \n"
            );
            configuration3.setRecEntity(RecEntity.USER, "?user");
            configuration3.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration3.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration3.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration3.preprocessBeforeRecommending(true);
        configuration3.setSimMetric(RecSimMetric.COSINE);
        configuration3.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration3.setDecimalPlaces(3);
        configuration3.setNeighborhoodSize(4);
        
        
        VsmCfRecConfig configuration4 = new VsmCfRecConfig("config4");

        try {
            configuration4.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating ."
            );
            configuration4.setRecEntity(RecEntity.USER, "?user");
            configuration4.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration4.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration4.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration4.preprocessBeforeRecommending(true);
        configuration4.setSimMetric(RecSimMetric.COSINE);
        configuration4.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration4.setDecimalPlaces(3);
        configuration4.setNeighborhoodSize(4);
        
        
        recConfigList.add(configuration1);
        recConfigList.add(configuration2);
        recConfigList.add(configuration3);
        recConfigList.add(configuration4);
        
        try {
        
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
            EvaluationResult result3 = recEvalRepository.getEvaluator().getEvalResultByName("config3");
            EvaluationResult result4 = recEvalRepository.getEvaluator().getEvalResultByName("config4");
                
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
            
            Assert.assertEquals(expectedMAE      , result3.getOverallPerformance().getMAEScore(), DELTA);
            Assert.assertEquals(expectedRMSE     , result3.getOverallPerformance().getRMSEScore(), DELTA);
            Assert.assertEquals(expectedMRR      , result3.getOverallPerformance().getMRRScore(1), DELTA);
            Assert.assertEquals(expectedPrecision, result3.getOverallPerformance().getPrecisionScore(1), DELTA);
            Assert.assertEquals(expectedRecall   , result3.getOverallPerformance().getRecallScore(1), DELTA);
            Assert.assertEquals(expectedFMeasure , result3.getOverallPerformance().getFMeasureScore(1), DELTA);
            
            Assert.assertEquals(expectedMAE      , result4.getOverallPerformance().getMAEScore(), DELTA);
            Assert.assertEquals(expectedRMSE     , result4.getOverallPerformance().getRMSEScore(), DELTA);
            Assert.assertEquals(expectedMRR      , result4.getOverallPerformance().getMRRScore(1), DELTA);
            Assert.assertEquals(expectedPrecision, result4.getOverallPerformance().getPrecisionScore(1), DELTA);
            Assert.assertEquals(expectedRecall   , result4.getOverallPerformance().getRecallScore(1), DELTA);
            Assert.assertEquals(expectedFMeasure , result4.getOverallPerformance().getFMeasureScore(1), DELTA);
            
        } catch (Exception ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRecEvaluatorGraphPattern11 COMPLETED");
    }
    
    /**
     * Test graph patterns of multiple recommender configurations.
     * 
     * Same graph patterns case with different strings. 
     * 
     * Positive Feedback Case
     * 
     * 4 Different Pattern Strings:
     * Normal-Extra Spaces-Extra New Line-Extra Dot at the end
     *
     */
    @Test
    public void testRecEvaluatorGraphPattern12() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern12 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config2");

        try {
            configuration2.setPosGraphPattern(
                    "?user    <http://example.org/movies#hasRated> ?movie"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration2.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration2.preprocessBeforeRecommending(true);
        configuration2.setSimMetric(RecSimMetric.COSINE);
        configuration2.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration2.setDecimalPlaces(3);
        configuration2.setNeighborhoodSize(4);
        
        
        VsmCfRecConfig configuration3 = new VsmCfRecConfig("config3");

        try {
            configuration3.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie \n"
            );
            configuration3.setRecEntity(RecEntity.USER, "?user");
            configuration3.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration3.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration3.preprocessBeforeRecommending(true);
        configuration3.setSimMetric(RecSimMetric.COSINE);
        configuration3.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration3.setDecimalPlaces(3);
        configuration3.setNeighborhoodSize(4);
        
        
        VsmCfRecConfig configuration4 = new VsmCfRecConfig("config4");

        try {
            configuration4.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie ."
            );
            configuration4.setRecEntity(RecEntity.USER, "?user");
            configuration4.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
            
        configuration4.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration4.preprocessBeforeRecommending(true);
        configuration4.setSimMetric(RecSimMetric.COSINE);
        configuration4.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration4.setDecimalPlaces(3);
        configuration4.setNeighborhoodSize(4);
        
        
        recConfigList.add(configuration1);
        recConfigList.add(configuration2);
        recConfigList.add(configuration3);
        recConfigList.add(configuration4);
        
        try {
        
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
            
        } catch (Exception ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRecEvaluatorGraphPattern12 COMPLETED");
    }    
    
    /**
     * Test if the UnsupportedOperationException is thrown. Multiple recommender configurations.
     * 
     * Different graph patterns case.
     * 
     * Ratings Feedback Case
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern21() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern21 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#has> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern21 COMPLETED");
    }
    
    
    /**
     * Test if the UnsupportedOperationException is thrown. Multiple recommender configurations.
     * 
     * Different graph patterns case
     *
     * Positive Feedback Case
     * 
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern22() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern22 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie "
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRatedZ> ?movie "
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern21 COMPLETED");
    }
    
    
    
    /**
     * Test if the UnsupportedOperationException is thrown. Multiple recommender configurations.
     * 
     * Different graph patterns case
     *
     * Negative Feedback Case
     * 
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern23() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern23 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie "
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setNegGraphPattern(
                    "?user <http://example.org/movies#notRatedZ> ?movie "
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern23 COMPLETED");
    }
    
    
    
    /**
     * Test if the UnsupportedOperationException is thrown. Multiple recommender configurations.
     * 
     * Different graph patterns case
     *
     * Positive Feedback Case
     * 
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern24() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern24 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie "
            );
            configuration1.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie "
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.POS_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRatedZ> ?movie "
            );
            configuration2.setNegGraphPattern(
                    "?user <http://example.org/movies#notRatedZ> ?movie "
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern24 COMPLETED");
    }
    
    
    /**
     * Test if the EvaluatorException is thrown. Multiple recommender configurations.
     * 
     * Configurations with different feedback types case. Ratings Feedback - Positive Feedback
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern51() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern51 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#has> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern51 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Multiple recommender configurations.
     * 
     * Configurations with different feedback types case. Positive Feedback - Negative Feedback
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern52() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern52 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern52 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Multiple recommender configurations.
     * 
     * Configurations with different feedback types case. Negative Feedback - Positive & Negative Feedback
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern53() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern53 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie"
            );
            configuration2.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern53 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Multiple recommender configurations.
     * 
     * Configurations with different feedback types case. Rating Feedback - Positive & Negative Feedback
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern54() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern54 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#has> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");            
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setPosGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?movie"
            );
            configuration2.setNegGraphPattern(
                    "?user <http://example.org/movies#notRated> ?movie"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.POS_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.NEG_ITEM, "?movie");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern54 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. Multiple recommender configurations.
     * 
     * Configurations with different RecEntity values case.
     *
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern3() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern3 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#has> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        VsmCfRecConfig configuration2 = new VsmCfRecConfig("config1");

        try {
            configuration2.setRatGraphPattern(
                    "?user <http://example.org/movies#has> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration2.setRecEntity(RecEntity.USER, "?fuser");
            configuration2.setRecEntity(RecEntity.RAT_ITEM, "?loser");
            configuration1.setRecEntity(RecEntity.RATING, "?suser");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
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

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        
        
        System.out.println("testRecEvaluatorGraphPattern3 COMPLETED");
    }
    
    /**
     * Test if the EvaluatorException is thrown. 
     * 
     * Not all of the recommender configurations are Cross Domain configurations.
     * 
     */
    @Test(expected = EvaluatorException.class)
    public void testRecEvaluatorGraphPattern4() throws EvaluatorException {

        System.out.println("");
        System.out.println("testRecEvaluatorGraphPattern4 starting...");
        
        
        ArrayList<RecConfig> recConfigList = new ArrayList();

        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");

        try {
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration1.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration1.preprocessBeforeRecommending(true);
        configuration1.setSimMetric(RecSimMetric.COSINE);
        configuration1.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration1.setDecimalPlaces(3);
        configuration1.setNeighborhoodSize(4);

        
        LinkAnalysisRecConfig configuration2 = new LinkAnalysisRecConfig("config1");

        try {
            configuration2.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration2.setRecEntity(RecEntity.USER, "?user");
            configuration2.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration2.setRecEntity(RecEntity.RATING, "?rating");
            
        } catch (RecommenderException ex) {
            Logger.getLogger(SailRecEvaluatorRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        configuration2.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
        configuration2.preprocessBeforeRecommending(true);
        configuration2.setRecStorage(RecStorage.INVERTED_LISTS);
        configuration2.setDecimalPlaces(3);

        recConfigList.add(configuration1);
        recConfigList.add(configuration2);
        
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(recConfigList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.addRankingMetricTopKSize(5);
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setNumberOfFolds(5);

        recEvalRepository.loadEvalConfiguration(evalConfig); 
        
        
        System.out.println("testRecEvaluatorGraphPattern4 COMPLETED");
    }
}

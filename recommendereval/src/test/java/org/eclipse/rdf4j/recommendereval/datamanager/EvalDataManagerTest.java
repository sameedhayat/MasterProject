/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;


/**
 * Test class for EvalDataManager Class
 */
public class EvalDataManagerTest {    

    /**
     * Test if the users and ratings are stored & sorted correctly.
     * 
     * Dataset: moviesFromBook.ttl
     */
    @Test
    public void testRatingsSorted1() {

        System.out.println("");
        System.out.println("testRatingsSorted1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            ArrayList<String> users = dataManager.getUsers();
            ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();

            String userAlice = "http://example.org/movies#Alice";
            String user1 = "http://example.org/movies#User1";
            String user2 = "http://example.org/movies#User2";
            String user3 = "http://example.org/movies#User3";
            String user4 = "http://example.org/movies#User4";

            // User: Alice
            Rating rating0 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item1", 5.0);
            Rating rating1 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item2", 3.0);
            Rating rating2 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item3", 4.0);
            Rating rating3 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item4", 4.0);

            // User: User1
            Rating rating4 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating5 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating6 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 2.0);
            Rating rating7 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 3.0);
            Rating rating8 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 3.0);

            // User: User2
            Rating rating9  = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 4.0);
            Rating rating10 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 3.0);
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 4.0);
            Rating rating12 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 3.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);

            // User: User3
            Rating rating14 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 3.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);
            Rating rating16 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 5.0);
            Rating rating18 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 4.0);

            // User: User4
            Rating rating19 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating20 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 5.0);
            Rating rating21 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 5.0);
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            Rating rating23 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);
            
            
            // test Users ArrayList            
            Assert.assertEquals(users.get(0), userAlice);
            Assert.assertEquals(users.get(1), user1);
            Assert.assertEquals(users.get(2), user2);
            Assert.assertEquals(users.get(3), user3);
            Assert.assertEquals(users.get(4), user4);

            // test UsersRatings ArrayList
            Assert.assertEquals(usersRatings.get(0).get(0), rating0);
            Assert.assertEquals(usersRatings.get(0).get(1), rating1);
            Assert.assertEquals(usersRatings.get(0).get(2), rating2);
            Assert.assertEquals(usersRatings.get(0).get(3), rating3);

            Assert.assertEquals(usersRatings.get(1).get(0), rating4);
            Assert.assertEquals(usersRatings.get(1).get(1), rating5);
            Assert.assertEquals(usersRatings.get(1).get(2), rating6);
            Assert.assertEquals(usersRatings.get(1).get(3), rating7);
            Assert.assertEquals(usersRatings.get(1).get(4), rating8);

            Assert.assertEquals(usersRatings.get(2).get(0), rating9);
            Assert.assertEquals(usersRatings.get(2).get(1), rating10);
            Assert.assertEquals(usersRatings.get(2).get(2), rating11);
            Assert.assertEquals(usersRatings.get(2).get(3), rating12);
            Assert.assertEquals(usersRatings.get(2).get(4), rating13);

            Assert.assertEquals(usersRatings.get(3).get(0), rating14);
            Assert.assertEquals(usersRatings.get(3).get(1), rating15);
            Assert.assertEquals(usersRatings.get(3).get(2), rating16);
            Assert.assertEquals(usersRatings.get(3).get(3), rating17);
            Assert.assertEquals(usersRatings.get(3).get(4), rating18);

            Assert.assertEquals(usersRatings.get(4).get(0), rating19);
            Assert.assertEquals(usersRatings.get(4).get(1), rating20);
            Assert.assertEquals(usersRatings.get(4).get(2), rating21);
            Assert.assertEquals(usersRatings.get(4).get(3), rating22);
            Assert.assertEquals(usersRatings.get(4).get(4), rating23);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRatingsSorted1 COMPLETED");
    }
    
    
    /**
     * Test if the users and ratings are stored & sorted correctly (Cross Domain).
     * 
     * Dataset: moviesFromBookCrossDomain.ttl
     */
    @Test
    public void testCrossDomain1() {

        System.out.println("");
        System.out.println("testCrossDomain1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList3();
        
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository4(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            ArrayList<String> users = dataManager.getUsers();
            ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();

            String userAlice = "http://example.org/movies#Alice";
            String user1 = "http://example.org/movies#User1";
            String user2 = "http://example.org/movies#User2";
            String user3 = "http://example.org/movies#User3";
            String user4 = "http://example.org/movies#User4";

            // test target domain ratings
            
            // User: Alice
            Rating rating0 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item6", 5.0);
            Rating rating1 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item7", 3.0);
            Rating rating2 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item8", 4.0);
            Rating rating3 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item9", 4.0);

            // User: User1
            Rating rating4 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item6", 3.0);
            Rating rating5 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item7", 1.0);
            Rating rating6 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item8", 2.0);
            Rating rating7 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item9", 3.0);
            Rating rating8 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item10", 3.0);

            // User: User2
            Rating rating9  = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item6", 4.0);
            Rating rating10 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item7", 3.0);
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item8", 4.0);
            Rating rating12 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item9", 3.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item10", 5.0);

            // User: User3
            Rating rating14 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item6", 3.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item7", 3.0);
            Rating rating16 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item8", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item9", 5.0);
            Rating rating18 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item10", 4.0);

            // User: User4
            Rating rating19 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item6", 1.0);
            Rating rating20 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item7", 5.0);
            Rating rating21 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item8", 5.0);
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item9", 2.0);
            Rating rating23 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item10", 1.0);
            
            
            // test Users ArrayList            
            Assert.assertEquals(users.get(0), userAlice);
            Assert.assertEquals(users.get(1), user1);
            Assert.assertEquals(users.get(2), user2);
            Assert.assertEquals(users.get(3), user3);
            Assert.assertEquals(users.get(4), user4);

            // test UsersRatings ArrayList
            Assert.assertEquals(usersRatings.get(0).get(0), rating0);
            Assert.assertEquals(usersRatings.get(0).get(1), rating1);
            Assert.assertEquals(usersRatings.get(0).get(2), rating2);
            Assert.assertEquals(usersRatings.get(0).get(3), rating3);

            Assert.assertEquals(usersRatings.get(1).get(0), rating4);
            Assert.assertEquals(usersRatings.get(1).get(1), rating5);
            Assert.assertEquals(usersRatings.get(1).get(2), rating6);
            Assert.assertEquals(usersRatings.get(1).get(3), rating7);
            Assert.assertEquals(usersRatings.get(1).get(4), rating8);

            Assert.assertEquals(usersRatings.get(2).get(0), rating9);
            Assert.assertEquals(usersRatings.get(2).get(1), rating10);
            Assert.assertEquals(usersRatings.get(2).get(2), rating11);
            Assert.assertEquals(usersRatings.get(2).get(3), rating12);
            Assert.assertEquals(usersRatings.get(2).get(4), rating13);

            Assert.assertEquals(usersRatings.get(3).get(0), rating14);
            Assert.assertEquals(usersRatings.get(3).get(1), rating15);
            Assert.assertEquals(usersRatings.get(3).get(2), rating16);
            Assert.assertEquals(usersRatings.get(3).get(3), rating17);
            Assert.assertEquals(usersRatings.get(3).get(4), rating18);

            Assert.assertEquals(usersRatings.get(4).get(0), rating19);
            Assert.assertEquals(usersRatings.get(4).get(1), rating20);
            Assert.assertEquals(usersRatings.get(4).get(2), rating21);
            Assert.assertEquals(usersRatings.get(4).get(3), rating22);
            Assert.assertEquals(usersRatings.get(4).get(4), rating23);

            
            // test source domain ratings
            // not needed right now since source domain ratings are not used
            ArrayList<Rating> sourceDomainRatings = dataManager.getSourceDomainRatings();
            
            // User: Alice
            Rating rating100 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item1", 5.0);
            Rating rating101 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item2", 3.0);
            Rating rating102 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item3", 4.0);
            Rating rating103 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item4", 4.0);

            // User: User1
            Rating rating104 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating105 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating106 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 2.0);
            Rating rating107 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 3.0);
            Rating rating108 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 3.0);

            // User: User2
            Rating rating109 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 4.0);
            Rating rating110 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 3.0);
            Rating rating111 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 4.0);
            Rating rating112 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 3.0);
            Rating rating113 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);

            // User: User3
            Rating rating114 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 3.0);
            Rating rating115 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);
            Rating rating116 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating117 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 5.0);
            Rating rating118 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 4.0);

            // User: User4
            Rating rating119 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating120 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 5.0);
            Rating rating121 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 5.0);
            Rating rating122 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            Rating rating123 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);            
            
            Folds folds = dataManager.splitRatingsKFold(5, true);
            
            // First index is the user, second one is the fold, third one is the rating.
            Assert.assertEquals(folds.getRatingsOfFold(0, 0).get(0), rating1);
            Assert.assertEquals(folds.getRatingsOfFold(0, 1).get(0), rating2);
            Assert.assertEquals(folds.getRatingsOfFold(0, 2).get(0), rating3);
            Assert.assertEquals(folds.getRatingsOfFold(0, 3).get(0), rating0);

            Assert.assertEquals(folds.getRatingsOfFold(1, 0).get(0), rating6);
            Assert.assertEquals(folds.getRatingsOfFold(1, 1).get(0), rating4);
            Assert.assertEquals(folds.getRatingsOfFold(1, 2).get(0), rating7);
            Assert.assertEquals(folds.getRatingsOfFold(1, 3).get(0), rating8);
            Assert.assertEquals(folds.getRatingsOfFold(1, 4).get(0), rating5);

            Assert.assertEquals(folds.getRatingsOfFold(2, 0).get(0), rating12);
            Assert.assertEquals(folds.getRatingsOfFold(2, 1).get(0), rating9);
            Assert.assertEquals(folds.getRatingsOfFold(2, 2).get(0), rating11);
            Assert.assertEquals(folds.getRatingsOfFold(2, 3).get(0), rating13);
            Assert.assertEquals(folds.getRatingsOfFold(2, 4).get(0), rating10);

            Assert.assertEquals(folds.getRatingsOfFold(3, 0).get(0), rating14);
            Assert.assertEquals(folds.getRatingsOfFold(3, 1).get(0), rating15);
            Assert.assertEquals(folds.getRatingsOfFold(3, 2).get(0), rating17);
            Assert.assertEquals(folds.getRatingsOfFold(3, 3).get(0), rating18);
            Assert.assertEquals(folds.getRatingsOfFold(3, 4).get(0), rating16);

            Assert.assertEquals(folds.getRatingsOfFold(4, 0).get(0), rating22);
            Assert.assertEquals(folds.getRatingsOfFold(4, 1).get(0), rating20);
            Assert.assertEquals(folds.getRatingsOfFold(4, 2).get(0), rating21);
            Assert.assertEquals(folds.getRatingsOfFold(4, 3).get(0), rating23);
            Assert.assertEquals(folds.getRatingsOfFold(4, 4).get(0), rating19);
            
        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testCrossDomain1 COMPLETED");
    }
    
    
    /**
     * Test if the users and ratings are stored & sorted correctly 
     * (Cross Domain - Likes).
     * 
     * Dataset: moviesFromBookCrossDomainLikes.ttl
     */
    @Test
    public void testCrossDomain2() {

        System.out.println("");
        System.out.println("testCrossDomain2 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList4();
        
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository5(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            ArrayList<String> users = dataManager.getUsers();
            ArrayList<ArrayList<Rating>> usersRatings = dataManager.getUsersRatings();

            String userAlice = "http://example.org/movies#Alice";
            String user1 = "http://example.org/movies#User1";
            String user2 = "http://example.org/movies#User2";
            String user3 = "http://example.org/movies#User3";
            String user4 = "http://example.org/movies#User4";

            // test target domain ratings
            
            // User: Alice
            Rating rating0 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item6", 1.0);
            Rating rating1 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item7", 1.0);
            Rating rating2 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item8", 1.0);
            Rating rating3 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item9", 1.0);

            // User: User1
            Rating rating4 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item6", 1.0);
            Rating rating5 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item7", 1.0);
            Rating rating6 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item8", 1.0);
            Rating rating7 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item9", 1.0);
            Rating rating8 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item10", 1.0);

            // User: User2
            Rating rating9  = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item6", 1.0);
            Rating rating10 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item7", 1.0);
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item8", 1.0);
            Rating rating12 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item9", 1.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item10", 1.0);

            // User: User3
            Rating rating14 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item6", 1.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item7", 1.0);
            Rating rating16 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item8", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item9", 1.0);
            Rating rating18 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item10", 1.0);

            // User: User4
            Rating rating19 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item6", 1.0);
            Rating rating20 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item7", 1.0);
            Rating rating21 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item8", 1.0);
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item9", 1.0);
            Rating rating23 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item10", 1.0);
            
            
            // test Users ArrayList            
            Assert.assertEquals(users.get(0), userAlice);
            Assert.assertEquals(users.get(1), user1);
            Assert.assertEquals(users.get(2), user2);
            Assert.assertEquals(users.get(3), user3);
            Assert.assertEquals(users.get(4), user4);         
            
            // test UsersRatings ArrayList
            Assert.assertEquals(usersRatings.get(0).get(0), rating0);
            Assert.assertEquals(usersRatings.get(0).get(1), rating1);
            Assert.assertEquals(usersRatings.get(0).get(2), rating2);
            Assert.assertEquals(usersRatings.get(0).get(3), rating3);

            Assert.assertEquals(usersRatings.get(1).get(0), rating4);
            Assert.assertEquals(usersRatings.get(1).get(1), rating5);
            Assert.assertEquals(usersRatings.get(1).get(2), rating6);
            Assert.assertEquals(usersRatings.get(1).get(3), rating7);
            Assert.assertEquals(usersRatings.get(1).get(4), rating8);

            Assert.assertEquals(usersRatings.get(2).get(0), rating9);
            Assert.assertEquals(usersRatings.get(2).get(1), rating10);
            Assert.assertEquals(usersRatings.get(2).get(2), rating11);
            Assert.assertEquals(usersRatings.get(2).get(3), rating12);
            Assert.assertEquals(usersRatings.get(2).get(4), rating13);

            Assert.assertEquals(usersRatings.get(3).get(0), rating14);
            Assert.assertEquals(usersRatings.get(3).get(1), rating15);
            Assert.assertEquals(usersRatings.get(3).get(2), rating16);
            Assert.assertEquals(usersRatings.get(3).get(3), rating17);
            Assert.assertEquals(usersRatings.get(3).get(4), rating18);

            Assert.assertEquals(usersRatings.get(4).get(0), rating19);
            Assert.assertEquals(usersRatings.get(4).get(1), rating20);
            Assert.assertEquals(usersRatings.get(4).get(2), rating21);
            Assert.assertEquals(usersRatings.get(4).get(3), rating22);
            Assert.assertEquals(usersRatings.get(4).get(4), rating23);

            
            // test source domain ratings
            // not needed right now since source domain ratings are not used
            ArrayList<Rating> sourceDomainRatings = dataManager.getSourceDomainRatings();
            
            // User: Alice
            Rating rating100 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item1", 1.0);
            Rating rating101 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item2", 1.0);
            Rating rating102 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item3", 1.0);
            Rating rating103 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item4", 1.0);

            // User: User1
            Rating rating104 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 1.0);
            Rating rating105 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating106 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 1.0);
            Rating rating107 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 1.0);
            Rating rating108 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 1.0);

            // User: User2
            Rating rating109 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 1.0);
            Rating rating110 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 1.0);
            Rating rating111 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 1.0);
            Rating rating112 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 1.0);
            Rating rating113 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 1.0);

            // User: User3
            Rating rating114 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 1.0);
            Rating rating115 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 1.0);
            Rating rating116 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating117 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 1.0);
            Rating rating118 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 1.0);

            // User: User4
            Rating rating119 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating120 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 1.0);
            Rating rating121 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 1.0);
            Rating rating122 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 1.0);
            Rating rating123 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);
            
            Folds folds = dataManager.splitRatingsKFold(5, true);
            
            // First index is the user, second one is the fold, third one is the rating.
            Assert.assertEquals(folds.getRatingsOfFold(0, 0).get(0), rating0);
            Assert.assertEquals(folds.getRatingsOfFold(0, 1).get(0), rating1);
            Assert.assertEquals(folds.getRatingsOfFold(0, 2).get(0), rating2);
            Assert.assertEquals(folds.getRatingsOfFold(0, 3).get(0), rating3);

            Assert.assertEquals(folds.getRatingsOfFold(1, 0).get(0), rating5);
            Assert.assertEquals(folds.getRatingsOfFold(1, 1).get(0), rating6);
            Assert.assertEquals(folds.getRatingsOfFold(1, 2).get(0), rating7);
            Assert.assertEquals(folds.getRatingsOfFold(1, 3).get(0), rating8);
            Assert.assertEquals(folds.getRatingsOfFold(1, 4).get(0), rating4);

            Assert.assertEquals(folds.getRatingsOfFold(2, 0).get(0), rating10);
            Assert.assertEquals(folds.getRatingsOfFold(2, 1).get(0), rating11);
            Assert.assertEquals(folds.getRatingsOfFold(2, 2).get(0), rating12);
            Assert.assertEquals(folds.getRatingsOfFold(2, 3).get(0), rating13);
            Assert.assertEquals(folds.getRatingsOfFold(2, 4).get(0), rating9);

            Assert.assertEquals(folds.getRatingsOfFold(3, 0).get(0), rating15);
            Assert.assertEquals(folds.getRatingsOfFold(3, 1).get(0), rating16);
            Assert.assertEquals(folds.getRatingsOfFold(3, 2).get(0), rating17);
            Assert.assertEquals(folds.getRatingsOfFold(3, 3).get(0), rating18);
            Assert.assertEquals(folds.getRatingsOfFold(3, 4).get(0), rating14);

            Assert.assertEquals(folds.getRatingsOfFold(4, 0).get(0), rating20);
            Assert.assertEquals(folds.getRatingsOfFold(4, 1).get(0), rating21);
            Assert.assertEquals(folds.getRatingsOfFold(4, 2).get(0), rating22);
            Assert.assertEquals(folds.getRatingsOfFold(4, 3).get(0), rating23);
            Assert.assertEquals(folds.getRatingsOfFold(4, 4).get(0), rating19);
            
        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testCrossDomain2 COMPLETED");
    }

    /**
     * Test if the given graph pattern is stored correctly and graph entities
     * exist.
     */
    @Test
    public void testGraphPattern1() {

        System.out.println("");
        System.out.println("testGraphPattern1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            String pattern = "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating";

            Assert.assertEquals(dataManager.getGraphPattern(), pattern);
            Assert.assertEquals("?user", dataManager.getEntityMap().get(EvalEntity.USER));
            Assert.assertEquals("?movie", dataManager.getEntityMap().get(EvalEntity.RAT_ITEM));
            Assert.assertEquals("?rating", dataManager.getEntityMap().get(EvalEntity.RATING));

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testGraphPattern1 COMPLETED");
    }
    
    /**
     * Test if Data Manager deletes users.
     * 
     * In the input file, 10 users exist. Each one has 10 ratings. 
     * 
     * Number of Users: 5
     * 
     * After the user selection, 5 users should remain with total number of 
     * 50 ratings.
     * 
     * Dataset: moviesLarger.ttl
     */
    @Test
    public void testRandomUserSelection1() {

        System.out.println("");
        System.out.println("testRandomUserSelection1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository2(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);
            
            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            
            int numberOfUsers = dataManager.getUsers().size();            
            int numberOfRatings = 0;
            
            for( int i = 0 ; i < numberOfUsers ; i++ ) {
                numberOfRatings += dataManager.getUsersRatings().get(i).size();
            }
            
            Assert.assertEquals(5, numberOfUsers);
            Assert.assertEquals(50, numberOfRatings);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRandomUserSelection1 COMPLETED");
    }
    
    /**
     * Test if Data Manager deletes users.
     * 
     * In the input file, 10 users exist. Each one has 10 ratings. 
     * 
     * Number of Users: 10
     * 
     * After the user selection, 10 users should remain with total number of 
     * 500 ratings.
     * 
     * Dataset: moviesLarger.ttl
     */
    @Test
    public void testRandomUserSelection2() {

        System.out.println("");
        System.out.println("testRandomUserSelection2 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository2(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,10,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);
            
            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            
            int numberOfUsers = dataManager.getUsers().size();            
            int numberOfRatings = 0;
            
            for( int i = 0 ; i < numberOfUsers ; i++ ) {
                numberOfRatings += dataManager.getUsersRatings().get(i).size();
            }
            
            Assert.assertEquals(10, numberOfUsers);
            Assert.assertEquals(100, numberOfRatings);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRandomUserSelection2 COMPLETED");
    }
    
    /**
     * Test if Data Manager deletes users.
     * 
     * In the input file, 500 users exist. Each one has 50 ratings.
     * 
     * Number of Users: 0
     * 
     * After the user selection, 500 users should remain with total number of 
     * 5000 ratings.
     * 
     * Dataset: moviesLarger.ttl
     */
    @Test
    public void testRandomUserSelection3() {

        System.out.println("");
        System.out.println("testRandomUserSelection3 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository2(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,0,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);
            
            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            
            int numberOfUsers = dataManager.getUsers().size();            
            int numberOfRatings = 0;
            
            for( int i = 0 ; i < numberOfUsers ; i++ ) {
                numberOfRatings += dataManager.getUsersRatings().get(i).size();
            }
            
            Assert.assertEquals(500, numberOfUsers);
            Assert.assertEquals(5000, numberOfRatings);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRandomUserSelection3 COMPLETED");
    }

    /**
     * Test splitRatingsKFold method of Evaluator Data Manager
     * 
     * Dataset: moviesFromBook.ttl
     */
    @Test
    public void testSplitRatingsKFold1() {

        System.out.println("");
        System.out.println("testSplitRatingsKFold1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            Folds folds = dataManager.splitRatingsKFold(5, true);
            
            // User: Alice
            Rating rating0 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item1", 5.0);
            Rating rating1 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item2", 3.0);
            Rating rating2 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item3", 4.0);
            Rating rating3 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item4", 4.0);

            // User: User1
            Rating rating4 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating5 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating6 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 2.0);
            Rating rating7 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 3.0);
            Rating rating8 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 3.0);

            // User: User2
            Rating rating9 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 4.0);
            Rating rating10 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 3.0);
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 4.0);
            Rating rating12 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 3.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);

            // User: User3
            Rating rating14 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 3.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);
            Rating rating16 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 5.0);
            Rating rating18 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 4.0);

            // User: User4
            Rating rating19 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating20 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 5.0);
            Rating rating21 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 5.0);
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            Rating rating23 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);

            // First index is the user, second one is the fold, third one is therating.
            Assert.assertEquals(folds.getRatingsOfFold(0, 0).get(0), rating1);
            Assert.assertEquals(folds.getRatingsOfFold(0, 1).get(0), rating2);
            Assert.assertEquals(folds.getRatingsOfFold(0, 2).get(0), rating3);
            Assert.assertEquals(folds.getRatingsOfFold(0, 3).get(0), rating0);

            Assert.assertEquals(folds.getRatingsOfFold(1, 0).get(0), rating6);
            Assert.assertEquals(folds.getRatingsOfFold(1, 1).get(0), rating4);
            Assert.assertEquals(folds.getRatingsOfFold(1, 2).get(0), rating7);
            Assert.assertEquals(folds.getRatingsOfFold(1, 3).get(0), rating8);
            Assert.assertEquals(folds.getRatingsOfFold(1, 4).get(0), rating5);

            Assert.assertEquals(folds.getRatingsOfFold(2, 0).get(0), rating12);
            Assert.assertEquals(folds.getRatingsOfFold(2, 1).get(0), rating9);
            Assert.assertEquals(folds.getRatingsOfFold(2, 2).get(0), rating11);
            Assert.assertEquals(folds.getRatingsOfFold(2, 3).get(0), rating13);
            Assert.assertEquals(folds.getRatingsOfFold(2, 4).get(0), rating10);

            Assert.assertEquals(folds.getRatingsOfFold(3, 0).get(0), rating14);
            Assert.assertEquals(folds.getRatingsOfFold(3, 1).get(0), rating15);
            Assert.assertEquals(folds.getRatingsOfFold(3, 2).get(0), rating18);
            Assert.assertEquals(folds.getRatingsOfFold(3, 3).get(0), rating17);
            Assert.assertEquals(folds.getRatingsOfFold(3, 4).get(0), rating16);

            Assert.assertEquals(folds.getRatingsOfFold(4, 0).get(0), rating23);
            Assert.assertEquals(folds.getRatingsOfFold(4, 1).get(0), rating22);
            Assert.assertEquals(folds.getRatingsOfFold(4, 2).get(0), rating20);
            Assert.assertEquals(folds.getRatingsOfFold(4, 3).get(0), rating21);
            Assert.assertEquals(folds.getRatingsOfFold(4, 4).get(0), rating19);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        
        System.out.println("testSplitRatingsKFold1 COMPLETED");
    }

    /**
     * Test splitRatingsKFold method of Evaluator Data Manager
     * 
     * Dataset: custom
     */
    @Test
    public void testSplitRatingsKFold2() {

        System.out.println("");
        System.out.println("testSplitRatingsKFold2 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository9(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,4,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(3);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            Folds folds = dataManager.splitRatingsKFold(3, true);
            
            /*
            System.out.println(".........................................................");
            dataManager.printUsersFoldsRatingsList(foldsList);
            System.out.println(".........................................................");
            */

            // User: User1
            Rating rating11 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating12 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating13 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 2.0);
            Rating rating14 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 3.0);
            Rating rating15 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 3.0);
            Rating rating16 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item6", 4.0);

            // User: User2
            Rating rating21 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 4.0);
            Rating rating22 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 3.0);
            Rating rating23 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 4.0);
            Rating rating24 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 3.0);
            Rating rating25 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);
            Rating rating26 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item6", 3.0);

            // User: User3
            Rating rating31 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 3.0);
            Rating rating32 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);
            Rating rating33 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating34 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 5.0);
            Rating rating35 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 4.0);
            Rating rating36 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item6", 2.0);

            // User: User4
            Rating rating41 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating42 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 5.0);
            Rating rating43 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 5.0);
            Rating rating44 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            Rating rating45 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);
            Rating rating46 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item6", 1.0);
            
            
            // First index is the user, second one is the fold, third one is therating.
            Assert.assertEquals(folds.getRatingsOfFold(0, 0).get(0), rating12);
            Assert.assertEquals(folds.getRatingsOfFold(0, 0).get(1), rating14);
            Assert.assertEquals(folds.getRatingsOfFold(0, 1).get(0), rating13);
            Assert.assertEquals(folds.getRatingsOfFold(0, 1).get(1), rating15);
            Assert.assertEquals(folds.getRatingsOfFold(0, 2).get(0), rating11);
            Assert.assertEquals(folds.getRatingsOfFold(0, 2).get(1), rating16);

            Assert.assertEquals(folds.getRatingsOfFold(1, 0).get(0), rating22);
            Assert.assertEquals(folds.getRatingsOfFold(1, 0).get(1), rating21);
            Assert.assertEquals(folds.getRatingsOfFold(1, 1).get(0), rating24);
            Assert.assertEquals(folds.getRatingsOfFold(1, 1).get(1), rating23);
            Assert.assertEquals(folds.getRatingsOfFold(1, 2).get(0), rating26);
            Assert.assertEquals(folds.getRatingsOfFold(1, 2).get(1), rating25);

            Assert.assertEquals(folds.getRatingsOfFold(2, 0).get(0), rating33);
            Assert.assertEquals(folds.getRatingsOfFold(2, 0).get(1), rating32);
            Assert.assertEquals(folds.getRatingsOfFold(2, 1).get(0), rating36);
            Assert.assertEquals(folds.getRatingsOfFold(2, 1).get(1), rating35);
            Assert.assertEquals(folds.getRatingsOfFold(2, 2).get(0), rating31);
            Assert.assertEquals(folds.getRatingsOfFold(2, 2).get(1), rating34);

            Assert.assertEquals(folds.getRatingsOfFold(3, 0).get(0), rating41);
            Assert.assertEquals(folds.getRatingsOfFold(3, 0).get(1), rating44);
            Assert.assertEquals(folds.getRatingsOfFold(3, 1).get(0), rating45);
            Assert.assertEquals(folds.getRatingsOfFold(3, 1).get(1), rating42);
            Assert.assertEquals(folds.getRatingsOfFold(3, 2).get(0), rating46);
            Assert.assertEquals(folds.getRatingsOfFold(3, 2).get(1), rating43);
            

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }       
        System.out.println("testSplitRatingsKFold2 COMPLETED");
    }
    
    /**
     * Test getGraphPatternParts method of Evaluator Data Manager
     */
    @Test
    public void testGraphPatternParts1() {

        System.out.println("");
        System.out.println("testGraphPatternParts1 starting...");
        
    
        String graphPattern = 
                "?user <http://example.org/movies#hasRated> ?intermNode . \n "
              + "?intermNode <http://example.org/movies#ratedMovie> ?movie . \n "
              + "?intermNode <http://example.org/movies#hasRating> ?rating";
        
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        evalConfig.setGraphPattern(graphPattern);
        
        EvalDataManager edm = new EvalDataManager(evalConfig, null);
        
        String[] parts = edm.getGraphPatternParts();
        
        Assert.assertEquals("?user", parts[0]);
        Assert.assertEquals("<http://example.org/movies#hasRated>", parts[1]);
        Assert.assertEquals("?intermNode", parts[2]);
        Assert.assertEquals("?intermNode", parts[3]);
        Assert.assertEquals("<http://example.org/movies#ratedMovie>", parts[4]);
        Assert.assertEquals("?movie", parts[5]);
        Assert.assertEquals("?intermNode", parts[6]);
        Assert.assertEquals("<http://example.org/movies#hasRating>", parts[7]);
        Assert.assertEquals("?rating", parts[8]);
        
        
        System.out.println("testGraphPatternParts1 COMPLETED");
    }
    
    /**
     * Test getGraphPatternParts method of Evaluator Data Manager
     * 
     * (Graph pattern with dot at the end)
     */
    @Test
    public void testGraphPatternParts2() {

        System.out.println("");
        System.out.println("testGraphPatternParts2 starting...");
    
        
        String graphPattern = 
                "?user <http://example.org/movies#hasRated> ?intermNode . \n "
              + "?intermNode <http://example.org/movies#ratedMovie> ?movie . \n "
              + "?intermNode <http://example.org/movies#hasRating> ?rating .";
        
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        evalConfig.setGraphPattern(graphPattern);
        
        EvalDataManager edm = new EvalDataManager(evalConfig, null);
        
        String[] parts = edm.getGraphPatternParts();
        
        Assert.assertEquals("?user", parts[0]);
        Assert.assertEquals("<http://example.org/movies#hasRated>", parts[1]);
        Assert.assertEquals("?intermNode", parts[2]);
        Assert.assertEquals("?intermNode", parts[3]);
        Assert.assertEquals("<http://example.org/movies#ratedMovie>", parts[4]);
        Assert.assertEquals("?movie", parts[5]);
        Assert.assertEquals("?intermNode", parts[6]);
        Assert.assertEquals("<http://example.org/movies#hasRating>", parts[7]);
        Assert.assertEquals("?rating", parts[8]);
        
        
        System.out.println("testGraphPatternParts2 COMPLETED");
    }
    
    /**
     * Test getOtherEntities method of Evaluator Data Manager
     */
    @Test
    public void testGetOtherEntities1() {

        System.out.println("");
        System.out.println("testGetOtherEntities1 starting...");
        
    
        String graphPattern = 
                "?user <http://example.org/movies#hasRated> ?intermNode . \n "
              + "?user <http://example.org/movies#hasTested> ?test . \n "
              + "?intermNode <http://example.org/movies#ratedMovie> ?movie . \n "
              + "?test <http://example.org/movies#ratedTest> ?movie . \n "
              + "?intermNode <http://example.org/movies#hasRating> ?rating";
        
        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        evalConfig.setGraphPattern(graphPattern);
        HashMap<EvalEntity, String> evalEntityMap = new HashMap<>();
        
        evalEntityMap.put(EvalEntity.USER, "?user");
        evalEntityMap.put(EvalEntity.RAT_ITEM, "?movie");
        evalEntityMap.put(EvalEntity.RATING, "?rating");
        
        EvalDataManager edm = new EvalDataManager(evalConfig, evalEntityMap);
        
        HashSet<String> otherEntities;
        try {
            otherEntities = edm.getOtherEntities();
            Iterator iter = otherEntities.iterator();
            String first  = (String) iter.next();
            String second = (String) iter.next();
        
            Assert.assertEquals("?intermNode", first);
            Assert.assertEquals("?test", second);
            try {
                // should throw an exception
                String third = (String) iter.next(); 
                Assert.fail();
            }
            catch (NoSuchElementException ex) {
                // Success
            }
        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testGetOtherEntities1 COMPLETED");
    }
    
    /**
     * Test calculateAllUsersAverages method of Evaluator Data Manager
     */
    @Test
    public void testCalculateAllUsersAverages1() { 

        System.out.println("");
        System.out.println("testCalculateAllUsersAverages1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();
            Folds folds = dataManager.splitRatingsKFold(5, true);

            // User: Alice
            Rating rating0 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item1", 5.0);
            Rating rating1 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item2", 3.0);
            Rating rating2 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item3", 4.0);
            Rating rating3 = new Rating("http://example.org/movies#Alice", "http://example.org/movies#Item4", 4.0);

            // User: User1
            Rating rating4 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating5 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating6 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item3", 2.0);
            Rating rating7 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item4", 3.0);
            Rating rating8 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item5", 3.0);

            // User: User2
            Rating rating9 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item1", 4.0);
            Rating rating10 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item2", 3.0);
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item3", 4.0);
            Rating rating12 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 3.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);

            // User: User3
            Rating rating14 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item1", 3.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);
            Rating rating16 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item3", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 5.0);
            Rating rating18 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item5", 4.0);

            // User: User4
            Rating rating19 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item1", 1.0);
            Rating rating20 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 5.0);
            Rating rating21 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item3", 5.0);
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            Rating rating23 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item5", 1.0);
            
            
            ArrayList<Double> userAverages = dataManager.calculateAllUsersAverages(folds,2);
            Assert.assertEquals(4.0, userAverages.get(0),0.000001);
            Assert.assertEquals(2.25, userAverages.get(1),0.000001);
            Assert.assertEquals(3.75, userAverages.get(2),0.000001);
            Assert.assertEquals(3.0, userAverages.get(3),0.000001);
            Assert.assertEquals(2.25, userAverages.get(4),0.000001);
            
            ArrayList<Double> userAverages2 = dataManager.calculateAllUsersAverages(folds,4);
            Assert.assertEquals(4.0, userAverages2.get(0),0.000001);
            Assert.assertEquals(2.75, userAverages2.get(1),0.000001);
            Assert.assertEquals(4.0, userAverages2.get(2),0.000001);
            Assert.assertEquals(3.75, userAverages2.get(3),0.000001);
            Assert.assertEquals(3.25, userAverages2.get(4),0.000001);

        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testCalculateAllUsersAverages1 COMPLETED");
    }
    
    /**
     * Test removeStatements method of Evaluator Data Manager
     */
    @Test
    public void testRemoveStatements1() { 

        System.out.println("");
        System.out.println("testRemoveStatements1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            SailRecommenderRepository sRecommender = new SailRecommenderRepository(new MemoryStore());
            sRecommender.initialize();              
            SailRepositoryConnection conn = sRecommender.getConnection();
            conn.add(recEvalRepository.getConnection().getStatements(null, null, null, true));        
            
            ArrayList<Rating> testSet = new ArrayList<>();
            
            Rating rating4  = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item1", 3.0);
            Rating rating13 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item5", 5.0);
            Rating rating15 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item2", 3.0);            
            Rating rating22 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 2.0);
            rating4.addEntity("?intermNode", "http://example.org/movies#Rating6");
            rating13.addEntity("?intermNode", "http://example.org/movies#Rating15");
            rating15.addEntity("?intermNode", "http://example.org/movies#Rating17");
            rating22.addEntity("?intermNode", "http://example.org/movies#Rating24");
            
            testSet.add(rating4);
            testSet.add(rating13);
            testSet.add(rating15);
            testSet.add(rating22);                
            
            // SparqlUtils.printAllTriplesOfRepository(sRecommender);
            sRecommender = dataManager.removeStatements(sRecommender,testSet);   
            
            // For manual checking
            // System.out.println("AFTER DELETION");
            // SparqlUtils.printAllTriplesOfRepository(sRecommender); 
            
            String query = "SELECT $s $p $o WHERE { $s $p $o }";        

            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();

            int tripleCounter = 0;
            
            while (result.hasNext()) {
                tripleCounter++;
                BindingSet bs = result.next();        
            }                   
            
            // 8 triples should be removed out of 82 triples
            Assert.assertEquals(74, tripleCounter);

        } catch (EvaluatorException | RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRemoveStatements1 COMPLETED");
    }
    
    /**
     * Test removeStatements method of Evaluator Data Manager. Binary case.
     */
    @Test
    public void testRemoveStatementsBinary1() { 

        System.out.println("");
        System.out.println("testRemoveStatementsBinary1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList2();
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository6(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.PRE) );
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.REC) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            SailRecommenderRepository sRecommender = new SailRecommenderRepository(new MemoryStore());
            sRecommender.initialize();              
            SailRepositoryConnection conn = sRecommender.getConnection();
            conn.add(recEvalRepository.getConnection().getStatements(null, null, null, true));        
            
            ArrayList<Rating> testSet = new ArrayList<>();
            
            Rating rating1  = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item2", 1.0);
            Rating rating2  = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item4", 1.0);
            Rating rating3  = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item4", 1.0);            
            Rating rating4  = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item4", 1.0);          
            Rating rating5  = new Rating("http://example.org/movies#User5", "http://example.org/movies#Item5", 1.0);          
            Rating rating6  = new Rating("http://example.org/movies#User6", "http://example.org/movies#Item8", 1.0);          
            Rating rating7  = new Rating("http://example.org/movies#User7", "http://example.org/movies#Item9", 1.0);          
            Rating rating8  = new Rating("http://example.org/movies#User8", "http://example.org/movies#Item1", 1.0);          
            Rating rating9  = new Rating("http://example.org/movies#User9", "http://example.org/movies#Item3", 1.0);     
            
            Rating rating10 = new Rating("http://example.org/movies#User1", "http://example.org/movies#Item9", 1.0);   
            Rating rating11 = new Rating("http://example.org/movies#User2", "http://example.org/movies#Item10", 1.0);   
            Rating rating12 = new Rating("http://example.org/movies#User3", "http://example.org/movies#Item9", 1.0);   
            Rating rating13 = new Rating("http://example.org/movies#User4", "http://example.org/movies#Item2", 1.0);   
            Rating rating14 = new Rating("http://example.org/movies#User5", "http://example.org/movies#Item3", 1.0);
            Rating rating15 = new Rating("http://example.org/movies#User6", "http://example.org/movies#Item1", 1.0);
            Rating rating16 = new Rating("http://example.org/movies#User7", "http://example.org/movies#Item10", 1.0);
            Rating rating17 = new Rating("http://example.org/movies#User8", "http://example.org/movies#Item9", 1.0);
            
            testSet.add(rating1);
            testSet.add(rating2);
            testSet.add(rating3);
            testSet.add(rating4);   
            testSet.add(rating5);
            testSet.add(rating6);
            testSet.add(rating7);
            testSet.add(rating8);   
            testSet.add(rating9);
            testSet.add(rating10);
            testSet.add(rating11);
            testSet.add(rating12);   
            testSet.add(rating13);
            testSet.add(rating14);
            testSet.add(rating15);
            testSet.add(rating16); 
            testSet.add(rating17);                
            
            //SparqlUtils.printAllTriplesOfRepository(sRecommender);
            sRecommender = dataManager.removeStatements(sRecommender,testSet);   
            
            // For manual checking
            // System.out.println("AFTER DELETION");
            // SparqlUtils.printAllTriplesOfRepository(sRecommender); 
            
            String query = "SELECT $s $p $o WHERE { $s $p $o }";        

            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult result = tupleQuery.evaluate();

            int tripleCounter = 0;
            
            while (result.hasNext()) {
                tripleCounter++;
                BindingSet bs = result.next();        
            }                   
            
            // 17 triples should be removed out of 77 triples
            Assert.assertEquals(60, tripleCounter);

        } catch (EvaluatorException | RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testRemoveStatementsBinary1 COMPLETED");
    }
    
    /**
     * Test items Arraylist and itemFeatures ArrayList.
     * 
     * Dataset: moviesFromBookFeatures.ttl
     */
    @Test
    public void testItemsList1() { 
        
        System.out.println("");
        System.out.println("testItemsList1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        
        try {
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.setFeatureGraphPattern(
                    "?movie <http://example.org/movies#hasGenre> ?feature" );
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feature");
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addRankingMetricTopKSize(1);

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.getEvaluator().preprocess(configList);

            EvalDataManager dataManager = recEvalRepository.getEvaluator().getDataManager();

            ArrayList<String> items = dataManager.getItems();
            ArrayList<ArrayList<String>> itemFeatures = dataManager.getItemFeatures();

            String item1 = "http://example.org/movies#Item1";
            String item2 = "http://example.org/movies#Item2";
            String item3 = "http://example.org/movies#Item3";
            String item4 = "http://example.org/movies#Item4";
            String item5 = "http://example.org/movies#Item5";

            String itemFeature1 = "http://example.org/movies#Genre1";
            String itemFeature2 = "http://example.org/movies#Genre2";
            String itemFeature3 = "http://example.org/movies#Genre3";
            String itemFeature4 = "http://example.org/movies#Genre4";
            String itemFeature5 = "http://example.org/movies#Genre5";
            String itemFeature6 = "http://example.org/movies#Genre6";

            Assert.assertEquals(item1, items.get(0));
            Assert.assertEquals(item2, items.get(1));
            Assert.assertEquals(item3, items.get(2));
            Assert.assertEquals(item4, items.get(3));
            Assert.assertEquals(item5, items.get(4));

            Assert.assertEquals(itemFeature1, itemFeatures.get(0).get(0));
            Assert.assertEquals(itemFeature2, itemFeatures.get(0).get(1));
            Assert.assertEquals(itemFeature2, itemFeatures.get(1).get(0));
            Assert.assertEquals(itemFeature5, itemFeatures.get(1).get(1));
            Assert.assertEquals(itemFeature3, itemFeatures.get(2).get(0));
            Assert.assertEquals(itemFeature1, itemFeatures.get(3).get(0));
            Assert.assertEquals(itemFeature4, itemFeatures.get(3).get(1));
            Assert.assertEquals(itemFeature6, itemFeatures.get(3).get(2));
            Assert.assertEquals(itemFeature1, itemFeatures.get(4).get(0));
            Assert.assertEquals(itemFeature6, itemFeatures.get(4).get(1));
            
        } catch (EvaluatorException ex) {
            Logger.getLogger(EvalDataManagerTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        
        
        System.out.println("testItemsList1 COMPLETED");
    }
    
    /**
     * Tests if EvalDataManager wraps other errors and throws an EvaluatorException. 
     * 
     * Dataset: moviesFromBookFeatures.ttl
     */
    @Test(expected = EvaluatorException.class)
    public void testGraphPatternError1() { 
        
        System.out.println("");
        System.out.println("testGraphPatternError1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigListGraphPatternError1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.setFeatureGraphPattern(
                "?movie <http://example.org/movies#hasGenre> ?feature" );
        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feature");
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);
        evalConfig.addRankingMetricTopKSize(1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testGraphPatternError1 COMPLETED");
    }
    
    /**
     * Tests if EvalDataManager wraps other errors and throws an EvaluatorException. 
     * 
     * Dataset: moviesFromBookFeatures.ttl
     */
    @Test(expected = EvaluatorException.class)
    public void testGraphEntityError1() { 
        
        System.out.println("");
        System.out.println("testGraphEntityError1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigListGraphEntityError1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.setFeatureGraphPattern(
                "?movie <http://example.org/movies#hasGenre> ?feature" );
        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feature");
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);
        evalConfig.addRankingMetricTopKSize(1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testGraphEntityError1 COMPLETED");
    }
    
    /**
     * Tests if EvalDataManager wraps other errors and throws an EvaluatorException. 
     * 
     * Dataset: moviesFromBookFeatures.ttl
     */
    @Test(expected = EvaluatorException.class)
    public void testFeatureGraphPatternError1() { 
        
        System.out.println("");
        System.out.println("testFeatureGraphPatternError1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.setFeatureGraphPattern(
                "?item <http://example.org/movies#hasGenre> ?feature" );
        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feature");
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);
        evalConfig.addRankingMetricTopKSize(1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testFeatureGraphPatternError1 COMPLETED");
    }
    
    /**
     * Tests if EvalDataManager wraps other errors and throws an EvaluatorException. 
     * 
     * Dataset: moviesFromBookFeatures.ttl
     */
    @Test(expected = EvaluatorException.class)
    public void testFeatureGraphEntityError1() { 
        
        System.out.println("");
        System.out.println("testFeatureGraphEntityError1 starting...");
        
        
        ArrayList<RecConfig> configList = EvalTestRepositoryInstantiator.getRecConfigList1();

        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(configList);

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();
        
        evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY) );
        evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
        evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
        evalConfig.setStorage(EvalStorage.MEMORY);
        evalConfig.setFeatureGraphPattern(
                "?movie <http://example.org/movies#hasGenre> ?feature" );
        evalConfig.addEvalEntity(EvalEntity.FEATURE, "?feat");
        evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
        evalConfig.setIsReproducible(true);
        evalConfig.setNumberOfFolds(5);
        evalConfig.addRankingMetricTopKSize(1);

        recEvalRepository.loadEvalConfiguration(evalConfig);
        recEvalRepository.evaluate();
        
        
        System.out.println("testFeatureGraphEntityError1 COMPLETED");
    }
}

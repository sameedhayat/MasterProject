/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.evaluator;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommendereval.config.CrossKFoldEvalConfig;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Folds;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import org.eclipse.rdf4j.recommendereval.evaluator.crosskfold.CrossKFoldEvaluatorTest;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import static org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion.RANDOM;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Test class for AbstractEvaluator Class
 * 
 * 4 cases are tested:
 * 1) With ratings
 * 2) With likes
 * 3) Partially Filled lists
 * 4) Empty lists
 */
public class EvaluatorTest {
    /**
     * Allowed error.
     */
    private double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    
    /**
     * Tests calculatePrecision() method
     */
    @Test
    public void testPrecision() {
     
        System.out.println("");
        System.out.println("testPrecision starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.6);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 4.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.7);
        
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
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // true positive
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.62);
        // true positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        
        // false positive
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 4.6);
        // false positive
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item7>", 4.1);   
        // true positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item4>", 3.11);
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.PRE, testSet,
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP numbers and result calculated by hand
        
        Double expectedResult = 0.3125;
            
        Assert.assertEquals(expectedResult, result);     
        
        
        System.out.println("testPrecision COMPLETED");
    }
    
    /**
     * Tests calculatePrecision() method. Likes case.
     *//*
    @Test
    public void testPrecisionLikes() {
     
        System.out.println("");
        System.out.println("testPrecisionLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 1.0);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.0);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 1.0);
        
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
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item3>", 0.95);
        // true positive
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.7);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 0.62);
        // true positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 0.1);
        
        // false positive
        rrArray3[0] = new RatedResource("<http://example.org/users#Item1>", 0.6);
        // false positive 1.1 intentional
        rrArray3[1] = new RatedResource("<http://example.org/users#Item2>", 1.1);
        
        // false positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item5>", 0.6);
        // false positive
        rrArray4[1] = null;   
        // true positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item4>", 0.41);
        // true positive
        rrArray4[3] = new RatedResource("<http://example.org/users#Item1>", 0.31);
        
        // Average of the training set
        userAverages.add(1.00); //avg of Jack 
        userAverages.add(1.00); //avg of Carlos
        userAverages.add(1.00); //avg of Macy
        userAverages.add(1.00); //avg of Rajesh
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.PRE, testSet,
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP numbers and result calculated by hand
        
        Double expectedResult = 0.3125;
            
        Assert.assertEquals(expectedResult, result);     
        
        
        System.out.println("testPrecisionLikes COMPLETED");
    }*/
    
    /**
     * Tests calculatePrecision() method. Partially filled case.
     */
    @Test
    public void testPrecisionPartiallFilled() {
     
        System.out.println("");
        System.out.println("testPrecisionPartiallFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[3];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.6);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 4.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.7);
        
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
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // null
        rrArray1[1] = null;
        // true positive
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.62);
        // null
        rrArray2[1] = null;
        // true positive
        rrArray2[2] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        
        // null
        rrArray3[0] = null;
        // false positive
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 4.6);
        // false positive
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);   
        // true positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item4>", 3.11);
        // null
        rrArray4[3] = null;
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.PRE, testSet,
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP numbers and result calculated by hand
        
        Double expectedResult = 0.3125;
            
        Assert.assertEquals(expectedResult, result);     
        
        
        System.out.println("testPrecisionPartiallFilled COMPLETED");
    }
    
    /**
     * Tests calculatePrecision() method. Empty set case.
     */
    @Test
    public void testPrecisionEmpty() {
     
        System.out.println("");
        System.out.println("testPrecisionEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[1];
        RatedResource[] rrArray2 = new RatedResource[1];
        RatedResource[] rrArray3 = new RatedResource[1];
        RatedResource[] rrArray4 = new RatedResource[1];
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.PRE, null,
                topRecommendations, userAverages, ignoreUsers, 1, null, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);     
        
        
        System.out.println("testPrecisionEmpty COMPLETED");
    }
    
    /**
     * Tests calculateRecall() method
     */
    @Test
    public void testRecall() {
     
        System.out.println("");
        System.out.println("testRecall starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 3.7);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.6);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // true positive
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.62);
        // false positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        // false negative
        
        // true positive
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 4.6);
        // true positive
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);   
        // false positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 1.6);
        // false negative
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.REC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FN numbers and result calculated by hand
        
        Double expectedResult = 0.75;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testRecall COMPLETED");        
    }
    
    /**
     * Tests calculateRecall() method. Likes case.
     */
    @Test
    public void testRecallLikes() {
     
        System.out.println("");
        System.out.println("testRecallLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 1.0);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.0);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 1.0);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // true positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.6);
        // 
        rrArray1[1] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        // false negative
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 0.62);
        // true positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 0.1);
        // false negative
        
        // 
        rrArray3[0] = new RatedResource("<http://example.org/users#Item1>", 0.7);
        // 
        rrArray3[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        // false negative
        // false negative
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 0.7);
        // 
        rrArray4[1] = null;   
        // true positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.4);
        // false negative
        // false negative
        
        // Average of the training set
        userAverages.add(1.0); //avg of Jack 
        userAverages.add(1.0); //avg of Carlos
        userAverages.add(1.0); //avg of Macy
        userAverages.add(1.0); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.REC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FN numbers and result calculated by hand
        
        Double expectedResult = 0.416666666666666;
            
        Assert.assertEquals(result, expectedResult, 0.0000001);        


        System.out.println("testRecallLikes COMPLETED");        
    }
    
    /**
     * Tests calculateRecall() method. Partially filled case.
     */
    @Test
    public void testRecallPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testRecallPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[3];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 3.7);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.6);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // null
        rrArray1[1] = null;
        // true positive
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.63);
        // null
        rrArray2[1] = null;
        // false positive
        rrArray2[2] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        
        // null
        rrArray3[0] = null;
        // true positive
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 4.6);
        // true positive
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);   
        // false positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 1.6);
        // null
        rrArray4[3] = null;
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.REC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FN numbers and result calculated by hand
        
        Double expectedResult = 0.75;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testRecallPartiallyFilled COMPLETED");        
    }
    
    /**
     * Tests calculateRecall() method. Empty set case.
     */
    @Test
    public void testRecallEmpty() {
     
        System.out.println("");
        System.out.println("testRecallEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[1];
        RatedResource[] rrArray2 = new RatedResource[1];
        RatedResource[] rrArray3 = new RatedResource[1];
        RatedResource[] rrArray4 = new RatedResource[1];
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.REC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 1, null, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testRecallEmpty COMPLETED");        
    }
    /**
     * Tests calculateAccuracy() method
     */
    @Test
    public void testAccuracy() {
     
        System.out.println("");
        System.out.println("testAccuracy starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 3.7);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.6);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // true positive
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.62);
        // true positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        // true negative
        
        // true positive
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 4.6);
        // true positive
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);   
        // false positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 1.6);
        // false negative
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            ((AbstractEvaluator)eval).setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Double result = eval.evaluateRankingMetric(EvalMetric.ACC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP & FN & TN numbers and result calculated by hand
        
        Double expectedResult = 0.3875;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testAccuracy COMPLETED");        
    }
    
    /**
     * Tests calculateAccuracy() method. Likes case.
     *//*
    @Test
    public void testAccuracyLikes() {
     
        System.out.println("");
        System.out.println("testAccuracyLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 1.0);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.0);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 1.0);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // true positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.6);
        // 
        rrArray1[1] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        // false negative
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 0.62);
        // true positive
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 0.1);
        // false negative
        
        // 
        rrArray3[0] = new RatedResource("<http://example.org/users#Item1>", 0.7);
        // 
        rrArray3[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        // false negative
        // false negative
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 0.7);
        // 
        rrArray4[1] = null;   
        // true positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.4);
        // false negative
        // false negative
        
        // Average of the training set
        userAverages.add(1.0); //avg of Jack 
        userAverages.add(1.0); //avg of Carlos
        userAverages.add(1.0); //avg of Macy
        userAverages.add(1.0); //avg of Rajesh
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.ACC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP & FN & TN numbers and result calculated by hand
        
        Double expectedResult = 0.416666666666666;
            
        Assert.assertEquals(result, expectedResult, 0.0000001);        


        System.out.println("testAccuracyLikes COMPLETED");        
    }*/
    
    /**
     * Tests calculateAccuracy() method. Partially filled case.
     */
    @Test
    public void testAccuracyPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testAccuracyPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[3];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 3.7);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.6);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        
        // false positive
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        // false positive - null
        rrArray1[1] = null;
        // true positive
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        // false positive - empty slot at topKList
        
        // true positive
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.63);
        // false positive - null
        rrArray2[1] = null;
        // true positive
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        // true negative
        // false positive - empty slot at topKList
        
        // false positive - null
        rrArray3[0] = null;
        // false positive - null
        rrArray3[1] = null;
        // true positive
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 4.1);
        // false positive - empty slot at topKList
        // false negative
        
        // true positive
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        // false positive
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);   
        // false positive
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 1.6);
        // false positive - null
        rrArray4[3] = null;
        // false negative
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);

        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            ((AbstractEvaluator)eval).setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Double result = eval.evaluateRankingMetric(EvalMetric.ACC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        // TP & FP & FN & TN numbers and result calculated by hand
        
        Double expectedResult = 0.3125;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testAccuracyPartiallyFilled COMPLETED");        
    }
    
    /**
     * Tests calculateAccuracy() method. Empty set case.
     */
    @Test
    public void testAccuracyEmpty() {
     
        System.out.println("");
        System.out.println("testAccuracyEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[1];
        RatedResource[] rrArray2 = new RatedResource[1];
        RatedResource[] rrArray3 = new RatedResource[1];
        RatedResource[] rrArray4 = new RatedResource[1];
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.58); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            ((AbstractEvaluator)eval).setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Double result = eval.evaluateRankingMetric(EvalMetric.ACC, datasetTestSet, 
                topRecommendations, userAverages, ignoreUsers, 1, null, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);        


        System.out.println("testAccuracyEmpty COMPLETED");        
    }
    
    /**
     * Tests calculateNDCG() method
     */
    @Test
    public void testNDCG() {
     
        System.out.println("");
        System.out.println("testNDCG starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.0);
        Rating rating3  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 3.0);
        Rating rating4  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 0.0);
        Rating rating5  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0);
        Rating rating6  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 2.0);
        
        Rating rating7  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.0);
        Rating rating8  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 0.0);
        Rating rating9  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item4>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.0);
        Rating rating12 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 3.0);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        datasetTestSet.add(rating12);
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 4.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        rrArray1[2] = new RatedResource("<http://example.org/users#Item4>", 3.6);
        /*
        rrArray1[3] = new RatedResource("<http://example.org/users#Item4>", 3.1);
        rrArray1[4] = new RatedResource("<http://example.org/users#Item5>", 2.6);
        rrArray1[5] = new RatedResource("<http://example.org/users#Item6>", 1.1);
        */
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.0);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 3.1);        
        rrArray2[2] = new RatedResource("<http://example.org/users#Item7>", 3.0);
        /*
        rrArray2[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);        
        rrArray2[4] = new RatedResource("<http://example.org/users#Item5>", 1.2);
        rrArray2[5] = new RatedResource("<http://example.org/users#Item6>", 1.0);
        */
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(1.61); //avg of Carlos
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> jackRatings1   = new ArrayList<>();
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        jackRatings0.add(rating3);
        jackRatings1.add(rating4);
        jackRatings1.add(rating5);
        jackRatings1.add(rating6);
        
        carlosRatings0.add(rating7);
        carlosRatings0.add(rating8);
        carlosRatings0.add(rating9);  
        carlosRatings1.add(rating10);
        carlosRatings1.add(rating11);
        carlosRatings1.add(rating12);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
             
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);        
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        
        Folds folds = new Folds(foldsList);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        
        AbstractEvaluator eval = (AbstractEvaluator) EvalTestRepositoryInstantiator.getEvaluator();
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            eval.setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NDCG, datasetTestSet,
                topRecommendations, userAverages, ignoreUsers, 3, folds, 0); 

        // result calculated by hand
        
        Double expectedResult = 0.9751172083949179;
            
        Assert.assertEquals(expectedResult, result, DELTA);
        
        
        System.out.println("testNDCG COMPLETED");
    }
    
    /**
     * Tests calculateNDCG() method. Likes case
     */
    @Test
    public void testNDCGLikes() {
     
        System.out.println("");
        System.out.println("testNDCGLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        Rating rating3  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 1.0);
        Rating rating4  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0);
        Rating rating6  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 1.0);
        
        Rating rating7  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating8  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating9  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item7>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item8>", 1.0);
        Rating rating12 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item10>", 1.0);
        
        /*
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        datasetTestSet.add(rating12);
        */
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item9>", 0.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.5);
        rrArray1[2] = new RatedResource("<http://example.org/users#Item3>", 0.4);
        /*
        rrArray1[3] = null;
        rrArray1[4] = new RatedResource("<http://example.org/users#Item5>", 0.1);
        rrArray1[5] = null;
        */
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 1.0);     
        rrArray2[1] = new RatedResource("<http://example.org/users#Item7>", 0.8);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item2>", 0.9);   
        /*
        rrArray2[3] = null;        
        rrArray2[4] = new RatedResource("<http://example.org/users#Item5>", 0.2);
        rrArray2[5] = new RatedResource("<http://example.org/users#Item6>", 0.1);
        */
        
        // Average of the training set
        userAverages.add(1.0); //avg of Jack 
        userAverages.add(1.0); //avg of Carlos
        
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> jackRatings1   = new ArrayList<>();
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        jackRatings0.add(rating3);
        jackRatings1.add(rating4);
        jackRatings1.add(rating5);
        jackRatings1.add(rating6);
        
        carlosRatings0.add(rating7);
        carlosRatings0.add(rating8);
        carlosRatings0.add(rating9);  
        carlosRatings1.add(rating10);
        carlosRatings1.add(rating11);
        carlosRatings1.add(rating12);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
             
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);        
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        
        Folds folds = new Folds(foldsList);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        
        AbstractEvaluator eval = (AbstractEvaluator) EvalTestRepositoryInstantiator.getEvaluator();
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            eval.setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NDCG, datasetTestSet,
                topRecommendations, userAverages, ignoreUsers, 6, folds, 0); 

        // result calculated by hand
        
        Double expectedResult = 0.8934038989167449;

        Assert.assertEquals(expectedResult, result, DELTA); 
        
        System.out.println("testNDCGLikes COMPLETED");
    }
    
    /**
     * Tests calculateNDCG() method. Partially filled case.
     */
    @Test
    public void testNDCGPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testNDCGPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[5];
        RatedResource[] rrArray2 = new RatedResource[5];
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.0);
        Rating rating3  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 3.0);
        Rating rating4  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 0.0);
        Rating rating5  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0);
        Rating rating6  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 2.0);
        
        Rating rating7  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.0);
        Rating rating8  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 0.0);
        Rating rating9  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item4>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.0);
        Rating rating12 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 3.0);
        
        datasetTestSet.add(rating1);
        datasetTestSet.add(rating2);
        datasetTestSet.add(rating3);
        datasetTestSet.add(rating4);
        datasetTestSet.add(rating5);
        datasetTestSet.add(rating6);
        datasetTestSet.add(rating7);
        datasetTestSet.add(rating8);
        datasetTestSet.add(rating9);
        datasetTestSet.add(rating10);
        datasetTestSet.add(rating11);
        datasetTestSet.add(rating12);
        
        rrArray1[0] = null;
        rrArray1[1] = new RatedResource("<http://example.org/users#Item1>", 4.9);
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 3.1);
        rrArray1[3] = null;
        rrArray1[4] = new RatedResource("<http://example.org/users#Item6>", 2.6);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.0);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);        
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 1.1);
        rrArray2[3] = new RatedResource("<http://example.org/users#Item7>", 1.1); 
        rrArray2[4] = null;        
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(1.61); //avg of Carlos
        
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> jackRatings1   = new ArrayList<>();
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        jackRatings0.add(rating3);
        jackRatings1.add(rating4);
        jackRatings1.add(rating5);
        jackRatings1.add(rating6);
        
        carlosRatings0.add(rating7);
        carlosRatings0.add(rating8);
        carlosRatings0.add(rating9);  
        carlosRatings1.add(rating10);
        carlosRatings1.add(rating11);
        carlosRatings1.add(rating12);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
             
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);        
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        
        Folds folds = new Folds(foldsList);
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        jackRatings.add(rating3);
        jackRatings.add(rating4);
        jackRatings.add(rating5);
        jackRatings.add(rating6);
        
        carlosRatings.add(rating7);
        carlosRatings.add(rating8);
        carlosRatings.add(rating9);  
        carlosRatings.add(rating10);
        carlosRatings.add(rating11);
        carlosRatings.add(rating12);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        
        AbstractEvaluator eval = (AbstractEvaluator) EvalTestRepositoryInstantiator.getEvaluator();
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            eval.setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NDCG, datasetTestSet,
                topRecommendations, userAverages, ignoreUsers, 8, folds, 0); 

        // result calculated by hand
        
        Double expectedResult = 0.7774073810988479;
            
        Assert.assertEquals(expectedResult, result, DELTA); 
        
        
        System.out.println("testNDCGPartiallyFilled COMPLETED");
    }
    
    /**
     * Tests calculateNDCG() method. Empty set case.
     */
    @Test
    public void testNDCGEmpty() {
     
        System.out.println("");
        System.out.println("testNDCGEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        ArrayList<Rating> datasetTestSet = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();

        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        
        ArrayList<Rating> jackRatings0   = new ArrayList<>();
        ArrayList<Rating> jackRatings1   = new ArrayList<>();
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
             
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);        
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        
        Folds folds = new Folds(foldsList);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        
        AbstractEvaluator eval = (AbstractEvaluator) EvalTestRepositoryInstantiator.getEvaluator();
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            eval.setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NDCG, datasetTestSet,
                topRecommendations, userAverages, ignoreUsers, 1, folds, 0); 

        // result calculated by hand
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result); 
        
        
        System.out.println("testNDCGEmpty COMPLETED");
    }
    
    /**
     * Tests calculateMRR() method
     */
    @Test
    public void testMRR() {
     
        System.out.println("");
        System.out.println("testMRR starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.1);
        
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.6);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 2.1);
        Rating rating6  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 4.1);
        
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating8  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.1);
        
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.1);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 3.6);
        Rating rating12 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 4.1);
        
        testSet.add(rating1);
        testSet.add(rating2);
        testSet.add(rating4);
        testSet.add(rating5);
        testSet.add(rating6);
        testSet.add(rating7);
        testSet.add(rating8);
        testSet.add(rating9);
        testSet.add(rating10);
        testSet.add(rating11);
        testSet.add(rating12);
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);
        carlosRatings.add(rating6);
        
        macyRatings.add(rating7);
        macyRatings.add(rating8);
        
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        rajeshRatings.add(rating12);
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 3.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        
        // 1/1 , 1/3 , 0 , 1/3         
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.MRR, testSet,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.41666666666666663;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testMRR COMPLETED");
    }
    
    /**
     * Tests calculateMRR() method. Likes case.
     */
    @Test
    public void testMRRLikes() {
     
        System.out.println("");
        System.out.println("testMRRLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating6  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 1.0);
        Rating rating8  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.0);
        Rating rating12 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 1.0);
        
        testSet.add(rating1);
        testSet.add(rating2);
        testSet.add(rating4);
        testSet.add(rating5);
        testSet.add(rating6);
        testSet.add(rating7);
        testSet.add(rating8);
        testSet.add(rating9);
        testSet.add(rating10);
        testSet.add(rating11);
        testSet.add(rating12);
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);
        carlosRatings.add(rating6);
        
        macyRatings.add(rating7);
        macyRatings.add(rating8);
        
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        rajeshRatings.add(rating12);
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item5>", 0.7);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item6>", 0.4);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item8>", 0.4);
        rrArray3[1] = null;
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item9>", 0.6);
        rrArray4[1] = null;        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.2);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 0.1);
        
        // 1/1 , 1/3 , 0 , 1/3         
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        // Average of the training set
        userAverages.add(1.0); //avg of Jack 
        userAverages.add(1.0); //avg of Carlos
        userAverages.add(1.0); //avg of Macy
        userAverages.add(1.0); //avg of Rajesh
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.MRR, testSet,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.41666666666666663;
            
        Assert.assertEquals(result, expectedResult, DELTA);      
        
        
        System.out.println("testMRRLikes COMPLETED");
    }
    
    /**
     * Tests calculateMRR() method. Partially filled case.
     */
    @Test
    public void testMRRPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testMRRPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.1);
        
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.6);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 2.1);
        Rating rating6  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 4.1);
        
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating8  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.1);
        
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.1);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 2.6);
        Rating rating12 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 4.1);
        
        testSet.add(rating1);
        testSet.add(rating2);
        testSet.add(rating4);
        testSet.add(rating5);
        testSet.add(rating6);
        testSet.add(rating7);
        testSet.add(rating8);
        testSet.add(rating9);
        testSet.add(rating10);
        testSet.add(rating11);
        testSet.add(rating12);
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);
        carlosRatings.add(rating6);
        
        macyRatings.add(rating7);
        macyRatings.add(rating8);
        
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        rajeshRatings.add(rating12);
        
        RatedResource[] rrArray1 = new RatedResource[5];
        RatedResource[] rrArray2 = new RatedResource[5];
        RatedResource[] rrArray3 = new RatedResource[5];
        RatedResource[] rrArray4 = new RatedResource[5];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = null;
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        rrArray2[2] = null;
        rrArray2[3] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        
        rrArray3[0] = null;
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        rrArray4[4] = null;
        
        // 1/1 , 1/4 , 0 , 1/4         
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.MRR, testSet,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.375;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testMRRPartiallyFilled COMPLETED");
    }
    
    /**
     * Tests calculateMRR() method. Empty set case.
     */
    @Test
    public void testMRREmpty() {
     
        System.out.println("");
        System.out.println("testMRREmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[2];
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.MRR, null,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testMRREmpty COMPLETED");
    }
    
    /**
     * Tests calculateNovelty method
     */
    @Test
    public void testNovelty() {
     
        System.out.println("");
        System.out.println("testNovelty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();     
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 3.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.6);
        
        Rating rating111  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 2.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.6);
        
        Rating rating112  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.6);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating113  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item6>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 4.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.7);
        
        Rating rating114 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item5>", 3.7);
        Rating rating115 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item6>", 3.7);
        
        ArrayList<Rating> jackRatings0 = new ArrayList<>();
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        carlosRatings0.add(rating3);
        carlosRatings0.add(rating4);
        carlosRatings0.add(rating5);
        
        ArrayList<Rating> macyRatings0 = new ArrayList<>();
        macyRatings0.add(rating6);
        macyRatings0.add(rating7);
        
        ArrayList<Rating> rajeshRatings0 = new ArrayList<>();
        rajeshRatings0.add(rating8);
        rajeshRatings0.add(rating9);
        rajeshRatings0.add(rating10);
        rajeshRatings0.add(rating11);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
        
        // Add ratings for other fold, and create folds
        ArrayList<Rating> jackRatings1 = new ArrayList<>();
        jackRatings1.add(rating111);        
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        carlosRatings1.add(rating112);
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);
        
        ArrayList<Rating> macyRatings1 = new ArrayList<>();
        macyRatings1.add(rating113);
        ArrayList<ArrayList<Rating>> macyFolds = new ArrayList<>();
        macyFolds.add(macyRatings0);
        macyFolds.add(macyRatings1);
        
        ArrayList<Rating> rajeshRatings1 = new ArrayList<>();
        rajeshRatings1.add(rating114);
        rajeshRatings1.add(rating115);
        ArrayList<ArrayList<Rating>> rajeshFolds = new ArrayList<>();
        rajeshFolds.add(rajeshRatings0);
        rajeshFolds.add(rajeshRatings1);
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        foldsList.add(macyFolds);
        foldsList.add(rajeshFolds);
        
        Folds folds = new Folds(foldsList);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        usersRatings.add(macyRatings0);
        usersRatings.add(rajeshRatings0);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        items.add("<http://example.org/users#Item6>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(0).add("<http://example.org/users#Feature2>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature1>");
        itemFeatures.get(2).add("<http://example.org/users#Feature3>");
        itemFeatures.get(3).add("<http://example.org/users#Feature3>");
        itemFeatures.get(4).add("<http://example.org/users#Feature1>");
        itemFeatures.get(5).add("<http://example.org/users#Feature1>");
        itemFeatures.get(5).add("<http://example.org/users#Feature2>");
        
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        eval.setItemFeatures(itemFeatures);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NOVELTY, null,
                topRecommendations, userAverages, ignoreUsers, 4, folds, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.348001217791;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testNovelty COMPLETED");
    }
    
    /**
     * Tests calculateNovelty method. Likes case.
     */
    @Test
    public void testNoveltyLikes() {
     
        System.out.println("");
        System.out.println("testNoveltyLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();   
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item5>", 0.7);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item6>", 0.4);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item8>", 0.4);
        rrArray3[1] = null;
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item9>", 0.6);
        rrArray4[1] = null;        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.2);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 0.1);
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        
        Rating rating111 = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item10>", 1.0);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.0);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating112 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item11>", 1.0);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 1.0);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating113 = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item10>", 1.0);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 1.0);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 1.0);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 1.0);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 1.0);
        
        Rating rating114 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item10>", 1.0);
        Rating rating115 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item11>", 1.0);
        
        ArrayList<Rating> jackRatings = new ArrayList<>();
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        carlosRatings.add(rating3);
        carlosRatings.add(rating4);
        carlosRatings.add(rating5);
        
        ArrayList<Rating> macyRatings = new ArrayList<>();
        macyRatings.add(rating6);
        macyRatings.add(rating7);
        
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        rajeshRatings.add(rating8);
        rajeshRatings.add(rating9);
        rajeshRatings.add(rating10);
        rajeshRatings.add(rating11);
        
        ArrayList<Rating> jackRatings0 = new ArrayList<>();
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        carlosRatings0.add(rating3);
        carlosRatings0.add(rating4);
        carlosRatings0.add(rating5);
        
        ArrayList<Rating> macyRatings0 = new ArrayList<>();
        macyRatings0.add(rating6);
        macyRatings0.add(rating7);
        
        ArrayList<Rating> rajeshRatings0 = new ArrayList<>();
        rajeshRatings0.add(rating8);
        rajeshRatings0.add(rating9);
        rajeshRatings0.add(rating10);
        rajeshRatings0.add(rating11);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
        
        // Add ratings for other fold, and create folds
        ArrayList<Rating> jackRatings1 = new ArrayList<>();
        jackRatings1.add(rating111);        
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        carlosRatings1.add(rating112);
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);
        
        ArrayList<Rating> macyRatings1 = new ArrayList<>();
        macyRatings1.add(rating113);
        ArrayList<ArrayList<Rating>> macyFolds = new ArrayList<>();
        macyFolds.add(macyRatings0);
        macyFolds.add(macyRatings1);
        
        ArrayList<Rating> rajeshRatings1 = new ArrayList<>();
        rajeshRatings1.add(rating114);
        rajeshRatings1.add(rating115);
        ArrayList<ArrayList<Rating>> rajeshFolds = new ArrayList<>();
        rajeshFolds.add(rajeshRatings0);
        rajeshFolds.add(rajeshRatings1);
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        foldsList.add(macyFolds);
        foldsList.add(rajeshFolds);
        
        Folds folds = new Folds(foldsList);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        items.add("<http://example.org/users#Item6>");
        items.add("<http://example.org/users#Item8>");
        items.add("<http://example.org/users#Item9>");
        items.add("<http://example.org/users#Item10>");
        items.add("<http://example.org/users#Item11>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature2>");
        itemFeatures.get(3).add("<http://example.org/users#Feature1>");
        itemFeatures.get(4).add("<http://example.org/users#Feature3>");
        itemFeatures.get(5).add("<http://example.org/users#Feature3>");
        itemFeatures.get(6).add("<http://example.org/users#Feature1>");
        itemFeatures.get(7).add("<http://example.org/users#Feature4>");
        itemFeatures.get(8).add("<http://example.org/users#Feature3>");
        itemFeatures.get(9).add("<http://example.org/users#Feature2>");
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        eval.setItemFeatures(itemFeatures);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NOVELTY, null,
                topRecommendations, userAverages, ignoreUsers, 4, folds, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.46875;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testNoveltyLikes COMPLETED");
    }
    
    /**
     * Tests calculateNovelty method. Partially filled case.
     */
    @Test
    public void testNoveltyPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testNoveltyPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();       
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();   
        
        RatedResource[] rrArray1 = new RatedResource[5];
        RatedResource[] rrArray2 = new RatedResource[5];
        RatedResource[] rrArray3 = new RatedResource[5];
        RatedResource[] rrArray4 = new RatedResource[5];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = null;
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        rrArray2[2] = null;
        rrArray2[3] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        
        rrArray3[0] = null;
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        rrArray4[4] = null;
        
        
        
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.6);
        
        Rating rating111  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 2.6);
        
        Rating rating3  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.62);
        Rating rating4  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.7);
        Rating rating5  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.6);
        
        Rating rating112  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.6);
        
        Rating rating6  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item3>", 2.6);
        Rating rating7  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item4>", 2.6);
        
        Rating rating113  = new Rating("<http://example.org/users#Macy>", "<http://example.org/users#Item6>", 2.6);
        
        Rating rating8  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item1>", 3.6);
        Rating rating9  = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item2>", 2.6);
        Rating rating10 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item3>", 4.6);
        Rating rating11 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item4>", 3.7);
        
        Rating rating114 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item5>", 3.7);
        Rating rating115 = new Rating("<http://example.org/users#Rajesh>", "<http://example.org/users#Item6>", 3.7);
        
        ArrayList<Rating> jackRatings0 = new ArrayList<>();
        jackRatings0.add(rating1);
        jackRatings0.add(rating2);
        
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        carlosRatings0.add(rating3);
        carlosRatings0.add(rating4);
        carlosRatings0.add(rating5);
        
        ArrayList<Rating> macyRatings0 = new ArrayList<>();
        macyRatings0.add(rating6);
        macyRatings0.add(rating7);
        
        ArrayList<Rating> rajeshRatings0 = new ArrayList<>();
        rajeshRatings0.add(rating8);
        rajeshRatings0.add(rating9);
        rajeshRatings0.add(rating10);
        rajeshRatings0.add(rating11);
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
        
        // Add ratings for other fold, and create folds
        ArrayList<Rating> jackRatings1 = new ArrayList<>();
        jackRatings1.add(rating111);        
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        carlosRatings1.add(rating112);
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);
        
        ArrayList<Rating> macyRatings1 = new ArrayList<>();
        macyRatings1.add(rating113);
        ArrayList<ArrayList<Rating>> macyFolds = new ArrayList<>();
        macyFolds.add(macyRatings0);
        macyFolds.add(macyRatings1);
        
        ArrayList<Rating> rajeshRatings1 = new ArrayList<>();
        rajeshRatings1.add(rating114);
        rajeshRatings1.add(rating115);
        ArrayList<ArrayList<Rating>> rajeshFolds = new ArrayList<>();
        rajeshFolds.add(rajeshRatings0);
        rajeshFolds.add(rajeshRatings1);
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        foldsList.add(macyFolds);
        foldsList.add(rajeshFolds);
        
        Folds folds = new Folds(foldsList);
        
        usersRatings.add(jackRatings0);
        usersRatings.add(carlosRatings0);
        usersRatings.add(macyRatings0);
        usersRatings.add(rajeshRatings0);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        items.add("<http://example.org/users#Item6>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(0).add("<http://example.org/users#Feature2>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature1>");
        itemFeatures.get(2).add("<http://example.org/users#Feature3>");
        itemFeatures.get(3).add("<http://example.org/users#Feature1>");
        itemFeatures.get(4).add("<http://example.org/users#Feature1>");
        itemFeatures.get(5).add("<http://example.org/users#Feature2>");
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        eval.setItemFeatures(itemFeatures);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NOVELTY, null,
                topRecommendations, userAverages, ignoreUsers, 5, folds, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.2909009742330;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testNoveltyPartiallyFilled COMPLETED");
    }
    
    /**
     * Tests calculateNovelty method. Empty set case.
     */
    @Test
    public void testNoveltyEmpty() {
     
        System.out.println("");
        System.out.println("testNoveltyEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[2];
        
        
        ArrayList<ArrayList<ArrayList<Rating>>> foldsList = new ArrayList<>();
        
        // Add ratings for other fold, and create folds
        ArrayList<Rating> jackRatings0 = new ArrayList<>();
        ArrayList<Rating> jackRatings1 = new ArrayList<>();
        ArrayList<ArrayList<Rating>> jackFolds = new ArrayList<>();
        jackFolds.add(jackRatings0);
        jackFolds.add(jackRatings1);
        
        ArrayList<Rating> carlosRatings0 = new ArrayList<>();
        ArrayList<Rating> carlosRatings1 = new ArrayList<>();
        ArrayList<ArrayList<Rating>> carlosFolds = new ArrayList<>();
        carlosFolds.add(carlosRatings0);
        carlosFolds.add(carlosRatings1);
        
        ArrayList<Rating> macyRatings0 = new ArrayList<>();
        ArrayList<Rating> macyRatings1 = new ArrayList<>();
        ArrayList<ArrayList<Rating>> macyFolds = new ArrayList<>();
        macyFolds.add(macyRatings0);
        macyFolds.add(macyRatings1);
        
        ArrayList<Rating> rajeshRatings0 = new ArrayList<>();
        ArrayList<Rating> rajeshRatings1 = new ArrayList<>();
        ArrayList<ArrayList<Rating>> rajeshFolds = new ArrayList<>();
        rajeshFolds.add(rajeshRatings0);
        rajeshFolds.add(rajeshRatings1);
        
        foldsList.add(jackFolds);
        foldsList.add(carlosFolds);
        foldsList.add(macyFolds);
        foldsList.add(rajeshFolds);
        
        Folds folds = new Folds(foldsList);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.NOVELTY, null,
                topRecommendations, userAverages, ignoreUsers, 5, folds, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testNoveltyEmpty COMPLETED");
    }
    
    /**
     * Tests calculateCoverage method
     */
    @Test
    public void testCoverage() {
     
        System.out.println("");
        System.out.println("testCoverage starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 3.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateGlobalMetric(EvalMetric.COVERAGE, null,
                topRecommendations, userAverages, ignoreUsers, 5); 
        
        // result calculated by hand
        
        Double expectedResult = 0.8;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testCoverage COMPLETED");
    }
    
    /**
     * Tests calculateCoverage method. Likes case.
     */
    @Test
    public void testCoverageLikes() {
     
        System.out.println("");
        System.out.println("testCoverageLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item5>", 0.7);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item6>", 0.4);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item8>", 0.4);
        rrArray3[1] = null;
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item9>", 0.6);
        rrArray4[1] = null;        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.2);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 0.1);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        items.add("<http://example.org/users#Item6>");
        items.add("<http://example.org/users#Item7>");
        items.add("<http://example.org/users#Item8>");
        items.add("<http://example.org/users#Item9>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateGlobalMetric(EvalMetric.COVERAGE, null,
                topRecommendations, userAverages, ignoreUsers, 5); 

        
        // result calculated by hand
        
        Double expectedResult = 0.8888888888888;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testCoverageLikes COMPLETED");
    }
    
    /**
     * Tests calculateCoverage method. Partially filled case.
     */
    @Test
    public void testCoveragePartiallyFilled() {
     
        System.out.println("");
        System.out.println("testCoveragePartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        RatedResource[] rrArray1 = new RatedResource[5];
        RatedResource[] rrArray2 = new RatedResource[5];
        RatedResource[] rrArray3 = new RatedResource[5];
        RatedResource[] rrArray4 = new RatedResource[5];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = null;
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        rrArray2[2] = null;
        rrArray2[3] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        
        rrArray3[0] = null;
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        rrArray4[4] = null;
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item8>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateGlobalMetric(EvalMetric.COVERAGE, null,
                topRecommendations, userAverages, ignoreUsers, 5); 

        
        // result calculated by hand
        
        Double expectedResult = 0.8;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testCoveragePartiallyFilled COMPLETED");
    }
    
    /**
     * Tests calculateCoverage method. Empty set case.
     */
    @Test
    public void testCoverageEmpty() {
     
        System.out.println("");
        System.out.println("testCoverageEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[2];
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateGlobalMetric(EvalMetric.COVERAGE, null,
                topRecommendations, userAverages, ignoreUsers, 5); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testCoverageEmpty COMPLETED");
    }
    
    /**
     * Tests calculateDiversity method
     */
    @Test
    public void testDiversity() {
     
        System.out.println("");
        System.out.println("testDiversity starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        RatedResource[] rrArray1 = new RatedResource[3];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        rrArray1[2] = new RatedResource("<http://example.org/users#Item3>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item4>", 3.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[1] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(0).add("<http://example.org/users#Feature2>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature1>");
        itemFeatures.get(2).add("<http://example.org/users#Feature3>");
        itemFeatures.get(3).add("<http://example.org/users#Feature3>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setItemFeatures(itemFeatures);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.DIVERSITY, null,
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.331852753919;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testDiversity COMPLETED");
    }
    
    /**
     * Tests calculateDiversity method. Likes case.
     */
    @Test
    public void testDiversityLikes() {
     
        System.out.println("");
        System.out.println("testDiversityLikes starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[3];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[4];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 0.9);
        rrArray1[1] = new RatedResource("<http://example.org/users#Item2>", 0.4);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item5>", 0.7);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item6>", 0.4);
        rrArray2[2] = new RatedResource("<http://example.org/users#Item3>", 0.1);
        
        rrArray3[0] = new RatedResource("<http://example.org/users#Item8>", 0.4);
        rrArray3[1] = null;
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item9>", 0.6);
        rrArray4[1] = null;        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 0.2);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 0.1);
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        items.add("<http://example.org/users#Item5>");
        items.add("<http://example.org/users#Item6>");
        items.add("<http://example.org/users#Item8>");
        items.add("<http://example.org/users#Item9>");
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature3>");
        itemFeatures.get(3).add("<http://example.org/users#Feature3>");
        itemFeatures.get(4).add("<http://example.org/users#Feature1>");
        itemFeatures.get(5).add("<http://example.org/users#Feature2>");
        itemFeatures.get(6).add("<http://example.org/users#Feature3>");
        itemFeatures.get(7).add("<http://example.org/users#Feature3>");
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        usersRatings.add(new ArrayList<>());
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setItemFeatures(itemFeatures);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.DIVERSITY, null,
                topRecommendations, userAverages, ignoreUsers, 4, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.1666666666666666666;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testDiversityLikes COMPLETED");
    }
    
    /**
     * Tests calculateDiversity method. Partially filled case.
     */
    @Test
    public void testDiversityPartiallyFilled() {
     
        System.out.println("");
        System.out.println("testDiversityPartiallyFilled starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();      
        
        RatedResource[] rrArray1 = new RatedResource[5];
        RatedResource[] rrArray2 = new RatedResource[5];
        RatedResource[] rrArray3 = new RatedResource[5];
        RatedResource[] rrArray4 = new RatedResource[5];
        
        rrArray1[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray1[1] = null;
        rrArray1[2] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        
        rrArray2[0] = new RatedResource("<http://example.org/users#Item1>", 3.6);
        rrArray2[1] = new RatedResource("<http://example.org/users#Item2>", 2.1);
        rrArray2[2] = null;
        rrArray2[3] = new RatedResource("<http://example.org/users#Item3>", 4.1);
        
        rrArray3[0] = null;
        rrArray3[1] = new RatedResource("<http://example.org/users#Item3>", 2.6);
        rrArray3[2] = new RatedResource("<http://example.org/users#Item4>", 1.1);
        
        rrArray4[0] = new RatedResource("<http://example.org/users#Item1>", 4.6);
        rrArray4[1] = new RatedResource("<http://example.org/users#Item2>", 4.1);        
        rrArray4[2] = new RatedResource("<http://example.org/users#Item3>", 3.6);
        rrArray4[3] = new RatedResource("<http://example.org/users#Item4>", 2.1);
        rrArray4[4] = null;
        
        items.add("<http://example.org/users#Item1>");
        items.add("<http://example.org/users#Item2>");
        items.add("<http://example.org/users#Item3>");
        items.add("<http://example.org/users#Item4>");
        
        items.stream().forEach((_item) -> {
            itemFeatures.add( new ArrayList<>() );
        });
        
        itemFeatures.get(0).add("<http://example.org/users#Feature1>");
        itemFeatures.get(1).add("<http://example.org/users#Feature2>");
        itemFeatures.get(2).add("<http://example.org/users#Feature3>");
        itemFeatures.get(3).add("<http://example.org/users#Feature3>");
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setItemFeatures(itemFeatures);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.DIVERSITY, null,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 

        
        // result calculated by hand
        
        Double expectedResult = 0.225;
            
        Assert.assertEquals(expectedResult, result, DELTA);      
        
        
        System.out.println("testDiversityPartiallyFilled COMPLETED");
    }
    
    /**
     * Tests calculateDiversity method. Empty set case.
     */
    @Test
    public void testDiversityEmpty() {
     
        System.out.println("");
        System.out.println("testDiversityEmpty starting...");
        
        
        ArrayList<RatedResource[]> topRecommendations = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<ArrayList<String>> itemFeatures = new ArrayList<>();
        ArrayList<Boolean> ignoreUsers = new ArrayList<>();  
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();        
        
        RatedResource[] rrArray1 = new RatedResource[2];
        RatedResource[] rrArray2 = new RatedResource[2];
        RatedResource[] rrArray3 = new RatedResource[2];
        RatedResource[] rrArray4 = new RatedResource[2];
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        ignoreUsers.add(Boolean.FALSE);
        
        topRecommendations.add(rrArray1);
        topRecommendations.add(rrArray2);
        topRecommendations.add(rrArray3);
        topRecommendations.add(rrArray4);      
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setItems(items);
        eval.setUsers(users);
        eval.setItemFeatures(itemFeatures);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluateRankingMetric(EvalMetric.DIVERSITY, null,
                topRecommendations, userAverages, ignoreUsers, 5, null, 0); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);      
        
        
        System.out.println("testDiversityEmpty COMPLETED");
    }
    
    /**
     * Tests calculateAUC() method
     */
    @Test
    public void testAUC() { 
        
        System.out.println("");
        System.out.println("testAUC starting...");
    
        
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        
        // P positive N negative
        // P P P N P P N
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 3.6);
        Rating rating3  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 3.6);
        Rating rating4  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 1.6);
        Rating rating5  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 3.6);
        Rating rating6  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 3.6);
        Rating rating7  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item7>", 1.6);
        
        // P N P N P N P
        Rating rating8  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 3.9);
        Rating rating9  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 1.7);
        Rating rating10 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 3.9);
        Rating rating11 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item4>", 1.7);
        Rating rating12 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 3.9);
        Rating rating13 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 1.7);
        Rating rating14 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item7>", 3.9);
        
        
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
        
        recommenderRatings.add(4.6);
        recommenderRatings.add(3.95);
        recommenderRatings.add(3.45);
        recommenderRatings.add(3.2);
        recommenderRatings.add(2.19);
        recommenderRatings.add(2.01);
        recommenderRatings.add(1.1);
        recommenderRatings.add(4.62);
        recommenderRatings.add(4.12);
        recommenderRatings.add(4.01);
        recommenderRatings.add(3.45);
        recommenderRatings.add(3.22);
        recommenderRatings.add(3.01);
        recommenderRatings.add(2.1);
        
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(2.91); //avg of Carlos
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        jackRatings.add(rating3);
        jackRatings.add(rating4);
        jackRatings.add(rating5);
        jackRatings.add(rating6);
        jackRatings.add(rating7);
        
        carlosRatings.add(rating8);
        carlosRatings.add(rating9);
        carlosRatings.add(rating10);
        carlosRatings.add(rating11);
        carlosRatings.add(rating12);
        carlosRatings.add(rating13);
        carlosRatings.add(rating14);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluatePredictionMetric(EvalMetric.AUC, testSet, 
                recommenderRatings, userAverages); 
        
        // calculated by hand, (0.8 + 0.5) / 2        
        Double expectedResult = 0.6222222222222;
            
        Assert.assertEquals(expectedResult, result, DELTA);  
        
        
        System.out.println("testAUC COMPLETED");
    }
    
    /**
     * Tests calculateAUC() method. Likes case
     */
    @Test
    public void testAUCLikes() { 
        
        System.out.println("");
        System.out.println("testAUCLikes starting...");
    
        
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        
        // P positive N negative
        // P P P N P P N
        Rating rating1  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0);
        Rating rating2  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0);
        Rating rating3  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating5  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0);
        Rating rating6  = new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item6>", 1.0);
        
        // P N P N P N P
        Rating rating8  = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0);
        
        Rating rating10 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0);
        
        Rating rating12 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 1.0);
        
        Rating rating14 = new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item7>", 1.0);
        
        
        testSet.add(rating1);
        testSet.add(rating2);
        testSet.add(rating3);
        testSet.add(rating5);
        testSet.add(rating6);
        testSet.add(rating8);
        testSet.add(rating10);
        testSet.add(rating12);
        testSet.add(rating14);
        
        recommenderRatings.add(0.95);
        recommenderRatings.add(0.9);
        recommenderRatings.add(0.45);
        recommenderRatings.add(0.25);
        recommenderRatings.add(0.18);
        
        recommenderRatings.add(0.92);
        recommenderRatings.add(0.56);
        recommenderRatings.add(0.29);
        recommenderRatings.add(0.1);        
        
        // Average of the training set
        userAverages.add(1.0); //avg of Jack 
        userAverages.add(1.0); //avg of Carlos
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        
        jackRatings.add(rating1);
        jackRatings.add(rating2);
        jackRatings.add(rating6);
        
        carlosRatings.add(rating8);
        carlosRatings.add(rating14);
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        
        Evaluator eval = EvalTestRepositoryInstantiator.getEvaluator();
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        Double result = eval.evaluatePredictionMetric(EvalMetric.AUC, testSet, 
                recommenderRatings, userAverages); 

        // calculated by hand, (0.8 + 0.5) / 2        
        Double expectedResult = 1.0;
            
        Assert.assertEquals(expectedResult, result);  
        
        System.out.println("testAUCLikes COMPLETED");
    }
    
    /**
     * Tests calculateAUC() method. Empty set case.
     */
    @Test
    public void testAUCEmpty() { 
        
        System.out.println("");
        System.out.println("testAUCEmpty starting...");
    
        
        ArrayList<Rating> testSet = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        ArrayList<Double> userAverages = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<ArrayList<Rating>> usersRatings = new ArrayList<>();
        
        // Average of the training set
        userAverages.add(2.54); //avg of Jack 
        userAverages.add(3.61); //avg of Carlos
        userAverages.add(2.98); //avg of Macy
        userAverages.add(3.12); //avg of Rajesh
        
        ArrayList<Rating> jackRatings   = new ArrayList<>();
        ArrayList<Rating> carlosRatings = new ArrayList<>();
        ArrayList<Rating> macyRatings   = new ArrayList<>();
        ArrayList<Rating> rajeshRatings = new ArrayList<>();
        
        usersRatings.add(jackRatings);
        usersRatings.add(carlosRatings);
        usersRatings.add(macyRatings);
        usersRatings.add(rajeshRatings);
        
        users.add("<http://example.org/users#Jack>");
        users.add("<http://example.org/users#Carlos>");
        users.add("<http://example.org/users#Macy>");
        users.add("<http://example.org/users#Rajesh>");        
        
        AbstractEvaluator eval = (AbstractEvaluator) EvalTestRepositoryInstantiator.getEvaluator();
        
        ArrayList<RecConfig> configList = new ArrayList<>();
        
        VsmCfRecConfig configuration1 = new VsmCfRecConfig("config1");
            configuration1.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
        try {
            configuration1.setRecEntity(RecEntity.USER, "?user");
            configuration1.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration1.setRecEntity(RecEntity.RATING, "?rating"); 
            configList.add(configuration1);
            
            SailRecEvaluatorRepository recRepository = new SailRecEvaluatorRepository(
                        new MemoryStore(), configList);  
            recRepository.initialize();
        
            CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.MAE));
            evalConfig.addEvalMetric( new PredictionEvalMetric(EvalMetric.RMSE));
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);

            recRepository.loadEvalConfiguration(evalConfig);
            eval.setEvaluatorRepository(recRepository);
        } catch (RecommenderException | RepositoryException | EvaluatorException ex) {
            Logger.getLogger(EvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        eval.setUsers(users);
        eval.setUsersRatings(usersRatings);
        
        
        Double result = eval.evaluatePredictionMetric(EvalMetric.AUC, testSet, 
                recommenderRatings, userAverages); 
        
        Double expectedResult = 0.5;
    
        Assert.assertEquals(expectedResult, result);  

        
        System.out.println("testAUCEmpty COMPLETED");
    }  
    
    /**
     * Tests calculateMAE() method
     */
    @Test
    public void testMAE() {
        
        System.out.println("");
        System.out.println("testMAE starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6));
        recommenderRatings.add(3.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.1));
        recommenderRatings.add(2.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 1.0));
        recommenderRatings.add(1.7);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 5.0));
        recommenderRatings.add(3.9);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 3.0));
        recommenderRatings.add(3.0);
        
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 2.5));
        recommenderRatings.add(2.46);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.3));
        recommenderRatings.add(1.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 2.9));
        recommenderRatings.add(3.8);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item4>", 4.5));
        recommenderRatings.add(4.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.4));
        recommenderRatings.add(5.0);  
                
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.MAE, datasetTestSet,
                        recommenderRatings, null); 

        // result calculated by hand
        
        Double expectedResult = 0.8854377448471;
            
        Assert.assertEquals(expectedResult, result, DELTA);  
        
        
        System.out.println("testMAE COMPLETED");
    }
    
    /**
     * Tests calculateMAE() method. Likes case. (Does not make much sense, 
     * I know)
     */
    @Test
    public void testMAELikes() {
        
        System.out.println("");
        System.out.println("testMAELikes starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        
        // 0.76
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0));
        recommenderRatings.add(null);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0));
        recommenderRatings.add(0.9);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 1.0));
        recommenderRatings.add(0.2);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0));
        recommenderRatings.add(0.1);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item7>", 1.0));
        recommenderRatings.add(null);
        
        // 0,614
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0));
        recommenderRatings.add(0.76);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0));
        recommenderRatings.add(0.87);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 1.0));
        recommenderRatings.add(0.1);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 1.0));
        recommenderRatings.add(0.0);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item7>", 1.0));
        recommenderRatings.add(0.2);  
                
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.MAE, datasetTestSet,
                        recommenderRatings, null); 

        // result calculated by hand
        
        Double expectedResult = 0.828854631404084;
            
        Assert.assertEquals(expectedResult, result, DELTA);  
        
        
        System.out.println("testMAELikes COMPLETED");
    }
    
    /**
     * Tests calculateMAE() method. Empty set case.
     */
    @Test
    public void testMAEEmpty() {
        
        System.out.println("");
        System.out.println("testMAEEmpty starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
                
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.MAE, datasetTestSet,
                        recommenderRatings, null); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result);  
        
        
        System.out.println("testMAEEmpty COMPLETED");
    }
    
    /**
     * Tests calculateRMSE() method
     */
    @Test
    public void testRMSE() {
        
        System.out.println("");
        System.out.println("testRMSE starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
        
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 3.6));
        recommenderRatings.add(3.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 2.1));
        recommenderRatings.add(2.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item3>", 1.0));
        recommenderRatings.add(1.7);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 5.0));
        recommenderRatings.add(3.9);
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 3.0));
        recommenderRatings.add(3.0);
        
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 2.5));
        recommenderRatings.add(2.46);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item2>", 3.3));
        recommenderRatings.add(1.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 2.9));
        recommenderRatings.add(3.8);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item4>", 4.5));
        recommenderRatings.add(4.4);
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 2.4));
        recommenderRatings.add(5.0);  
                
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.RMSE, datasetTestSet, 
                        recommenderRatings, null); 

        // result calculated by hand
        
        Double expectedResult = 1.1411222546248057;
            
        Assert.assertEquals(expectedResult, result); 
        
        
        System.out.println("testRMSE COMPLETED");
    }
    
    /**
     * Tests calculateRMSE() method. Likes case. (Does not make much sense, 
     * I know)
     */
    @Test
    public void testRMSELikes() {
        
        System.out.println("");
        System.out.println("testRMSELikes starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
                
        // 0.8318653
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item1>", 1.0));
        recommenderRatings.add(null); // 1,0
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item2>", 1.0));
        recommenderRatings.add(0.9);  // 0,01
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item4>", 1.0));
        recommenderRatings.add(0.2);  // 0,64
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item5>", 1.0));
        recommenderRatings.add(0.1);  // 0,81
        datasetTestSet.add( new Rating("<http://example.org/users#Jack>", "<http://example.org/users#Item7>", 1.0));
        recommenderRatings.add(null); // 1,0
        
        // 0.7105631
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item1>", 1.0));
        recommenderRatings.add(0.76); // 0,0576
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item3>", 1.0));
        recommenderRatings.add(0.87); // 0,0169
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item5>", 1.0));
        recommenderRatings.add(0.1);  // 0,81
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item6>", 1.0));
        recommenderRatings.add(0.0);  // 1,0
        datasetTestSet.add( new Rating("<http://example.org/users#Carlos>", "<http://example.org/users#Item7>", 1.0));
        recommenderRatings.add(0.2);  // 0,64
        
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.RMSE, datasetTestSet, 
                        recommenderRatings, null); 

        // result calculated by hand
        
        Double expectedResult = 0.773595;
            
        Assert.assertEquals(result, expectedResult, 0.00001); 
        
        
        System.out.println("testRMSELikes COMPLETED");
    }
    
    /**
     * Tests calculateRMSE() method. Empty set case.
     */
    @Test
    public void testRMSEEmpty() {
        
        System.out.println("");
        System.out.println("testRMSEEmpty starting...");
        
        
        ArrayList<Rating> datasetTestSet     = new ArrayList<>();
        ArrayList<Double> recommenderRatings = new ArrayList<>();
                
        Double result = EvalTestRepositoryInstantiator.getEvaluator().
                evaluatePredictionMetric(EvalMetric.RMSE, datasetTestSet, 
                        recommenderRatings, null); 
        
        Double expectedResult = 0.0;
            
        Assert.assertEquals(expectedResult, result); 
        
        
        System.out.println("testRMSEEmpty COMPLETED");
    }
    
    
    /**
     * Tests if getItemDistance() method works as intended.
     */
    @Test
    public void testGetItemDistance() {     
    
        System.out.println("");
        System.out.println("testGetItemDistance starting...");
        
        
        SailRecEvaluatorRepository recEvalRepository
                = EvalTestRepositoryInstantiator.createTestRepository1(
                        EvalTestRepositoryInstantiator.getRecConfigList1());

        CrossKFoldEvalConfig evalConfig = new CrossKFoldEvalConfig();

        try {       
            evalConfig.setOutputMethod(EvalOutput.EXTERNAL_FILE, "evaluationOutput.csv");
            evalConfig.setStorage(EvalStorage.MEMORY);
            evalConfig.addEvalMetric( new RankingEvalMetric(EvalMetric.DIVERSITY));
            evalConfig.addRankingMetricTopKSize(1);
            evalConfig.selectSpecificUsersForEvaluation(new EvalUserSelectionWrapper(RANDOM,5,0));
            evalConfig.setIsReproducible(true);
            evalConfig.setNumberOfFolds(5);
            evalConfig.addEvalEntity(EvalEntity.FEATURE, "?genre");
            evalConfig.setFeatureGraphPattern("?movie <http://example.org/movies#hasGenre> ?genre ");

            recEvalRepository.loadEvalConfiguration(evalConfig);
            recEvalRepository.evaluate();
                
            // distance with itself
            Double itemDistance1 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "http://example.org/movies#Item2", "http://example.org/movies#Item2");
            // distance between 2 different items
            Double itemDistance2 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "http://example.org/movies#Item1", "http://example.org/movies#Item2");
            Double itemDistance3 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "http://example.org/movies#Item1", "http://example.org/movies#Item4");
            // item URIs that do not exist
            Double itemDistance4 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "", "http://example.org/movies#Item2");
            Double itemDistance5 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "http://example.org/movies#Item2", "");
            Double itemDistance6 = ( (AbstractEvaluator) recEvalRepository.getEvaluator()).getItemDistance(
                    "", "");
            
            Double expectedItemDistance1 = 0.0;      
            Double expectedItemDistance2 = 0.5;    
            Double expectedItemDistance3 = 1.0 - 0.40824829046386296;      
            Double expectedItemDistance4 = 1.0;    
            Double expectedItemDistance5 = 1.0;    
            Double expectedItemDistance6 = 1.0;      
            
            Assert.assertEquals(expectedItemDistance1 , itemDistance1, DELTA);
            Assert.assertEquals(expectedItemDistance2 , itemDistance2, DELTA);
            Assert.assertEquals(expectedItemDistance3 , itemDistance3, DELTA);
            Assert.assertEquals(expectedItemDistance4 , itemDistance4, DELTA);
            Assert.assertEquals(expectedItemDistance5 , itemDistance5, DELTA);
            Assert.assertEquals(expectedItemDistance6 , itemDistance6, DELTA);
            
        } catch (EvaluatorException ex) {
            Logger.getLogger(CrossKFoldEvaluatorTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        } 
        
        
        System.out.println("testGetItemDistance COMPLETED");    
    }
}

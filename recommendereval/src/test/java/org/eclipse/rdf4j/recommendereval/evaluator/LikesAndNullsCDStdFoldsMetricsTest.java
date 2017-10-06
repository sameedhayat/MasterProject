/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.recommendereval.repository.LikesAndNullsSrrDriver;
import org.eclipse.rdf4j.recommendereval.util.EvalTestRepositoryInstantiator;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * This test has been designed to verify all the metrics for the following cases:
 * - Likes are used
 * - It is cross domain
 * Folds a created by a standard data manager.
 * The recommender can return null items as part of the top-k recommendations.
 */
public class LikesAndNullsCDStdFoldsMetricsTest {    
    /**
     * Allowed error.
     */
    private double DELTA = EvalTestRepositoryInstantiator.DELTA_7;
    
    /**
     * TODO
     * This method has been copied from another test case. 
     * Results have to be recalculated. Remove this comment when this is fixed.
     */
    @Test    
    public void testResultsOfFixedCrossDomainSRRWithLikes() {
            System.out.println("");
            System.out.println("testResultsOfFixedCrossDomainSRRWithLikes starting...");
            
            // Note: this is not a mistake. We use LikesAndNullsSrrDriver as 
            // the recommender, because the source domain is completely ignored
            // and the recommendations would be the same.
            
            LikesAndNullsSrrDriver fixedRep = new LikesAndNullsSrrDriver(new MemoryStore());
            fixedRep.loadRecConfiguration(EvalTestRepositoryInstantiator.getRecConfigListLikesSDFixed().get(0));
            
            Assert.assertEquals(new Double(1.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User1", null)));
            Assert.assertEquals(new Double(0.0), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User2", null)));
            Assert.assertEquals(new Double(0.5), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User3", null)));
            Assert.assertEquals(new Double(0.2), 
                    new Double(fixedRep.predictRating("http://example.org/fixed#User4", null)));
            
            /*
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
            */
            
            System.out.println("testResultsOfFixedCrossDomainSRRWithLikes COMPLETED");
    }
    
    /**
     * TODO
     * This method has been copied from another test case. 
     * Results have to be recalculated. Remove this comment when this is fixed.
     */
    /**
     * Tests all metrics at once by comparing them with the results of the 
     * metrics of a  non cross-domain recommender.
     */
    @Test
    public void testAllMetrics() {
            //TODO
    }
    
}



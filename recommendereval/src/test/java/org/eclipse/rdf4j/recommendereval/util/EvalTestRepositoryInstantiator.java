/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rdf4j.recommender.config.HybridRecConfig;
import org.eclipse.rdf4j.recommender.config.LinkAnalysisRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.config.VsmCfRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.junit.Assert;
import org.eclipse.rdf4j.recommendereval.datamanager.EvalDataManager;
import org.eclipse.rdf4j.recommendereval.evaluator.AbstractEvaluator;
import org.eclipse.rdf4j.recommendereval.evaluator.Evaluator;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.repository.LikesCDStdFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesAndNullsCDStdFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesSDCusFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesAndNullsSDCusFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.SailRecEvaluatorRepository;
import org.eclipse.rdf4j.recommendereval.repository.RatingsSDCusFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.RatingsAndNullsSDCusFoldsSerDriver;
import org.eclipse.rdf4j.recommendereval.repository.LikesSDStdFoldsSerDriver;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Static class that helps in creating repositories with different test datasets.
 * In this way all JUnit tests have a centralized access to these repositories.
 * In addition to this we will keep three centralized levels of DELTA values to
 * determine the precision in all the tests.
 */
public final class EvalTestRepositoryInstantiator {

    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    /**
     * Centralized DELTA values
     */
    //public static final double DELTA_2 = 1e-3;
    public static final double DELTA_7 = 1e-7;
    public static final double DELTA_3 = 1e-3;

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesFromBook.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository1(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesFromBook.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesLarger.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository2(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesLarger2.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesBadDistribution.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository3(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesBadDistribution.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesFromBookCrossDomain.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository4(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesFromBookCrossDomain.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesFromBookCrossDomainLikes.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository5(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesFromBookCrossDomainLikes.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesLikes.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository6(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesLikes.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesFromBookDifferentOrder.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository7(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesFromBookDifferentOrder.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }

    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: merge_complete_level1.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepositoryHybrid(ArrayList<RecConfig> configList) {
    	
    	String resource = "testcases/merge_complete_level1.ttl";
    	String baseURI = "";
        return createTestRepositoryHelper(configList,resource,baseURI);
    }
    
    /**
     * Creates a repository with given Recommender Configuration list.
     *
     * DataSet: moviesFromBookModified1.ttl
     *
     * @param configList Recommender Configurations to evaluate
     * @return
     */
    public static SailRecEvaluatorRepository createTestRepository9(ArrayList<RecConfig> configList) {

        String resource = "testcases/moviesFromBookModified1.ttl";
        String baseURI = "http://example.org/movies#";

        return createTestRepositoryHelper(configList,resource,baseURI);
    }
    
    
    /**
     * Creates a RatingsSDCusFoldsSerDriver which is fixed to use the following 
     * dataset:
     * - fixed-ratings.ttl
     *
     * @return
     */
    public static RatingsSDCusFoldsSerDriver createRatingsSDCusFoldsSer() {

        String resource = "testcases/fixed-ratings.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        RatingsSDCusFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new RatingsSDCusFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }
    
    /**
     * Creates a LikesSDCusFoldsSerDriver which is fixed to use the following
     * dataset:
     * - fixed-likes.ttl
     *
     * @return
     */
    public static LikesSDCusFoldsSerDriver createLikesSDCusFoldsSer() {

        String resource = "testcases/fixed-likes.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        LikesSDCusFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new LikesSDCusFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }
        
    /**
     * Creates a LikesSDStdFoldsSerDriver which is fixed to use the following
     * dataset:
     * - fixed-likes.ttl
     *
     * @return
     */    
    public static LikesSDStdFoldsSerDriver createLikesSDStdFoldsSer() {

        String resource = "testcases/fixed-likes.ttl";
        String baseURI = "http://example.org/movies#";
        
        RepositoryConnection conn = null;
        LikesSDStdFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new LikesSDStdFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }
    
    /**
     * Creates a LikesCDStdFoldsSerDriver which is fixed to use the 
     * following dataset:
     * - fixed-likes-cd.ttl
     *
     * @return
     */
    public static LikesCDStdFoldsSerDriver createLikesCDStdFoldsSer() {

        String resource = "testcases/fixed-likes-cd.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        LikesCDStdFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new LikesCDStdFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }
    
    
    /**
     * Creates a RatingsAndNullsSDCusFoldsSerDriver which is fixed to use the 
     * following dataset:
     * - fixed-ratings.ttl
     *
     * @return
     */
    public static RatingsAndNullsSDCusFoldsSerDriver createRatingsAndNullsSDCusFoldsSer() {

        String resource = "testcases/fixed-ratings.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        RatingsAndNullsSDCusFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new RatingsAndNullsSDCusFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }
    
    /**
     * Creates a LikesAndNullsSDCusFoldsSerDriver which is fixed to use the 
     * following dataset:
     * - fixed-likes.ttl
     *
     * @return
     */
    public static LikesAndNullsSDCusFoldsSerDriver createLikesAndNullsSDCusFoldsSer() {

        String resource = "testcases/fixed-likes.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        LikesAndNullsSDCusFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new LikesAndNullsSDCusFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }    
    
    /**
     * Creates a LikesAndNullsCDStdFoldsSerDriver which is fixed to use the 
     * following dataset:
     * - fixed-likes-cd.ttl
     *
     * @return
     */
    public static LikesAndNullsCDStdFoldsSerDriver createLikesAndNullsCDStdFoldsSer() {

        String resource = "testcases/fixed-likes-cd.ttl";
        String baseURI = "http://example.org/fixed#";
        
        RepositoryConnection conn = null;
        LikesAndNullsCDStdFoldsSerDriver recRepository = null;
        
        try {
            recRepository = new LikesAndNullsCDStdFoldsSerDriver(
                    new MemoryStore());
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recRepository;
    }            
                
    
    /**
     * Creates a repository with given Recommender Configuration list and given 
     * dataset.
     *
     * @param configList Recommender Configurations to evaluate
     * @param resource File name and path 
     * @param baseURI 
     * @return
     */
    private static SailRecEvaluatorRepository createTestRepositoryHelper(
            ArrayList<RecConfig> configList, String resource, String baseURI) {

        RepositoryConnection conn = null;
        SailRecEvaluatorRepository recRepository = null;

        try {
            recRepository = new SailRecEvaluatorRepository(
                    new MemoryStore(), configList);
            recRepository.initialize();
            conn = recRepository.getConnection();

            conn.add(CLASS_LOADER.getResource(resource), baseURI, RDFFormat.TURTLE);
            
        } catch (RepositoryException | IOException | RDFParseException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        return recRepository;
    }
    
    /**
     * Creates a list of Recommender Configurations.
     *
     * Returns a recommender configuration list with 1 item: VsmCfRecConfig
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigList1() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration.setRecEntity(RecEntity.RATING, "?rating");

            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(9);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return recConfigList;
    }
    
    /**
     * Creates a list of Recommender Configurations.
     *
     * Dataset: Likes
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigList2() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

            configuration.setPosGraphPattern(
                    "?user <http://example.org/movies#hasLiked> ?movie "
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.POS_ITEM, "?movie");

            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(8);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
        return recConfigList;
    }

    /**
     * Creates a list of Recommender Configurations.
     * 
     * Dataset: Cross Domain
     *
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigList3() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            
            LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/movies#ratedMovie> ?movie . \n "
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration.setRecEntity(RecEntity.RATING, "?rating");
            
            configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                    "?movieNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/movies#Hollywood>");
            configuration.setRecEntity(RecEntity.TARGET_DOMAIN, 
                    "?movieNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/movies#European>"); 

            configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY);
            configuration.preprocessBeforeRecommending(true);
            configuration.setkMarkovSteps(6);
            configuration.setMaxIterations(6);
            configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
            configuration.setDecimalPlaces(3);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        return recConfigList;
    }

    /**
     * Creates a list of Recommender Configurations.
     * 
     * Dataset: Cross Domain - Likes 
     *
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigList4() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            
            LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("config1");

            configuration.setPosGraphPattern(
                    "?user <http://example.org/movies#hasLiked> ?movie "
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.POS_ITEM, "?movie");
            
            configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                    "?movieNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/movies#Hollywood>");
            configuration.setRecEntity(RecEntity.TARGET_DOMAIN, 
                    "?movieNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/movies#European>"); 

            configuration.setRecParadigm(RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY);
            configuration.preprocessBeforeRecommending(true);
            configuration.setkMarkovSteps(6);
            configuration.setMaxIterations(6);
            configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
            configuration.setDecimalPlaces(3);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        return recConfigList;
    }
    
    /**
     * Creates a list of Recommender Configurations.
     * 
     * Dataset: Cross Domain - Likes 
     *
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigListHybrid() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            
            HybridRecConfig configuration = new HybridRecConfig("config1");

            configuration.setPosGraphPattern(
                    "?u <http://example.org/data#likes> ?o ."
                    + "?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t ."
                    + "FILTER (?t=<http://schema.org/Movie> || ?t=<http://schema.org/Book>) "
            		);

	        configuration.setRecEntity(RecEntity.USER, "?u");
	        configuration.setRecEntity(RecEntity.POS_ITEM, "?o");
	        configuration.setGraphOrientation(RecGraphOrientation.DIRECTED);
	        //TODO
	        //Modify this later
	        configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
	                "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Movie>");                        
	        configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
	                "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Book>");
	        configuration.setRecParadigm(RecParadigm.HYBRID);
	        configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH); 
	        //configuration.computeDoc2Vec();
	        //configuration.computeRdf2Vec();
	        //configuration.doc2VecInputPath("input_abstract.csv");
	        //configuration.rdf2VecInputPath("rdf2vec_model");
	        
	        configuration.doc2VecOutputPath("doc2vec_embeddings.csv");
	        configuration.rdf2VecOutputPath("rdf2vec_embeddings.csv");
	        configuration.readUserEmbeddings("user_embeddings.csv");
	        configuration.mlInputFile("ml_training_data.csv");
	        configuration.trainTreeModel();
	        recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }

        return recConfigList;
    }
    
    /**
     * Creates a list of Recommender Configurations with incorrect graph pattern.
     *
     * Returns a recommender configuration list with 1 item: VsmCfRecConfig
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigListGraphPatternError1() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?item <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");
            configuration.setRecEntity(RecEntity.RATING, "?rating");

            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(9);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return recConfigList;
    }
    
    /**
     * Creates a list of Recommender Configurations with incorrect graph entity.
     *
     * Returns a recommender configuration list with 1 item: VsmCfRecConfig
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigListGraphEntityError1() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("config1");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#hasRated> ?intermNode . \n "
                    + "?item <http://example.org/movies#ratedMovie> ?movie ."
                    + "?intermNode <http://example.org/movies#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");
            configuration.setRecEntity(RecEntity.RATING, "?rating");

            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(9);

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return recConfigList;
    }
    
    /**
     * Returns a single recommender configuration list with 1 item: 
     * VsmCfRecConfig.
     * This configuration fits the following datasets:
     * - fixed-ratings.ttl
     * Configuration is single-domain (SD).
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigListRatingsSDFixed() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("ratingsSDConfig");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/fixed#hasRated> ?intermNode . \n "
                    + "?intermNode <http://example.org/fixed#ratedMovie> ?item ."
                    + "?intermNode <http://example.org/fixed#hasRating> ?rating"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?item");
            configuration.setRecEntity(RecEntity.RATING, "?rating");

            //This part is not relevant at all. The configuration won't be validated.
            /*
            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(4);
            */
            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recConfigList;
    }
    
    /**
     * Returns a single recommender configuration list with 1 item: 
     * VsmCfRecConfig.
     * This configuration fits the following datasets:
     * - fixed-likes.ttl
     * Configuration is single-domain (SD).
     * 
     * @return
     */
    public static ArrayList<RecConfig> getRecConfigListLikesSDFixed() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("likesSDConfig");

            configuration.setPosGraphPattern(
                    "?user <http://example.org/fixed#likes> ?item"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.POS_ITEM, "?item");

            //This part is not relevant at all. The configuration won't be validated.
            /*
            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(4);
            */
            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recConfigList;
    }
        
    /**
     * Returns a single recommender configuration list with 1 item: 
     * LinkAnalysisRecConfig.
     * This configuration fits the following datasets:
     * - fixed-likes-cd.ttl
     * Configuration is cross-domain (CD).
     * 
     * @return
     */    
    public static ArrayList<RecConfig> getRecConfigListLikesCDFixed() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            LinkAnalysisRecConfig configuration = new LinkAnalysisRecConfig("likesCDConfig");

            configuration.setPosGraphPattern(
                    "?user <http://example.org/fixed#likes> ?item"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.POS_ITEM, "?item");
            configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                    "?itemNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/fixed#Source>");
            configuration.setRecEntity(RecEntity.TARGET_DOMAIN, 
                    "?itemNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/fixed#Target>"); 

            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recConfigList;
    }
    
    /**
     * Returns a single recommender configuration list with 1 item: 
     * VsmCfRecConfig.
     * 
     * 
     * @return
     */
    /*
    public static ArrayList<RecConfig> getRecConfigListForFixedSingleDomainSRRWithLikes() {

        ArrayList<RecConfig> recConfigList = new ArrayList();

        try {
            VsmCfRecConfig configuration = new VsmCfRecConfig("Configuration for "
                    + "FixedSRRWithLikes and FixedSRRWithLikesAndNulls");

            configuration.setRatGraphPattern(
                    "?user <http://example.org/movies#likes> ?movie"
            );
            configuration.setRecEntity(RecEntity.USER, "?user");
            configuration.setRecEntity(RecEntity.RAT_ITEM, "?movie");

            //This part is not relevant at all. The configuration won't be validated.
            /*
            configuration.setRecParadigm(RecParadigm.USER_COLLABORATIVE_FILTERING);
            configuration.preprocessBeforeRecommending(true);
            configuration.setSimMetric(RecSimMetric.COSINE);
            configuration.setRecStorage(RecStorage.INVERTED_LISTS);
            configuration.setDecimalPlaces(3);
            configuration.setNeighborhoodSize(4);
            */
        /*
            recConfigList.add(configuration);

        } catch (RecommenderException ex) {
            Logger.getLogger(EvalTestRepositoryInstantiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recConfigList;
    }
    */        
    
    /**
     * Creates an Evaluator that is going to be used when testing.
     * 
     * @return 
     */
    public static Evaluator getEvaluator() {

        Evaluator eval = new AbstractEvaluator(new EvalDataManager(null,null)) {
            @Override
            public void evaluate(List<RecConfig> configurations) throws EvaluatorException {
                throw new UnsupportedOperationException("Should not be used.");
            }
        };
                
        return eval;
    }
}

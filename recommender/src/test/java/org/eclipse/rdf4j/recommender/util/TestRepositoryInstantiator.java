/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rdf4j.recommender.config.HybridRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEdgeDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecPriorsDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecSimMetric;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Static class that helps in creating repositories with different test datasets.
 * In this way all JUnit tests have a centralized access to these repositories.
 */
public final class TestRepositoryInstantiator {
        /**
         * Private constructor to have a static class behavior.
         */
        private TestRepositoryInstantiator() {};
        
        private static final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        
        /**
         * This dataset was making some trouble in the test suite of the evaluation
         * module, in the sense that results of the recommender changed every 
         * time the test case was executed
         */      
        public static SailRecommenderRepository createHybridRecommenderDataset1() {
                RepositoryConnection con = null;
                SailRecommenderRepository recRepository = null;
                try {
                        recRepository = new SailRecommenderRepository(
                            new MemoryStore());
                        recRepository.initialize();    
                        con = recRepository.getConnection();
                    
                        String resource = "testcases/moviesLikes.ttl";
                        String baseURI = "http://example.org/movies#";
                        System.out.println("IN Test Rep");
                        con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                        
                        HybridRecConfig configuration = new HybridRecConfig("config1");
                        configuration.setPosGraphPattern(
                                "?user <http://example.org/movies#hasLiked> ?movie "
                        );
                        configuration.setRecEntity(RecEntity.USER, "?user1");
                        configuration.setRecEntity(RecEntity.POS_ITEM, "?movie");

                        configuration.setRecParadigm(RecParadigm.HYBRID);
//                        configuration.preprocessBeforeRecommending(true);
//                        configuration.setSimMetric(RecSimMetric.COSINE);
//                        configuration.setRecStorage(RecStorage.INVERTED_LISTS);
//                        configuration.setDecimalPlaces(3);
//                        configuration.setNeighborhoodSize(4);

                        recRepository.loadRecConfiguration(configuration);
                } catch (RecommenderException ex) { 
                    System.out.println(ex.getMessage());
                } catch (IOException ex) { 
                    System.out.println(ex.getMessage());
                } catch (RDFParseException ex) {
                    System.out.println(ex.getMessage());
                } catch (RepositoryException ex) {
                    System.out.println(ex.getMessage());
                } 
                return recRepository;
        }

        
        
        /** Creates a repository with a Hybrid cross-domain based approach.
        * Dataset: Books and movies data
        * @return 
        */      
       public static SailRecommenderRepository createHybridRecommenderDataset() {
               RepositoryConnection con = null;
               SailRecommenderRepository recRepository = null;
               int numberOfDecimalPlaces = 3;
               try {
                       recRepository = new SailRecommenderRepository(
                           new MemoryStore());
                       recRepository.initialize();    
                       con = recRepository.getConnection();
                   
                       String resource = "testcases/merge_complete_level1.ttl";
//                       String baseURI = "http://example.org/data#";
                       String baseURI = "";
                        
                       con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                
                       //RECOMMENDATION                        
                       //One needs first to create a configuration for the recommender.
                       HybridRecConfig configuration = new HybridRecConfig("config1");

                       //NEW thing: 
                       //Set a recommendation configuration. Right now only one configuration is
                       //possible. In the future more configurations should be possible.
                       configuration.setPosGraphPattern(
                                   "?u <http://example.org/data#likes> ?o"
                       );

                       configuration.setRecEntity(RecEntity.USER, "?u");
                       configuration.setRecEntity(RecEntity.POS_ITEM, "?o");
                       configuration.setGraphOrientation(RecGraphOrientation.DIRECTED);
                       //TODO
                       //Modify this later
                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);

                       recRepository.loadRecConfiguration(configuration);
               } catch (RecommenderException ex) { 
                   System.out.println(ex.getMessage());
               } catch (IOException ex) { 
                   System.out.println(ex.getMessage());
               } catch (RDFParseException ex) {
                   System.out.println(ex.getMessage());
               } catch (RepositoryException ex) {
                   System.out.println(ex.getMessage());
               } 
               return recRepository;
       }
       
       /** Creates a repository with a Hybrid cross-domain based approach with precomputed Doc2Vec and Rdf2Vec.
        * Dataset: Books and movies data, precomputed Doc2Vec and Rdf2Vec
        * @return 
        */      
       public static SailRecommenderRepository createHybridRecommenderDatasetPreComputed() {
               
    	   	   RepositoryConnection con = null;
               SailRecommenderRepository recRepository = null;
               int numberOfDecimalPlaces = 3;
               try {
                       recRepository = new SailRecommenderRepository(
                           new MemoryStore());
                       recRepository.initialize();    
                       con = recRepository.getConnection();
                   
                       String resource = "testcases/merge_complete_level1.ttl";
//                       String baseURI = "http://example.org/data#";
                       String baseURI = "";
                        
                       con.add(classLoader.getResource(resource), baseURI, RDFFormat.TURTLE);
                
                       //RECOMMENDATION                        
                       //One needs first to create a configuration for the recommender.
                       HybridRecConfig configuration = new HybridRecConfig("config1");

                       //NEW thing: 
                       //Set a recommendation configuration. Right now only one configuration is
                       //possible. In the future more configurations should be possible.
                       configuration.setPosGraphPattern(
                                   "?u <http://example.org/data#likes> ?o"
                       );

                       configuration.setRecEntity(RecEntity.USER, "?u");
                       configuration.setRecEntity(RecEntity.POS_ITEM, "?o");
                       configuration.setGraphOrientation(RecGraphOrientation.DIRECTED);
                       //TODO
                       //Modify this later
                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                       
                       //configuration.computeDoc2Vec();
                       //configuration.computeRdf2Vec();
                       //configuration.doc2VecInputPath("input_abstract.csv");
                       //configuration.rdf2VecInputPath("rdf2vec_model");
                       
                       configuration.doc2VecOutputPath("doc2vec_embeddings.csv");
                       configuration.rdf2VecOutputPath("rdf2vec_embeddings.csv");
                       //configuration.readUserEmbeddings("user_embeddings.csv");
                       
                       recRepository.loadRecConfiguration(configuration);
               } catch (RecommenderException ex) { 
                   System.out.println(ex.getMessage());
               } catch (IOException ex) { 
                   System.out.println(ex.getMessage());
               } catch (RDFParseException ex) {
                   System.out.println(ex.getMessage());
               } catch (RepositoryException ex) {
                   System.out.println(ex.getMessage());
               } 
               return recRepository;
       }
       
        
}

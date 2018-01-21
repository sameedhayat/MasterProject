/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universität Freiburg
 * Institut für Informatik
 */
package org.eclipse.rdf4j.recommender.util;

import java.io.IOException;

import org.eclipse.rdf4j.recommender.config.HybridRecConfig;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
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
   
       
       /** Creates a repository with a Hybrid cross-domain based approach by computing Doc2Vec and Rdf2Vec.
        * Dataset: Books and movies data, precomputed Doc2Vec and Rdf2Vec
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
                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                       
                       configuration.computeDoc2Vec("input_abstract.csv", "doc2vec_embeddings.csv");
                       configuration.computeRdf2Vec("rdf2vec_model", "rdf2vec_embeddings.csv");
                       configuration.doc2VecInputPath("input_abstract.csv");
                       configuration.rdf2VecInputPath("rdf2vec_model");
                       
                       configuration.loadDoc2VecEmbeddings("doc2vec_embeddings.csv");
                       configuration.loadRdf2VecEmbeddings("rdf2vec_embeddings.csv");
                       configuration.computeUserEmbeddings("user_embeddings.csv");
                       configuration.loadUserEmbeddings("user_embeddings.csv");
                       configuration.createMlInputFile("ml_training_data.csv");
                       configuration.trainTreeModel("ml_training_data.csv");
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
       
       
       /** Creates a repository with a Hybrid cross-domain based approach with only Doc2Vec embeddings
        * Dataset: Books and movies data, precomputed Doc2Vec and Rdf2Vec
        * @return 
        */      
       public static SailRecommenderRepository createHybridRecommenderOnlyDoc2Vec() {
               
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

                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                       
                       configuration.loadDoc2VecEmbeddings("doc2vec_embeddings.csv");
//                       configuration.loadRdf2VecEmbeddings("rdf2vec_embeddings.csv");
//                       configuration.computeUserEmbeddings("user_embeddings.csv");
                       configuration.loadUserEmbeddings("user_embeddings.csv");
                       configuration.createMlInputFile("ml_training_data.csv");
                       configuration.trainTreeModel("ml_training_data.csv");
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
       public static SailRecommenderRepository createHybridRecommenderOnlyRdf2Vec() {
               
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

                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                       
//                       configuration.loadDoc2VecEmbeddings("doc2vec_embeddings.csv");
                       configuration.loadRdf2VecEmbeddings("rdf2vec_embeddings.csv");
//                       configuration.computeUserEmbeddings("user_embeddings.csv");
                       configuration.loadUserEmbeddings("user_embeddings.csv");
                       configuration.createMlInputFile("ml_training_data.csv");
                       configuration.trainTreeModel("ml_training_data.csv");
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

                       configuration.setRecEntity(RecEntity.SOURCE_DOMAIN, 
                               "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film>");                        
                       configuration.setRecEntity(RecEntity.TARGET_DOMAIN,
                               "?t <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Book>");

                       configuration.setRecParadigm(RecParadigm.HYBRID);
                       configuration.setRecStorage(RecStorage.EXTERNAL_GRAPH);
                       
                       configuration.loadDoc2VecEmbeddings("doc2vec_embeddings.csv");
                       configuration.loadRdf2VecEmbeddings("rdf2vec_embeddings.csv");
                       configuration.computeUserEmbeddings("user_embeddings.csv");
                       configuration.createMlInputFile("ml_training_data.csv");
                       configuration.trainTreeModel("ml_training_data.csv");
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

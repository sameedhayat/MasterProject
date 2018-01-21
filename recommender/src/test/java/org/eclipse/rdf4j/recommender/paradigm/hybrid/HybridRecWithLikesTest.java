package org.eclipse.rdf4j.recommender.paradigm.hybrid;


import java.io.IOException;

import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.junit.Test;

public class HybridRecWithLikesTest {
	
	private static final double DELTA = 1e-3;
	

    /**
     * HybridRecommender implementation using precomputed embeddings
     * 
     * Dataset: cross domain data
     */
	@Test
    public void HybridRecommenderPreComputed() throws RecommenderException, IOException {
            System.out.println("Loading rep");
            
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderDatasetPreComputed();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            String sourceUri = "http://example.org/data#u144199";
            String targetUri = "http://dbpedia.org/resource/Homecoming_(novel)";
            
            Double prediction = graphStorage.predictRating(graphStorage.getIndexOf(sourceUri), graphStorage.getIndexOf(targetUri));
            System.out.println(prediction);
	}
	
	/**
     * HybridRecommender implementation by computing embeddings
     * 
     * Dataset: cross domain data
     */
	@Test
    public void HybridRecommender() throws RecommenderException, IOException {
            System.out.println("Loading rep");
            
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderDataset();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            String sourceUri = "http://example.org/data#u144199";
            String targetUri = "http://dbpedia.org/resource/Homecoming_(novel)";
            
            Double prediction = graphStorage.predictRating(graphStorage.getIndexOf(sourceUri), graphStorage.getIndexOf(targetUri));
            System.out.println(prediction);

      }  
	
	/**
     * HybridRecommender implementation by only using Doc2Vec embeddings
     * 
     * Dataset: cross domain data
     */
	@Test
    public void HybridRecommenderOnlyDoc2Vec() throws RecommenderException, IOException {
            System.out.println("Loading rep");
            
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderOnlyDoc2Vec();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            String sourceUri = "http://example.org/data#u144199";
            String targetUri = "http://dbpedia.org/resource/Homecoming_(novel)";
            
            Double prediction = graphStorage.predictRating(graphStorage.getIndexOf(sourceUri), graphStorage.getIndexOf(targetUri));
            System.out.println(prediction);

      }  
	
	/**
     * HybridRecommender implementation by using only Rdf2Vec embeddings
     * 
     * Dataset: cross domain data
     */
	@Test
    public void HybridRecommenderOnlyRdf2Vec() throws RecommenderException, IOException {
            System.out.println("Loading rep");
            
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderOnlyRdf2Vec();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            String sourceUri = "http://example.org/data#u144199";
            String targetUri = "http://dbpedia.org/resource/Homecoming_(novel)";
            
            Double prediction = graphStorage.predictRating(graphStorage.getIndexOf(sourceUri), graphStorage.getIndexOf(targetUri));
            System.out.println(prediction);

      }  
	

}
	


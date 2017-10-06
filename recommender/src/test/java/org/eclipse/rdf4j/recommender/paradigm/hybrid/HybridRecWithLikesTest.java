package org.eclipse.rdf4j.recommender.paradigm.hybrid;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import org.javatuples.Pair;
import org.javatuples.Quintet;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.CsvWriterAppend;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.junit.Assert;
import org.junit.Test;

import nlp.word2vec.DocModel;
import nlp.word2vec.Word2VecModel;

public class HybridRecWithLikesTest {
	
	private static final double DELTA = 1e-3;
//	@Test
//    public void testContent() throws RecommenderException {
////    	SailRecommenderRepository recRepository = 
////                TestRepositoryInstantiator.createHybridRecommenderDataset();
////        JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
////                ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
////        
////        int sourceId = graphStorage.getIndexOf("http://example.org/data#u39040");
////        int targetId = graphStorage.getIndexOf("http://dbpedia.org/resource/Burned_(Hopkins_novel)");
////        
////        System.out.println(graphStorage.contentBasedSubject(sourceId, targetId));
////        Assert.assertEquals(2.772588722239781, graphStorage.contentBasedSubject(sourceId, targetId), DELTA); 
////        
//	}
	
	
	@Test
    public void loadModelAndCalculateSimilarity() throws RecommenderException, FileNotFoundException {
            System.out.println("Loading rep");
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderDataset();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            //get all the users indexes
            Set<Integer> allUserIndexes = graphStorage.getAllUserIndexes();
//            int sourceId = graphStorage.getIndexOf("http://example.org/data#u96328");
//            int targetId = graphStorage.getIndexOf("http://dbpedia.org/resource/Burned_(Hopkins_novel)");
            String inputPath = "input_abstract.csv";
            DocModel docModel = new DocModel();
            docModel.trainDoc2VecModel(inputPath);
           // int sourceId = graphStorage.getIndexOf("http://example.org/data#u39040");
//            List<Pair<Double, Double>> pList = graphStorage.Doc2VecRating(sourceId, docModel);
            
             Word2VecModel w2v = new Word2VecModel();
             String modelPath = "rdf2vec_model";
             w2v.readWord2VecModel(modelPath);
            
	    for(Integer u: allUserIndexes){            
            List<Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>> pList = graphStorage.getAllFeatures(u, w2v, docModel);
            CsvWriterAppend.appendCsv("ml_training_data.csv", pList);

}
//            for(Quintet<Double, Double, Double, Double, Integer> p: pList) {
//            	String s = p.getValue0() + "," + p.getValue1() + p.getValue3() + "," + p.getValue4();
//                
//            }
//            List<Pair<Double, Double>> pList = graphStorage.Word2VecRating(sourceId, w2v);
//            CsvWriterAppend.appendCsv("ml_training_data_w2v.csv", pList);
//            for(Pair<Double, Double> p: pList) {
////            	String s = p.getValue0() + "," + p.getValue1();
//            	
//          }
            
//           graphStorage.RDFToVecRating(sourceId, targetId, vec);                           
    }  
	
	
//	@Test
//    public void testTopKRecommendationsBasedOnTFIDF() throws RecommenderException {
//            
//            SailRecommenderRepository recRepository = 
//                    TestRepositoryInstantiator.createHybridRecommenderDataset();
//
//            //We rank the objects according to the predictions (we assume 
//            //these are correct.
//            RatedResource[] expectedTopK = new RatedResource[3];
//            expectedTopK[0] = new RatedResource("http://dbpedia.org/resource/The_Lorax", 1.0);
//            expectedTopK[1] = new RatedResource("http://dbpedia.org/resource/Agnes_Grey", 0.8281444907572746);
//            expectedTopK[2] = new RatedResource("http://dbpedia.org/resource/Green_Eggs_and_Ham", 0.8281444907572746);
//            
//            //First we test the method that returns a top-5 list of recommendations
//            //In the data set there are only five items.
//            RatedResource[] actualtopK 
//                    = recRepository.getTopRecommendations("http://example.org/data#u39040", 3, true);
//            
//            for (int i = 0; i < actualtopK.length; i++) {
//                    if (actualtopK[i] != null) {
//                            Assert.assertEquals(expectedTopK[i].getResource(), 
//                                    actualtopK[i].getResource());
//                            Assert.assertEquals(expectedTopK[i].getRating(), 
//                                    actualtopK[i].getRating(), DELTA);                                                
//                    } else {
//                            Assert.assertNull(expectedTopK[i]);
//                            Assert.assertNull(actualtopK[i]);
//                    }                        
//            }
//            Assert.assertEquals(expectedTopK.length, actualtopK.length);                              
//    }   


}
	


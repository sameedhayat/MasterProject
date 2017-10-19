package org.eclipse.rdf4j.recommender.paradigm.hybrid;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.CsvWriterAppend;
import org.eclipse.rdf4j.recommender.util.TestRepositoryInstantiator;
import org.junit.Test;

import nlp.word2vec.DocModel;
import nlp.word2vec.TreeModel;
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
    public void loadModelAndCalculateSimilarity() throws RecommenderException, IOException {
            System.out.println("Loading rep");
            
            SailRecommenderRepository recRepository = 
                    TestRepositoryInstantiator.createHybridRecommenderDatasetPreComputed();

            //We rank the objects according to the predictions (we assume 
            //these are correct.
            JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
                    ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
            
            String sourceUri = "http://example.org/data#u96328";
            String targetUri = "http://dbpedia.org/resource/Harry_Potter";
//            for(int user: graphStorage.getusersEmbeddingsAverageHashMap()) {
//            	for(int t: graphStorage.getTargetNodes()) {
//                	if(graphStorage.getLabel(user, t) == "Like") {
//                		System.out.println("Like Found:" + user + " " +  t);
//                	}
//            }
//            }
            System.out.println(recRepository.predictRating(sourceUri, targetUri));
            System.out.println("Done");
//            if(graphStorage.getAllUserIndexes().contains(graphStorage.getIndexOf(sourceUri))) {
//            	System.out.println("------Contains-------");
//            }
//            for(int t: graphStorage.getTargetNodes()) {
//            	if(graphStorage.getLabel(graphStorage.getIndexOf(sourceUri), t) == "Like") {
//            		System.out.println("Like Found:" + sourceUri + " " +  graphStorage.getIndexOf(targetUri));
//            	}
//            System.out.println(graphStorage.getLabel(graphStorage.getIndexOf(sourceUri), graphStorage.getIndexOf(targetUri)));
//            }
            //graphStorage.computeUsersEmbeddingsAverage();
            //graphStorage.writeUsersEmbeddingsAverage("user_embeddings.csv");
            
            //graphStorage.printEmbeddings();
            //Use Doc2Vec Model and save the embeddings for source and targer in csv file
            /*            
            String inputPath = "input_abstract.csv";
            DocModel docModel = new DocModel();
            docModel.trainDoc2VecModel(inputPath);
            // int sourceId = graphStorage.getIndexOf("http://example.org/data#u39040");
            
            
            HashMap<String,List<Double>> hm = graphStorage.sourceDoc2Vec(docModel);
            CsvWriterAppend.csvHashMap("doc2vec_source.csv",hm);
            
            hm = graphStorage.targetDoc2Vec(docModel);
            CsvWriterAppend.csvHashMap("doc2vec_target.csv",hm);
            
            Word2VecModel w2v = new Word2VecModel();
            String modelPath = "rdf2vec_model";
            w2v.readWord2VecModel(modelPath);
            
            HashMap<String,List<Double>> hm = graphStorage.sourceRdf2Vec(w2v);
            CsvWriterAppend.csvHashMap("rdf2vec_source.csv",hm);
            
            hm = graphStorage.targetRdf2Vec(w2v);
            CsvWriterAppend.csvHashMap("rdf2vec_target.csv",hm);
            */            
            
            
            /*
            //get all the users indexes
            Set<Integer> allUserIndexes = graphStorage.getAllUserIndexes();
//            int sourceId = graphStorage.getIndexOf("http://example.org/data#u96328");
//            int targetId = graphStorage.getIndexOf("http://dbpedia.org/resource/Burned_(Hopkins_novel)");
            String inputPath = "input_abstract.csv";
            DocModel docModel = new DocModel();
            docModel.trainDoc2VecModel(inputPath);
           // int sourceId = graphStorage.getIndexOf("http://example.org/data#u39040");
//            List<Pair<Double, Double>> pList = graphStorage.Doc2VecRating(sourceId, docModel);
            
             
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
    */}  
	
	
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
	


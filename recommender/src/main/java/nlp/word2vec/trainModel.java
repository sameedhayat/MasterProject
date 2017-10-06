package nlp.word2vec;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.eclipse.rdf4j.recommender.paradigm.hybrid.HybridRecommender;
import org.eclipse.rdf4j.recommender.repository.SailRecommenderRepository;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
public class trainModel {

    public static void main(String[] args) throws Exception {
    	
    	
//    	 SailRecommenderRepository recRepository = 
//                 TestRepositoryInstantiator.createHybridRecommenderDataset();
//
//         //We rank the objects according to the predictions (we assume 
//         //these are correct.
//         
//         JungGraphIndexBasedStorage graphStorage = (JungGraphIndexBasedStorage)
//                 ((HybridRecommender)recRepository.getRecommender()).getDataManager().getStorage();
//          
//         int sourceId = graphStorage.getIndexOf("http://example.org/data#u39040");
//         int targetId = graphStorage.getIndexOf("http://dbpedia.org/resource/Burned_(Hopkins_novel)");
//         
         
         String modelPath = "target/trained_model.txt";
     	Word2Vec vec = WordVectorSerializer.readWord2VecModel(modelPath);
         
        //graphStorage.RDFToVecRating(sourceId, targetId, vec);             
    	
    	
    	
////    	String modelPath = new ClassPathResource("trained_model.txt").getFile().getAbsolutePath();
//	String modelPath = "target/trained_model.txt";
////      MultiLayerNetwork restored = ModelSerializer.restoreMultiLayerNetwork(modelPath);
//      
//      //WordVectorSerializer.loadFullModel(modelPath);
// //     File modelFile = new ClassPathResource("trained_model.txt").getFile();
//       Word2Vec vec = WordVectorSerializer.readWord2VecModel(modelPath);
       Collection<String> lst = vec.wordsNearest("dbr:Rocky", 20);
       System.out.println("10 Words closest to 'dbr:Rocky': " + lst);

//      Word2Vec vec = WordVectorSerializer.readWord2VecModel(modelFile);
//      Word2Vec vec = WordVectorSerializer.readWord2VecModel(modelFile, true);
      //WordVectorSerializer.loadGoogleModel(modelFile, true);
      
        // Gets Path to Text file
//         String filePath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();

//         log.info("Load & Vectorize Sentences....");
//         // Strip white space before and after for each line
//         SentenceIterator iter = new BasicLineIterator(filePath);
//         // Split on white spaces in the line to get words
//         TokenizerFactory t = new DefaultTokenizerFactory();

//         /*
//             CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
//             So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
//             Additionally it forces lower case for all tokens.
//          */
//         t.setTokenPreProcessor(new CommonPreprocessor());
        
        

//         log.info("Building model....");
//         vec = new Word2Vec.Builder()
//                 .minWordFrequency(5)
//                 .iterations(1)
//                 .layerSize(100)
//                 .seed(42)
//                 .windowSize(5)
//                 .iterate(iter)
//                 .tokenizerFactory(t)
//                 .build();

//         log.info("Fitting Word2Vec model....");
//         vec.fit();

//         log.info("Writing word vectors to text file....");

//         // Write word vectors to file
//         WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");
        
        

//         // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
//         log.info("Closest Words:");
//         Collection<String> lst = vec.wordsNearest("school", 10);
//         System.out.println("10 Words closest to 'day': " + lst);

//         // TODO resolve missing UiServer
// //        UiServer server = UiServer.getInstance();
     System.out.println("Trained Model End");
    }
}

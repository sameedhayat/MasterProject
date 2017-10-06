package nlp.word2vec;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
public class DocModel {
	
	private ParagraphVectors vec;    
	/**
     * Sets the size of the neighborhood (collaborative methods).
     * @param size 
	 * @throws FileNotFoundException 
     */
    public void trainDoc2VecModel(String inputPath) throws FileNotFoundException {
    	
        File file = new File(inputPath);
        SentenceIterator iter = new BasicLineIterator(file);
        AbstractCache<VocabWord> cache = new AbstractCache<>();

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());


        LabelsSource source = new LabelsSource("DOC_");

        vec = new ParagraphVectors.Builder()
                .minWordFrequency(1)
                .iterations(5)
                .epochs(1)
                .layerSize(200)
                .learningRate(0.025)
                .labelsSource(source)
                .windowSize(5)
                .iterate(iter)
                .trainWordVectors(false)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .sampling(0)
                .build();

        vec.fit();
    }
    
    public List<Double> inferVector(String abstractText) {
	
//	System.out.println("--------" + abstractText + "--------");
    	INDArray r = vec.inferVector(abstractText);

    	List<Double> ret = convertINDArraytoArray(r);
    	return ret;
    }
    
    public List<Double> convertINDArraytoArray(INDArray arr) {
    	List<Double> ret = new ArrayList<Double>();
    	INDArray arr1 = arr.get(NDArrayIndex.point(0), NDArrayIndex.all());
    	for(int i=0 ; i<arr1.length(); i ++) {
    		ret.add(i, arr.getDouble(i));
    	}
    	return ret;
    }
    
    public double cosineSimilarityDoc2Vec(String abstractText1, String abstractText2) {
    	INDArray v1 = vec.inferVector(abstractText1);
    	INDArray v2 = vec.inferVector(abstractText2);
    	
    	double ret = Transforms.cosineSim(v1, v2);
    	return ret;
    }
    
        
    
    
    

}

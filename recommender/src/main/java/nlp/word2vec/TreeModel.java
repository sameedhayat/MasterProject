package nlp.word2vec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

public class TreeModel {

	private J48 tree;    
	/**
     * Sets the size of the neighborhood (collaborative methods).
     * @param size 
	 * @throws IOException 
     */
    public void readData(String inputPath,String outputPath) throws IOException {
		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.noHeaderRowPresentTipText();
		loader.setSource(new File(inputPath));
		Instances data = loader.getDataSet();//get instances object
		//      // save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);//set the dataset we want to convert
		//and save as ARFF
		saver.setFile(new File(outputPath));
		saver.writeBatch();
    }
    
    public void loadDataAndTrain(String inputPath, double bias) throws Exception {
    	//load datasets
		DataSource source = new DataSource(inputPath);
		Instances dataset = source.getDataSet();	
		dataset.setClassIndex(dataset.numAttributes()-1);
		
		
		final Resample filter = new Resample();
		Instances filteredIns = null;
		filter.setBiasToUniformClass(1.0);
		try {
			filter.setInputFormat(dataset);
			filter.setNoReplacement(false);
			filter.setSampleSizePercent(100);
			
			filteredIns = Filter.useFilter(dataset, filter);
		} catch (Exception e) {
			System.out.println("Error when resampling input data!");
			e.printStackTrace();
		}
			Random rand = new Random(1);
			filteredIns.randomize(rand);
		
		
		int trainSize = (int) Math.round(filteredIns.numInstances() * 1.0);
		System.out.println("Train Size : " + trainSize);
		Instances train = new Instances(filteredIns, 0, trainSize);
		//set class index to the last attribute
		train.setClassIndex(train.numAttributes()-1);
		//create and build the classifier!
		J48 tree = new J48();
		tree.buildClassifier(train);
    }
    
    public void predict(String inputPath, double bias) throws Exception {
    	String n = "Hello";
    }
    
    
    	
    
    

}

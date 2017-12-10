package nlp.word2vec;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import weka.filters.unsupervised.attribute.Add;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.core.SerializationHelper;
import weka.filters.supervised.instance.SMOTE;

public class TreeModel {

	private AdaBoostM1 model;
	private Classifier trainedModel;
//	private CostSensitiveClassifier model;
	/**
     * Sets the size of the neighborhood (collaborative methods).
     * @param size 
	 * @throws IOException 
     */
    public void readData(String inputPath,String outputPath) throws IOException {
		// load CSV
    	System.out.println("Reading data:" + inputPath);
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputPath));
		loader.setFieldSeparator(",");
		loader.setNoHeaderRowPresent(true);
		
//		String[] options = new String[1]; 
//		options[0] = "-H";
//		try {
//			loader.setOptions(options);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Instances data = loader.getDataSet();//get instances object
		//      // save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);//set the dataset we want to convert
		//and save as ARFF
		saver.setFile(new File(outputPath));
		saver.writeBatch();
		System.out.println("Converted to " + outputPath);
    }
    
    public void loadTrainedModel(String path) {
    	try {
			trainedModel = (Classifier) SerializationHelper.read(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public void loadDataAndTrain(String inputPath) throws Exception {
    	//load datasets
    	System.out.println("Loading data:" + inputPath);
		DataSource source = new DataSource(inputPath);
		Instances dataset = source.getDataSet();	
		dataset.setClassIndex(dataset.numAttributes()-1);
		
		
//		final Resample filter = new Resample();
//		Instances filteredIns = null;
//		filter.setBiasToUniformClass(1);	
//		try {
//			filter.setInputFormat(dataset);
//			filter.setNoReplacement(false);
//			filter.setSampleSizePercent(100);
//			
//			filteredIns = Filter.useFilter(dataset, filter);
//		} catch (Exception e) {
//			System.out.println("Error when resampling input data!");
//			e.printStackTrace();
//		}
//			Random rand = new Random(1);
//			filteredIns.randomize(rand);
		
		SpreadSubsample ff = new SpreadSubsample();
		ff.setInputFormat(dataset);
		ff.setDistributionSpread(1);
		Instances filteredIns = Filter.useFilter(dataset, ff);

		
//		double percentage = 1000;
//		SMOTE smote = new SMOTE();
//		smote.setClassValue("1");
//		smote.setNearestNeighbors(5);
//		smote.setPercentage(percentage);
//		smote.setRandomSeed(1);
//		smote.setInputFormat(dataset);
//		Instances filteredIns = Filter.useFilter(dataset, smote);
		
		int trainSize = (int) Math.round(filteredIns.numInstances() * 1.0);
		System.out.println("Train Size : " + trainSize);
		Instances train = new Instances(filteredIns, 0, trainSize);
		//set class index to the last attribute
		train.setClassIndex(train.numAttributes()-1);
		//create and build the classifier!
		//AdaBoost .. 
//		AdaBoostM1 classifier_ada = new AdaBoostM1();
//		classifier_ada.setClassifier(new DecisionStump());//needs one base-classifier
		model = new AdaBoostM1();
		RandomForest rf = new RandomForest();
		model.setNumIterations(100);
		model.setClassifier(rf);
		
		
//		model = new CostSensitiveClassifier();
//        CostMatrix costMatrix = new CostMatrix(2);
////      costMatrix.setCell(0, 0, 0.8d);
//        //          costMatrix.setCell(0, 1, 5.0d);
//        costMatrix.setCell(1, 0, 2d);
//        
//        model.setClassifier(classifier_ada);
//        model.setCostMatrix(costMatrix);
        model.buildClassifier(train);
//        SerializationHelper.write("trained_full.model", model);
        
//		model.buildClassifier(train);
//		tree.buildClassifier(train);
    }
    
    public double predict(String inputPath) throws Exception {
    	
    	CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputPath));
		loader.setFieldSeparator(",");
		loader.setNoHeaderRowPresent(true);
		
		Instances data = loader.getDataSet();//get instances object
		//      // save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);//set the dataset we want to convert
		//and save as ARFF
		saver.setFile(new File("tmp.arff"));
		saver.writeBatch();
		DataSource source = new DataSource("tmp.arff");
		Instances dataset = source.getDataSet();
		
		Add filter;
        filter = new Add();
        filter.setAttributeIndex("last");
        filter.setNominalLabels("Dislike,Like");
        filter.setAttributeName("class");
        filter.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, filter);
        dataset.setClassIndex(dataset.numAttributes()-1);
		dataset.get(0).setClassValue('?');
		
		Instance predicationDataSet = dataset.get(0);
		
		double[] predictionDistribution = 
        		model.distributionForInstance(predicationDataSet);
		
		String predictedClassLabel =
        		predicationDataSet.classAttribute().value((int) 1);
		
		if(predictedClassLabel == "Like") {
			return predictionDistribution[1];
		}else {
			return predictionDistribution[0];
		}
    }
    
    /*
    public double predict1(List<Double> data) throws Exception {
    	Instance instance = new Ins
    	for(int i = 0; i < 800; i++) {
    		Attribute position = new Attribute("attr" + i, data.get(i).toString());
    	}
    	// Create numeric attributes "length" and "weight" 
    	
    	Attribute weight = new Attribute("weight"); 

    	// Create vector to hold nominal values "first", "second", "third" 
    	
    	my_nominal_values.addElement("first"); 
    	my_nominal_values.addElement("second"); 
    	my_nominal_values.addElement("third"); 

    	// Create nominal attribute "position" 
    	Attribute position = new Attribute("position", my_nominal_values); 


		DataSource source = new DataSource(inputPath);
		Instances dataset = source.getDataSet();	
		return tree.classifyInstance(dataset.get(0));
    }
    */

    
    

}

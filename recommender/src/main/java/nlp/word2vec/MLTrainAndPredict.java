package nlp.word2vec;

import java.io.File;
import java.io.IOException;
import weka.filters.unsupervised.attribute.Add;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;

public class MLTrainAndPredict {
	private MLModel mlModel;
	private Classifier trainedModel;

	public abstract class MLModel {
		/**
		 * Gets distribution for Instance
		 * 
		 * @param predicationDataSet : training instances
		 * @throws Exception
		 */
		public abstract double[] getDistributionForInstance(Instance predicationDataSet) throws Exception;

		/**
		 * Sets ML model and build classifier upon training instances
		 * 
		 * @param train : training instances
		 * @param modelName : model to train instances upon
		 * @throws Exception
		 */
		public abstract void setMLModel(Instances trainingInstances) throws Exception;
	}

	public class AdaBoostModel extends MLModel {
		private AdaBoostM1 adaBoostM1;

		public AdaBoostModel() {
			adaBoostM1 = new AdaBoostM1();
		}

		@Override
		public double[] getDistributionForInstance(Instance predicationDataSet) throws Exception {
			return adaBoostM1.distributionForInstance(predicationDataSet);
		}

		@Override
		public void setMLModel(Instances trainingInstances) throws Exception {
			RandomForest rf = new RandomForest();
			rf.setNumIterations(100);
			adaBoostM1.setNumIterations(100);
			adaBoostM1.setClassifier(rf);
			adaBoostM1.buildClassifier(trainingInstances);
		}
	}

	public class MultiLayerPerceptronModel extends MLModel {
		private MultilayerPerceptron multilayerPerceptron;

		public MultiLayerPerceptronModel() {
			multilayerPerceptron = new MultilayerPerceptron();
		}

		@Override
		public double[] getDistributionForInstance(Instance predicationDataSet) throws Exception {
			return multilayerPerceptron.distributionForInstance(predicationDataSet);
		}

		@Override
		public void setMLModel(Instances trainingInstances) throws Exception {
			multilayerPerceptron.setLearningRate(0.1);
			multilayerPerceptron.setMomentum(0.2);
			multilayerPerceptron.setHiddenLayers("2");
			multilayerPerceptron.buildClassifier(trainingInstances);
		}
	}

	/**
	 * Reads ML input training data from input CSV file and converts into ARFF
	 * format for WEKA to process
	 * 
	 * @param inputPath : path of input CSV file
	 * @param outputPath : path of output ARFF file
	 * @throws IOException
	 */
	public void readData(String inputPath, String outputPath) throws IOException {
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputPath));
		loader.setFieldSeparator(",");
		loader.setNoHeaderRowPresent(true);
		Instances data = loader.getDataSet();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(outputPath));
		saver.writeBatch();
	}

	/**
	 * Loads input training data and train our chosen model on it
	 * 
	 * @param inputPath : path of input data source
	 * @throws Exception
	 */
	public void loadDataAndTrain(String inputPath) throws Exception {
		DataSource source = new DataSource(inputPath);
		Instances dataset = source.getDataSet();
		dataset.setClassIndex(dataset.numAttributes() - 1);
		SpreadSubsample ff = new SpreadSubsample();
		ff.setInputFormat(dataset);
		ff.setDistributionSpread(1);
		Instances filteredIns = Filter.useFilter(dataset, ff);
		int trainSize = (int) Math.round(filteredIns.numInstances() * 1.0);
		Instances train = new Instances(filteredIns, 0, trainSize);
		// set class index to the last attribute
		train.setClassIndex(train.numAttributes() - 1);
		mlModel = new AdaBoostModel();
		mlModel.setMLModel(train);
	}

	/**
	 * ML prediction function
	 * 
	 * @param train : training instances
	 * @param modelName : model to train instances upon
	 * @throws Exception
	 */
	public double predict(String inputPath) throws Exception {

		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(inputPath));
		loader.setFieldSeparator(",");
		loader.setNoHeaderRowPresent(true);

		Instances data = loader.getDataSet();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
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
		dataset.setClassIndex(dataset.numAttributes() - 1);
		dataset.get(0).setClassValue('?');

		Instance predicationDataSet = dataset.get(0);
		double[] predictionDistribution = mlModel.getDistributionForInstance(predicationDataSet);
		String predictedClassLabel = predicationDataSet.classAttribute().value((int) 1);

		if (predictedClassLabel == "Like") {
			return predictionDistribution[1];
		} else {
			return predictionDistribution[0];
		}
	}

	public void loadTrainedModel(String path) {
		try {
			trainedModel = (Classifier) SerializationHelper.read(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
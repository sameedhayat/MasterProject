package org.eclipse.rdf4j.recommender.config;

import java.io.IOException;

import org.eclipse.rdf4j.recommender.datamanager.DataManager;
import org.eclipse.rdf4j.recommender.datamanager.impl.GraphBasedDataManager;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecStorage;

import nlp.word2vec.MLTrainAndPredict;

public class HybridRecConfig extends CrossDomainRecConfig {
	 /*--------------*
		 * Constructors *
		 *--------------*/
	
	
	private RecGraphOrientation graphOrientation = RecGraphOrientation.DIRECTED; //default
	
	//Path to doc2vec model file
	private String doc2VecInputPath = "";
	
	//Path to rdf2vec model file
	private String rdf2VecInputPath = "";
	
	//Path to doc2vec csv file
	private String doc2VecOutputPath = "";
		
	//Path to rdf2vec csv file
	private String rdf2VecOutputPath = "";
	
	//Path to userEmbedding path csv file
	private String userEmbeddingsPath = "";
	
	//Path to userEmbedding path csv file
	private String mlInputFilePath = "";
	
	//Whether to compute Doc2Vec or its precomputed
	private Boolean computeDoc2Vec = false;
	
	//Whether to compute Rdf2Vec or its precomputed
	private Boolean computeRdf2Vec = false;
	
	//Whether to compute user embeddings or its precomputed
	private Boolean computerUserEmbeddings = false;
	
	private Boolean trainTreeModel = false;
	//Whether to compute user embeddings or its precomputed
	
	      
	        public HybridRecConfig (String configName) {
	            super(configName);
	        }
	        
	        public HybridRecConfig (HybridRecConfig config) {
	                super(config);
	        }
	        
	        
		    public RecGraphOrientation getGraphOrientation() {
	                return graphOrientation;
	        }
	
	        public void setGraphOrientation(RecGraphOrientation graphOrientation) {
	                this.graphOrientation = graphOrientation;
	        }    
	        
	        /**
	         * get doc2vec output csv file path
	         */
	    	public String getDoc2VecOutputPath() {
                return doc2VecOutputPath;
	        }
	    	
	    	/**
	         * get rdf2vec output csv file path
	         */
	    	public String getRdf2VecOutputPath() {
                return rdf2VecOutputPath;
	        }
	    	
	    	/**
	         * get doc2vec input file for training
	         */
	    	public String getDoc2VecInputPath() {
                return doc2VecInputPath;
	        }
	    	
	    	/**
	         * get rdf2vec trained model path
	         */
	    	public String getRdf2VecInputPath() {
                return rdf2VecInputPath;
	        }
	    	
	    	public Boolean getComputeDoc2Vec() {
                return computeDoc2Vec;
	        }
	    	public Boolean getComputeRdf2Vec() {
                return computeRdf2Vec;
	        }
	        
	    	/**
	         * load Doc2Vec embeddings from csv file 
	         * @param path : Path to Doc2Vec csv file
	         */
	    	public void loadDoc2VecEmbeddings(String path) {
                this.doc2VecOutputPath = path;
	        }
	        
	    	/**
	         * load Rdf2Vec embeddings from csv file 
	         * @param path : Path to Rdf2Vec csv file
	         */
	        public void loadRdf2VecEmbeddings(String path) {
                this.rdf2VecOutputPath = path;
	        }
	        
	        /**
	         * load Doc2Vec training file 
	         * @param path : Path to Doc2Vec input file
	         */
	    	public void doc2VecInputPath(String path) {
                this.doc2VecInputPath = path;
	        }
	        
	    	/**
	         * load Rdf2Vec model file 
	         * @param path : Path to Rdf2Vec model file
	         */
	        public void rdf2VecInputPath(String path) {
                this.rdf2VecInputPath = path;
	        }
	        
	        
	        /**
	         * Whether to compute Doc2Vec or its precomputed
	         * @param inputPath : Path to Doc2Vec training file
	         * @param outputPath : Path to Doc2Vec embedding file
	         */
	        public void computeDoc2Vec(String inputPath, String outputPath) {
	        	this.computeDoc2Vec = true;
                this.doc2VecInputPath = inputPath;
                this.doc2VecOutputPath = outputPath;
	        }
	        
	        /**
	         * Whether to compute Rdf2Vec or its precomputed
	         * @param inputPath : Path to Rdf2Vec model file
	         * @param outputPath : Path to Rdf2Vec embedding file
	         */
	        public void computeRdf2Vec(String inputPath, String outputPath) {
	        	this.computeRdf2Vec = true;
	        	this.rdf2VecInputPath = inputPath;
	        	this.rdf2VecOutputPath = outputPath;
	        }
	        
	        public void loadUserEmbeddings(String path) {
	        	this.userEmbeddingsPath = path;
	        }
	        
	        /**
	         * Whether to compute user embeddings or its precomputed
	         * @param path : Path to user embedding
	         */
	        public void computeUserEmbeddings(String path) {
	        	this.computerUserEmbeddings = true;
	        	this.userEmbeddingsPath = path;
	        }
	        
	        public Boolean getComputeUserEmbeddings() {
	        	return computerUserEmbeddings;
	        }
	        
	        public String getUserEmbeddingsPath() {
	        	return userEmbeddingsPath;
	        }
	        
	        public void createMlInputFile(String path) {
	        	this.mlInputFilePath = path;
	        }
	        
	        public String getMlInputFile() {
	        	return mlInputFilePath;
	        }
	        
	        /**
	         * Train ML model
	         * @param inputPath : path to input file
	         */
	        public void trainTreeModel(String inputPath) {
	        	this.trainTreeModel = true;
	        	this.mlInputFilePath = inputPath;
	        }
	        
	        public Boolean getTrainTreeModel() {
	        	return trainTreeModel;
	        }
	        
	        @Override
	        public DataManager validateConfiguration() throws RecommenderException{
	                super.validateConfiguration();
	                
	                if (getRecStorage() != RecStorage.EXTERNAL_GRAPH) {
	                        throw new RecommenderException("THIS CONFIGURATION DOES NOT SUPPORT CHOSEN RECOMMENDATION'S STORAGE");
	                }
	                
	                return new GraphBasedDataManager(this);
	        }
}

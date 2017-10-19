/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage.index.graph.impl;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.storage.GraphBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.AbstractIndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.CsvWriterAppend;
import org.eclipse.rdf4j.recommender.util.ListOperations;
import org.eclipse.rdf4j.recommender.util.TFIDF;
import org.eclipse.rdf4j.recommender.util.VectorOperations;
import org.javatuples.Pair;
import org.javatuples.Quintet;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.KStepMarkov;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import nlp.word2vec.DocModel;
import nlp.word2vec.TreeModel;
import nlp.word2vec.Word2VecModel;

/**
 * Class that maintains an internal graph using the JUNG java library. 
 */
public class JungGraphIndexBasedStorage extends AbstractIndexBasedStorage 
        implements GraphBasedStorage {
    
        /*--------*
	 * Fields *
	 *--------*/
		private Set<Integer> sourceResources = new HashSet<Integer>();
        private Set<Integer> targetResources = new HashSet<Integer>();
        
        private HashMap<String,List<Double>> doc2vecEmbeddingsHashMap = new HashMap<String,List<Double>>();
        private HashMap<String,List<Double>> rdf2vecEmbeddingsHashMap = new HashMap<String,List<Double>>();
        
        private HashMap<Integer,List<Double>> usersEmbeddingsAverageHashMap = new HashMap<Integer,List<Double>>();
        private TreeModel treeModel = new TreeModel();
    	
        /**
         * The complete graph (the RDF graph translated to this specific model)
         */       
        private Graph<Integer, String> jungCompleteGraph = new DirectedSparseGraph<Integer, String>();
        
        /**
         * Set of seeds used to compute the relative importance in the network
         * for one user. It is emptied each time that the centrality is computed.
         */
        private Set<Integer> seedsSet = new HashSet<Integer>();
        
        /**
         * The Id of the user for which the subgraph jungUserGraph was built the
         * last.
         */
        private int lastUserNodeId = -1;
            
        /**
         * Graph related to a user. This is built at query time, i.e. when 
         * computeKStepMarkovCentrality() is invoked.
         */       
        private Graph<Integer, String> jungUserGraph = new DirectedSparseGraph<Integer, String>();
        
        /**
         * Ranker from which can obtain a set of ranked entities after computing
         * the KStepMarkov approach.
         */
        private KStepMarkov<Integer, String> ksmRanker = null;
        
        
        private PageRankWithPriors<Integer, String> prrRanker = null;
        
        /**
         * It counts for each predicate the number of occurrences in the graph.
         */
        private HashMap<String, Integer> predicatesOcurrences = new HashMap<String, Integer>();
        
        /**
         * The key is the ID a node ID of an item rated by a user.
         */
        private HashMap<Integer, Set<Integer>> reachabilityTwoHops = new HashMap<Integer, Set<Integer>>();       
        
        private int numberOfTriples = 0;
        
        private double currentMaxRanking = -1.0;
        
        /*---------*
	 * Methods *
	 *---------*/
        
        @Override
        public void addNode(String URI) {
                createIndex(URI);
                jungCompleteGraph.addVertex(getIndexOf(URI));
        }
        
        @Override
        public void setSourceNode(String URI) {
                sourceResources.add(getIndexOf(URI));
        }
        
        @Override
        public void setTargetNode(String URI) {
                targetResources.add(getIndexOf(URI));
        }
        
        @Override
        public Set<Integer> getSourceNodes() {
                return sourceResources;
        }
        
        @Override
        public Set<Integer> getTargetNodes() {
                return targetResources;
        }
        //Get Doc2Vec embedding
        public List<Double> getDoc2VecEmbedding(int id) {
        	return doc2vecEmbeddingsHashMap.get(getURI(id));
        }
        
        //Get Rdf2Vec embedding
        public List<Double> getRdf2VecEmbedding(int id) {
        	return rdf2vecEmbeddingsHashMap.get(getURI(id));
        }
        
        //Get user embedding
        public Set<Integer> getusersEmbeddingsAverageHashMap() {
        	return usersEmbeddingsAverageHashMap.keySet();
        }
        
        @Override
        public void addEdge(String sourceURI, String targetURI, String predicateURI) {
                createIndex(sourceURI);
                createIndex(targetURI);
                if (predicateURI != null) {
                        jungCompleteGraph.addEdge(getIndexOf(sourceURI) + "->" + 
                                predicateURI + "->" + getIndexOf(targetURI), 
                                getIndexOf(sourceURI), getIndexOf(targetURI), EdgeType.DIRECTED); 
                        if (predicatesOcurrences.containsKey(predicateURI)) {
                                predicatesOcurrences.put(predicateURI, predicatesOcurrences.get(predicateURI) + 1);
                        } else  predicatesOcurrences.put(predicateURI, 1);
                        numberOfTriples++;
                } else {
                        jungCompleteGraph.addEdge(getIndexOf(sourceURI) + "->" + getIndexOf(targetURI), 
                                getIndexOf(sourceURI), getIndexOf(targetURI), EdgeType.DIRECTED);
                        numberOfTriples++;
                }                
        }
        
        @Override
        public void storeReachability(int node1, int node2, int numberOfHops) {
                if (reachabilityTwoHops.containsKey(node1)) {
                        Set<Integer> reachableNodes =  reachabilityTwoHops.get(node1);
                        if (!reachableNodes.contains(node2)) {
                                Set<List<String>> paths =  findAllPaths(node1, node2, numberOfHops);
                                if (!paths.isEmpty()) {
                                    reachableNodes.add(node2);
                                    reachabilityTwoHops.put(node1, reachableNodes);
                                    
                                }                                                                
                        }
                }
                else {
                        Set<List<String>> paths =  findAllPaths(node1, node2, numberOfHops);
                        if (!paths.isEmpty()) {
                                Set<Integer> reachableNodes = new HashSet<Integer>();
                                reachableNodes.add(node2); 
                                reachabilityTwoHops.put(node1, reachableNodes);   
                        }
                }
        }
        
        @Override
        public Set<Integer> getAllReachableNodes(int sourceId, int numberOfHops) {
                //TODO do this using numberOfHops
                if (reachabilityTwoHops.containsKey(sourceId))
                        return reachabilityTwoHops.get(sourceId);
                return new HashSet<Integer>();
        }
        
        @Override
        public int numberOfTriples() {
                return numberOfTriples;
        }
        
        @Override
        public double getNormalizedInPredFreq(int nodeId, String predicateURI) {
                double totalNumOfTriplesInWhichNodeAppears = 
                        jungCompleteGraph.getInEdges(nodeId).size() + 
                        jungCompleteGraph.getOutEdges(nodeId).size();
                
                double numberOfTimesPredAppearsAsIn = 0;
                Collection<String> inEdges = jungCompleteGraph.getInEdges(nodeId);
                for (String inEdge: inEdges) {
                        if (inEdge.endsWith(predicateURI + "->" + nodeId))
                                numberOfTimesPredAppearsAsIn++;
                }                
                return numberOfTimesPredAppearsAsIn / 
                        totalNumOfTriplesInWhichNodeAppears;
        }
        
        @Override
        public double getNormalizedOutPredFreq(int nodeId, String predicateURI) {
                double totalNumOfTriplesInWhichNodeAppears = 
                        jungCompleteGraph.getInEdges(nodeId).size() + 
                        jungCompleteGraph.getOutEdges(nodeId).size();
                               
                double numberOfTimesPredAppearsAsOut = 0;
                Collection<String> outEdges = jungCompleteGraph.getOutEdges(nodeId);
                for (String outEdge: outEdges) {
                        if (outEdge.startsWith(nodeId + "->" + predicateURI))
                            
                                numberOfTimesPredAppearsAsOut++;
                }                
                return numberOfTimesPredAppearsAsOut / 
                        totalNumOfTriplesInWhichNodeAppears;
        }
        
        @Override
        public double getInvertedTripleFrequency(String predicateURI) {
                return Math.log(numberOfTriples / 
                        (double) predicatesOcurrences.get(predicateURI));
        }
        
        @Override
        public double getInPFITF(int nodeId, String predicateURI) {
                double pf = getNormalizedInPredFreq(nodeId, predicateURI);
                double itf = getInvertedTripleFrequency(predicateURI);
                return pf * itf;                
        }
        
        @Override
        public double getOutPFITF(int nodeId, String predicateURI) {
                double pf = getNormalizedOutPredFreq(nodeId, predicateURI);
                double itf = getInvertedTripleFrequency(predicateURI);
                return pf * itf;
        }
        
                
        @Override
        public double getPredicateInformativeness(String statement) {
                double predInf = 0.0;
                String[] predicateTokens = statement.split("->");
                
                int sourceId = Integer.parseInt(predicateTokens[0]);
                String predicateLabel = predicateTokens[1];
                int targetId = Integer.parseInt(predicateTokens[2]);
                
                double invTripleFrequency = getInvertedTripleFrequency(predicateLabel);
                
                predInf = (((getNormalizedOutPredFreq(sourceId, predicateLabel) * 
                        invTripleFrequency)) + 
                        ((getNormalizedInPredFreq(targetId, predicateLabel) * 
                        invTripleFrequency))) / 2.0;
                
                return predInf;
        }
        
        @Override
        public double getPathInformativeness(List<String> statementsPath) {
                double pathInf = 0.0;
                for (String statement: statementsPath) {
                        pathInf = pathInf + getPredicateInformativeness(statement);
                }
                return pathInf/(double)statementsPath.size();
        }
        
        //get user subjects from graph
        public List<List<String>> getUserSubjects(int userId) {
        	
        	Collection<Integer> userLikesSource = jungCompleteGraph.getNeighbors(userId);
        	userLikesSource.containsAll(getSourceNodes());
        	List<List<String>> userSubjects = new ArrayList<List<String>>();
        	
        	for (Integer ver: userLikesSource) {
        		List<String> tmp = new ArrayList<>();
        		
        		for (String edge :jungCompleteGraph.getOutEdges(ver)){
        			if(edge.contains("http://purl.org/dc/terms/subject")){
        				List<String> tokens = Arrays.asList(getURI(jungCompleteGraph.getDest(edge)).replace("http://dbpedia.org/resource/Category:", "").split("_")); 
        				tmp.addAll(tokens);
        			}
        		}
        		userSubjects.add(tmp);
        	}
        	return userSubjects;
        	
        }
       
        
      //get user subjects from graph
        public List<String> getTargetSubjects(int targetId) {
        	
        	Collection<String> targetNeighbours = jungCompleteGraph.getOutEdges(targetId);
        	List<String> targetSubjects = new ArrayList<>();
        	for (String edge: targetNeighbours) {
        		
        		if(edge.contains("http://purl.org/dc/terms/subject")){
        			List<String> tokens = Arrays.asList(getURI(jungCompleteGraph.getDest(edge)).replace("http://dbpedia.org/resource/Category:", "").split("_"));
        			targetSubjects.addAll(tokens);
    			}
        	}
        	
        	return targetSubjects;
        	
        }
        
        //content based subject TFIDF
        @Override
        public double contentBasedSubject(int userId, int targetId) {
        	List<List<String>> userSubjects = getUserSubjects(userId);
        	List<String> targetSubjects = getTargetSubjects(targetId);
        	
        	List<String> userSubjectsFlat = ListOperations.flatList(userSubjects);
        	
        	Set<String> userSubjectsDistinct = new HashSet<>(userSubjectsFlat);
        	Set<String> targetSubjectsDistinct = new HashSet<>(targetSubjects);

        	List<String> commonElements = ListOperations.commonElements(userSubjectsDistinct, targetSubjectsDistinct);
        	
        	TFIDF sourceTFIDF = new TFIDF(userSubjects);
        	Map<String, Double> sourceTfIdfMap = sourceTFIDF.allTfIdf(commonElements);
        	System.out.println(sourceTfIdfMap);
        	List<Double> sourceTfIdfScores = new ArrayList<Double>(sourceTFIDF.allTfIdf(commonElements).values());
        	double avg = ListOperations.getMax(sourceTfIdfScores);
        	return avg;
        }
        
        
        public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
            double dotProduct = 0.0;
            double normA = 0.0;
            double normB = 0.0;
            for (int i = 0; i < vectorA.length; i++) {
                dotProduct += vectorA[i] * vectorB[i];
                normA += Math.pow(vectorA[i], 2);
                normB += Math.pow(vectorB[i], 2);
            }   
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }
        
        
        
        public void RDFToVecRating(int userId, int targetId, Word2Vec vec) {
        	
        	Collection<Integer> userLikesSource = jungCompleteGraph.getNeighbors(userId);
        	userLikesSource.containsAll(getSourceNodes());
        	
        	Collection<String> userLikesURI = new ArrayList<String>();
        	
        	for(Integer userLike: userLikesSource){
        		userLikesURI.add(getURI(userLike));
        	}
        	
        	String targetURI = getURI(targetId);
	       	double[] targetNodeVector = vec.getWordVector(targetURI);
        	
        	for(String URI: userLikesURI){
        		double[] sourceNodeVector = vec.getWordVector(URI);
        			System.out.println(cosineSimilarity(targetNodeVector, sourceNodeVector));
        	}
        }
        @Override
        public void computeUsersEmbeddingsAverage() {
        	Set<Integer> allUserIndexes = getAllUserIndexes();
        	
        	for (Integer userId: allUserIndexes) {
        		
        		ArrayList<List<Double>> doc2vecSourceList = new ArrayList<List<Double>>();
            	ArrayList<List<Double>> rdf2vecSourceList = new ArrayList<List<Double>>();
            	
        		//items liked by user in the source domain
	        	Collection<Integer> userLikesSource = new HashSet(jungCompleteGraph.getNeighbors(userId));
	        	Set sourceNodes = new HashSet(getSourceNodes());
	        	userLikesSource.retainAll(sourceNodes);
	        	
	        	if(!userLikesSource.isEmpty()) {
	        		for(Integer s: userLikesSource) {
	        			if(getAbstract(s).isEmpty()) {
	        				continue;
	        			}
	        			doc2vecSourceList.add(doc2vecEmbeddingsHashMap.get(getURI(s)));
	        			rdf2vecSourceList.add(rdf2vecEmbeddingsHashMap.get(getURI(s)));
	        		}
	        		List<Double> tmp = new ArrayList<Double>();
	            	
	        		tmp.addAll(ListOperations.averageList(doc2vecSourceList));
	        		tmp.addAll(ListOperations.averageList(rdf2vecSourceList));
	        		usersEmbeddingsAverageHashMap.put(userId, tmp);
	        	}
        	}
        }
        public List<Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>> getAllFeatures(int userId, Word2VecModel w2v,DocModel d2v) {
        	List<Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>> ret = new ArrayList<Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>>();
        	
        	//items liked by user in the source domain
        	Collection<Integer> userLikesSource = new HashSet(jungCompleteGraph.getNeighbors(userId));
        	Set sourceNodes = new HashSet(getSourceNodes());
        	System.out.println(sourceNodes);
        	userLikesSource.retainAll(sourceNodes);
        	
        	//items liked by user in the target domain
        	Collection<Integer> userLikesTarget = new HashSet(jungCompleteGraph.getNeighbors(userId));
        	Set targetNodes = new HashSet(getTargetNodes());
        	userLikesTarget.retainAll(targetNodes);
        	
        	ArrayList<List<Double>> w2vSourceList = new ArrayList<List<Double>>();
        	ArrayList<List<Double>> r2vSourceList = new ArrayList<List<Double>>();
        	
        	Set<Integer> targetUris = getTargetNodes();
        	for(Integer targetUri: targetUris){
        		String targetUri_1 = getURI(targetUri).replace("http://dbpedia.org/resource/", "dbr:");
    			if(getAbstract(targetUri).isEmpty()){
				continue;
			}
        		List<Double> targetD2vVector = d2v.inferVector(getAbstract(targetUri));
                        System.out.println(targetUri_1);
        		List<Double> targetR2vVector = w2v.inferVector(targetUri_1);
        		
        		for(Integer userUri: userLikesSource){
				if(getURI(userUri)== null){
					continue;
				}
        			if(getAbstract(userUri).isEmpty()) {
        				continue;
        			}
        			String userUri_1 = getURI(userUri).replace("http://dbpedia.org/resource/", "dbr:");
        			System.out.println(userUri_1);
				List<Double> a = w2v.inferVector(userUri_1);
				if(a.isEmpty()){
					continue;
				}
                                w2vSourceList.add(a);
        			r2vSourceList.add(d2v.inferVector(getAbstract(userUri)));
	
        		}
        	    //check if the target item is liked by user
        		int label = (userLikesTarget.contains(targetUri) ? 1 : 0);
        		if(w2vSourceList.size() == 0 || w2vSourceList==null || r2vSourceList.size() ==0 || r2vSourceList==null ){
				continue;
			}
			List<Double> sourceW2vAverage = w2v.averageList(w2vSourceList);
        		List<Double> sourceR2vAverage = w2v.averageList(r2vSourceList);
        		Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer> r = new Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>(sourceW2vAverage, targetR2vVector,sourceR2vAverage, 
        				targetD2vVector, label);
        		
        		ret.add(r);
        	}
        	return ret;
        }
        
        
        public List<Pair<Double, Double>> Word2VecRating(int userId, Word2VecModel w2v) {
        	List<Pair<Double, Double>> ret = new ArrayList<Pair<Double,Double>>();
        	
        	Collection<Integer> userLikesSource = jungCompleteGraph.getNeighbors(userId);
        	userLikesSource.containsAll(getSourceNodes());
        	List<Double> cosineList = new ArrayList<Double>();
        	List<Double> w2vCosineList = new ArrayList<Double>();
        	Set<Integer> targetUris = getTargetNodes();
        	
        	for(Integer targetUri: targetUris){
        		for(Integer userUri: userLikesSource){
        			String userUri_1 = getURI(userUri).replace("http://dbpedia.org/resource/", "dbr:");
        			String targetUri_1 = getURI(targetUri).replace("http://dbpedia.org/resource/", "dbr:");
        			System.out.println(getURI(targetUri));
//        			w2vCosineList.add(w2v.cosineSimilarity(userUri_1, targetUri_1));
        		}
        		Pair<Double, Double> p = new Pair<Double, Double>(ListOperations.getAverage(w2vCosineList), 
						  ListOperations.getMax(w2vCosineList));
        		ret.add(p);
        		
        	}
        	return ret;
        }
        
        public String getLabel(Integer userId, Integer targetId) {
        	Collection<Integer> userLikesSource = jungCompleteGraph.getNeighbors(userId);
//        	System.out.println("user like sources:" + userLikesSource);
//        	System.out.println("Target item:" + targetId);
        	if(userLikesSource.contains(targetId)) {
        		return "Like";
        	}else {
        		return "Dislike";
        	}
        }
        
        public void printSource() {
        	System.out.println("--------Target nodes--------");
        	System.out.println(getTargetNodes());
        	System.out.println("--------Source nodes--------");
        	System.out.println(getSourceNodes());
        }
            
        
        @Override
        public void computeDoc2VecEmbeddings(DocModel vec) {
        	System.out.println("Computing Rdf2Vec Embeddings");
        	
        	for(Integer s :getSourceNodes()) {
        		if(getAbstract(s).isEmpty()) {
        			System.out.println(s);
    				continue;
    			}
        		List<Double> sourceD2vVector = vec.inferVector(getAbstract(s));
        		doc2vecEmbeddingsHashMap.put(getURI(s), sourceD2vVector);
        	}
        	
        	for(Integer t :getTargetNodes()) {
        		if(getAbstract(t).isEmpty()) {
        			System.out.println(t);
    				continue;
    			}
        		List<Double> targetD2vVector = vec.inferVector(getAbstract(t));
        		doc2vecEmbeddingsHashMap.put(getURI(t), targetD2vVector);
        	}
        	System.out.println("Doc2Vec Embeddings Computed");
        }
        
        @Override
        public void readDoc2VecEmbeddings(String path) {
        	
        	try {
        		System.out.println("Reading Doc2Vec Embeddings from CSV");
				doc2vecEmbeddingsHashMap = CsvWriterAppend.readCsvHashMap(path);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        @Override
        public void readRdf2VecEmbeddings(String path) {
        	try {
        		System.out.println("Reading Rdf2Vec Embeddings from CSV");
				rdf2vecEmbeddingsHashMap = CsvWriterAppend.readCsvHashMap(path);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        public void printEmbeddings() {
        	System.out.println("Printing Doc2Vec Embeddings from CSV");
        	for (String name: doc2vecEmbeddingsHashMap.keySet()){
        		String key =name.toString();
                String value = doc2vecEmbeddingsHashMap.get(name).toString();  
                System.out.println(key + ":" + value);  
        	}
        	
        	System.out.println("Printing Rdf2Vec Embeddings from CSV");
        	for (String name: rdf2vecEmbeddingsHashMap.keySet()){
                String key =name.toString();
                String value = doc2vecEmbeddingsHashMap.get(name).toString();  
                System.out.println(key + ":" + value);  
        	}
        }
        
        @Override
        public void computeRdf2VecEmbeddings(Word2VecModel vec) {
        	System.out.println("Computing Rdf2Vec Embeddings");
        	for(Integer s :getSourceNodes()) {
        		String tmpUri = getURI(s).replace("http://dbpedia.org/resource/", "dbr:");
        		List<Double> sourceRdf2Vector = vec.inferVector(tmpUri);
        		rdf2vecEmbeddingsHashMap.put(getURI(s), sourceRdf2Vector);
        	}
        	for(Integer t :getTargetNodes()) {
        		String tmpUri = getURI(t).replace("http://dbpedia.org/resource/", "dbr:");
        		List<Double> targetRdf2Vector = vec.inferVector(tmpUri);
        		rdf2vecEmbeddingsHashMap.put(getURI(t), targetRdf2Vector);
        	}
        	System.out.println("Rdf2Vec Embeddings Computed");
        }
        
        @Override
        public void writeRdf2VecEmbeddings(String path) {
        	System.out.println("Writing Rdf2Vec Embeddings");
        	CsvWriterAppend.writeCsvHashMap(path,rdf2vecEmbeddingsHashMap);
        }
        
        @Override
        public void writeDoc2VecEmbeddings(String path) {
        	System.out.println("Writing Doc2Vec Embeddings");
        	CsvWriterAppend.writeCsvHashMap(path, doc2vecEmbeddingsHashMap);
        }
        
        
        @Override
        public void writeUsersEmbeddingsAverage(String path) {
        	System.out.println("Writing User embeddings average");
        	CsvWriterAppend.writeCsvHashMapUser(path, usersEmbeddingsAverageHashMap);
        }
        
        @Override
        public void mlTrainingData(String path) {
        	for(Integer u: getusersEmbeddingsAverageHashMap()) {
        		HashMap<Integer,Pair<List<Double>,String>> ret = new HashMap<Integer,Pair<List<Double>,String>>();
        		for(Integer t: getTargetNodes()) {
        			if(getAbstract(t).isEmpty()) {
        				continue;
        			}
        			if(getLabel(u, t) == "Like") {
        				System.out.println("Liked Item:" + u+ " " + t);
        				System.out.println("Embedding:" + doc2vecEmbeddingsHashMap.get(getURI(t)));
        			}
        			List<Double> val = new ArrayList<Double>();
        			val.addAll(doc2vecEmbeddingsHashMap.get(getURI(t)));
	        		val.addAll(rdf2vecEmbeddingsHashMap.get(getURI(t)));
	        		val.addAll(usersEmbeddingsAverageHashMap.get(u));
	        		Pair<List<Double>,String> p = new Pair<List<Double>,String>(val,getLabel(u, t));
	        		ret.put(u,p);
        			}
        		CsvWriterAppend.writeMlData(path,ret);
        	}
        }
        
		
        
        public void trainTreeModel(String inputPath, String outputPath) {
        	try {
				treeModel.readData(inputPath, outputPath);
				treeModel.loadDataAndTrain(outputPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        @Override
        public double predictRating(Integer userId, Integer targetId) {
        	HashMap<Integer,List<Double>> ret = new HashMap<Integer,List<Double>>();
        	List<Double> val = new ArrayList<Double>();
			
        	val.addAll(doc2vecEmbeddingsHashMap.get(getURI(targetId)));
			val.addAll(doc2vecEmbeddingsHashMap.get(getURI(targetId)));
			val.addAll(usersEmbeddingsAverageHashMap.get(userId));
			List<Double> p = new ArrayList<Double>(val);
			ret.put(userId,p);
        	try {
				CsvWriterAppend.writeMlDataOneInstance("tmp.csv",ret);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	double pred = -1.0;
        	try {
        		pred = treeModel.predict("tmp.csv");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return pred;
        	
        	
        	
        }
        
        
        
        @Override
        public void readUsersEmbeddingsAverage(String path) {
        	System.out.println("Reading User embeddings average");
        	try {
        		usersEmbeddingsAverageHashMap = CsvWriterAppend.readCsvHashMapUser(path);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
        @Override
        public void trainModel(String path) {
        	System.out.println("Training ML Model");
        	try {
        		usersEmbeddingsAverageHashMap = CsvWriterAppend.readCsvHashMapUser(path);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
        public List<Pair<Double, Double>> Doc2VecRating(int userId, DocModel vec) {
        	List<Pair<Double, Double>> ret = new ArrayList<Pair<Double,Double>>();
        	
        	Collection<Integer> userLikesSource = jungCompleteGraph.getNeighbors(userId);
        	userLikesSource.containsAll(getSourceNodes());
        	List<Double> cosineList = new ArrayList<Double>();
        	
        	Set<Integer> targetUris = getTargetNodes();
        	
        	for(Integer targetUri: targetUris){
        		for(Integer userUri: userLikesSource){
        			if(getAbstract(userUri).isEmpty() || getAbstract(targetUri).isEmpty()) {
        				continue;
        			}
        			cosineList.add(vec.cosineSimilarityDoc2Vec(getAbstract(userUri), getAbstract(targetUri)));
        		}
        		Pair<Double, Double> p = new Pair<Double, Double>(ListOperations.getAverage(cosineList), 
        														  ListOperations.getMax(cosineList));
        		ret.add(p);
        	}
        	return ret;
        }
        
        //get user abstract from graph
        public String getAbstract(int userSource) {
        	String abs = "";
        	for (String edge :jungCompleteGraph.getOutEdges(userSource)){
    			if(edge.contains("http://dbpedia.org/ontology/abstract")){
    				abs = getURI(jungCompleteGraph.getDest(edge)); 
    			}
    		}
        	return abs;
        }
        
      //get user abstract from graph
        public String getAbstractTarget(int targetId) {
        	String abs = "";
        	for (String edge :jungCompleteGraph.getOutEdges(targetId)){
    			if(edge.contains("http://dbpedia.org/ontology/abstract")){
    				abs = getURI(jungCompleteGraph.getDest(edge)); 
    			}
    		}
        	return abs;
        }
        
        //Implemented very costly at the moment
        @Override
        public Set<List<String>> findAllPaths(int sourceId, int targetId, int reachability) {
                //all paths found
                Set<List<String>> allPaths = new HashSet<List<String>>();
                //collects path in one iteration
                Set<List<String>> newPathsCollector = new HashSet<List<String>>();
                //same as the solution but with inverted edges
                Set<List<String>> tmpPathsToTarget = new HashSet<List<String>>();
                //final solution
                Set<List<String>> pathsToTarget = new HashSet<List<String>>();
                
                String[] statementTokens = null;                
                Set<Integer> allProcessedNodes = new HashSet<Integer>(); //to avoid duplicates
                List<String> statementsFound = new ArrayList<String>();
                
                for (int i = 0; i < reachability; i++) {
                        if (i ==  0) {                                
                                Collection<String> inEdges = jungCompleteGraph.getInEdges(sourceId);
                                for (String inEdge: inEdges) {
                                        
                                        statementsFound = new ArrayList<String>();
                                        statementTokens = inEdge.split("->");
                                        int inNodeId = Integer.parseInt(statementTokens[0]);
                                        //allProcessedNodes.add(inNodeId); 
                                        //Incoming nodes are reversed
                                        statementsFound.add(new StringBuilder(inEdge).reverse().toString());
                                        allPaths.add(statementsFound);
                                        if (inNodeId == targetId) tmpPathsToTarget.add(statementsFound);
                                }
                                
                                Collection<String> outEdges = jungCompleteGraph.getOutEdges(sourceId);
                                
                                for (String outEdge: outEdges) {
                                    
                                        statementsFound = new ArrayList<String>();
                                        statementTokens = outEdge.split("->");
                                        int outNodeId = Integer.parseInt(statementTokens[2]);
                                        statementsFound.add(outEdge);
                                        allPaths.add(statementsFound);
                                        if (outNodeId == targetId) tmpPathsToTarget.add(statementsFound);
                                                                                
                                }
                                
                        } else {
                                newPathsCollector = new HashSet<List<String>>();

                                for (List<String> statements: allPaths) {
                                        allProcessedNodes = new HashSet<Integer>(); //to avoid duplicates
                                        int lastNodeInPath = 0;
                                        
                                        for (String predicate: statements) {
                                                if (predicate.contains(">-")) {
                                                        statementTokens = predicate.split(">-");
                                                        allProcessedNodes.add(Integer.parseInt(
                                                                new StringBuilder(statementTokens[0]).reverse().toString()));
                                                        allProcessedNodes.add(Integer.parseInt(
                                                                new StringBuilder(statementTokens[2]).reverse().toString()));
                                                        lastNodeInPath = Integer.parseInt(
                                                                new StringBuilder(statementTokens[2]).reverse().toString());
                                                } else {
                                                        statementTokens = predicate.split("->");   
                                                        allProcessedNodes.add(Integer.parseInt(statementTokens[0]));
                                                        allProcessedNodes.add(Integer.parseInt(statementTokens[2]));
                                                        lastNodeInPath = Integer.parseInt(statementTokens[2]);
                                                }
                                        }
                                        
                                        Collection<String> inEdges = jungCompleteGraph.getInEdges(lastNodeInPath);
                                        for (String inEdge: inEdges) {
                                            
                                                statementsFound = new ArrayList<String>();
                                                statementsFound.addAll(statements);
                                                statementTokens = inEdge.split("->");
                                                int inNodeId = Integer.parseInt(statementTokens[0]);
                                                if (allProcessedNodes.contains(inNodeId)) continue;
                                                
                                                //Incoming nodes are reversed                                                
                                                statementsFound.add(new StringBuilder(inEdge).reverse().toString());
                                                newPathsCollector.add(statementsFound);
                                                if (inNodeId == targetId) tmpPathsToTarget.add(statementsFound);
    
                                        }
                                        
                                        Collection<String> outEdges = jungCompleteGraph.getOutEdges(lastNodeInPath);
                                        for (String outEdge: outEdges) {
                                            
                                                statementsFound = new ArrayList<String>();
                                                statementsFound.addAll(statements);
                                                statementTokens = outEdge.split("->");
                                                int outNodeId = Integer.parseInt(statementTokens[2]);
                                                if (allProcessedNodes.contains(outNodeId)) continue;
                                              
                                                //Incoming nodes are reversed                                                
                                                statementsFound.add(outEdge);
                                                newPathsCollector.add(statementsFound);
                                                if (outNodeId == targetId) tmpPathsToTarget.add(statementsFound);
                                        }
                                }
                                allPaths.addAll(newPathsCollector);
                        }
                        
                        //System.out.println("*****End of iteration " + i);
                        //System.out.println("#SIZE OF allPaths: " + allPaths.size());
                        int numItems = 0;
                        for (List<String> currList: allPaths) {
                            numItems = numItems + currList.size();
                        }
                        //System.out.println("#NUMBER OF ELEMS OF ALL LISTS: " + numItems);
                        //System.out.println("#SIZE OF tmpPathsToTarget : " + tmpPathsToTarget.size());
                }
                
                //We need to reverse back all the predicates
                for (List<String> statements: tmpPathsToTarget) {        
                        List<String> fixedStatements =  new ArrayList<String>();                        
                        for (int i = 0; i < statements.size(); i++) {                            
                                String statement = statements.get(i);                                
                                if (statement.contains(">-")) {
                                        fixedStatements.add(new StringBuilder(statement).reverse().toString());
                                } else {
                                        fixedStatements.add(statement);
                                }                                
                        }
                        pathsToTarget.add(fixedStatements);
                }
                return pathsToTarget;
        }
        
        @Override
        public double computeRewordRelatedness(int nodeId1, int nodeId2) {
                Collection<String> inEdgesNode1 = jungCompleteGraph.getInEdges(nodeId1);
                Collection<String> inEdgesNode2 = jungCompleteGraph.getInEdges(nodeId2);
                Collection<String> outEdgesNode1 = jungCompleteGraph.getOutEdges(nodeId1);
                Collection<String> outEdgesNode2 = jungCompleteGraph.getOutEdges(nodeId2);
                //To compute cosine similarity
                double inNormNode1 = 0.0;
                double inNormNode2 = 0.0; 
                double outNormNode1 = 0.0;
                double outNormNode2 = 0.0;
                IndexedRatedRes[] inVectorNode1 = null;
                IndexedRatedRes[] inVectorNode2 = null;
                IndexedRatedRes[] outVectorNode1 = null;
                IndexedRatedRes[] outVectorNode2 = null;  
                Map<String, Integer> predIndexMap = new HashMap<String, Integer>();
                int predicateCounter = 0;
                
                //to represent the vectors
                Map<String, Double> inMapNode1 = new HashMap<String, Double>();
                for (String statement: inEdgesNode1) {
                        String[] predicateTokens = statement.split("->");
                        inMapNode1.put(predicateTokens[1], getInPFITF(nodeId1, predicateTokens[1]));
                }
                Map<String, Double> inMapNode2 = new HashMap<String, Double>();
                for (String statement: inEdgesNode2) {
                        String[] predicateTokens = statement.split("->");
                        inMapNode2.put(predicateTokens[1], getInPFITF(nodeId2, predicateTokens[1]));
                }
                Map<String, Double> outMapNode1 = new HashMap<String, Double>();
                for (String statement: outEdgesNode1) {
                        String[] predicateTokens = statement.split("->");
                        outMapNode1.put(predicateTokens[1], getOutPFITF(nodeId1, predicateTokens[1]));
                }
                Map<String, Double> outMapNode2 = new HashMap<String, Double>();
                for (String statement: outEdgesNode2) {
                        String[] predicateTokens = statement.split("->");
                        outMapNode2.put(predicateTokens[1], getOutPFITF(nodeId2, predicateTokens[1]));
                        System.out.println("getOutPFITF between " + nodeId2 + " and " + predicateTokens[1] + " = " + getOutPFITF(nodeId2, predicateTokens[1]));
                }
                
                //At the moment reachability is set to 3.
                Set<List<String>> allPathsBetweenNode1AndNode2 = findAllPaths(nodeId1, nodeId2, 3);
                List<String> maxInfPath = null;
                double maxInformativeness = Double.MIN_VALUE;
                for (List<String> currentPath: allPathsBetweenNode1AndNode2) {
                        double currentPathInf = getPathInformativeness(currentPath);
                        if (currentPathInf > maxInformativeness) {
                                maxInformativeness = currentPathInf;
                                maxInfPath = currentPath;
                        }
                }                                
                
                //We work on the path with the maximum informativeness
                for (String statement: maxInfPath) {
                        double predInf = getPredicateInformativeness(statement);
                        String[] predicateTokens = statement.split("->");
                        String predicateURI = predicateTokens[1];
                        
                        if (inMapNode1.containsKey(predicateURI)) {
                                double updatedScore = inMapNode1.get(predicateURI) + 
                                        predInf;
                                inMapNode1.put(predicateURI, updatedScore);
                        } else inMapNode1.put(predicateURI, predInf);
                        if (inMapNode2.containsKey(predicateURI)) {
                                double updatedScore = inMapNode2.get(predicateURI) + 
                                        predInf;
                                inMapNode2.put(predicateURI, updatedScore);
                        } else inMapNode2.put(predicateURI, predInf);
                        if (outMapNode1.containsKey(predicateURI)) {
                                double updatedScore = outMapNode1.get(predicateURI) + 
                                        predInf;
                                outMapNode1.put(predicateURI, updatedScore);
                        } else outMapNode1.put(predicateURI, predInf);
                        if (outMapNode2.containsKey(predicateURI)) {
                                double updatedScore = outMapNode2.get(predicateURI) + 
                                        predInf;
                                outMapNode2.put(predicateURI, updatedScore);
                        } else outMapNode2.put(predicateURI, predInf);
                }
                
                
                //We compute the norms of the four vectors and create indices for
                //the predicates.
                for (String predicate: inMapNode1.keySet()){
                        if (!predIndexMap.containsKey(predicate)) {
                                predIndexMap.put(predicate, predicateCounter);
                                predicateCounter++;
                        }
                        double pfitf = inMapNode1.get(predicate);
                        inNormNode1 = inNormNode1 + (pfitf * pfitf);
                }
                for (String predicate: inMapNode2.keySet()){
                        if (!predIndexMap.containsKey(predicate)) {
                                predIndexMap.put(predicate, predicateCounter);
                                predicateCounter++;
                        }
                        double pfitf = inMapNode2.get(predicate);
                        inNormNode2 = inNormNode2 + (pfitf * pfitf);
                }
                for (String predicate: outMapNode1.keySet()){
                        if (!predIndexMap.containsKey(predicate)) {
                                predIndexMap.put(predicate, predicateCounter);
                                predicateCounter++;
                        }
                        double pfitf = outMapNode1.get(predicate);
                        outNormNode1 = outNormNode1 + (pfitf * pfitf);
                }
                for (String predicate: outMapNode2.keySet()){
                        if (!predIndexMap.containsKey(predicate)) {
                                predIndexMap.put(predicate, predicateCounter);
                                predicateCounter++;
                        }
                        double pfitf = outMapNode2.get(predicate);
                        outNormNode2 = outNormNode2 + (pfitf * pfitf);
                }
                inNormNode1 = Math.sqrt(inNormNode1);
                inNormNode2 = Math.sqrt(inNormNode2);
                outNormNode1 = Math.sqrt(outNormNode1);
                outNormNode2 = Math.sqrt(outNormNode2);
                
                //Now we have to build the vectors
                inVectorNode1 = new IndexedRatedRes[inMapNode1.size()];
                inVectorNode2 = new IndexedRatedRes[inMapNode2.size()];
                outVectorNode1 = new IndexedRatedRes[outMapNode1.size()];
                outVectorNode2 = new IndexedRatedRes[outMapNode2.size()];               
                
                int index = 0;
                for (String predicate: inMapNode1.keySet()){
                        inVectorNode1[index] = new IndexedRatedRes(predIndexMap.get(predicate), 
                                inMapNode1.get(predicate)/inNormNode1);
                        index++;
                }
                index = 0;
                for (String predicate: inMapNode2.keySet()){
                        inVectorNode2[index] = new IndexedRatedRes(predIndexMap.get(predicate), 
                                inMapNode2.get(predicate)/inNormNode2);
                        index++;
                }
                index = 0;
                for (String predicate: outMapNode1.keySet()){
                        outVectorNode1[index] = new IndexedRatedRes(predIndexMap.get(predicate), 
                                outMapNode1.get(predicate)/outNormNode1);
                        index++;
                }
                index = 0;
                for (String predicate: outMapNode2.keySet()){
                        outVectorNode2[index] = new IndexedRatedRes(predIndexMap.get(predicate), 
                                outMapNode2.get(predicate)/outNormNode2);
                        index++;
                }
                //IndexedRatedRes has implements Comparable, i.e. it sorts by 
                //ID by default.
                Arrays.sort(inVectorNode1);
                Arrays.sort(inVectorNode2);
                Arrays.sort(outVectorNode1);
                Arrays.sort(outVectorNode2);               
                
                System.out.println("Node 1 In Vector");
                for (IndexedRatedRes irr:  inVectorNode1)
                        System.out.println(irr);
                System.out.println("Node 1 Out Vector");
                for (IndexedRatedRes irr:  outVectorNode1)
                        System.out.println(irr);
                
                System.out.println("Node 2 In Map");
                for (IndexedRatedRes irr:  inVectorNode2)
                        System.out.println(irr);
                System.out.println("Node 2 Out Map");
                for (IndexedRatedRes irr:  outVectorNode2)
                        System.out.println(irr); 
                
                System.out.println("In Sim: " + VectorOperations.computeSimilarityOfNormalizedSortedVectors(inVectorNode1, inVectorNode2));
                System.out.println("Out Sim: " + VectorOperations.computeSimilarityOfNormalizedSortedVectors(outVectorNode1, outVectorNode2));
                                                                                
                //Now we have to compute the pairwise cosine similarity
                //We reuse the method VectorOperations.computeSimilarityOfNormalizedSortedVectors
                //which has been tested.                                
                double inSimScore = VectorOperations.
                        computeSimilarityOfNormalizedSortedVectors(inVectorNode1, inVectorNode2);
                double outSimScore = VectorOperations.
                        computeSimilarityOfNormalizedSortedVectors(outVectorNode1, outVectorNode2);                               
                
                return (inSimScore + outSimScore) / 2;
        }                
          
        
        @Override
        public void pageRankWithPriorsUniform(int userId, int numMaxIterations, double alpha) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;
                     
                prrRanker = new PageRankWithPriors<Integer, String>(jungUserGraph, 
                        edges_uniform_distribution, priors_uniform_distribution, alpha);
                prrRanker.setMaxIterations(numMaxIterations);
                prrRanker.evaluate();
        }
        
        @Override
        public void ksmcUniform(int userId, int numberOfSteps, 
                        int numMaxIterations) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;                
                /*
                ksmRanker = new KStep
                
                Markov<Integer, String>(jungUserGraph, 
                        edge_weigths, vertex_rootSet, numberOfSteps);
                */       
                ksmRanker = new KStepMarkov<Integer, String>(jungUserGraph, 
                        edges_uniform_distribution, priors_uniform_distribution,
                        numberOfSteps);
                ksmRanker.setMaxIterations(numMaxIterations);
                ksmRanker.evaluate();                   
        }
        
        @Override
        public void pageRankWithPriorsUniformEdgesSumOne(int userId, int numMaxIterations, double alpha) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;
                     
                prrRanker = new PageRankWithPriors<Integer, String>(jungUserGraph, 
                        edges_outgoing_sum_one, priors_uniform_distribution, alpha);
                prrRanker.setMaxIterations(numMaxIterations);
                prrRanker.evaluate();
        }

        
        @Override
        public void ksmcUniformEdgesSumOne(int userId, int numberOfSteps, 
                        int numMaxIterations) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;                
                /*
                ksmRanker = new KStep
                
                Markov<Integer, String>(jungUserGraph, 
                        edge_weigths, vertex_rootSet, numberOfSteps);
                */       
                ksmRanker = new KStepMarkov<Integer, String>(jungUserGraph, 
                        edges_outgoing_sum_one, priors_uniform_distribution,
                        numberOfSteps);
                ksmRanker.setMaxIterations(numMaxIterations);
                ksmRanker.evaluate();                   
        }
        
        @Override
        public void pageRankWithPriorsLikesEdgesSumOne(int userId, int numMaxIterations, double alpha) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;
                     
                prrRanker = new PageRankWithPriors<Integer, String>(jungUserGraph, 
                        edges_outgoing_sum_one, priors_liked_items, alpha);
                prrRanker.setMaxIterations(numMaxIterations);
                prrRanker.evaluate();
        }

        
        @Override
        public void ksmcLikesEdgesSumOne(int userId, int numberOfSteps, 
                        int numMaxIterations) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;                
                /*
                ksmRanker = new KStep
                
                Markov<Integer, String>(jungUserGraph, 
                        edge_weigths, vertex_rootSet, numberOfSteps);
                */       
                ksmRanker = new KStepMarkov<Integer, String>(jungUserGraph, 
                        edges_outgoing_sum_one, priors_liked_items,
                        numberOfSteps);
                ksmRanker.setMaxIterations(numMaxIterations);
                ksmRanker.evaluate();                   
        }
        
        /*
        @Override
        public void pageRankWithPriorsUniform(int userId, int numMaxIterations, double alpha) {
                
                Set<IndexedRatedRes> ratedResSet = getIndexedRatedResOfUser(userId);
                
                computeUserSubgraph(userId, ratedResSet);
                currentMaxRanking = -1.0;
                     
                prrRanker = new PageRankWithPriors<Integer, String>(jungUserGraph, 
                        edge_weigths_uniform_distribution, priors_liked_items, alpha);
                prrRanker.setMaxIterations(numMaxIterations);
                prrRanker.evaluate();
        }
        */
        
        @Override
        public double getKsmVertexScore(int resId) {
                if (!jungUserGraph.containsVertex(resId)) return 0.0;
            
                if (ksmRanker == null)
                        return 0.0;
                if (currentMaxRanking == -1.0) {
                        for (Integer vertex: targetResources){
                                if (jungUserGraph.containsVertex(vertex) && 
                                        ksmRanker.getVertexScore(vertex) > currentMaxRanking) {
                                                currentMaxRanking = ksmRanker.getVertexScore(vertex);
                                }
                        }
                }                
                //return ksmRanker.getVertexScore(resId);
                return (ksmRanker.getVertexScore(resId) / currentMaxRanking);
        }
        
        @Override
        public double getPrpVertexScore(int resId) {
                if (!jungUserGraph.containsVertex(resId)) return 0.0;
            
                if (prrRanker == null)
                        return 0.0;
                if (currentMaxRanking == -1.0) {
                        for (Integer vertex: targetResources){
                                if (jungUserGraph.containsVertex(vertex) && 
                                        prrRanker.getVertexScore(vertex) > currentMaxRanking) {
                                                currentMaxRanking = prrRanker.getVertexScore(vertex);
                                }
                        }
                }                
                return (prrRanker.getVertexScore(resId) / currentMaxRanking);
        }
        
        @Override
        public Set<Integer> getSubgraphVertices(int nodeId) {
                Set<Integer> verticesSet = new HashSet<Integer>();
                
                if (nodeId != lastUserNodeId) {
                        computeUserSubgraph(nodeId, getIndexedRatedResOfUser(nodeId));
                }
                verticesSet.addAll(jungUserGraph.getVertices());
                return verticesSet;
        }                
        
        public void computeUserSubgraph(int userId, Set<IndexedRatedRes> ratedResSet) {
                this.lastUserNodeId = userId;
                seedsSet = new HashSet<Integer>();
                Set<Integer> processedNodes = new HashSet<Integer>();
                Set<Integer> nodesWithoutOutgoingEdges = new HashSet<Integer>();
                Set<Integer> newFoundNodes = new HashSet<Integer>();
                Set<Integer> nodesForNextIteration = new HashSet<Integer>();
                jungUserGraph = new DirectedSparseGraph<Integer, String>();
                
                //First we add the user node
                jungUserGraph.addVertex(userId);
               
                for(IndexedRatedRes rr: ratedResSet) {
                        seedsSet.add(rr.getResourceId());
                        processedNodes.add(rr.getResourceId());
                        //newFoundNodes.add(rr.getResourceId());
                        nodesForNextIteration.add(rr.getResourceId());
                        jungUserGraph.addVertex(rr.getResourceId());
                        //we connect the user with the resources he rated
                        //positively
                        jungUserGraph.addEdge(userId + "->" + rr.getResourceId(),
                                userId, rr.getResourceId(), EdgeType.DIRECTED);
                }
                
                int oldNumOfProcessedNodes = 0;
                int currentNumOfProcessedNodes = processedNodes.size();
                
                //Here we need to build a graph at runtime.
                //First we add the seeds and .                
                while(oldNumOfProcessedNodes < currentNumOfProcessedNodes) {
                        newFoundNodes.clear();
                        for(Integer currentNodeId: nodesForNextIteration) {
                                    if (!jungUserGraph.containsVertex(currentNodeId))
                                            jungUserGraph.addVertex(currentNodeId);
                                    
                                    Collection<String> seedOutEdges = jungCompleteGraph.getOutEdges(currentNodeId);
                                    if (seedOutEdges.isEmpty()) nodesWithoutOutgoingEdges.add(currentNodeId);
                                    for (String currentEdge: seedOutEdges) {
                                            if (!jungUserGraph.containsEdge(currentEdge)) {
                                            	int outNeighbor = Integer.parseInt(currentEdge.split("->")[2]);
                                                    
                                                newFoundNodes.add(outNeighbor);
                                                jungUserGraph.addVertex(outNeighbor);
                                                jungUserGraph.addEdge(currentEdge, 
                                                currentNodeId, outNeighbor, EdgeType.DIRECTED);                               
                                        }
                                    }                                                           
                        }
                        nodesForNextIteration.clear();
                        newFoundNodes.removeAll(processedNodes);
                        nodesForNextIteration.addAll(newFoundNodes);
                        processedNodes.addAll(newFoundNodes);
                        oldNumOfProcessedNodes = currentNumOfProcessedNodes;
                        currentNumOfProcessedNodes = processedNodes.size();
                }
                //Node without outgoing edges have to be reconnected to the user
                for (Integer vertex: nodesWithoutOutgoingEdges) {
                        jungUserGraph.addEdge(vertex + "->" + userId,
                                vertex, userId, EdgeType.DIRECTED);
                }
        }
        
        
        
                
        @Override
        public void resetStorage() {
                super.resetStorage();
                targetResources = new HashSet<Integer>();                
                jungCompleteGraph = new DirectedSparseGraph<Integer, String>();
                lastUserNodeId = -1;
                jungUserGraph = new DirectedSparseGraph<Integer, String>();
                seedsSet = new HashSet<Integer>();
                ksmRanker = null;
                prrRanker = null;
                predicatesOcurrences = new HashMap<String, Integer>();
                reachabilityTwoHops = new HashMap<Integer, Set<Integer>>();
                numberOfTriples = 0;
                currentMaxRanking = -1.0;
        }
        
        
        /**
         * Each vertex gets a weight equal to (1 / #vertices in the user's subgraph).
         */
        private final Transformer<Integer, Double> priors_uniform_distribution = 
                new Transformer<Integer, Double>() {    
                        @Override
                        public Double transform(Integer v) {
                                double numberOfVertices = jungUserGraph.getVertexCount();
                                if(jungUserGraph.containsVertex(v))
                                    return 1.0 / numberOfVertices;
                                else return 0.0;
                        }
                };
        
        /**
         * Each vertex liked by the user gets weight equal to 
         * (1 / # items liked by a user).
         */
        private final Transformer<Integer, Double> priors_liked_items = 
                new Transformer<Integer, Double>() {
                        @Override
                        public Double transform(Integer v) {
                                if(seedsSet.contains(v))
                                    return 1.0 / seedsSet.size();
                                else return 0.0;
                        }
                };   
        
        /**
         * Each edge gets a weight equal to (1 / #edges in the user's subgraph).
         */
        private final Transformer<String, Double> edges_uniform_distribution = 
                new Transformer<String, Double>() {
                        @Override
                        public Double transform(String e) {
                                double numberOfEdges = jungUserGraph.getEdgeCount();
                                if(jungUserGraph.containsEdge(e))
                                    return 1.0 / numberOfEdges;
                                else return 0.0;
                        }           
                };
        
        /**
         * Each edge gets a weight equal to 
         * (1 / #edges outgoing from the same source).
         */
        private final Transformer<String, Double> edges_outgoing_sum_one = 
                new Transformer<String, Double>() {
                        @Override
                        public Double transform(String e) {                                
                                if(jungUserGraph.containsEdge(e)) {
                                    Integer vertex = jungUserGraph.getSource(e);
                                    return 1.0 / jungUserGraph.outDegree(vertex);
                                }
                                else return 0.0;
                        }           
                };
        
        /**
         * Each edge gets a weight proportional to the informativeness with
         * respect to other edges outgoing from the same source.
         */
        private final Transformer<String, Double> edges_informativeness = 
                new Transformer<String, Double>() {
                        @Override
                        public Double transform(String e) {                     
                                if(jungUserGraph.containsEdge(e)) {
                                    //TODO
                                    return 0.0;
                                }
                                else return 0.0;
                        }
                };
        
        //FOR DEBUGGING PURPOSES
        public void visualizeGraph(Graph jungGraph) {
                // The Layout<V, E> is parameterized by the vertex and edge types
                Layout<Integer, String> layout = new CircleLayout<Integer, String>(jungGraph);
                layout.setSize(new Dimension(300,300)); // sets the initial size of the space
                // The BasicVisualizationServer<V,E> is parameterized by the edge types
                VisualizationViewer<Integer,String> vv =
                        new VisualizationViewer<Integer,String>(layout);
                vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
                vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
                vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
                vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
                DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
                gm.setMode(ModalGraphMouse.Mode.PICKING);
                vv.setGraphMouse(gm);

                JFrame frame = new JFrame("Simple Graph View");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(vv);
                frame.pack();
                frame.setVisible(true);
        }
        
        public Graph<Integer, String> getCompleteGraph() {
                return jungCompleteGraph;
        }
        
        public Graph<Integer, String> getUserGraph() {
                return jungUserGraph;
        }
        
        public KStepMarkov<Integer, String> getKsmVertexScorer() {
                return ksmRanker;
        }
        
        @Override
        public Map<String, Integer> getPredicateOccurrenciesMap() {
                return predicatesOcurrences;
        }        
}

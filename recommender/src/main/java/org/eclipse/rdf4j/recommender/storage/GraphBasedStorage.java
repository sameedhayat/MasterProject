/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.storage;

import edu.uci.ics.jung.graph.Graph;
import nlp.word2vec.DocModel;
import nlp.word2vec.TreeModel;
import nlp.word2vec.Word2VecModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface of an graph-based storage for a recommender.
 */
public interface GraphBasedStorage extends Storage {
        /**
         * Adds a node to the inner graph model.
         * @param URI 
         */
        public void addNode(String URI);
        
        /**
         * Sets one of the nodes as source of the recommendation.
         * @param URI 
         */
        public void setSourceNode(String URI);
        
        /**
         * Sets one of the nodes as target of the recommendation.
         * @param URI 
         */
        public void setTargetNode(String URI);
        
        /**
         * Get a set of all nodes marked as target.
         * @return 
         */
        public Set<Integer> getSourceNodes();        
        
        
        /**
         * Get a set of all nodes marked as target.
         * @return 
         */
        public Set<Integer> getTargetNodes();        
        
        /**
         * Adds an edge to the inner graph model. If the predicate is null then
         * a predicate is created on-the-fly by making a string out of the
         * source and target as follows: "source -> target"
         * 
         * @param sourceURI
         * @param targetURI
         * @param predicateURI 
         */
        public void addEdge(String sourceURI, String targetURI, String predicateURI);
        
        /**
         * If two nodes are reachable in a graph one can store this information.
         * If the graph is directed it is treated as undirected.
         * This method should help to speed up some of the methods. 
         * @param node1
         * @param node2 
         * @param numberOfHops 
         */
        public void storeReachability(int node1, int node2, int numberOfHops);
        
        /**
         * Allows to get all nodes reachable from a starting point "sourceId"
         * at distance at most numberOfHops.
         * @param sourceId
         * @param numberOfHops
         * @return 
         */
        public Set<Integer> getAllReachableNodes(int sourceId, int numberOfHops);
        
        /**
         * Returns the overall number of triples in the system.
         * @return 
         */
        public int numberOfTriples();
        
        /**
         * Get a normalized predicate frequency for a given resource and an
         * incoming predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getNormalizedInPredFreq(int nodeId, String predicateURI);
        
        /**
         * Get a normalized predicate frequency for a given resource and an
         * outgoing predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getNormalizedOutPredFreq(int nodeId, String predicateURI);
        
        /**
         * Returns the inverted triple frequency.
         * @param predicateURI
         * @return 
         */
        public double getInvertedTripleFrequency(String predicateURI);
        
        /**
         * Returns the incoming PFITF of a predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getInPFITF(int nodeId, String predicateURI);
        
        /**
         * Returns the outgoing PFITF of a predicate.
         * @param nodeId
         * @param predicateURI
         * @return 
         */
        public double getOutPFITF(int nodeId, String predicateURI);
        
        /**
         * To get the predicate informativeness.
         * @param statement
         * @return 
         */
        public double getPredicateInformativeness(String statement);
        
        /**
         * Path informativeness (builds upon predicate informativeness).
         * @param statementsPath
         * @return 
         */
        public double getPathInformativeness(List<String> statementsPath);
        
        /**
         * Finds all path from sourceId to targetId within a given reachability.
         * @param sourceId
         * @param targetId
         * @param reachability
         * @return
         */
        public Set<List<String>> findAllPaths(int sourceId, int targetId, int reachability);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: uniform distribution over all vertices.
         * Uniform distribution over all outgoing edges.          
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsUniform(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: uniform distribution over all vertices.
         * Uniform distribution over all outgoing edges.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcUniform(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: uniform distribution over all vertices.
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsUniformEdgesSumOne(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: uniform distribution over all vertices.
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcUniformEdgesSumOne(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        /**
         * Computes PageRank With Priors.
         * Vertex priors: the items the user liked. Each of these items gets the weight (1 / #items the user liked).
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numMaxIterations 
         * @param alpha 
         */
        public void pageRankWithPriorsLikesEdgesSumOne(int userId, int numMaxIterations,
                double alpha);
        
        /**
         * Computes K Step Markov Centrality.
         * Vertex priors: the items the user liked. Each of these items gets the weight (1 / #items the user liked).
         * The outgoing edge weights for each vertex will be equal and sum to 1.
         * @param userId
         * @param numberOfSteps
         * @param numMaxIterations
         */
        public void ksmcLikesEdgesSumOne(int userId, int numberOfSteps, 
                int numMaxIterations);
        
        
        /**
         * Computes content based subject.
         * @param nodeId1 user node
         * @param nodeId2 target node
         * @return 
         */
        public double contentBasedSubject(int nodeId1, int nodeId2);
        
        
        /**
         * Computes Doc2Vec Embeddings for all the source and the target nodes.
         * @param DocModel
         * @return Hashmap with uri as key and embeddings as value
         */
        public void computeDoc2VecEmbeddings(DocModel vec);
       
        /**
         * Computes Rdf2Vec Embeddings for all the target nodes.
         * @param Word2VecModel
         * @return Hashmap with uri as key and embeddings as value
         */
        public void computeRdf2VecEmbeddings(Word2VecModel vec);
        
        
        /**
         * Computes Rdf2Vec Embeddings for all the target nodes.
         * @param Word2VecModel
         * @return Hashmap with uri as key and embeddings as value
         */
        public void writeRdf2VecEmbeddings(String path);
        
        
        /**
         * Computes Rdf2Vec Embeddings for all the target nodes.
         * @param Word2VecModel
         * @return Hashmap with uri as key and embeddings as value
         */
        public void writeDoc2VecEmbeddings(String path);
        
        /**
         * Read Doc2Vec Embeddings from the csv file.
         * @param path to csv file
         */
        public void readDoc2VecEmbeddings(String path);
        
        
        /**
         * Computes Rdf2Vec Embeddings for the csv file.
         * @param path to csv file
         */
        public void readRdf2VecEmbeddings(String path);
        
        
        /**
         * computes users embeddings average to the csv file.
         * @param path to csv file
         */
        public void computeUsersEmbeddingsAverage();
        
        
        /**
         * read users embeddings average to the csv file.
         * @param path to csv file
         */
        public void readUsersEmbeddingsAverage(String path);
        
        /**
         * Combine ML data
         * @param path to csv file
         */
        public void mlTrainingData(String path);
        
        
        /**
         * Train model for prediction
         * @param path to csv file
         */
        public void trainModel(String path);
        
        
        /**
         * predict rating for one instance
         * @param path to csv file
         */
        public double predictRating(Integer userId, Integer targetId);
        
        
        
        /**
         * training data with cosine
         * @param path to csv file
         */
        public double predictRatingCosine(Integer userId, Integer targetId);
        
        
        /**
         * training data with cosine
         * @param path to csv file
         */
        public void mlTrainingDataCosine(String inputPath);
        
        
        
        /**
         * training data with cosine
         * @param path to csv file
         */
        public void createUserProfile();
         
        
        /**
         * train tree model
         * @param path to csv file
         */
        public void trainTreeModel(String inputPath, String outputPath);
            
        
        /**
         * saves users embeddings average to the csv file.
         * @param path to csv file
         */
        public void writeUsersEmbeddingsAverage(String path);
        
        /**
         * Computes Reword.
         * @param nodeId1
         * @param nodeId2
         * @return 
         */
        public double computeRewordRelatedness(int nodeId1, int nodeId2);
        
        /**
         * It returns the score of a node computed based on K Step Markov 
         * Centrality algorithm. 
         * The value returned should be a value between 0 and 1, i.e. the value
         * should be normalized according to the maximum value found among the
         * nodes in the target set.
         * @param resId
         * @return 
         */
        public double getKsmVertexScore(int resId);
        
        /**
         * It returns the score of a node computed based on PageRanking with
         * Priors algorithm.
         * The value returned should be a value between 0 and 1, i.e. the value
         * should be normalized according to the maximum value found among the
         * nodes in the target set.
         * @param resId
         * @return 
         */
        public double getPrpVertexScore(int resId);
        
        /**
         * Gets all vertices of a subgraph built from the complete graph from 
         * all nodes connected to the nodeId.
         * @param nodeId
         * @return 
         */
        public Set<Integer> getSubgraphVertices(int nodeId);
        
        
        //FOR DEBUGGING PURPOSES       
        public Map<String, Integer> getPredicateOccurrenciesMap();

}

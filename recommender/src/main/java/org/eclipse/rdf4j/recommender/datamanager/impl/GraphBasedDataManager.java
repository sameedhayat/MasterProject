/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datamanager.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.recommender.config.CrossDomainRecConfig;
import org.eclipse.rdf4j.recommender.config.HybridRecConfig;
import org.eclipse.rdf4j.recommender.config.LinkAnalysisRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.datamanager.AbstractIndexBasedDataManager;
import org.eclipse.rdf4j.recommender.datamanager.model.IndexedRatedRes;
import org.eclipse.rdf4j.recommender.datamanager.model.RatedResource;
import org.eclipse.rdf4j.recommender.exception.RecommenderException;
import org.eclipse.rdf4j.recommender.parameter.RecEdgeDistribution;
import org.eclipse.rdf4j.recommender.parameter.RecGraphOrientation;
import org.eclipse.rdf4j.recommender.parameter.RecParadigm;
import org.eclipse.rdf4j.recommender.parameter.RecPriorsDistribution;
import org.eclipse.rdf4j.recommender.storage.GraphBasedStorage;
import org.eclipse.rdf4j.recommender.storage.index.graph.impl.JungGraphIndexBasedStorage;
import org.eclipse.rdf4j.recommender.util.CsvWriterAppend;
import org.eclipse.rdf4j.repository.RepositoryException;

import nlp.word2vec.DocModel;
import nlp.word2vec.Word2VecModel;


public final class GraphBasedDataManager extends AbstractIndexBasedDataManager{
        /*--------*
	 * Fields *
	 *--------*/
        /**
         * The user for which recommendations where generated the last.
         */
        private String userOfLastRecommendation = "";


        /*-------------*
	 * Constructor *
	 *-------------*/  
        /**
         * The constructor has an important role. Depending on the configuration
         * it creates the appropriate storage.
         * @param config 
         */
        public GraphBasedDataManager(RecConfig config) {
                super(config);
                                
                switch(config.getRecParadigm()) {
                        case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: 
                        case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                        case HYBRID:
                                setStorage(new JungGraphIndexBasedStorage());
                        case CROSS_DOMAIN_REWORD:
                                setStorage(new JungGraphIndexBasedStorage());
                        break;
                }
        }                     

        @Override
        public void preprocessWithRatings() {
                //Do nothing for now
        }
        
        @Override
        public void preprocessWithoutRatings() {
                switch(getRecConfig().getRecParadigm()) {
                        case CROSS_DOMAIN_REWORD:
                                //Here for each user I will store which items are reachable from
                                //items liked by the user in the past to the candidate items
                                //First we get all users:
                                Set<Integer> allUsersIndexes = getStorage().getAllUserIndexes();
                                int i = 0;
                                for (Integer indexOfUser: allUsersIndexes) {
                                        Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(indexOfUser);
                                        for (IndexedRatedRes irr: irrSet) {
                                                for (int targetIndex: ((GraphBasedStorage)getStorage()).getTargetNodes()) {
                                                        ((GraphBasedStorage)getStorage()).
                                                                storeReachability(irr.getResourceId(), targetIndex, 2);
                                                }
                                        }
                                }
                        break;
                }
        }
        
        @Override
        public void populateStorage() throws RecommenderException {
                super.populateStorage();

                try {                
                        String subjectRes = null;
                        String objectRes = null;
                        String predicateRes = null;
                        String sourceRes = null;
                        String targetRes = null;

                        TupleQuery tupleQuery = null;
                        TupleQueryResult result = null;       
                        String preprocessingSPARQLQuery = null;
                        
                        Pattern pat = null;
                        Matcher mat = null;
                        String sourceItemVar = "";
                        String targetItemVar = "";

                        //I am querying the RDF model using the repository connection 
                        //to build the graph, processing RDF resources nodes and predicates
                        //and build the Jung graph.
                        /*
                        preprocessingSPARQLQuery =
                        "SELECT * "
                        + "WHERE {\n"
                        +       config.getRatGraphPattern()
                        + "}";  
                        */

                        //The complete RDF model.
                        preprocessingSPARQLQuery = 
                                "SELECT * "
                                + "WHERE {\n"
                                +       "?s ?p ?o"
                                + "}";

                        tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
                                preprocessingSPARQLQuery);

                        result = tupleQuery.evaluate();

                        if (result.hasNext() == false)
                            throw new RecommenderException("GRAPH PATTERN IS NOT MATCHING TO ANYTHING");

                        switch(getRecConfig().getRecParadigm()) {
                                case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                                        while (result.hasNext()) {
                                                BindingSet bs = result.next();
                                                //We can access each of the variables configured:
                                                subjectRes = bs.getValue("s").stringValue();
                                                objectRes = bs.getValue("o").stringValue();

                                                //we store the resources found and build the new graph model
                                                ((GraphBasedStorage)getStorage()).addNode(subjectRes);
                                                ((GraphBasedStorage)getStorage()).addNode(objectRes);
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.DIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, null);                                                
                                                } else if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.UNDIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, null); 
                                                        ((GraphBasedStorage)getStorage()).addEdge(objectRes, subjectRes, null);
                                                }
                                        }
                                break;
                                case CROSS_DOMAIN_REWORD:
                                        while (result.hasNext()) {
                                                BindingSet bs = result.next();
                                                //We can access each of the variables configured:
                                                subjectRes = bs.getValue("s").stringValue();
                                                objectRes = bs.getValue("o").stringValue();
                                                predicateRes = bs.getValue("p").stringValue();

                                                //we store the resources found and build the new graph model
                                                ((GraphBasedStorage)getStorage()).addNode(subjectRes);
                                                ((GraphBasedStorage)getStorage()).addNode(objectRes);
                                                
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.DIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes);                                                
                                                } else if (((LinkAnalysisRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.UNDIRECTED) {
                                                        ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes); 
                                                        ((GraphBasedStorage)getStorage()).addEdge(objectRes, subjectRes, predicateRes); 
                                                }
                                        }                                        
                                break;
                                case HYBRID:
                            
                                	
                                	while (result.hasNext()) {
                                        BindingSet bs = result.next();
                                        //We can access each of the variables configured:
                                        subjectRes = bs.getValue("s").stringValue();
                                        objectRes = bs.getValue("o").stringValue();
                                        predicateRes = bs.getValue("p").stringValue();

                                        //we store the resources found and build the new graph model
                                        ((GraphBasedStorage)getStorage()).addNode(subjectRes);
                                        ((GraphBasedStorage)getStorage()).addNode(objectRes);
                                        
                                        if (((HybridRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.DIRECTED) {
                                                ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes);                                                
                                        } else if (((HybridRecConfig)getRecConfig()).getGraphOrientation() == RecGraphOrientation.UNDIRECTED) {
                                                ((GraphBasedStorage)getStorage()).addEdge(subjectRes, objectRes, predicateRes); 
                                                ((GraphBasedStorage)getStorage()).addEdge(objectRes, subjectRes, predicateRes); 
                                        }
                                }
                        break;
                        }
                        
                       
                            
                        
                        if (getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY ||
                                        getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_PAGERANK_WITH_PRIORS  || 
                                        getRecConfig().getRecParadigm() == RecParadigm.CROSS_DOMAIN_REWORD ||
                                        getRecConfig().getRecParadigm() == RecParadigm.HYBRID) {  
                        		
	                            //When dealing with cross-domain recommendations one has to
	                            //store the resource from the graph are part of the source domain.
	                            pat = Pattern.compile("\\?(\\w)+");
	                            mat = pat.matcher(((CrossDomainRecConfig)getRecConfig()).getSourceDomain());
	
	                            if (mat.find()) sourceItemVar = mat.group();
	
	                            preprocessingSPARQLQuery =
	                                    "SELECT " + sourceItemVar
	                                    + " WHERE {\n"
	                                    +   ((CrossDomainRecConfig)getRecConfig()).getSourceDomain()
	                                    + "}";
	
	                            tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
	                                    preprocessingSPARQLQuery);
	
	                            result = tupleQuery.evaluate();
	
	                            if (result.hasNext() == false)
	                                throw new RecommenderException("NO SOURCE NODES FOUND");
	
	                            while (result.hasNext()) {
	                                BindingSet bs = result.next();
	                                sourceRes = bs.getValue(sourceItemVar.replace("?", "")).stringValue();
	                                //We set the found resource as target
	                                ((GraphBasedStorage)getStorage()).setSourceNode(sourceRes);
	                            }
	                            
	                            
                                //When dealing with cross-domain recommendations one has to
                                //store the resource from the graph are part of the target domain.
                                pat = Pattern.compile("\\?(\\w)+");
                                mat = pat.matcher(((CrossDomainRecConfig)getRecConfig()).getTargetDomain());

                                if (mat.find()) targetItemVar = mat.group();

                                preprocessingSPARQLQuery =
                                        "SELECT " + targetItemVar
                                        + " WHERE {\n"
                                        +   ((CrossDomainRecConfig)getRecConfig()).getTargetDomain()
                                        + "}";

                                tupleQuery = getConnection().prepareTupleQuery(QueryLanguage.SPARQL,
                                        preprocessingSPARQLQuery);

                                result = tupleQuery.evaluate();

                                if (result.hasNext() == false)
                                    throw new RecommenderException("NO TARGET NODES FOUND");

                                while (result.hasNext()) {
                                    BindingSet bs = result.next();
                                    targetRes = bs.getValue(targetItemVar.replace("?", "")).stringValue();
                                    //We set the found resource as target
                                    ((GraphBasedStorage)getStorage()).setTargetNode(targetRes);
                                }
                        }
                        
                       if(getRecConfig().getRecParadigm() == RecParadigm.HYBRID) {
                    	   
                         	 //Compute Rdf2Vec or read precomputed embeddings from the csv file
                             if (((HybridRecConfig)getRecConfig()).getComputeRdf2Vec() == true) {
                             	String modelPath = ((HybridRecConfig)getRecConfig()).getRdf2VecInputPath();;
                             	Word2VecModel vec = new Word2VecModel();
                                 vec.readWord2VecModel(modelPath);
                                 ((GraphBasedStorage)getStorage()).computeRdf2VecEmbeddings(vec);
                                 String rdf2VecOutputPath = ((HybridRecConfig)getRecConfig()).getRdf2VecOutputPath();
                                 ((GraphBasedStorage)getStorage()).writeRdf2VecEmbeddings(rdf2VecOutputPath);
                             } else {
                             	String rdf2VecOutputPath = ((HybridRecConfig)getRecConfig()).getRdf2VecOutputPath();
                             	((GraphBasedStorage)getStorage()).readRdf2VecEmbeddings(rdf2VecOutputPath);
                             }
                             
                             //Compute Doc2Vec or read precomputed embeddings from the csv file
                             if (((HybridRecConfig)getRecConfig()).getComputeDoc2Vec() == true) {
                             	String inputPath = ((HybridRecConfig)getRecConfig()).getDoc2VecInputPath();
                                 DocModel vec = new DocModel();
                                 vec.trainDoc2VecModel(inputPath);
                                 ((GraphBasedStorage)getStorage()).computeDoc2VecEmbeddings(vec);
                                 String doc2VecOutputPath = ((HybridRecConfig)getRecConfig()).getDoc2VecOutputPath();
                                 ((GraphBasedStorage)getStorage()).writeDoc2VecEmbeddings(doc2VecOutputPath);
                             } else {
                             	String doc2VecOutputPath = ((HybridRecConfig)getRecConfig()).getDoc2VecOutputPath();
                             	((GraphBasedStorage)getStorage()).readDoc2VecEmbeddings(doc2VecOutputPath);
                             }
                             
                             if (((HybridRecConfig)getRecConfig()).getComputeUserEmbeddings() == true) {
                            	 String userEmbeddingPath = ((HybridRecConfig)getRecConfig()).getUserEmbeddingsPath();
                            	 ((GraphBasedStorage)getStorage()).computeUsersEmbeddingsAverage();
                            	 ((GraphBasedStorage)getStorage()).writeUsersEmbeddingsAverage(userEmbeddingPath);
                             }else {
                                 String userEmbeddingPath = ((HybridRecConfig)getRecConfig()).getUserEmbeddingsPath();
	                             ((GraphBasedStorage)getStorage()).readUsersEmbeddingsAverage(userEmbeddingPath);
	                         }
                             /*
                             String mlTrainingInput = ((HybridRecConfig)getRecConfig()).getMlInputFile();
                        	 ((GraphBasedStorage)getStorage()).mlTrainingData(mlTrainingInput);
                        	 
                             
                             if(((HybridRecConfig)getRecConfig()).getTrainTreeModel() == true) {
                            	  ((GraphBasedStorage)getStorage()).trainTreeModel(mlTrainingInput, mlTrainingInput.substring(0, mlTrainingInput.length()-3).concat("arff"));
                             }*/
                             
                      }
                       
                } catch (RepositoryException ex) {
                        throw new RecommenderException(ex);
                } catch (MalformedQueryException ex) {
                        throw new RecommenderException(ex);
                } catch (QueryEvaluationException ex) {
                        throw new RecommenderException(ex);
                } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        
        @Override
        public Set<RatedResource> getNeighbors(String URI) throws RecommenderException {
                throw new UnsupportedOperationException();
        }        
        
        
        @Override
        public Set<String> getRecCandidates(String userURI) throws RecommenderException {
                Set<String> recCandidates = new HashSet<String>();
                
                //Retrieve the index of user
                int indexOfUser = getStorage().getIndexOf(userURI);
                //If this cannot be retrieved then throw an exception
                if (indexOfUser == -1){
                        throw new RecommenderException("User resource was not found");
                }
                
                GraphBasedStorage graphStorage = (GraphBasedStorage) getStorage();
                Set<Integer> targetNodeIds = new HashSet<Integer>();
                targetNodeIds.addAll(graphStorage.getTargetNodes());
                                        
                switch(getRecConfig().getRecParadigm()) {
                                case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY: case CROSS_DOMAIN_PAGERANK_WITH_PRIORS: case HYBRID:                                 
                                        Set<Integer> allUserNodes = graphStorage.getSubgraphVertices(indexOfUser);
                                        targetNodeIds.retainAll(allUserNodes);
                                        for (Integer nodeId: targetNodeIds) {
                                                recCandidates.add(getStorage().getURI(nodeId));
                                        }
                                break;
                                case CROSS_DOMAIN_REWORD:
                                        if (hasPreprocessed()) {
                                                //we collect all candidates reachable from the items liked / rated by the user.
                                                Set<IndexedRatedRes> irrSet = getStorage().getIndexedRatedResOfUser(indexOfUser);
                                                Set<Integer> allReachableCandidatesFromConsumedItem = new HashSet<Integer>();

                                                for (IndexedRatedRes irr: irrSet) {
                                                        allReachableCandidatesFromConsumedItem.
                                                                addAll(graphStorage.getAllReachableNodes(irr.getResourceId(), 2));
                                                }
                                                allReachableCandidatesFromConsumedItem.retainAll(targetNodeIds);

                                                for (Integer nodeId: allReachableCandidatesFromConsumedItem) {
                                                        recCandidates.add(getStorage().getURI(nodeId));
                                                }                                        
                                        } else {
                                                //In case the pre-processing is not done we simply use the whole candidate set.
                                                for (Integer nodeId: targetNodeIds) {
                                                        recCandidates.add(getStorage().getURI(nodeId));
                                                }
                                        }
                                break;
                        }                
                return recCandidates;
        }
        
        
        @Override
        public double getResRelativeImportance(String node1, String node2) 
                        throws RecommenderException {
                //Retrieve the index of user
                int indexOfNode1 = getStorage().getIndexOf(node1);
                //If this cannot be retrieved then throw an exception
                if (indexOfNode1 == -1){
                        throw new RecommenderException(node1 + " was not found in the recommender model");
                }
                
                //Retrieve the index of user
                int indexOfNode2 = getStorage().getIndexOf(node2);
                //If this cannot be retrieved then throw an exception
                if (indexOfNode2 == -1){
                        throw new RecommenderException(node2 + " was not found in the recommender model");
                }
                if (!((GraphBasedStorage)getStorage()).getTargetNodes().contains(indexOfNode2)){
                        throw new RecommenderException(node2 + " is not a target resource");
                }
                
                //TODO
                switch(getRecConfig().getRecParadigm()) {
                        case CROSS_DOMAIN_PAGERANK_WITH_PRIORS:
                                //The first node is treated as the user
                                if (!userOfLastRecommendation.equals(node1)) {                                    
                                        if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.UNIFORM) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.UNIFORM) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsUniform(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                
                                                else if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsUniformEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                                                
                                        }                                    
                                        else if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.PRIORS_LIKED_ITEMS) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).pageRankWithPriorsLikesEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                        }
                                        userOfLastRecommendation = node1;
                                }
                                return ((GraphBasedStorage)getStorage()).getPrpVertexScore(indexOfNode2); 
                            
                        case CROSS_DOMAIN_K_STEP_MARKOV_CENTRALITY:
                                //The first node is treated as the user
                                if (!userOfLastRecommendation.equals(node1)) {
                                        if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.UNIFORM) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.UNIFORM) {
                                                        ((GraphBasedStorage)getStorage()).ksmcUniform(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }                                                
                                                else if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).ksmcUniformEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                                
                                        }
                                        else if (((LinkAnalysisRecConfig)getRecConfig()).getPriorsDistribution() == RecPriorsDistribution.PRIORS_LIKED_ITEMS) {
                                                if (((LinkAnalysisRecConfig)getRecConfig()).getEdgesDistribution() == RecEdgeDistribution.OUTGOING_WEIGHTS_SUM_1) {
                                                        ((GraphBasedStorage)getStorage()).ksmcLikesEdgesSumOne(
                                                                indexOfNode1, 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getkMarkovSteps(), 
                                                                ((LinkAnalysisRecConfig)getRecConfig()).getMaxIterations());         
                                                }
                                        }
                                        userOfLastRecommendation = node1;
                                }
                                return ((GraphBasedStorage)getStorage()).getKsmVertexScore(indexOfNode2);                                                                  
                        case CROSS_DOMAIN_REWORD:
                                double score = ((GraphBasedStorage)getStorage()).computeRewordRelatedness(indexOfNode1, indexOfNode2);
                                return score;
                        
                        case HYBRID:
                            return ((GraphBasedStorage)getStorage()).predictRating(indexOfNode1, indexOfNode2);
                }
                return -1.0;
        }

        @Override
        public void releaseResources() {}
}

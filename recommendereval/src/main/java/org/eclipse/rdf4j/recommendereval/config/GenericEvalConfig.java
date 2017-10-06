/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eclipse.rdf4j.recommender.config.CrossDomainRecConfig;
import org.eclipse.rdf4j.recommender.config.RecConfig;
import org.eclipse.rdf4j.recommender.parameter.RecEntity;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;
import org.eclipse.rdf4j.recommendereval.parameter.EvalEntity;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.EvalOutput;
import org.eclipse.rdf4j.recommendereval.parameter.EvalStorage;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionCriterion;
import org.eclipse.rdf4j.recommendereval.parameter.EvalUserSelectionWrapper;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.GlobalEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.PredictionEvalMetric;
import org.apache.log4j.Logger;

/**
 * This abstract class represents a configuration of a generic recommender 
 * evaluator, not related to any specific recommender technique.
 */
public abstract class GenericEvalConfig implements EvalConfig {
    
    private boolean isReproducible = false;

    private final HashMap<EvalEntity,String> evalEntityMap;
    
    // ArrayList is picked intentionally
    private ArrayList<GenericEvalMetric> evalMetrics = null;
    private ArrayList<Integer> topKSizeList = null;
    
    private EvalStorage evalStorage     = null;
    private EvalOutput evalOutput       = null;
    private String outputFilePath       = null;
    private String evalGraphPattern     = null;
    private String featureGraphPattern  = null;
    
    private EvalUserSelectionWrapper evalUserSelectionType = null;
    
    final static Logger LOGGER = Logger.getLogger(GenericEvalConfig.class);
    
    /**
     * Set of recommendation configurations. Allows multiple recommenders to be 
     * evaluated at once.
     */
    private ArrayList<RecConfig> recConfigList = null;
    
    public GenericEvalConfig() {
        this.evalMetrics   = new ArrayList<>();
        this.topKSizeList  = new ArrayList<>();
        this.evalEntityMap = new HashMap<>();
    }
    
    @Override
    public boolean validateConfiguration() 
            throws EvaluatorException {
        
        //removeNotComputableMetrics();
        checkGraphPatterns();
        checkCDRecConfig();
        checkTopK();
                
        if( checkDuplicateMetrics() )
            throw new EvaluatorException("CONFIGURATION NOT VALID. DUPLICATE EVALUATION METRICS"); 
        if( evalMetrics == null || evalMetrics.isEmpty() ) 
            throw new EvaluatorException("CONFIGURATION NOT VALID. EVALUATION METRIC IS MISSING");                
        if( evalOutput == null ) 
            throw new EvaluatorException("CONFIGURATION NOT VALID. EVALUATION OUTPUT IS MISSING");
        if( evalStorage == null ) 
            throw new EvaluatorException("CONFIGURATION NOT VALID. EVALUATION STORAGE IS MISSING");
        if( outputFilePath == null || outputFilePath.isEmpty() ) 
            throw new EvaluatorException("CONFIGURATION NOT VALID. EVALUATION OUTPUT FILE PATH IS MISSING");
        if( evalGraphPattern == null )
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO GRAPH PATTERN DETECTED");
        if( featureGraphPattern == null && evalMetricsContain(EvalMetric.DIVERSITY) )
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO FEATURE GRAPH PATTERN DETECTED");
        if( featureGraphPattern == null && evalMetricsContain(EvalMetric.NOVELTY) )
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO FEATURE GRAPH PATTERN DETECTED");
        if( evalEntityMap.get(EvalEntity.USER) == null )
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO USER STRING DETECTED AS EVALUATOR ENTITY");
        if( evalEntityMap.get(EvalEntity.RAT_ITEM) == null && evalEntityMap.get(EvalEntity.POS_ITEM) == null
                && evalEntityMap.get(EvalEntity.NEG_ITEM) == null)
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO ITEM STRING DETECTED AS EVALUATOR ENTITY");
        if( evalEntityMap.get(EvalEntity.FEATURE) == null && evalMetricsContain(EvalMetric.DIVERSITY) || 
                evalEntityMap.get(EvalEntity.FEATURE) == null && evalMetricsContain(EvalMetric.NOVELTY) )
            throw new EvaluatorException("CONFIGURATION NOT VALID. NO FEATURE STRING DETECTED AS EVALUATOR ENTITY");
        if( evalUserSelectionType == null ) {
            evalUserSelectionType = new EvalUserSelectionWrapper(EvalUserSelectionCriterion.RANDOM, 
                    Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        
        checkFMeasure();
        setTopKSize();
        
        return true;
    }

    /**
     * Removes Precision, F-Measure, Accuracy metrics which 
     * are not computable for "Likes" case.
     */
    /*
    private void removeNotComputableMetrics() {
        
        // if Likes case
        if( evalEntityMap.get(EvalEntity.RATING) == null ) {
            
            Iterator<GenericEvalMetric> iterator = evalMetrics.iterator();
            while (iterator.hasNext()) {
                
               GenericEvalMetric metric = iterator.next(); 
               
                if( metric.getMetric() == EvalMetric.PRE ||
                        metric.getMetric() == EvalMetric.F_MEASURE ||
                        metric.getMetric() == EvalMetric.ACC ) {
                    LOGGER.warn("Following metric cannot be computed therefore it is going to be removed: " + metric.getMetric().toString());
                    iterator.remove();
                }
            }
        }
    }
    */

    /**
     * Checks for duplicate Evaluation Metrics. 
     * 
     * If a duplicate metric is found, returns true. Otherwise false.
     */
    private boolean checkDuplicateMetrics() throws EvaluatorException {

        for( int i = 0 ; i < evalMetrics.size() ; i++ ) {
            for( int j = 0 ; j < evalMetrics.size() && j!=i ; j++ ) {
                if( evalMetrics.get(i).getMetric() == evalMetrics.get(j).getMetric() ) {
                    return true;
                }
            }
        }
        return false;
    }  

    /**
     * Checks if topKSizes list is valid.
     * 
     * @throws EvaluatorException 
     */
    private void checkTopK() throws EvaluatorException {
        
        boolean rankingFlag = false;
        
        for( GenericEvalMetric gem : evalMetrics ) {
            if( !(gem instanceof PredictionEvalMetric) ) { 
                rankingFlag = true;
                break;
            }
        }
        
        if( topKSizeList.isEmpty() && rankingFlag ) {
            throw new EvaluatorException("NO TOP K SIZE PROVIDED");
        }
        
        for( int i = 0 ; i < topKSizeList.size() ; i++ ) {
            if( topKSizeList.get(i) < 1 ) {                
                throw new EvaluatorException("INVALID TOP K SIZE. TOP K SIZE SHOULD BE A POSITIVE INTEGER");
            }
        }
    }

    /**
     * Helper method for F-Measure Evaluation Metric
     * 
     * If one or both of Precision and Recall metrics does not exist in the
     * metrics list, add it or them to metrics list.
     * 
     * Push Precision, Recall and F-Measure to the end of the list. The order is 
     * important, it should not be changed.
     */
    private void checkFMeasure() throws EvaluatorException {
        
        if( !evalMetricsContain(EvalMetric.F_MEASURE) ) {
            return;
        }
        
        if( evalMetricsContain(EvalMetric.F_MEASURE) && !evalMetricsContain(EvalMetric.PRE) ) {
            evalMetrics.add(new RankingEvalMetric(EvalMetric.PRE));
            System.out.println("For calculating F-Measure, Precision value should be calculated. "
                    + "Therefore Precision Metric is added to Metrics List!");
        }
        
        if( evalMetricsContain(EvalMetric.F_MEASURE) && !evalMetricsContain(EvalMetric.REC) ) {
            evalMetrics.add(new RankingEvalMetric(EvalMetric.REC));
            System.out.println("For calculating F-Measure, Recall value should be calculated. "
                    + "Therefore Recall Metric is added to Metrics List!");
        }
        
        // reorder metrics
        evalMetricsRemove(EvalMetric.PRE);
        evalMetrics.add(new RankingEvalMetric(EvalMetric.PRE));       
        
        evalMetricsRemove(EvalMetric.REC);
        evalMetrics.add(new RankingEvalMetric(EvalMetric.REC));   
        
        evalMetricsRemove(EvalMetric.F_MEASURE);
        evalMetrics.add(new RankingEvalMetric(EvalMetric.F_MEASURE)); 
    }

    /**
     * Assigns topKSizes to Ranking Evaluator Metrics. 
     * 
     * Adds duplicate Ranking Evaluator Metrics if the evaluation configuration has 
     * multiple topKSizes.
     */
    private void setTopKSize() throws EvaluatorException {
        
        if( topKSizeList.isEmpty() ) {
            return;
        }
        
        // Ascending
        Collections.sort(topKSizeList);
        
        // Set topKSize for all metrics
        int firstTopKSize = topKSizeList.get(0);       
        
        for( int i = 0 ; i < evalMetrics.size() ; i++ ) {
            if( evalMetrics.get(i) instanceof RankingEvalMetric ) {
                ( (RankingEvalMetric) evalMetrics.get(i)).setTopKSize(firstTopKSize);
            }
        }
        
        /**
         *  For the first topKSize, the value inside Ranking Evaluator Metric is changed, 
         * since Ranking Evaluator Metrics already exist in the list.
         * 
         *  For the other topKSize s, add more Ranking Evaluator Metrics with the
         * new topKSize.
         */
        
        ArrayList<RankingEvalMetric> metricsToAdd = new ArrayList<>();
        
        // then if moreTopKSize exists, duplicate metrics
        for( int i = 1 ; i < topKSizeList.size() ; i++ ) {
        
            for( int j = 0 ; j < evalMetrics.size() ; j++ ) {
                
                if( evalMetrics.get(j) instanceof RankingEvalMetric ) {
                    RankingEvalMetric newMetric = new RankingEvalMetric(evalMetrics.get(j).getMetric());
                    newMetric.setTopKSize(topKSizeList.get(i));
                    metricsToAdd.add(newMetric);
                }
            }
        }         
        
        evalMetrics.addAll(metricsToAdd);
    }
    
    /** 
     * Normalizes Graph Patterns, checks Graph Patterns and Entities.
     * 
     * @throws EvaluatorException 
     */
    private void checkGraphPatterns() throws EvaluatorException {
        
        if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
            if( evalEntityMap.get(EvalEntity.POS_ITEM) != null 
                    || evalEntityMap.get(EvalEntity.NEG_ITEM) != null) {
                throw new EvaluatorException("EVALUATOR FEEDBACK TYPE INVALID, PLEASE CHECK EVALUATION ENTITIES");
            }
        }
        
        if( recConfigList == null ) {
            // if you move this or if you delete this, consider checkCDRecConfig()
            throw new EvaluatorException("NO RECOMMENDER CONFIGURATION DETECTED");
        }
        else if( recConfigList.size() == 1 ) {
            // No need to check for different graph patterns
        }
        else if ( recConfigList.size() > 1 ) {
                            
            for( int i = 0 ; i < recConfigList.size() ; i++ ) {
                if( recConfigList.get(i).getRecEntity(RecEntity.RAT_ITEM) != null ) {
                    if( recConfigList.get(i).getRecEntity(RecEntity.POS_ITEM) != null 
                            || recConfigList.get(i).getRecEntity(RecEntity.NEG_ITEM) != null) {
                        throw new EvaluatorException("RECOMMENDER FEEDBACK TYPE INVALID, PLEASE CHECK RECOMMENDER ENTITIES");
                    }
                }
            }
                
            List<String> firstPatterns  = new ArrayList<>();
            List<String> secondPatterns = new ArrayList<>();
            
            if( evalEntityMap.get(EvalEntity.RAT_ITEM) != null ) {
                for( int i = 0 ; i < recConfigList.size() ; i++ ) {
                    firstPatterns.add(recConfigList.get(i).getRatGraphPattern());
                    if( i > 0 ) {      
                        if( !recConfigList.get(0).getRecEntity(RecEntity.RATING).equals(recConfigList.get(i).getRecEntity(RecEntity.RATING))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }            
                        if( !recConfigList.get(0).getRecEntity(RecEntity.RAT_ITEM).equals(recConfigList.get(i).getRecEntity(RecEntity.RAT_ITEM))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }      
                    }
                }
            }
            else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                     evalEntityMap.get(EvalEntity.NEG_ITEM) == null ) {                
                for( int i = 0 ; i < recConfigList.size() ; i++ ) {
                    firstPatterns.add(recConfigList.get(i).getPosGraphPattern());
                    if( i > 0 ) {      
                        if( !recConfigList.get(0).getRecEntity(RecEntity.POS_ITEM).equals(recConfigList.get(i).getRecEntity(RecEntity.POS_ITEM))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }
                    }
                }
            }
            else if( evalEntityMap.get(EvalEntity.NEG_ITEM) != null && 
                     evalEntityMap.get(EvalEntity.POS_ITEM) == null ) {                
                for( int i = 0 ; i < recConfigList.size() ; i++ ) {
                    firstPatterns.add(recConfigList.get(i).getNegGraphPattern());
                    if( i > 0 ) {      
                        if( !recConfigList.get(0).getRecEntity(RecEntity.NEG_ITEM).equals(recConfigList.get(i).getRecEntity(RecEntity.NEG_ITEM))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }
                    }
                }
            }
            else if( evalEntityMap.get(EvalEntity.POS_ITEM) != null && 
                     evalEntityMap.get(EvalEntity.NEG_ITEM) != null ) {                
                for( int i = 0 ; i < recConfigList.size() ; i++ ) {
                    firstPatterns.add(recConfigList.get(i).getPosGraphPattern());
                    secondPatterns.add(recConfigList.get(i).getNegGraphPattern());
                    if( i > 0 ) {      
                        if( !recConfigList.get(0).getRecEntity(RecEntity.POS_ITEM).equals(recConfigList.get(i).getRecEntity(RecEntity.POS_ITEM))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }
                        if( !recConfigList.get(0).getRecEntity(RecEntity.NEG_ITEM).equals(recConfigList.get(i).getRecEntity(RecEntity.NEG_ITEM))) {
                            throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                        }
                    }
                }
            }
            
            for( int i = 1 ; i < recConfigList.size() ; i++ ) {
                
                checkPatternPair(firstPatterns.get(0),firstPatterns.get(i));
                
                if( !secondPatterns.isEmpty() ) {                    
                    checkPatternPair(secondPatterns.get(0),secondPatterns.get(i));
                }
                
                if( !recConfigList.get(0).getRecEntity(RecEntity.USER).equals(recConfigList.get(i).getRecEntity(RecEntity.USER))) {
                    throw new EvaluatorException("RECOMMENDER ENTITIES OF DIFFIRENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");
                }          
            }                
        }
        else  {
            throw new EvaluatorException("NO RECOMMENDER CONFIGURATION DETECTED");
        }
    }

    /**
     * Checks if all of the Recommender Configurations are:
     * 
     * 1) Instances of CrossDomainRecConfig
     *      If 1) is true:
     *      1a) Checks if target domain is a triple
     *      2a) All target domains are the same
     * 
     * 2) Not Instances of CrossDomainRecConfig
     * 
     * 
     * @throws EvaluatorException 
     */
    private void checkCDRecConfig() throws EvaluatorException {
        
        // not empty since it is checked before
        if( recConfigList.get(0) instanceof CrossDomainRecConfig ) {
            
            String targetDomain = "";
            String toSplit = ((CrossDomainRecConfig) recConfigList.get(0)).getTargetDomain();
            String[] parts = toSplit.split("\\s+");

            if( parts.length != 3 ) {
                throw new EvaluatorException("TARGET DOMAIN OF ONE OF THE CROSSDOMAINRECCONFIG S IS NOT A TRIPLE");
            }

            targetDomain = parts[2];            
            
            // check other Configurations
            for( int i = 1 ; i < recConfigList.size() ; i++ ) {
                // not instanceof
                if( !(recConfigList.get(i) instanceof CrossDomainRecConfig) ) {
                    throw new EvaluatorException("ALL OR NONE OF RECOMMENDER CONFIGURATIONS SHOULD BE CROSS DOMAIN RECCONFIG");
                }        
                                                 
                String toSplitNext = ( (CrossDomainRecConfig) recConfigList.get(i) ).getTargetDomain();
                String[] partsNext = toSplitNext.split("\\s+");

                if( partsNext.length != 3 ) {
                    throw new EvaluatorException("TARGET DOMAIN OF ONE OF THE CROSSDOMAINRECCONFIG S IS NOT A TRIPLE");
                }
                if( !partsNext[2].equals(targetDomain) ) {
                    throw new EvaluatorException("TARGET DOMAIN OF THE CROSSDOMAINRECCONFIG S SHOULD BE SAME");                        
                }                
            }
        }
        else {
            // check other Configurations
            for( int i = 1 ; i < recConfigList.size() ; i++ ) {
                if( recConfigList.get(i) instanceof CrossDomainRecConfig ) {
                    throw new EvaluatorException("ALL OR NONE OF RECOMMENDER CONFIGURATIONS SHOULD BE CROSS DOMAIN RECCONFIG");
                }
            }
        }   
    }

    /**
     * Checks if Evaluator Metric is in the list. 
     * 
     * @param evalMetric
     * @return true if Evaluator Metric exists in the list. Otherwise false. 
     */
    public boolean evalMetricsContain(EvalMetric evalMetric) {
        
        for( int i = 0 ; i < evalMetrics.size() ; i++ ) {
            if( evalMetrics.get(i).getMetric() == evalMetric ) {
                return true;
            }
        }        
        return false;
    }

    /**
     * Removes metric from the Evaluation Metrics list.
     * 
     * @param evalMetric 
     */
    private void evalMetricsRemove(EvalMetric evalMetric) {
        
        for( int i = 0 ; i < evalMetrics.size() ; i++ ) {
            if( evalMetrics.get(i).getMetric() == evalMetric ) {
                evalMetrics.remove(i);
            }
        }        
    }
    
    @Override
    public String getGraphPattern() {
        return this.evalGraphPattern;
    }
    
    @Override
    public void setGraphPattern(String graphPattern) {
        this.evalGraphPattern = graphPattern;
    }
    
    @Override
    public String getFeatureGraphPattern() {
        return this.featureGraphPattern;
    }
    
    @Override
    public void setFeatureGraphPattern(String featureGraphPattern) {
        this.featureGraphPattern = featureGraphPattern;
    }
    
    @Override
    public HashMap<EvalEntity,String> getEvalEntityMap() {
        return this.evalEntityMap;
    }
    
    @Override
    public void addEvalEntity(EvalEntity entity, String varName) throws EvaluatorException {
        if (varName != null) {
            evalEntityMap.put(entity, varName);
        }
    }

    @Override
    public boolean isReproducible() {
        return this.isReproducible;
    }
    
    @Override
    public void setIsReproducible(boolean isReproducible) {
        this.isReproducible = isReproducible;
    }
    
    @Override
    public void addEvalMetric(RankingEvalMetric rankingMetric) throws EvaluatorException {
        this.evalMetrics.add(new RankingEvalMetric(rankingMetric.getMetric()));
    }
    
    @Override
    public void addEvalMetric(PredictionEvalMetric predictionMetric) throws EvaluatorException {
        this.evalMetrics.add(new PredictionEvalMetric(predictionMetric.getMetric()));
    }
    
    @Override
    public void addEvalMetric(GlobalEvalMetric globalMetric) throws EvaluatorException {
        this.evalMetrics.add(new GlobalEvalMetric(globalMetric.getMetric()));
    }

    @Override
    public ArrayList<GenericEvalMetric> getEvalMetrics() {
        return this.evalMetrics;
    }
    
    @Override
    public void addRankingMetricTopKSize(Integer size) throws EvaluatorException {
        topKSizeList.add(size);                        
        Collections.sort(topKSizeList);
    }
    
    @Override
    public ArrayList<Integer> getTopKSizesToBeEvaluated() {
        return this.topKSizeList;
    }
    
    @Override
    public EvalOutput getOutputMethod() {
        return this.evalOutput;
    }
    
    @Override
    public void setOutputMethod(EvalOutput metric, String filePath) {
        this.evalOutput = metric;
        this.outputFilePath = filePath;
    }
    
    @Override
    public String getOutputPath() {
        return this.outputFilePath;
    }

    @Override
    public void selectSpecificUsersForEvaluation(EvalUserSelectionWrapper selectionType) {
        this.evalUserSelectionType = selectionType;
    }
    
    @Override
    public EvalUserSelectionWrapper getUserSelectionCriterion() {
        return this.evalUserSelectionType;
    }
    
    @Override
    public EvalStorage getStorage() {
        return this.evalStorage;
    }
    
    @Override
    public void setStorage(EvalStorage storage) {
        this.evalStorage = storage;
    }
    
    @Override    
    public void setRecommenderConfigurations(ArrayList<RecConfig> recConfigList) {
        this.recConfigList = recConfigList;
    }

    private void checkPatternPair(String patternA, String patternB) {         

        // replace dots with spaces
        patternA = patternA.replaceAll("\\s+[.]", " ");
        patternB = patternB.replaceAll("\\s+[.]", " ");

        // replace new line characters with spaces
        patternA = patternA.replaceAll("\\r|\\n", "");
        patternB = patternB.replaceAll("\\r|\\n", "");

        // trim start and end of the string and shrink spaces
        patternA = patternA.trim().replaceAll(" +", "");
        patternB = patternB.trim().replaceAll(" +", "");                

        if( !patternA.equals(patternB) ) {                
            throw new EvaluatorException("GRAPH PATTERNS OF DIFFERENT RECOMMENDER CONFIGURATIONS SHOULD BE SAME");         
        }
    }
}
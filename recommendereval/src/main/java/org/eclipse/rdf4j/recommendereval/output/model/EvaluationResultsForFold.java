/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.output.model;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.recommendereval.parameter.EvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.GenericEvalMetric;
import org.eclipse.rdf4j.recommendereval.parameter.RankingEvalMetric;

/**
 * A class for keeping result of Evaluator for one Recommender Configuration. 
 * 
 * It keeps all metrics and result values for all metrics. 
 */
public class EvaluationResultsForFold {
    
    final static Logger LOGGER = Logger.getLogger(EvaluationResultsForFold.class);
    
    private final ArrayList<GenericEvalMetric> metrics;
    private final ArrayList<Double> metricScores;
    
    
    public EvaluationResultsForFold() {
        this.metrics      = new ArrayList<>();
        this.metricScores = new ArrayList<>();
    }
    
    public void addResult(GenericEvalMetric metric, Double metricScore) {
        metrics.add(metric);
        metricScores.add(metricScore);
    }
    
    public ArrayList<GenericEvalMetric> getMetrics() {
        return this.metrics;
    }

    public ArrayList<Double> getMetricScores() {
        return this.metricScores;
    }

    public void setMetricScore(int metricIndex, Double metricScore) {
        metricScores.set(metricIndex, metricScore);
    }
    
    /**
     * Gets MAE score if it is found! Returns -1.0 if it is not found.
     * @return 
     */
    public Double getMAEScore() {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            if( metrics.get(i).getMetric() == EvalMetric.MAE ) {
                return metricScores.get(i);
            }
        }        
        LOGGER.warn("NO MAE SCORE FOUND!");
        System.out.println("NO MAE SCORE FOUND!");   
        return -1.0;
    }
    
    /**
     * Gets RMSE score if it is found! Returns -1.0 if it is not found.
     * @return 
     */
    public Double getRMSEScore() {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            if( metrics.get(i).getMetric() == EvalMetric.RMSE ) {
                return metricScores.get(i);
            }
        }
        LOGGER.warn("NO RMSE SCORE FOUND!");
        System.out.println("NO RMSE SCORE FOUND!");      
        return -1.0;
    }
    
    /**
     * Gets AUC score with given topKSize, if it is found. 
     * Returns -1.0 if it is not found.
     * 
     * @return 
     */
    public Double getAUCScore() {        
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            if( metrics.get(i).getMetric() == EvalMetric.AUC ) {
                return metricScores.get(i);
            }
        }
        LOGGER.warn("NO AUC SCORE FOUND!");
        System.out.println("NO AUC SCORE FOUND!");
        return -1.0;
    }
    
    /**
     * Gets Coverage score if it is found! Returns -1.0 if it is not found.
     * @return 
     */
    public Double getCoverageScore() {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            if( metrics.get(i).getMetric() == EvalMetric.COVERAGE ) {
                return metricScores.get(i);
            }
        }
        LOGGER.warn("NO COVERAGE SCORE FOUND!");
        System.out.println("NO COVERAGE SCORE FOUND!");     
        return -1.0;
    }
    
    /**
     * Gets Precision score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getPrecisionScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.PRE) {
                RankingEvalMetric precisionMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( precisionMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO PRECISION SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO PRECISION SCORE FOUND WITH TOPKSIZE: " + topKSize);
        return -1.0;
    }
    
    /**
     * Gets Recall score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getRecallScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.REC) {
                RankingEvalMetric recallMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( recallMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO RECALL SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO RECALL SCORE FOUND WITH TOPKSIZE: " + topKSize);       
        return -1.0;
    }
    
    /**
     * Gets MRR score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getMRRScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.MRR) {
                RankingEvalMetric mrrMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( mrrMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO MRR SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO MRR SCORE FOUND WITH TOPKSIZE: " + topKSize);        
        return -1.0;
    }
    
    /**
     * Gets F-Measure score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getFMeasureScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.F_MEASURE) {
                RankingEvalMetric fmeasureMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( fmeasureMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }        
        LOGGER.warn("NO FMEASURE SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO FMEASURE SCORE FOUND WITH TOPKSIZE: " + topKSize);
        return -1.0;
    }
    
    /**
     * Gets NDCG score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getNDCGScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.NDCG) {
                RankingEvalMetric ndcgMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( ndcgMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO NDCG SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO NDCG SCORE FOUND WITH TOPKSIZE: " + topKSize);     
        return -1.0;
    }
    
    /**
     * Gets Diversity score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getDiversityScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.DIVERSITY) {
                RankingEvalMetric diversityMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( diversityMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO DIVERSITY SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO DIVERSITY SCORE FOUND WITH TOPKSIZE: " + topKSize);  
        return -1.0;
    }
    
    /**
     * Gets Novelty score with given topKSize, if it is found.
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getNoveltyScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.NOVELTY) {
                RankingEvalMetric noveltyMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( noveltyMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO NOVELTY SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO NOVELTY SCORE FOUND WITH TOPKSIZE: " + topKSize);    
        return -1.0;
    }
    
    /**
     * Gets Accuracy score with given topKSize, if it is found. 
     * Returns -1.0 if it is not found.
     * 
     * TopK size should be provided.
     * 
     * @param topKSize of the ranking metric
     * @return 
     */
    public Double getAccuracyScore(int topKSize) {
        
        for( int i = 0 ; i < metrics.size() ; i++ ) {
            // check metric type first
            if( metrics.get(i).getMetric() == EvalMetric.ACC ) {
                RankingEvalMetric accuracyMetric = (RankingEvalMetric) metrics.get(i);
                // check topKSize
                if( accuracyMetric.getTopKSize() == topKSize ) {
                    return metricScores.get(i);
                }
            }
        }
        LOGGER.warn("NO ACCURACY SCORE FOUND WITH TOPKSIZE: " + topKSize);
        System.out.println("NO ACCURACY SCORE FOUND WITH TOPKSIZE: " + topKSize);   
        return -1.0;
    }
}
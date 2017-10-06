/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.parameter;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * RankingEvalMetric extends EvalMetric enum. A Ranking Evaluation Metric 
 * requires the size of a top k list.
 */
public class RankingEvalMetric extends GenericEvalMetric {

    private int topKSize;
    
    
    public RankingEvalMetric(EvalMetric metric) throws EvaluatorException {
        super(metric);
        if( metric != EvalMetric.PRE && metric != EvalMetric.REC && metric != EvalMetric.F_MEASURE &&
                metric != EvalMetric.MRR && metric != EvalMetric.NOVELTY && 
                metric != EvalMetric.DIVERSITY && metric != EvalMetric.NDCG &&
                metric != EvalMetric.ACC ) {
            throw new EvaluatorException("WRONG EVALMETRIC TYPE FOR RANKINGNEVALMETRIC");
        }
        this.topKSize = 0;
    }    
    
    public int getTopKSize() {
        return this.topKSize;
    }
    
    public void setTopKSize(int size) {
        topKSize = size;
    }
    
    @Override
    public EvalMetric getMetric() {
        return super.getMetric();
    }

    @Override
    public String toString() {
        return super.toString() + "@" + this.topKSize;
    }
}

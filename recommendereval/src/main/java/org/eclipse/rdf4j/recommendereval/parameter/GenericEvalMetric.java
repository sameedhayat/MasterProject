/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.parameter;

/**
 * GenericEvalMetric extends EvalMetric enum and is extended by PredictionEvalMetric
 * and RankingEvalMetric
 */
public abstract class GenericEvalMetric {

    private final EvalMetric metric;
    
    public GenericEvalMetric(EvalMetric metric) {
        this.metric = metric;
    }    
    
    public EvalMetric getMetric() {
        return this.metric;
    }

    @Override
    public String toString() {
        return this.metric.toString();
    }
}

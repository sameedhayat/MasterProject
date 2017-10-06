/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.parameter;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * PredictionEvalMetric extends EvalMetric enum.
 */
public class PredictionEvalMetric extends GenericEvalMetric {
    
    public PredictionEvalMetric(EvalMetric metric) throws EvaluatorException {
        super(metric);
        if( metric != EvalMetric.MAE && metric != EvalMetric.RMSE 
                && metric != EvalMetric.AUC ) {
            throw new EvaluatorException("WRONG EVALMETRIC TYPE FOR PREDICTIONEVALMETRIC");
        }
    }    
    
    @Override
    public EvalMetric getMetric() {
        return super.getMetric();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

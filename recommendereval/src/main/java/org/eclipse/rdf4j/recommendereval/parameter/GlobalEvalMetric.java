/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.parameter;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * GlobalEvalMetric extends EvalMetric enum.
 */
public class GlobalEvalMetric extends GenericEvalMetric {

    public GlobalEvalMetric(EvalMetric metric) throws EvaluatorException {
        super(metric);
        if( metric != EvalMetric.COVERAGE ) {
            throw new EvaluatorException("WRONG EVALMETRIC TYPE FOR GLOBALEVALMETRIC");
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

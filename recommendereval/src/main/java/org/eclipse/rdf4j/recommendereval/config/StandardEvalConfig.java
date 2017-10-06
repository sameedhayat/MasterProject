/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.config;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * Evaluator Configuration for StandardEvaluator Class
 */
public class StandardEvalConfig extends GenericEvalConfig {

    public StandardEvalConfig() {
        super();
    }
    
    @Override
    public boolean validateConfiguration() throws EvaluatorException {
        super.validateConfiguration();        
        return true;
    }
}

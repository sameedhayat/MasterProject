/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.config;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * Evaluator Configuration for CrossKFoldEvaluator Class
 */
public class CrossKFoldEvalConfig extends GenericEvalConfig {

    private int numberOfFolds = 0;
    
    public CrossKFoldEvalConfig() {
        super();
    }
    
    @Override
    public boolean validateConfiguration() throws EvaluatorException {
        super.validateConfiguration();
        
        if( numberOfFolds < 2 ) {
            throw new EvaluatorException("NUMBER OF FOLDS SHOULD BE GREATER THAN 1");
        }
        
        return true;
    }
    
    public int getNumberOfFolds() {
        return numberOfFolds;
    }
    
    public void setNumberOfFolds(int numberOfSets) {
        numberOfFolds = numberOfSets;
    }  
}

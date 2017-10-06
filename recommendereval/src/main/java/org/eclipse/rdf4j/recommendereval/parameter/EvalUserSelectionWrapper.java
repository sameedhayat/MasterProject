/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.parameter;

import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

public class EvalUserSelectionWrapper {
    
    private final EvalUserSelectionCriterion selectionCriterion;
    private final int numberOfUsers;
    private final int numberOfRatings;
    
    /**
     * 
     * @param selectionCriterion 
     * @param numberOfUsers 0 to select all users
     * @param numberOfRatings trivial if EvalUserSelectionCriterion is RANDOM,
     *                        for MORE_THAN_X_RATINGS, use 0 to select all users
     * 
     * @throws org.eclipse.rdf4j.recommendereval.exception.EvaluatorException
     */
    public EvalUserSelectionWrapper(EvalUserSelectionCriterion selectionCriterion, 
            int numberOfUsers, int numberOfRatings) throws EvaluatorException {
        
        if( selectionCriterion == EvalUserSelectionCriterion.MORE_THAN_X_RATINGS
                && numberOfRatings < 0 ) {
            throw new EvaluatorException("NUMBER OF RATINGS SHOULD NOT BE A NEGATIVE INTEGER!");
        }
        
        this.selectionCriterion = selectionCriterion;
        this.numberOfUsers      = numberOfUsers;
        this.numberOfRatings    = numberOfRatings;         
    }
    
    public EvalUserSelectionCriterion getSelection() {
        return this.selectionCriterion;
    }
    
    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }
    
    public int getNumberOfRatings() {
        return this.numberOfRatings;
    }
}

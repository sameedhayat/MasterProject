/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.parameter;

/**
 * Parameter to select the users participating in the off-line evaluation
 */
public enum EvalUserSelectionCriterion {
    RANDOM,
    MORE_THAN_X_RATINGS
}
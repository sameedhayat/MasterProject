/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.output.model;

import java.util.ArrayList;


public class EvaluationResult {

    private final String recommenderConfigurationName;
    
    private EvaluationResultsForFold overallPerformance;    
    private ArrayList<EvaluationResultsForFold> foldResults;
    
    public EvaluationResult(String name) {
        this.foldResults = new ArrayList<>();
        this.recommenderConfigurationName = name;
    }
    
    public EvaluationResultsForFold getOverallPerformance() {
        return this.overallPerformance;
    }
    
    public void setOverallPerformance( EvaluationResultsForFold performance ) {
        this.overallPerformance = performance;
    }
    
    public ArrayList<EvaluationResultsForFold> getAllFoldResults() {
        return this.foldResults;
    }
    
    /**
     * Gets fold result for given fold index. (starting from 0)
     * @param foldIndex
     * @return 
     */
    public EvaluationResultsForFold getFoldResult(int foldIndex) {
        return this.foldResults.get(foldIndex);
    }
    
    public void addFoldResults(EvaluationResultsForFold fResult) {
        this.foldResults.add(fResult);
    }
    
    public String getRecommenderConfigurationName() {
        return this.recommenderConfigurationName;
    }
}

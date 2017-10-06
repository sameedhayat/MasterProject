/* 
 * Victor Anthony Arrascue Ayala
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommendereval.parameter;

/**
 * Evaluation metrics supported.
 */
public enum EvalMetric {
    
    RMSE("Root Mean Squared Error"),
    MAE("Mean Absolute Error"),
    AUC("Area Under the ROC Curve"),
    NDCG("Normalized Discounted Cumulative Gain"),
    F_MEASURE("F-measure"),
    PRE("Precision"),
    REC("Recall"),
    ACC("Accuracy"),
    DIVERSITY("Diversity"),
    NOVELTY("Novelty"),
    COVERAGE("Coverage"),
    MRR("Mean Reciprocal Rank");

    private final String name;

    private EvalMetric(String s) {
        name = s;
    }    

    @Override
    public String toString() {
        return this.name;
    }
}

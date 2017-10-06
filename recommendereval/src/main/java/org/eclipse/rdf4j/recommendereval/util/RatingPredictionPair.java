/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.util;

import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;

/**
 * Used for sorting Rating-Prediction(double value) pairs.
 */
public class RatingPredictionPair {

    public Rating rating;
    public Double prediction;
    
    public RatingPredictionPair(Rating rating, Double prediction) {
        this.rating = rating;
        this.prediction = prediction;
    }
}

/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.util;

import java.util.Comparator;

/**
 *  For sorting Rating-Prediction(double value) pairs(descending).
 */
public class RatingPredictionPairComparator implements Comparator<RatingPredictionPair> {
    
    @Override
    public int compare(RatingPredictionPair o1, RatingPredictionPair o2) {
        return o2.prediction.compareTo(o1.prediction);
    }
}

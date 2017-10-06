/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.util;

import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import java.util.Comparator;

/**
 * A Comparator Class for comparing 2 Rating objects according to their HashCode s.
 */
public class HashCodeComparator implements Comparator<Rating> {

    @Override
    public int compare(Rating r1, Rating r2) {
        Integer int1 = r1.hashCode();
        Integer int2 = r2.hashCode();
        return int1.compareTo(int2);
    }
}
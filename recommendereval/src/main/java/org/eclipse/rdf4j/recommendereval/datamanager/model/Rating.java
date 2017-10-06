/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.datamanager.model;

import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A class that is used by Data Manager of Evaluator for keeping data.
 */
public class Rating {

    private String userURI;
    private String itemURI;
    private Double ratingValue;
    
    /**
     * Required when removing statements from the repository. (Test set ratings removal)
     * 
     * Check removeStatements() method at EvalDataManager class.
     */
    private final HashMap<String,String> otherEntities;
    
    public Rating() {
        this.otherEntities = new HashMap<>();
    }
    
    public Rating(String userURI, String itemURI, Double ratingValue) {
        this.userURI = userURI;
        this.itemURI = itemURI;
        this.ratingValue = ratingValue;
        this.otherEntities = new HashMap<>();
    }

    public String getUserURI() {
        return userURI;
    }

    public void setUserURI(String userURI) {
        this.userURI = userURI;
    }

    public String getItemURI() {
        return itemURI;
    }

    public void setItemURI(String itemURI) {
        this.itemURI = itemURI;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }
    
    public void addEntity(String key, String value) {
        this.otherEntities.put(key, value);
    }
    
    public HashMap<String,String> getOtherEntities() {
        return this.otherEntities;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(userURI).
            append(itemURI).
            append(ratingValue).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Rating other = (Rating) obj;
        
        if ((this.userURI == null) ? (other.userURI != null) : !this.userURI.equals(other.userURI)) {
            return false;
        }
        if ((this.itemURI == null) ? (other.itemURI != null) : !this.itemURI.equals(other.itemURI)) {
            return false;
        }
        return !(!Objects.equals(this.ratingValue, other.ratingValue) && (this.ratingValue == null || !this.ratingValue.equals(other.ratingValue)));
    }
    
    @Override
    public String toString() {
        return userURI + " " + itemURI + " " + ratingValue;
    }
}

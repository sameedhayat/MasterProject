/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.datamanager.model;

import java.util.ArrayList;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * This class is created to get rid of the confusion for the 3D Folds List.
 */
public class Folds {
    
    private final ArrayList<ArrayList<ArrayList<Rating>>> usersFoldsRatings;
    
    private int numberOfFolds;
    
    public Folds(ArrayList<ArrayList<ArrayList<Rating>>> foldsList) {
        this.usersFoldsRatings = foldsList;
        
        for( int i = 0 ; i < usersFoldsRatings.size() ; i++ ) {
            if( usersFoldsRatings.get(i).size() > 0 ) {
                numberOfFolds = usersFoldsRatings.get(i).size();
                break;
            }
        } 
        
        if( numberOfFolds == 0 ) 
            throw new EvaluatorException("Number of folds is 0! Please check the settings and input file.");
    }   
    
    /**
     * Return the folds of all users. Result is a 3D list and
     * the order is Users-Folds-Ratings(outer to inner)
     * @return 
     */
    public ArrayList<ArrayList<ArrayList<Rating>>> getFoldsOfAllUsers() {
        return usersFoldsRatings;
    } 
    
    /**
     * Return the folds of user with provided user index. Result is a 2D list and
     * the order is Folds-Ratings(outer to inner)
     * @param userIndex
     * @return 
     */
    public ArrayList<ArrayList<Rating>> getFoldsOfUser(int userIndex) {
        return usersFoldsRatings.get(userIndex);
    } 
    
    /**
     * Return the ratings of user with provided user index and provided fold. 
     * @param userIndex
     * @param foldIndex
     * @return 
     */
    public ArrayList<Rating> getRatingsOfFold(int userIndex, int foldIndex) {
        return usersFoldsRatings.get(userIndex).get(foldIndex);
    } 
    
    public int getNumberOfFolds() {
        return numberOfFolds;
    }
}

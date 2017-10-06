/* 
 * Kemal Cagin Gulsen
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */

package org.eclipse.rdf4j.recommendereval.util;

import org.eclipse.rdf4j.recommendereval.datamanager.model.Rating;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rdf4j.recommendereval.exception.EvaluatorException;

/**
 * Creates random ratings and saves them to a file. A helper class for creating 
 * files for testing.
 */
public class InputCreator {

    public void createAndSaveFile(int numberOfItems, int numberOfUsers, int itemsPerUser,
            int numberOfFeatures, double minRatingValue, double maxRatingValue, String fileName) 
            throws EvaluatorException {
     
        if( itemsPerUser > numberOfItems ) {
            throw new EvaluatorException("NUMBER OF ITEMS PER USER SHOULD BE LESS THAN NUMBER OF ITEMS");
        }
            
        ArrayList<String> itemUris = new ArrayList<>();
        ArrayList<String> userUris = new ArrayList<>();
        ArrayList<String> prefixes = new ArrayList<>();
        ArrayList<String> features = new ArrayList<>();
        
        ArrayList<Rating> ratings  = new ArrayList<>(); 
        
        prefixes.add("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");       
        
        for( int i = 0 ; i < numberOfItems ; i++ ) {
            itemUris.add("<http://example.org/movies#Item" + (i+1) + ">");
            int random1 = getRandomInt(1, numberOfFeatures);
            int random2 = getRandomInt(1, numberOfFeatures);
            features.add("<http://example.org/movies#Item" + (i+1) 
                    + "> <http://example.org/movies#hasGenre> " + 
                    " <http://example.org/movies#Genre" + random1 + "> .");
            if( random1 != random2 ) {
                features.add("<http://example.org/movies#Item" + (i+1) 
                        + "> <http://example.org/movies#hasGenre> " + 
                        " <http://example.org/movies#Genre" + random2 + "> .");
            }
        }
        
        for( int i = 0 ; i < numberOfUsers ; i++ ) {
            userUris.add("<http://example.org/movies#User" + (i+1) + ">");
        }
                
        for( int i = 0 ; i < numberOfUsers ; i++ ) {
            
            ArrayList<String> tempItems = new ArrayList<>(itemUris);
            Collections.shuffle(tempItems);
            
            for( int j = 0 ; j < itemsPerUser ; j++ ) {
                Rating newRating = new Rating(userUris.get(i), 
                        tempItems.get(j), getRandomDouble(minRatingValue,maxRatingValue));
                ratings.add(newRating);
            }
        }
        
        writeToFile(ratings,features,prefixes,fileName);        
    }

    private Double getRandomDouble(double minRatingValue, double maxRatingValue) {
        Random r = new Random();
        double randomValue = minRatingValue + (maxRatingValue - minRatingValue) * r.nextDouble();
        return randomValue;
    }

    private int getRandomInt(int minRatingValue, int maxRatingValue) {
        Random r = new Random();
        int randomValue = r.nextInt(maxRatingValue) + minRatingValue;
        return randomValue;
    }

    private void writeToFile(ArrayList<Rating> ratings, ArrayList<String> features,
            ArrayList<String> prefixes, String fileName) {
        
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.#", otherSymbols);
        
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");
            
            try (BufferedWriter bw = new BufferedWriter(w)) {
                for (int i = 0; i < prefixes.size() ; i++) {
                    bw.write(prefixes.get(i) + "\n");
                }
                
                bw.write("\n");
                
                for (int i = 0; i < ratings.size() ; i++) {
                    // movies:User1 movies:hasRated movies:Rating1 .
                    // movies:Rating1 movies:ratedMovie movies:Item1 .
                    // movies:Rating1 movies:hasRating "3.2" .
                    bw.write(ratings.get(i).getUserURI() + " ");
                    bw.write("<http://example.org/movies#hasRated> ");
                    bw.write("<http://example.org/movies#Rating" + (i+1) + "> ." + "\n");
                    bw.write("<http://example.org/movies#Rating" + (i+1) + "> ");
                    bw.write("<http://example.org/movies#ratedMovie> ");
                    bw.write(ratings.get(i).getItemURI() + " .\n");
                    bw.write("<http://example.org/movies#Rating" + (i+1) + "> ");
                    bw.write("<http://example.org/movies#hasRating> ");
                    bw.write("\"" + df.format(ratings.get(i).getRatingValue()) + "\" .\n");
                    bw.write("\n");
                }
                
                bw.write("\n");
                bw.write("\n");
                
                for (int i = 0; i < features.size() ; i++) {
                    bw.write(features.get(i) + "\n");
                }
                
                bw.flush();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InputCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InputCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

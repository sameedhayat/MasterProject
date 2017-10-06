package org.eclipse.rdf4j.recommender.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TFIDF {
	
	private HashMap<String, List<Integer>> hm = new HashMap<String, List<Integer>>();
	List<List<String>> docs = new ArrayList<List<String>>();
	Integer docLength;
	
	public TFIDF(List<List<String>> docs){
		
		this.docs = docs;
		docLength = docs.size();
		createHashMap(docs);
	
	}
	
	
	public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result;
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(String term) {
    	
    	double n = hm.get(term.toLowerCase()).size();
    	
        double idf_score = Math.log( docLength / n);
    	return idf_score;
    }


    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public HashMap<String,List<Integer>> createHashMap(List<List<String>> docs) {
    	
    	for (int i = 0; i < docs.size(); i++) {
    		List<String> doc = docs.get(i);
    		for (String word : doc) {
            	word = word.toLowerCase();
            	if(!hm.containsKey(word)){ 
            		List<Integer> l = new ArrayList<Integer>();
            		l.add(i);
            		hm.put(word,l);
            	}
            	else{
            		if (hm.get(word).get(hm.get(word).size()-1) != i){
            			List<Integer> l = hm.get(word);
            			l.add(i);
            			hm.put(word, l);
            		}
            	}
            }
    	}
        return hm;
    }

    
    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(List<String> doc, String term) {
        return tf(doc, term) * idf(term);

    }
    
    /**
     * @param term term
     * @return the TF-IDF of term
     */
    public List<String> findElementInHashMap(Set<String> doc) {
    	List<String> ret = new ArrayList<String>();
    	for(String word: doc){
    		word = word.toLowerCase();
    		if (hm.containsKey(word)) {
    		    ret.add(word);
    		}
    	
    	}
    	return ret;
    }
    

}

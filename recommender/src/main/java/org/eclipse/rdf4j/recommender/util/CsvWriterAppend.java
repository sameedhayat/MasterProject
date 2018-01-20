package org.eclipse.rdf4j.recommender.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quintet;

public class CsvWriterAppend {
	
	/**
     * Write hashmap to csv (Doc2Vec or Rdf2Vec embeddings)
     * @param path : path to save the data file
     * @param hm : hashmap containing resource URI as key and embedding as value
     */
	public static void writeCsvHashMap(String path,HashMap<String,List<Double>> hm) {
		String eol = System.getProperty("line.separator");
		try (Writer writer = new FileWriter(path)) {
            for (Map.Entry<String,List<Double>> entry : hm.entrySet()) {
              writer.append(entry.getKey())
                    .append(' ')
                    .append(convertListToString(entry.getValue()))
                    .append(eol);
            }
          } catch (IOException ex) {
            ex.printStackTrace(System.err);
          }
	}
	
	/**
     * Write user hashmap to csv (user embeddings)
     * @param path : path to save the data file
     * @param hm : hashmap containing user URI as key and embedding as value
     */
	public static void writeCsvHashMapUser(String path,HashMap<Integer,List<Double>> hm) {
		String eol = System.getProperty("line.separator");
		try (Writer writer = new FileWriter(path)) {
            for (Map.Entry<Integer,List<Double>> entry : hm.entrySet()) {
              writer.append(entry.getKey().toString())
                    .append(' ')
                    .append(convertListToString(entry.getValue()))
                    .append(eol);
            }
          } catch (IOException ex) {
            ex.printStackTrace(System.err);
          }
	}
	
	/**
     * Read hashmap to csv (Doc2Vec or Rdf2Vec embeddings)
     * @param path : path to save the data file
     * @param hm : hashmap containing resource URI as key and embedding as value
     */
	public static HashMap<String,List<Double>> readCsvHashMap(String path) throws NumberFormatException, IOException {
		HashMap<String,List<Double>> hm = new HashMap<String,List<Double>>();
		BufferedReader br = null;
	    String line = "";
	    String cvsSplitBy = " ";
		
	    br = new BufferedReader(new FileReader(path));
        while ((line = br.readLine()) != null) {
        	String[] l = line.split(cvsSplitBy);
        	String key = (String)l[0];
        	List<Double> doubleList= new ArrayList<Double>();
        	int size = l.length;
        	for(String s : Arrays.asList(l).subList(1, size)) doubleList.add(Double.parseDouble(s));
        	hm.put(key, doubleList);
        }
        
        return hm;
	}
	
	/**
     * Read user hashmap to csv (user embeddings)
     * @param path : path to save the data file
     * @param hm : hashmap containing user URI as key and embedding as value
     */
	public static HashMap<Integer,List<Double>> readCsvHashMapUser(String path) throws NumberFormatException, IOException {
		HashMap<Integer,List<Double>> hm = new HashMap<Integer,List<Double>>();
		BufferedReader br = null;
	    String line = "";
	    String cvsSplitBy = " ";
		
	    br = new BufferedReader(new FileReader(path));
        while ((line = br.readLine()) != null) {
        	String[] l = line.split(cvsSplitBy);
        	Integer key =Integer.parseInt(l[0]);
        	List<Double> doubleList= new ArrayList<Double>();
        	int size = l.length;
        	for(String s : Arrays.asList(l).subList(1, size)) doubleList.add(Double.parseDouble(s));
        	hm.put(key, doubleList);
        }
        
        return hm;
	}
	
	/**
     * Convert list to string seperated by space
     * @param l : list to be converted
     */
	public static String convertListToString(List<Double> l) {
		String res = "";
		int check = 0;
		for(Double d : l) {
			if(check == 0) {
				res = Double.toString(d);
				check = 1;
			}else {
				res += " " + Double.toString(d);
			}
		}
		return res;
	}
	
	/**
     * Convert list to string seperated by comma
     * @param l : list to be converted
     */
	public static String convertListToString1(List<Double> l) {
		String res = "";
		int check = 0;
		for(Double d : l) {
			if(check == 0) {
				res = Double.toString(d);
				check = 1;
			}else {
				res += "," + Double.toString(d);
			}
		}
		return res;
	}
	
	/**
     * Write ml file and append
     * @param path : path where file should be written
     * @param hm : list containing embedding
     */
	public static void writeMlData(String path, List<Pair<List<Double>,String>> hm) {
		File f = new File(path);
        if(!f.exists()){
        	  try{ 
        	    f.createNewFile();
        	  }catch(Exception e){
        	    e.printStackTrace();
        	  }
        }
        try {
        	
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
            
            for(Pair<List<Double>,String> userId : hm) {
//            	Pair<List<Double>,String> l = hm.get(userId);
            	if(userId.getValue0().size() < 800){
            		continue;
            	}
            	String result = convertListToString1(userId.getValue0());
            	String wr = result + "," + userId.getValue1();
            	out.println(wr);
			}
            out.close();
        }catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	/**
     * Write ml file using only one embedding and append
     * @param path : path where file should be written
     * @param hm : list containing embedding
     */
	public static void writeMlDataOneEmbedding(String path, List<Pair<List<Double>,String>> hm) {
		File f = new File(path);
        if(!f.exists()){
        	  try{ 
        	    f.createNewFile();
        	  }catch(Exception e){
        	    e.printStackTrace();
        	  }
        }
        try {
        	
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
            
            for(Pair<List<Double>,String> userId : hm) {
//            	Pair<List<Double>,String> l = hm.get(userId);
            	if(userId.getValue0().size() < 400){
            		continue;
            	}
            	String result = convertListToString1(userId.getValue0());
            	String wr = result + "," + userId.getValue1();
            	out.println(wr);
			}
            out.close();
        }catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	
	/**
     * Write one instance used for prediction
     * @param path : path where file should be written
     * @param hm : list containing embedding
     */
	public static void writeMlDataOneInstance(String path, HashMap<Integer,List<Double>> hm) throws IOException {
		File f = new File(path);
		f.delete();
		f.createNewFile();
        try {
        	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            
            for(Integer userId : hm.keySet()) {
            	List<Double> l = hm.get(userId);
            	String result = convertListToString1(l);
            	out.println(result);
			}
            out.close();
        }catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	
		
}

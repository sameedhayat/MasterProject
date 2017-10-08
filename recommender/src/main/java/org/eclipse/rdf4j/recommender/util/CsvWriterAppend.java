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

import org.javatuples.Quintet;

public class CsvWriterAppend {
	
	public static void writeCsvHashMap(String path,HashMap<String,List<Double>> hm) {
		String eol = System.getProperty("line.separator");
		try (Writer writer = new FileWriter(path)) {
            for (Map.Entry<String,List<Double>> entry : hm.entrySet()) {
              writer.append(entry.getKey())
                    .append(',')
                    .append(convertListToString(entry.getValue()))
                    .append(eol);
            }
          } catch (IOException ex) {
            ex.printStackTrace(System.err);
          }
	}
	
	public static HashMap<String,List<Double>> readCsvHashMap(String path) throws NumberFormatException, IOException {
		HashMap<String,List<Double>> hm = new HashMap<String,List<Double>>();
		BufferedReader br = null;
	    String line = "";
	    String cvsSplitBy = ",";
		
	    br = new BufferedReader(new FileReader(path));
        while ((line = br.readLine()) != null) {
        	String[] l = line.split(cvsSplitBy);
        	String key = (String)l[0];
        	List<Double> doubleList= new ArrayList<Double>();
        	int size = l.length;
        	for(String s : Arrays.asList(l)) doubleList.add(Double.parseDouble(s));
        	hm.put(l[0], doubleList.subList(1, size));
        }
        
        return hm;
	}
	
	
	public static void appendCsv(String path, List<Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer>> data) {
				
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
	                
	                for(Quintet<List<Double>, List<Double>, List<Double>, List<Double>, Integer> q : data) {
	                	String result = convertListToString(q.getValue0());
	                	result += "," + convertListToString(q.getValue1());
	                	result += "," + convertListToString(q.getValue2());
	                	result += "," + convertListToString(q.getValue3());
	                	result += "," + Integer.toString(q.getValue4());
	                	
	                	out.println(result);
					}
	                out.close();
		        }catch (IOException e){
	            	e.printStackTrace();
	            }
         }
	public static String convertListToString(List<Double> l) {
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
		
}

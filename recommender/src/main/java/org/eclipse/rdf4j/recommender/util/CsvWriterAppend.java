package org.eclipse.rdf4j.recommender.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Quintet;

public class CsvWriterAppend {
	
	public static void csvHashMap(String path,HashMap<String,List<Double>> hm) {
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

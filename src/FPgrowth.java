import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

class FrequentItem{
	private int itemId;
	private int count;
	
	public FrequentItem(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
	
	public int getItemId(){
		return itemId;
	}

	public int getCount(){
		return count;
	}
	
	public String toString(){
		return itemId+":"+count;
	}
	
}

class FrequentPattern{
	private ArrayList<Integer> pattern =  new ArrayList<Integer>();
	private int count;
	
	public FrequentPattern(ArrayList<Integer> pattern, int count){
		this.pattern = pattern;
		this.count = count;
	}
	
	public ArrayList<Integer> getFrequentPattern(){
		return pattern;
	}
	
	public int getCount(){
		return count;
	}
	
	public String toString(){
		return pattern.toString()+":"+count;
	}
	
	public boolean equals(FrequentPattern fp){
		ArrayList<Integer> itemset = fp.getFrequentPattern();
		if((itemset.size())!=(pattern.size())) {
            return false;
        }
		
		boolean found;
        for(int i=0;i<itemset.size();i++){
        	found = false;
        	for(int j=0;j<pattern.size();j++){
	            if(itemset.get(i) == pattern.get(j)) {
	                found = true;
	            }
        	}
        	
        	if(!found){
        		return false;
        	}
        }
        return true;
	}
}

class Pair{
	private int id;
	private int order;
	
	public Pair(int id, int order){
		this.id = id;
		this.order = order;
	}
	
	public int getId(){
		return id;
	}
	
	public int getOrder(){
		return order;
	}
	
	public String toString(){
		return id+" "+order;
	}
}


public class FPgrowth {
	
	public static double minMiniSup;
	public static double maxMiniSup;
	public static double minMiniConf;
	public static double maxMiniConf;
	public static double delta;
	public static char option;
	public static String DB;
	public static double MaxMemory;

	public static void main(String[] args) throws IOException {
		int numTran = 0;
		long startTime;
		long endTime;
		long totalTime;
		int numFP;
		int numRule;
		double current_memory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
		BufferedWriter bw;
		ArrayList<Double> cacheExp = new ArrayList<Double>();
		
		// Parameter setting
		if(!cmdParser(args)){
			System.out.println("Please enter your parameter.");
			System.exit(0);
		}else{
			double min = 0;
			double max = 0;
			switch(option){
			case 'c':
				min = minMiniConf;
				max = maxMiniConf;
				break;
			case 's':
				min = minMiniSup;
				max = maxMiniSup;
				break;
			default:
				
				break;
			}
			
			while(min <= max){
				cacheExp.add(min);
				min += delta;
			}
		}
		bw = new BufferedWriter(new FileWriter("exp/"+DB+"_"+option+"_result.csv"));
		bw.write("Minimum Support/Confidence,# of Frequent Patterns,# of Rules,Total Time,Maximum Memory");
		bw.newLine();
		
		int MSC;
		for(double minValue:cacheExp){
			numFP = 0;
			numRule = 0;
			MaxMemory = current_memory;
			
			//data structure initialization
			HashMap<Integer, Integer> L1 = new HashMap<Integer,Integer>();
			Tree FPtree = null;
			HashMap<String,Integer> fPatterns = new HashMap<String,Integer>();
			
			//start timing
			startTime = System.currentTimeMillis();
			
			numTran = genL1(L1);
			if(minMiniSup != maxMiniSup){
				MSC = (int)Math.ceil(((double)numTran*minValue/100.0));
			}else{
				MSC = (int)Math.ceil(((double)numTran*minMiniSup/100.0));
			}
			
			System.out.println("Minimum Support Count:"+MSC);
			
			FPtree = new Tree(MSC);
			FPtree.genFList(L1);
			
			constructFPtree(FPtree);
			
			current_memory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
			if(current_memory>MaxMemory)
				MaxMemory = current_memory;
			
			//FPtree.traverseTree();
			
			FPtree.growth(fPatterns);
			
			current_memory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
			if(current_memory>MaxMemory)
				MaxMemory = current_memory;
			

			//saveFrequentPattern(fPatterns);
			numFP = fPatterns.size();
			
			if(minMiniConf == maxMiniConf){
				numRule = genRule(fPatterns,MSC,minMiniConf);
			}else{
				numRule = genRule(fPatterns,MSC,minValue);
			}
			
			
			current_memory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
			if(current_memory>MaxMemory)
				MaxMemory = current_memory;
			
			endTime = System.currentTimeMillis();
			totalTime = (endTime - startTime)/1000;
			bw.write(minValue+","+numFP+","+numRule+","+totalTime+","+MaxMemory);
			bw.newLine();
			System.gc();
		}
		bw.close();
		System.out.println("Mission Completed!");
	}
	
	public static boolean cmdParser(String[] args){
		boolean pass = true;
		Options options = new Options();
		options.addOption("fs", true, "[Test Start Support(%)]");
		options.addOption("ts", true, "[Test End Support(%)]");
		options.addOption("fc", true, "[Test Start Confidence(%)]");
		options.addOption("tc", true, "[Test End Confidence(%)]");
		options.addOption("d", true, "[Delta Support(%)]");
		options.addOption("o", true, "[Option]c or s");
		options.addOption("db", true, "[Database Path]");
		
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if(	cmd.hasOption("fs") && cmd.hasOption("ts") 
				&& cmd.hasOption("d")&& cmd.hasOption("o")
				&& cmd.hasOption("fc") && cmd.hasOption("tc") 
				&& cmd.hasOption("db")){
				minMiniSup = Double.parseDouble(cmd.getOptionValue("fs"));
				maxMiniSup = Double.parseDouble(cmd.getOptionValue("ts"));
				minMiniConf = Double.parseDouble(cmd.getOptionValue("fc"));
				maxMiniConf = Double.parseDouble(cmd.getOptionValue("tc"));
				delta = Double.parseDouble(cmd.getOptionValue("d"));
				option = cmd.getOptionValue("o").charAt(0);
				DB = cmd.getOptionValue("db");
				
				System.out.println(	 "-fs "+minMiniSup
									+" -ts "+maxMiniSup
									+" -fc "+minMiniConf
									+" -tc "+maxMiniConf
									+" -d "+delta
									+" -o "+option
									+" -db "+DB);
			}else{
				pass = false;
			}
		} catch (org.apache.commons.cli.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pass;
	}
	
	public static int genL1(HashMap<Integer,Integer> L1){
		BufferedReader br = null;
		ArrayList<Integer> itemset = new ArrayList<Integer>();
		int numTran = 0;
		
		try {
			String trans;
			String[] items;
			
			br = new BufferedReader(new FileReader("Dataset/" + DB));
			
			while((trans = br.readLine())!= null){
				numTran++;
				items = trans.split(",");
				for(String item:items){
					itemset.add(Integer.parseInt(item.trim()));
				}
				
				for(int itemId:itemset){
					if(L1.containsKey(itemId)){
						L1.put(itemId, L1.get(itemId)+1);
					}else{
						L1.put(itemId, 1);
					}
				}
				itemset.clear();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return numTran;
	}
	
	public static void genFList(HashMap<Integer,Integer> L1, List<FrequentItem> fList, HashMap<Integer,Integer> HashFList, int miniSup){
		
		Iterator<Entry<Integer, Integer>> it = L1.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pairs = (Map.Entry<Integer,Integer>)it.next();
	        if(((Integer) pairs.getValue()) >= miniSup){
	        	fList.add(new FrequentItem((Integer)pairs.getKey(), (Integer)pairs.getValue()));
			}
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    /*System.out.println("fList before sorted");
	    System.out.println(tempList.toString());*/
		
		//sort fList with respect to minimum support
	    Collections.sort(fList,
	            new Comparator<FrequentItem>() {
	                public int compare(FrequentItem o1, FrequentItem o2) {
	                    return o2.getCount()-o1.getCount();
	                }
	            });
	    
	    //Show the fList after sorted
	   /*System.out.println("====fList after sorted====");
	    System.out.println(fList.toString());*/
	    int order = 0;
	    for(FrequentItem item:fList){
	    	HashFList.put(item.getItemId(), order);
	    	order++;
	    }
	}
	
	public static void constructFPtree(Tree FPtree){
		BufferedReader br = null;
		ArrayList<Integer> itemset = new ArrayList<Integer>();
		
		try {
			String trans;
			String[] items;
			
			br = new BufferedReader(new FileReader("Dataset/" + DB));
			
			while((trans = br.readLine())!= null){
				items = trans.split(",");
				for(String item:items){
					itemset.add(Integer.parseInt(item.trim()));
				}
				
				FPtree.sortItems(itemset);
				
				FPtree.insertTransaction(itemset);
				
				itemset.clear();
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveFrequentPattern(HashMap<String,Integer> fPatterns){
		Iterator<Entry<String, Integer>> it = fPatterns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> fp = (Map.Entry<String,Integer>)it.next();
			System.out.println(fp.getKey()+":"+fp.getValue());
		}
	}
	
	public static int genRule(HashMap<String,Integer> fPatterns, int minSup, double minConf){
		int numRule = 0;
		double LHSCount = 0;
		double count;
		double confidence;
		ArrayList<Integer> itemset = new ArrayList<Integer>();
		ArrayList<Integer> LHS = new ArrayList<Integer>();
		ArrayList<Integer> RHS = new ArrayList<Integer>();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("exp/"+DB+"_s"+minSup+"_c"+minConf+"_rule.txt"));
			Iterator<Entry<String, Integer>> it = fPatterns.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<String,Integer> fp = (Map.Entry<String,Integer>)it.next();
		        StringTokenizer tokens = new StringTokenizer(fp.getKey(), "[], ");
		        count = fp.getValue();
		        
		        while(tokens.hasMoreTokens()){
		        	itemset.add(Integer.parseInt(tokens.nextToken()));
		        }
		        
		        char[] digit = new char[itemset.size()]; 
		        
		        for(int i = 0; i <digit.length; i++) 
		            digit[i] = '0'; 
		        
		        while(true) { 
		            // 找第一個0，並將找到前所經過的元素變為0
		            int i;
		            for(i = 0; i < digit.length && digit[i] == '1';digit[i] = '0', i++); 

		            if(i == digit.length)  // 找不到0 
		                break; 
		            else          // 將第一個找到的0變為1 
		                digit[i] = '1'; 

		            // 找第一個1，並記錄對應位置 
		            for(i = 0; i < digit.length && digit[i] == '0'; i++){
		            	RHS.add(itemset.get(i));
		            }
		        
		            for(int j = i; j < digit.length; j++)
		            {
		                if(digit[j] == '1'){
		                	LHS.add(itemset.get(j));
		                }else{
		                	RHS.add(itemset.get(j));
		                }
		            }
		         
		            LHSCount = fPatterns.get(LHS.toString());
		            confidence = count/LHSCount;
		            
		            if(confidence >= (minConf/100.0) && LHS.size() < itemset.size()){
		            	numRule++;
		            	//System.out.println(LHS.toString()+"->"+RHS.toString());
						bw.write(LHS.toString()+"->"+RHS.toString());
						bw.newLine();    			
		            }
		            LHS.clear();
			        RHS.clear();
		        } 	        
		        itemset.clear();
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
			
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return numRule;
	}
}



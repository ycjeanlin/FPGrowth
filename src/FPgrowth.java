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
	public static double miniConf;
	public static String DB;
	public static double MaxMemory;

	public static void main(String[] args) throws IOException {
		int numTran = 0;
		int MSC;
		double minSup;
		int times;
		long startTime;
		long endTime;
		long totalTime;
		int numFP;
		int numRule;
		BufferedWriter bw = new BufferedWriter(new FileWriter(DB+"_result.csv"));
		
		// Parameter setting
		if(args.length == 0){
			System.out.println("Please enter your parameter.");
			System.exit(0);
		}else{
			minMiniSup = Double.parseDouble(args[0]);
			maxMiniSup = Double.parseDouble(args[1]);
			miniConf = Double.parseDouble(args[2]);
			DB = args[3];
		}
		
		bw.write("Minimum Support,# of Frequent Patterns,# of Rules,Total Time,Maximum Memory");
		bw.newLine();
		times = (int) (maxMiniSup/minMiniSup);
		
		while(times > 0){
			times--;
			numFP = 0;
			numRule = 0;
			
			//data structure initialization
			HashMap<Integer, Integer> L1 = new HashMap<Integer,Integer>();
			Tree FPtree = null;
			HashMap<String,Integer> fPatterns = new HashMap<String,Integer>();
			
			//start timing
			startTime = System.currentTimeMillis();
			
			numTran = genL1(L1);
			minSup = numTran*0.1/100.0;
			System.out.println(minSup);
			MSC = (int) minSup*times;
			
			FPtree = new Tree(MSC);
			FPtree.genFList(L1);
			
			constructFPtree(FPtree);
			
			//FPtree.traverseTree();
			
			FPtree.growth(fPatterns);
			
			saveFrequentPattern(fPatterns);
			numFP = fPatterns.size();
			
			numRule = genRule(fPatterns,MSC);
			
			endTime = System.currentTimeMillis();
			totalTime = (endTime - startTime)/1000;
			bw.write(minSup+","+numFP+","+numRule+","+totalTime+","+MaxMemory);
			bw.newLine();
			
		}
		
		bw.close();
	}
	
	public static int genL1(HashMap<Integer,Integer> L1){
		BufferedReader br = null;
		ArrayList<Integer> itemset = new ArrayList<Integer>();
		int numTran = 0;
		
		try {
			String trans;
			String[] items;
			
			br = new BufferedReader(new FileReader("Dataset\\" + DB));
			
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
			
			br = new BufferedReader(new FileReader("Dataset//" + DB));
			
			while((trans = br.readLine())!= null){
				items = trans.split(",");
				for(String item:items){
					itemset.add(Integer.parseInt(item.trim()));
				}
				
				FPtree.sortItems(itemset);
				
				FPtree.insertTransaction(itemset);
				
				itemset.clear();
			}
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
	
	public static int genRule(HashMap<String,Integer> fPatterns, int minSup){
		int numRule = 0;
		double LHSCount = 0;
		double count;
		double confidence;
		ArrayList<Integer> itemset = new ArrayList<Integer>();
		ArrayList<Integer> LHS = new ArrayList<Integer>();
		ArrayList<Integer> RHS = new ArrayList<Integer>();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(DB+"_"+minSup+"_rule.txt"));
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
		            System.out.println(LHS.toString());
		            LHSCount = fPatterns.get(LHS.toString());
		            confidence = count/LHSCount;
		            
		            if(confidence >= miniConf && LHS.size() < itemset.size()){
		            	numRule++;
		            	System.out.println(LHS.toString()+"->"+RHS.toString());
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



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
		int MCC;
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
			DB = args[2];
		}
		
		bw.write("Minimum Support,# of Frequent Patterns,# of Rules,Total Time,Maximum Memory");
		bw.newLine();
		times = (int) (maxMiniSup/minMiniSup);
		
		while(times > 0){
			times--;
			numFP = 0;
			numRule = 0;
			minSup = (numTran*minMiniSup);
			//data structure initialization
			HashMap<Integer, Integer> L1 = new HashMap<Integer,Integer>();
			Tree FPtree = null;
			HashMap<ArrayList<Integer>,Integer> fPatterns = new HashMap<ArrayList<Integer>,Integer>();
			
			//start timing
			startTime = System.currentTimeMillis();
			
			numTran = genL1(L1);
			
			MSC = (int) minSup*times;
			MCC = (int) (numTran*miniConf);
			
			FPtree = new Tree(MSC);
			FPtree.genFList(L1);
			
			constructFPtree(FPtree);
			
			//FPtree.traverseTree();
			
			FPtree.growth(fPatterns);
			
			numFP = fPatterns.size();
			
			numRule = genRule(fPatterns);
			
			endTime = System.currentTimeMillis();
			totalTime = (endTime - startTime)/1000;
			bw.write(minSup+","+numFP+","+totalTime+","+MaxMemory);
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
	
	public static int genRule(HashMap<ArrayList<Integer>,Integer> fPatterns){
		int numRule = 0;
		double sup1;
		double sup2;
		double confidence;
		ArrayList<Integer> itemset = null;
		ArrayList<Integer> LHS = null;
		ArrayList<Integer> RHS = null;
		
		Iterator<Entry<ArrayList<Integer>, Integer>> it = fPatterns.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry<ArrayList<Integer>,Integer> fp = (Map.Entry<ArrayList<Integer>,Integer>)it.next();
	        itemset = fp.getKey();
	        LHS = (ArrayList<Integer>) itemset.clone();
	        for(int itemId:itemset){
	        	RHS.add(LHS.remove(LHS.size()-1));
	        	sup1 = fPatterns.get(LHS);
	        	sup2 = fPatterns.get(RHS);
	        	
	        }
	        
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		return numRule;
	}
}



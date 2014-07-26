import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class frequentItem{
	private int itemId;
	private int count;
	
	public frequentItem(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
	
	public int getItemId(){
		return itemId;
	}

	public int count(){
		return count;
	}
	
	public String toString(){
		return itemId+"\t"+count;
	}
}

class Transaction{
	public ArrayList<Integer> itemset = new ArrayList<Integer>();

	public Transaction(ArrayList<Integer> itemset){
		this.itemset = itemset;
	}
}

public class FPgrowth {
	
	public static int MiniSup = 0;
	public static String DB;
	


	public static void main(String[] args) {
		// Parameter setting
		if(args.length == 0){
			System.out.println("Please enter your parameter.");
			System.exit(0);
		}else{
			MiniSup = Integer.parseInt(args[0]);
			DB = args[1];
		}
		
		//data structure initialization
		BufferedReader br = null;
		HashMap<Integer,Integer> fList =  new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> L1 = new HashMap<Integer,Integer>();
		Tree FPtree = new Tree();
		
		/*ArrayList<Pattern> fList = new ArrayList<Pattern>();
		ArrayList<Pattern> L1 = new ArrayList<Pattern>();*/
		ArrayList<Transaction> Database = new ArrayList<Transaction>();
		
		try {
			String trans;
			String[] items;
			
			br = new BufferedReader(new FileReader("Dataset//" + DB));
			
			while((trans = br.readLine())!= null){
				ArrayList<Integer> itemset = new ArrayList<Integer>();
				items = trans.split(",");
				for(String item:items){
					itemset.add(Integer.parseInt(item.trim()));
				}
				
				Database.add(new Transaction(itemset));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Check the database
		/*for(Transaction t:Database){
			System.out.println("transaction: "+t.itemset.toString());
		}*/
		
		genL1(Database, L1);
		genFList(L1, fList, MiniSup);
		
		//Check the correction of fList
		/*int key;
		Enumeration<Integer> Keys = fList.keys();
		while(Keys.hasMoreElements()){
			key = Keys.nextElement();
			System.out.println(key+":"+fList.get(key));
		}*/
		
	}
	
	public static void genL1(ArrayList<Transaction> database, HashMap<Integer,Integer> L1){
		for(Transaction t:database){
			for(int itemId:t.itemset){
				if(L1.containsKey(itemId)){
					L1.put(itemId, L1.get(itemId)+1);
				}else{
					L1.put(itemId, 1);
				}
			}
		}
	}
	
	public static void genFList(HashMap<Integer,Integer> L1, HashMap<Integer,Integer> fList, int miniSup){
		Enumeration<Integer> Keys = L1.keys();
		int key;
		
		while(Keys.hasMoreElements()){
			key = Keys.nextElement();
			if(L1.get(key)>= miniSup){
				fList.put(key, L1.get(key));
			}
		}
		
		//sort fList with respect to minimum support
		
	}
	
	public static void constructFPtree(Tree FPtree, ArrayList<Transaction> database){
		for(Transaction t:database){
			
		}
	}
	
	public void sortItems(Transaction t){
	  
	}
	
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
	    List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
	  
	    Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

	        @Override
	        public int compare(Entry<K, V> o1, Entry<K, V> o2) {
	            return o1.getValue().compareTo(o2.getValue());
	        }
	    });
	  
	    //LinkedHashMap will keep the keys in the order they are inserted
	    //which is currently sorted on natural ordering
	    Map<K,V> sortedMap = new LinkedHashMap<K,V>();
	  
	    for(Map.Entry<K,V> entry: entries){
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }
	  
	    return sortedMap;
	}
}



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
		return itemId+"\t"+count;
	}
}

/*class Transaction{
	public ArrayList<Integer> itemset = new ArrayList<Integer>();

	public Transaction(ArrayList<Integer> itemset){
		this.itemset = itemset;
	}
}*/

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
		List<FrequentItem> fList = new ArrayList<FrequentItem>();
		HashMap<Integer, Integer> L1 = new HashMap<Integer,Integer>();
		Tree FPtree = new Tree();
		
		genL1(L1);
		genFList(L1, fList, MiniSup);

		
	}
	
	public static void genL1(HashMap<Integer,Integer> L1){
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
				
				for(int itemId:itemset){
					if(L1.containsKey(itemId)){
						L1.put(itemId, L1.get(itemId)+1);
					}else{
						L1.put(itemId, 1);
					}
				}
				itemset.clear();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(L1.toString());

	}
	
	public static void genFList(HashMap<Integer,Integer> L1, List<FrequentItem> fList, int miniSup){
		
		Iterator<Entry<Integer, Integer>> it = L1.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer,Integer> pairs = (Map.Entry<Integer,Integer>)it.next();
	        if(((Integer) pairs.getValue()) >= miniSup){
				fList.add(new FrequentItem((Integer)pairs.getKey(), (Integer)pairs.getValue()));
			}
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    /*System.out.println("fList before sorted");
	    System.out.println(fList.toString());*/
		
		//sort fList with respect to minimum support
	    Collections.sort(fList,
	            new Comparator<FrequentItem>() {
	                public int compare(FrequentItem o1, FrequentItem o2) {
	                    return o2.getCount()-o1.getCount();
	                }
	            });
	    
	    /*System.out.println("fList after sorted");
	    for(Object o:fList){
	    	System.out.println(o);
	    }*/
		
	}
	
	public static void constructFPtree(Tree FPtree){

	}
	
	public void sortItems(){
	  
	}

}



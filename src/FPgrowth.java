import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


/*class Pattern{
	public int itemId;
	public int count;
	
	public Pattern(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
}*/

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
		Hashtable<Integer,Integer> fList =  new Hashtable<Integer,Integer>();
		Hashtable<Integer,Integer> L1 = new Hashtable<Integer,Integer>();
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
	
	public static void genL1(ArrayList<Transaction> database, Hashtable<Integer,Integer> L1){
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
	
	public static void genFList(Hashtable<Integer,Integer> L1, Hashtable<Integer,Integer> fList, int miniSup){
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
}

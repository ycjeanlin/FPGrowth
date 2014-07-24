import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


class Pattern{
	public int itemId;
	public int count;
	
	public Pattern(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
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
		BufferedReader br = null;
		ArrayList<Pattern> fList = new ArrayList<Pattern>();
		ArrayList<Transaction> Database = new ArrayList<Transaction>();
		
		try {
			String trans;
			String[] items;
			ArrayList<Integer> itemset = new ArrayList<Integer>();
			br = new BufferedReader(new FileReader(DB));
			
			while((trans = br.readLine())!= null){
				items = trans.split(",");
				for(String item:items){
					itemset.add(Integer.parseInt(item));
				}
				Database.add(new Transaction(itemset));
				itemset.clear();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//genL1();
	}
	
	public void genL1(){
		
	}

}

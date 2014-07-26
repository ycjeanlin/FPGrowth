import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Tree {
	private Node root;
	HashMap <Integer, Node> hTable = new HashMap<Integer, Node>();
	
	public Tree(List<FrequentItem> fList){
		for(FrequentItem it:fList){
			hTable.put(it.getItemId(), null);
		}
	}
	
	public void insertTransaction(ArrayList<Integer> transaction){
		
		for(int item:transaction){

		}
	}
	
	public void traverseTree(){
		
	}
	
}

class Node{
	int itemId;
	int count;
	Node headerLink;
	Node parentLink;
	ArrayList <Node> childlink;
	
	public Node(){
		
	}
	
	public Node(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
	
	
	
}

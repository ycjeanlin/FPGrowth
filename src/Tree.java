import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;



public class Tree {
	private Node root = new Node();
	HashMap <Integer, Node> hTable = new HashMap<Integer, Node>();
	
	public Tree(List<FrequentItem> fList){
		for(FrequentItem it:fList){
			hTable.put(it.getItemId(), null);
		}
	}
	
	public void insertTransaction(ArrayList<Integer> transaction){
		insertItem(transaction, 0 , root);
		
	}
	
	private void insertItem(ArrayList<Integer> transaction, int index, Node nextNode){
		boolean found = false;
		
		if(!nextNode.childlink.isEmpty()){
			for(Node child:nextNode.childlink){
				if(transaction.size() > index){
					if(child.itemId == transaction.get(index)){
						child.count++;
						insertItem(transaction, index+1, child);
						found = true;
						break;
					}
				}
			}
		}
		
		if(!found && transaction.size() > index){
			Node childNode = null;
			for(int i=index;i<transaction.size();i++){
				childNode = new Node(transaction.get(i),1);
				nextNode.childlink.add(childNode);
				childNode.parentLink = nextNode;
				addNodeLink(childNode);
				nextNode = childNode;
			}
		}
			
	}
	
	private void addNodeLink(Node newNode){
		Node tempNode = null;
		
		tempNode = hTable.get(newNode.itemId);
		if(!hTable.replace(newNode.itemId, tempNode, newNode)){
			System.out.println("Error addNodeLink()");
			System.exit(1);
		}
		
		newNode.headerLink = tempNode;
	}
	
	public void traverseTree(){
		Queue traverseSeq = new LinkedList();
		Node tempNode = null;
		
		System.out.println(root.toString());
		for(Node child:root.childlink){
			traverseSeq.add(child);
		}
		
		while(!traverseSeq.isEmpty()){
			tempNode = (Node)traverseSeq.remove();
			System.out.println(tempNode.toString());
			if(!tempNode.childlink.isEmpty()){
				for(Node child:tempNode.childlink){
					traverseSeq.add(child);
				}
			}
		}
	}
	
}

class Node{
	int itemId;
	int count;
	Node headerLink;
	Node parentLink;
	ArrayList <Node> childlink = new ArrayList<Node>();
	
	public Node(){
		this.itemId = 0;
		this.count = 0;
	}
	
	public Node(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
	
	public String toString(){
		return itemId+":"+count;
	}
	
	
}

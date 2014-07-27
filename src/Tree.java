import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;



public class Tree {
	private Node root = new Node();
	HashMap <Integer, Node> hTable = new HashMap<Integer, Node>();
	List<FrequentItem> fList;
	
	public Tree(){
		
	}
	
	public Tree(List<FrequentItem> fList){		
		root.parentLink = null;
		this.fList = fList;
	}
	
	public void insertTransaction(ArrayList<Integer> transaction){
		insertItem(transaction, 0 , root);
		checkHTable();
	}
	
	private void insertItem(ArrayList<Integer> transaction, int index, Node nextNode){
		boolean found = false;
		
		//Find the sharing node and if found, the count of the node increase 1
		if(!nextNode.childlink.isEmpty()){
			for(Node child:nextNode.childlink){
				if(transaction.size() > index){
					if(child.itemId == transaction.get(index)){
						child.count++;
						//Continue traversing the path until there is no sharing node
						insertItem(transaction, index+1, child);
						found = true;
						break;
					}
				}
			}
		}
		
		//Start growing new path if there is no sharing node
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
	
	
	//Traverse the FPtree
	public void traverseTree(){
		Queue<Node> traverseSeq = new LinkedList<Node>();
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
	
	public void growth(ArrayList<FrequentPattern> fPatterns){
		Node pNode = null;
		Tree subTree = new Tree();
		ArrayList<FrequentPattern> condDB = new ArrayList<FrequentPattern>();
		
		for(int i=fList.size()-1;i>=0;i--){
			FrequentItem item = fList.get(i);
			pNode = hTable.get(item.getItemId());
			genCondDB(condDB, pNode);
			System.out.println(condDB.toString());
			condDB.clear();
		}
	}
	
	//Add new node to the header table
	private void addNodeLink(Node newNode){
		Node tempNode = null;

		/*if(newNode.itemId == 4){
			System.out.println("Start Debugging");
		}*/

		tempNode = hTable.get(newNode.itemId);
		if(tempNode != null){
			if(!hTable.replace(newNode.itemId, tempNode, newNode)){
				System.out.println("Error addNode()");
				System.exit(1);
			}
			newNode.headerLink = tempNode;
		}else{
			hTable.put(newNode.itemId, newNode);
		}
	}
	
	private void genCondDB(ArrayList<FrequentPattern> condDB, Node currentNode){
		Node pNode = null;
		pNode = currentNode;
		Stack<Integer> cachePattern = new Stack<Integer>();
		int count;
		do{
			count = pNode.count;
			while(pNode.parentLink.parentLink != null){
				pNode = pNode.parentLink;
				cachePattern.push(pNode.itemId);
			}
			
			//reverse the order of the pattern
			ArrayList<Integer> pattern = new ArrayList<Integer>();
			while(!cachePattern.isEmpty()){
				pattern.add((Integer) cachePattern.pop());
			}
			
			condDB.add(new FrequentPattern(pattern, count));
			pNode = pNode.headerLink;
		}while(pNode != null);
	}
	
	private void checkHTable(){
		Node tempNode = null;
		
		for(int i = fList.size()-1;i>=0;i--){
			tempNode = hTable.get(fList.get(i).getItemId());
			while(tempNode != null){
				System.out.println(tempNode.toString());
				tempNode = tempNode.headerLink;
			}
		}
	}
		
}

class Node{
	int itemId;
	int count;
	Node headerLink = null;
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

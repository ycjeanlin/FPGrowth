import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;



public class Tree {
	private Node root = new Node();
	private HashMap <Integer, Node> hTable = new HashMap<Integer, Node>();
	private List<FrequentItem> fList = new ArrayList<FrequentItem>();
	private HashMap<Integer, Integer> HashFList = new HashMap<Integer, Integer>();//for sorting transaction
	private int miniSup;
	
	
	public Tree(){
		
	}
	
	public Tree(int miniSup){		
		root.parentLink = null;
		this.miniSup = miniSup;
	}
	
	public void genFList(HashMap<Integer,Integer> L1){
		
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
	
	public void sortItems(ArrayList<Integer> itemset){

		List<Pair> sortItem = new ArrayList<Pair>();
		
		for(int item:itemset){
			if(HashFList.containsKey(item)){
				sortItem.add(new Pair(item, HashFList.get(item)));
			}
		}
		
		Collections.sort(sortItem,
	            new Comparator<Pair>() {
	                public int compare(Pair o1, Pair o2) {
	                    return o1.getOrder()-o2.getOrder();
	                }
	            });
		
		itemset.clear();
		
		for(Pair p:sortItem){
			itemset.add(p.getId());
		}
	}
	
	public void insertTransaction(ArrayList<Integer> transaction){
		insertItem(transaction, 0 , root);
	}
	
	//Traverse the FPtree
	public void traverseTree(){
		Queue<Node> traverseSeq = new LinkedList<Node>();
		Node tempNode = null;
		
		System.out.println("====FPtree====");
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
		
		/*System.out.println("====Header Table====");
		checkHTable();*/
	}
	
	public void growth(HashMap<String,Integer> fPatterns){
		Node pNode = null;
		ArrayList<FrequentPattern> condDB = new ArrayList<FrequentPattern>();
		
		for(int i=fList.size()-1;i>=0;i--){
			//add frequent pattern to frequent pattern set 
			ArrayList<Integer> itemset = new ArrayList<Integer>();
			itemset.add(fList.get(i).getItemId());
			FrequentPattern fp = new FrequentPattern(itemset,fList.get(i).getCount());
			fPatterns.put(itemset.toString(),fList.get(i).getCount());
			
			//grow conditional FPtree for frequent pattern fp
			pNode = hTable.get(fList.get(i).getItemId());
			genCondDB(condDB, pNode);
			Tree condFPtree = new Tree(miniSup);
			condFPtree.genFList(condDB);
			condFPtree.constructFPtree(condDB);
			condFPtree.growth(fPatterns,fp);
			condDB.clear();
			
		}
	}
	
	private void growth(HashMap<String,Integer> fPatterns, FrequentPattern subfp){
		Node pNode;
		ArrayList<FrequentPattern> condDB = new ArrayList<FrequentPattern>();
		
		for(int i=fList.size()-1;i>=0;i--){
			//add frequent pattern to frequent pattern set 
			@SuppressWarnings("unchecked")
			ArrayList<Integer> itemset = (ArrayList<Integer>) subfp.getFrequentPattern().clone();
			itemset.add(fList.get(i).getItemId());
			Collections.sort(itemset);
			FrequentPattern fp = new FrequentPattern(itemset,fList.get(i).getCount());
			fPatterns.put(itemset.toString(),fList.get(i).getCount());
			
			pNode = hTable.get(fList.get(i).getItemId());
			genCondDB(condDB, pNode);
			Tree condFPtree = new Tree(miniSup);
			condFPtree.genFList(condDB);
			condFPtree.constructFPtree(condDB);

			condFPtree.growth(fPatterns,fp);
			condDB.clear();
		}
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
	
	//Add new node to the header table
	private void addNodeLink(Node newNode){
		Node tempNode = null;

		tempNode = hTable.get(newNode.itemId);
		hTable.put(newNode.itemId, newNode);
		newNode.headerLink = tempNode;
	}
	
	private void genFList(ArrayList<FrequentPattern> condDB){
		HashMap<Integer, Integer> L1 = new HashMap<Integer,Integer>();
		ArrayList<Integer> itemset = null;
		int count;
		
		//count the support of each 1-item
		for(FrequentPattern fp:condDB){
			itemset = fp.getFrequentPattern();
			count = fp.getCount();
			while(count>0){
				for(int itemId:itemset){
					if(L1.containsKey(itemId)){
						L1.put(itemId, L1.get(itemId)+1);
					}else{
						L1.put(itemId, 1);
					}
				}
				count--;
			}
		}

		genFList(L1);
	}
	
	private void genCondDB(ArrayList<FrequentPattern> condDB, Node currentNode){
		Node tempNode = null;
		Node pNode = currentNode;

		Stack<Integer> cachePattern = new Stack<Integer>();
		int count;
		do{
			count = pNode.count;
			tempNode = pNode;
			while(tempNode.parentLink.parentLink != null){
				tempNode = tempNode.parentLink;
				cachePattern.push(tempNode.itemId);
			}
			
			//reverse the order of the pattern
			ArrayList<Integer> pattern = new ArrayList<Integer>();
			while(!cachePattern.isEmpty()){
				pattern.add((Integer) cachePattern.pop());
			}
			
			if(!pattern.isEmpty()){
				condDB.add(new FrequentPattern(pattern, count));
			}
			pNode = pNode.headerLink;
		}while(pNode != null);
	}
	
	private void constructFPtree(ArrayList<FrequentPattern> condDB){
		ArrayList<Integer> itemset = null;
		int count;
		
		for(FrequentPattern fp:condDB){
			itemset = fp.getFrequentPattern();
			count = fp.getCount();
			sortItems(itemset);
			while(count>0){
				insertTransaction(itemset);
				count--;
			}
		}
	}
	
	private void checkHTable(){
		Node tempNode = null;
		
		for(int i = fList.size()-1;i>=0;i--){
			tempNode = hTable.get(fList.get(i).getItemId());
			while(tempNode != null){
				System.out.print(tempNode.toString()+ " ");
				tempNode = tempNode.headerLink;
			}
			System.out.println();
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

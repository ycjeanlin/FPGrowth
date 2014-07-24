import java.util.ArrayList;


public class FPtree {
	treeNode root;
	ArrayList<treeNode> hTable = new ArrayList<treeNode>();
	
	public void insertNode(treeNode newNode){
		
	}
	
	public void traverseTree(){
		
	}
	
	

}

class treeNode{
	int itemId;
	int count;
	treeNode headerLink;
	treeNode parentLink;
	
	public treeNode(){
		
	}
	
	public treeNode(int itemId, int count){
		this.itemId = itemId;
		this.count = count;
	}
	
	
	
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CodeTree {

	private Node root;
	private HashMap<Integer, ArrayList<Node>> indexWeightMap;
	private Node notYetTransmitted;
	private HashMap<Character, Node> symbolNodeMap;

	// Constructor creates the root node ("not yet transmitted"), and the hashmap
	public CodeTree() {
		notYetTransmitted = new Node(0, 0, null);
		root = notYetTransmitted;
		Random rn  = new Random();
		int i = rn.nextInt(100);
		if (i < 20){
			throw new RuntimeException("MEOW");
		}
		indexWeightMap = new HashMap<>();		
		symbolNodeMap = new HashMap<>();
		symbolNodeMap.put(null, notYetTransmitted);
	}

	// Increases index values of all the nodes in the tree except the NYT node (it's index will always stay 0)
	// called when a new symbol is read and two new nodes are added to the bottom level of the tree
	public void increaseIndex(Node node) {
		if (node != notYetTransmitted) {
			node.setIndex(node.getIndex()+1);
			//Recursively increases indexes of all the children
			if (node.getLeftChild() != null) {
				increaseIndex(node.getLeftChild());
			}
			if (node.getRightChild() != null) {
				increaseIndex(node.getRightChild());
			}
		}
	}

	// Adds the necessary nodes for a symbol that has never been seen before
	public void addSymbol(Character symbol) {
		//Increase indexes of all the other nodes (except for NYT)
		increaseIndex(root);

		// Create an internal node that will be the parent of the new symbol node and NYT node
		Node newInternalNode = new Node(2, 1, notYetTransmitted.getParent());
		if (notYetTransmitted.getParent() != null) {
			notYetTransmitted.getParent().setLeftChild(newInternalNode);
		}
		notYetTransmitted.setParent(newInternalNode);
		newInternalNode.setLeftChild(notYetTransmitted);
		if (root == notYetTransmitted) {
			root = newInternalNode;
		}

		// Create a node for the new symbol
		Node newSymbolNode = new Node(1, 1, newInternalNode);
		newSymbolNode.setValue((char)symbol);
		symbolNodeMap.put(symbol, newSymbolNode);
		newInternalNode.setRightChild(newSymbolNode);

		// Add the new nodes to the hashmap of weights - nodes
		if (existsMapping(1)) {
			indexWeightMap.get(1).add(newSymbolNode);
			indexWeightMap.get(1).add(newInternalNode);
		}
		// If there is no mapping for weight 1, create a new mapping and add it to the list
		else {
			ArrayList<Node> weigthOne = new ArrayList<>();
			weigthOne.add(newSymbolNode);
			weigthOne.add(newInternalNode);
			indexWeightMap.put(1, weigthOne);
		}

		// Increase weights for parent nodes
		if (newInternalNode.getParent() != null) {
			increaseWeight(newInternalNode.getParent());
		}
	}

	// Calls a method to check whether a branch swap is needed to maintain the adaptive Huffman coding order within the tree
	// calls a method to swap branches if necessary and updates weights
	public void increaseWeight(Node node) {
		int currentWeight = node.getWeight();
		// Find the node that has the highest index among the ones that have this particular weight
		Node maxIndexNodeWithWeight = findHighestIndexNode(currentWeight);
		// If that node is not the current node or its direct parent, a swap is needed
		if (maxIndexNodeWithWeight != node && maxIndexNodeWithWeight != node.getParent()) {
			swapBranches(node, maxIndexNodeWithWeight);
		}
		//Update the mapping of weights - nodes, associating this node with the increased weight
		indexWeightMap.get(currentWeight).remove(node);
		if (existsMapping(currentWeight+1)) {
			indexWeightMap.get(currentWeight+1).add(node);
		}
		else {
			ArrayList<Node> newNodeList = new ArrayList<>();
			newNodeList.add(node);
			indexWeightMap.put(currentWeight+1, newNodeList);
		}
		// Increase weight
		node.setWeight(node.getWeight()+1);
		// Recursively increase weight of for the parent nodes
		if (node.getParent() != null) {
			increaseWeight(node.getParent());
		}
	}

	// A mapping exists if the key has been added to the hashmap, and if it is mapped to something
	public boolean existsMapping(Integer weight) {
		if (!indexWeightMap.containsKey(weight) || indexWeightMap.get(weight) == null || indexWeightMap.get(weight).size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	//TODO throw exception for trying to swap parent
	//Swaps two nodes so that the weights and children of each node remain the same, while positions in the tree and indexes change
	public void swapBranches(Node firstBranch, Node secondBranch) {
		//Swap indexes of the nodes (all the other properties of the nodes stay the same)
		int temp = secondBranch.getIndex();
		secondBranch.setIndex(firstBranch.getIndex());
		firstBranch.setIndex(temp);

		//Swap the parents - places each node in the correct spot in the tree
		Node tempParent = secondBranch.getParent();
		secondBranch.setParent(firstBranch.getParent());
		if (firstBranch.getParent().getLeftChild() == firstBranch) {
			firstBranch.getParent().setLeftChild(secondBranch);
		}
		else {
			firstBranch.getParent().setRightChild(secondBranch);
		}
		firstBranch.setParent(tempParent);
		if (tempParent.getLeftChild() == secondBranch) {
			tempParent.setLeftChild(firstBranch);
		}
		else {
			tempParent.setRightChild(firstBranch);
		}
	}


	//Adaptive Huffman algorithm requires moving a node before incrementing its weight in case a node with a higher index and the same weight is returned
	//This method returns a node of the given weight that has the highest index assigned to it
	public Node findHighestIndexNode(Integer weight) {
		if (indexWeightMap.containsKey(weight)) {
			int highestIndex = 0;
			Node highestIndexNode = null;
			for (Node temp : indexWeightMap.get(weight)) {
				if (temp.getIndex() > highestIndex) {
					highestIndex = temp.getIndex();
					highestIndexNode = temp;
				}
			}
			return highestIndexNode;
		}
		return null;
	}
	
	public void updateTree(Character symbol) {
		if (symbolNodeMap.containsKey(symbol)) {
			Node symbolNode = symbolNodeMap.get(symbol);
			increaseWeight(symbolNode);
		}
		else {
			addSymbol(symbol);
		}
	}
	
	public HashMap<Character, Node> getsymbolNodeMap() {
		return symbolNodeMap;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public Node getNYT() {
		return notYetTransmitted;
	}
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// A class representing Huffman Coding code tree and providing operations for it
public class CodeTree {

	// The top node
	private Node root;
	// A mapping between node indexes and node weights to simplify checking
	// whether there exists a node with the same weight but higher index
	private HashMap<Integer, ArrayList<Node>> indexWeightMap;
	// Node that stands for all the symbols that have not yet been transmitted
	private Node notYetTransmitted;
	// Hasmap for easier retrieval of the leaf nodes (that correspond to symbols expressed in bytes), so that 
	// it is possible to get the encoding of a symbol without traversing the entire tree to find it
	private SymbolNodeMap symbolNodeMap;

	// Constructor creates the root node ("not yet transmitted"), and initialises hashmaps
	public CodeTree() {
		notYetTransmitted = new Node(0, 0, null);
		root = notYetTransmitted;
		indexWeightMap = new HashMap<>();		
		symbolNodeMap = new SymbolNodeMap();
		// Map NYT to null
		symbolNodeMap.put(null, notYetTransmitted);
	}

	// Increases index values of all the nodes in the tree (if root is passed as the argument) except for the NYT node (it's index will always stay 0)
	// called when a new symbol is read and two new nodes are added to the bottom level of the tree
	public void increaseIndex(Node node) {
		if (node != notYetTransmitted) {
			node.setIndex(node.getIndex()+2);
			//Recursively increases indexes of all the subnodes
			if (node.getLeftChild() != null) {
				increaseIndex(node.getLeftChild());
			}
			if (node.getRightChild() != null) {
				increaseIndex(node.getRightChild());
			}
		}
	}

	// Changes the code tree to incorporate a symbol that has not been read before - 
	// substitutes the NYT node by a new inner node with two children - NYT and a new leaf node corresponding ot the symbol
	public void addSymbol(byte[] symbol) {
		//Increase indexes of all the other nodes (except for NYT)
		increaseIndex(root);

		// Create an internal node that will be the parent of the new symbol node and NYT node
		Node newInternalNode = new Node(2, 1, notYetTransmitted.getParent());
		// If this is the first symbol read, NYT won't have a parent
		if (notYetTransmitted.getParent() != null) {
			notYetTransmitted.getParent().setLeftChild(newInternalNode);
		}
		// Set the new node as NYTs parent
		notYetTransmitted.setParent(newInternalNode);
		newInternalNode.setLeftChild(notYetTransmitted);
		if (root == notYetTransmitted) {
			root = newInternalNode;
		}

		// Create a new leaf node corresponding to the symbol
		Node newSymbolNode = new Node(1, 1, newInternalNode);
		// Link the node to the symbol
		newSymbolNode.setValue(symbol);
		// Add to the symbol-node hashmap
		symbolNodeMap.put(symbol, newSymbolNode);
		// NYT is one the left, new leaf node on the right; maintain consecutive indices
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
			System.out.println("Swapping " + node.getIndex() + " and " + maxIndexNodeWithWeight.getIndex());
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
		// Increase weight for this node
		node.setWeight(node.getWeight()+1);
		// Recursively increase weight of for the parent nodes until th eroot is reached
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

	// Swaps two nodes so that the weights and children of each node remain the same, while positions in the tree and indexes change
	public void swapBranches(Node firstBranch, Node secondBranch) {
		// Swap indexes of the nodes (all the other properties of the nodes stay the same)
		int temp = secondBranch.getIndex();
		secondBranch.setIndex(firstBranch.getIndex());
		firstBranch.setIndex(temp);

		//Swap the parents - places each node in the correct spot in the tree
		Node tempParent = secondBranch.getParent();
		boolean isLeft = (tempParent.getLeftChild() == secondBranch);
		secondBranch.setParent(firstBranch.getParent());
		// Need to maintain the correct structure (left vs right)
		if (firstBranch.getParent().getLeftChild() == firstBranch) {
			firstBranch.getParent().setLeftChild(secondBranch);
		}
		else {
			firstBranch.getParent().setRightChild(secondBranch);
		}
		firstBranch.setParent(tempParent);
		if (isLeft) {
			tempParent.setLeftChild(firstBranch);
		}
		else {
			tempParent.setRightChild(firstBranch);
		}
	}


	// Adaptive Huffman algorithm requires moving a node before incrementing its weight in case a node with a higher index and the same weight is found
	// This method returns a node of the given weight that has the highest index assigned to it
	public Node findHighestIndexNode(Integer weight) {
		// First check if there are any nodes with the given weight
		if (indexWeightMap.containsKey(weight)) {
			// If there are, loop through all and find the one with the highest index
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

	// Updates the tree given a symbol that has just been read
	public void updateTree(byte[] symbol) {
		// If such symbol has been read already, increase the weight of the corresponding node and its parents (and possibly restructure the tree)
		if (symbolNodeMap.containsKey(symbol)) {
			Node symbolNode = symbolNodeMap.get(symbol);
			increaseWeight(symbolNode);
		}
		// Otherwise update the tree to add this new symbol
		else {
			addSymbol(symbol);
		}
	}

	public HashMap<byte[], Node> getsymbolNodeMap() {
		return symbolNodeMap;
	}

	public Node getRoot() {
		return root;
	}

	public Node getNYT() {
		return notYetTransmitted;
	}
}

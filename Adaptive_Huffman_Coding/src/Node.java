// Class representing a node of the code tree
public class Node {
	
	// The number of times that the symbol corresponding to the node or the symbols corresponding to subnodes have been seen
	private int weight = 0;
	// Helps to order the nodes
	private int index;
	// Binary encoding, thus two children for each node
	private Node leftChild = null;
	private Node rightChild = null;
	// Node directly above this one
	private Node parent = null;
	// If the node is a leaf node, it will correspond to a character
	private byte[] value;
//	byte byteValue = (byte)value;
	
	public Node(int index, int weight, Node parent) {
		this.setIndex(index);
		this.setWeight(weight);
		this.setParent(parent);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
	
	

}

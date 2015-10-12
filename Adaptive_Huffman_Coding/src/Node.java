
public class Node {
	
	private int weight = 0;
	private int index;
	private Node leftChild = null;
	private Node rightChild = null;
	private Node parent = null;
	private char value = 'a';
	byte byteValue = (byte)value;
	
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

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}
	
	

}

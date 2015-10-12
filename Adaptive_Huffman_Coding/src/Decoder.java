import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Decoder {
	
	CodeTree codeTree;
	boolean readNYT = false;
	
	public Decoder() {
		codeTree = new CodeTree();
	}
	
	public void decode() {
		try {
			
			File decodedFile = new File("decompressed");
			FileWriter writer = new FileWriter(decodedFile, false);
			
			
			BufferedReader reader = new BufferedReader(new FileReader("compressed"));
			int currentChar;
			Node currentNode = codeTree.getRoot();
			while ((currentChar=reader.read()) != -1) {
				//TODO find a better way of determining whether a node is a root node
				if (currentNode!=codeTree.getNYT() && (currentNode.getLeftChild()==null && currentNode.getRightChild()==null)) {
					System.out.println("found again:" + currentNode.getValue());
					writer.append(currentNode.getValue());
					codeTree.updateTree(currentNode.getValue());
					currentNode = codeTree.getRoot();
				}
				if (currentChar == '0') {
					currentNode = currentNode.getLeftChild();
					System.out.println("Read 0!");
				}
				else if (currentChar == '1') {
					currentNode = currentNode.getRightChild();
					System.out.println("Read 1!");
				}
				else {
					System.out.println("first time seeing:" + (char)currentChar);
					writer.write(currentChar);
					codeTree.updateTree((char)currentChar);
					currentNode = codeTree.getRoot();
				}
				writer.flush();
			}
			writer.close();			
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

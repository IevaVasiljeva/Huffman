import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

// A class that provides methods for decoding a source that has been encoded using the Huffmann encoding
public class Decoder {

	// Decoder has a code tree that gets updated when information is received in a similar manner as the encoder updates the tree
	CodeTree codeTree;
	// If the code read stands for the NYT, then we know that the next symbol will be transmitted as it is (not encoded)
	boolean readNYT = false;

	public Decoder() {
		codeTree = new CodeTree();
	}

	// When NYT is read, new symbol follows. Thus the next n (where n is the unit length in bytes) bytes have to be taken together as representing the new symbol
	// Therefore we read n bytes, add them to the buffer, take the first n bytes from the buffer and return the rest
	// the end of the input stream is signalled by encoding of the NYT symbol followed by a byte representation of a symbol that has already been seen
	// therefore need to test for that
	public String getNewSymbol(int unitLength, FileInputStream input, FileOutputStream output, String buffer) {
		byte[] newlyRead = new byte[1];
		try {
			// Get the necessary number of bytes to have a byte representation of the symbol
			for (int index=0; index<unitLength; index++) {
				input.read(newlyRead);
				buffer += String.format("%8s", Integer.toBinaryString(newlyRead[0] & 0xFF)).replace(' ', '0');
			}
			// First n bytes represent the newly read symbol
			String byteRepOfSymbol = buffer.substring(0, 8*unitLength);
			// The rest is the leftovers - return it as buffer
			if (buffer.length()>8*unitLength) {
				buffer = buffer.substring(8*unitLength);
			}
			else {
				buffer = "";
			}
			// We also need to update the code tree, thus store the bytes
			byte[] symbolInBytes = new byte[unitLength];
			int index = 0;

			// Test whether the symbol just received has not already been seen (in that case it is the end of the stream)
			String temp = byteRepOfSymbol;
			while (temp.length()>0) {
				// Take the first 8 elements (length of a byte)
				String byteAsString = temp.substring(0, 8);
				// Parse them into a byte
				final byte[] byteArray = new BigInteger(byteAsString, 2).toByteArray();
				 byte toWrite = (byte)0;
				for (byte b : byteArray) {
					toWrite=b;
				}
				
				byte byteToWrite = toWrite;
				
				// Store in a byte array to be able to check
				symbolInBytes[index] = byteToWrite;

				// Truncate the string
				if (temp.length()>8) {
					temp = temp.substring(8);
				}
				else {
					temp = "";
				}
			}
			
			// If the symbol has already been seen, the end of the file is reached. Return.
			if (codeTree.getsymbolNodeMap().keySet().contains(symbolInBytes)) {
				return "";
			}
			
			
			// Loop through the string, cutting bytes off it, writing them to the output and updating the code tree using them
			while (byteRepOfSymbol.length()>0) {
				// Take the first 8 elements (length of a byte)
				String byteAsString = byteRepOfSymbol.substring(0, 8);
				
				// Parse them into a byte
				final byte[] byteArray = new BigInteger(byteAsString, 2).toByteArray();
				 byte toWrite = (byte)0;
				for (byte b : byteArray) {
					toWrite=b;
				}
				
				byte byteToWrite = toWrite;
				
				// Write the output
				output.write(byteToWrite);
				output.flush();
				if (byteRepOfSymbol.length()>8) {
					byteRepOfSymbol = byteRepOfSymbol.substring(8);
				}
				else {
					byteRepOfSymbol = "";
				}
			}
			
			// Update the code tree
			codeTree.updateTree(symbolInBytes);
			return buffer;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Reads the input source, decodes the information received and updates the code tree
	public void decode(int unitLength, String inputStreamName, String outputStreamName) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outputStreamName);

			FileInputStream inputStream = new FileInputStream(inputStreamName);
			// Read byte by byte
			byte[] currentByte = new byte[1];
			// 
			Node currentNode = codeTree.getRoot();

			String buffer = "";

			while (true) {
				if (buffer.length()==0) {
					if (inputStream.read(currentByte) == -1) {
						break;
					}
					buffer = String.format("%8s", Integer.toBinaryString(currentByte[0] & 0xFF)).replace(' ', '0');
				}

				// If the node reached is the leaf node, we have found a symbol and output its value to the output file
				if (currentNode!=codeTree.getNYT() && (currentNode.getLeftChild()==null && currentNode.getRightChild()==null)) {
					outputStream.write(currentNode.getValue());
					// Update the tree with the symbol read
					codeTree.updateTree(currentNode.getValue());
					// Start again from the root
					currentNode = codeTree.getRoot();
				}
				// If the symbol reached is NYT, the following bytes*unitLength stand for the new symbol read
				else if ((currentNode.getLeftChild()==null && currentNode.getRightChild()==null)){
					// call method to read and store the new symbol
					buffer = getNewSymbol(unitLength, inputStream, outputStream, buffer);
					currentNode = codeTree.getRoot();
				}
				// In case the buffer is empty, will need to read more
				char currentChar = '!';
				if (buffer.length()!=0) {
					currentChar = buffer.charAt(0);
				}

				// If we have read 0, go left in the code tree
				if (currentChar == '0') {
					currentNode = currentNode.getLeftChild();
					buffer = buffer.substring(1);
				}
				// If we have read 1, go right in the code tree
				else if (currentChar == '1') {
					currentNode = currentNode.getRightChild();
					buffer = buffer.substring(1);
				}
				outputStream.flush();	
			}

		} 
		catch (IOException e) {
			System.err.println("Could not read from the source file or write to the output.");
		}

	}

}

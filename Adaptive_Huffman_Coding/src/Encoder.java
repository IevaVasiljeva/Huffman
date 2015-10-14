import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

// A class for encoding input source symbols
public class Encoder {
	
	String buffer = "";

	// Method that enables the user to type in some text that he wants to compress.
	// Returns the length of the input and the length of the result in bits
	public int[] inputPrompt() {
		// Input prompt
		System.out.println("Please, enter a sentence to encode!");
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

		// Create a file for the compressed result
		File compressedFile = new File("compressed");
		// To keep track of the size of the source and the resulting size
		int resultingBitLength = 0;
		int inputBitLength = 0;
		
		try {
			FileWriter encodingWriter = new FileWriter(compressedFile, false);
			try {
				// Read input char by char
				int input = 0;
				// Create a new empty (with only NYT) code tree for this compression
				CodeTree codeTree = new CodeTree();	
				// Read till the end of the input
				while ((input=inputReader.read()) != -1) {
					
					// Break when @ is read
					if (input==64) {
						break;
					}
					
					// We know that the console input will be ASCII, thus each character takes one byte
					byte[] byteInput = new byte[1];
					byteInput[0] = (byte)input;
					inputBitLength += 8;
					
					//TODO count the input length here
					System.out.println("\nGot this: " + (char)input);
					
					// Encode the symbol
					String encoding = getEncoding(codeTree, byteInput);
					
					System.out.println("NYT: " + getEncoding(codeTree, null));
					
					
					resultingBitLength += encoding.length();
					// Add the encoding to the buffer
					buffer += encoding;
					// If the buffer exceeds a byte, cut the first eight binary digits, find the corresponding char and store them in the file
					// Do this till the buffer is smaller than a char
					while (buffer.length() >= 8) {
						System.out.println("In thebuffer: " + buffer);
						// Take the first byte
						String oneByte = "0" + buffer.substring(0, 7);	
						System.out.println("One byte: " + oneByte);
						// turn it into an ASCII char
						char asciiChar = (char)Byte.parseByte(oneByte, 2);
						System.out.println("In the buffer, wrote: " + asciiChar);
						// Append the results of the encoding to the output
						encodingWriter.append(asciiChar);
						// Remove the first eight bits from the buffer, as they have written to file
						buffer = buffer.substring(7);
						System.out.println("Leftover buffer: " + buffer);
					}
					// After storing the encoded version of the symbol, update the code tree
					// If the update is done before that, the decoder could get confused
					codeTree.updateTree(byteInput);
				}
			
				// Append whatever is left over in the buffer padded by 0s
				if (buffer.length()>0) {
					buffer = "0" + buffer;
					while (buffer.length()<8) {
						buffer += "0";
					}
					System.out.println("Last one: " + buffer);
					// turn it into an ASCII char
					char asciiChar = (char)Byte.parseByte(buffer, 2);
					// Append the results of the encoding to the output
					encodingWriter.append(asciiChar);
				}

				// Flush and close
				encodingWriter.flush();
				encodingWriter.close();
				int[] ioLengths = {inputBitLength, resultingBitLength};
				return ioLengths;
			} 
			catch (IOException e) {
				System.err.println("An error occured while reading input.");
			}			
		} catch (IOException e1) {
			System.err.println("Could not create the output file.");
		}
		return null;
	}
	
	public int[] encodeFile(String filename, int unitLength) {
		// To keep track of the size of the source and the resulting size
		int resultingBitLength = 0;
		int inputBitLength = 0;
		
		
		try {
			FileInputStream fileStream = new FileInputStream(filename);
			File compressedFile = new File("compressed");
			try {
				FileWriter encodingWriter = new FileWriter(compressedFile, false);
				byte[] input = new byte[unitLength];
				
				// Create a new empty (with only NYT) code tree for this compression
				CodeTree codeTree = new CodeTree();
				
				// Go through the input file byte by byte
				while (fileStream.read(input) != -1) {
					inputBitLength += unitLength*8;
					// Encode the symbol
					String encoding = getEncoding(codeTree, input);
					// Keep track of the length of the encoding
					resultingBitLength += encoding.length();
					// Add the encoding to the buffer
					buffer += encoding;
					// If the buffer exceeds a byte, cut the first eight binary digits, find the corresponding char and store them in the file
					// Do this till the buffer is smaller than a char
					while (buffer.length() >= 8) {
						// Take the first byte
						String oneByte = buffer.substring(0, 7);	
						// turn it into an ASCII char
						char asciiChar = (char)Byte.parseByte(oneByte, 2);
						// Append the results of the encoding to the output
						encodingWriter.append(asciiChar);
						// Remove the first eight bits from the buffer, as they have written to file
						buffer = buffer.substring(7);
					}
					// After storing the encoded version of the symbol, update the code tree
					// If the update is done before that, the decoder could get confused
					codeTree.updateTree(input);
				}
				// Flush and close
				encodingWriter.flush();
				encodingWriter.close();
				fileStream.close();
				int[] ioLengths = {inputBitLength, resultingBitLength};
				return ioLengths;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	// Returns the encoding of the symbol (or the NYT proceeded by the symbol itself, in case the symbol has not yet been seen)
	public String getEncoding(CodeTree tree, byte[] symbol) {
		
		// Just to have originalSymbol initialised
		byte[] originalSymbol = new byte[0];
		
		// If the symbol read is a new symbol, the symbol-node map will be searched for null instead of the symbol read,
		// as null is mapped to NYT (by the constructor of the CodeTree)
		if (!tree.getsymbolNodeMap().containsKey(symbol)) {
			originalSymbol = symbol;
			symbol = null;
		}			

		// Get the node corresponding to the symbol read (or NYT if the symbol has not been read yet)
		Node symbolNode = tree.getsymbolNodeMap().get(symbol);
		// Now go through the tree starting from that node in the direction of the root, and register whether left (0) or right (1) branch has been taken
		String reversedResult = "";
		Node parent = symbolNode.getParent();
		// Go till the root
		while (parent != null) {
			// Being the left child corresponds to 0
			if (symbolNode == parent.getLeftChild()) {
				reversedResult += "0";
			}
			// Being the right child corresponds to 1
			else {
				reversedResult += "1";
			}
			// Go one level up
			symbolNode = parent;
			parent = symbolNode.getParent();
		}
		// Reverse the resulting string, as we started from the bottom, while the encoding should have been read starting from the top
		String stringResult = new StringBuilder(reversedResult).reverse().toString();
		System.out.println("String encoding: " + stringResult);

		// Add the symbol itself if this is the first time it's seen
		if (symbol == null) {
			for (byte currentByte: originalSymbol) {
				// Want to add the symbol in it's binary representation
				stringResult += String.format("%8s", Integer.toBinaryString(currentByte & 0xFF)).replace(' ', '0');
				System.out.println("New symbol: " + String.format("%8s", Integer.toBinaryString(currentByte & 0xFF)).replace(' ', '0'));
			}
		}
		return stringResult;
	}

}

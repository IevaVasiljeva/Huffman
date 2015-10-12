import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Encoder {

		public void inputPrompt() {
			System.out.println("Please, enter a sentence to encode!");
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			
			//TODO for test
			Decoder decoder = new Decoder();
			
			File compressedFile = new File("compressed");
			try {
				FileWriter encodingWriter = new FileWriter(compressedFile, false);
			
				
				try {
					int input = 0;
					CodeTree codeTree = new CodeTree();			
					while ((input=inputReader.read()) != -1) {
						System.out.println("Got this: " + (char)input);
						char[] encoding = getEncoding(codeTree, Character.valueOf((char)input));
						System.out.println("Encoding: " + encoding.toString());
						for (char ch: encoding) {
							encodingWriter.append(ch);
						}						
						
						
						//TODO remove all decoders!
//						decoder.decode(encoding);
						codeTree.updateTree((char)input);
					}
					encodingWriter.flush();
					encodingWriter.close();
					
				} catch (IOException e) {
					System.err.println("An error occured while reading input.");
				}			
			
			
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
		
		
		//TODO probs need to throw error when a symbol not found
		public char[] getEncoding(CodeTree tree, Character symbol) {
			Character originalSymbol = ' ';
			
			if (!tree.getsymbolNodeMap().containsKey(symbol)) {
				originalSymbol = symbol;
				symbol = null;
				System.out.println("DOESNT CONTAIN!");
			}			
			
			Node symbolNode = tree.getsymbolNodeMap().get(symbol);
			String reversedResult = "";
			Node parent = symbolNode.getParent();
			while (parent != null) {
				if (symbolNode == parent.getLeftChild()) {
					reversedResult += "0";
					System.out.println("Is left");
				}
				else {
					reversedResult += "1";
					System.out.println("Is right");
				}
				symbolNode = parent;
				parent = symbolNode.getParent();
			}
			String stringResult = new StringBuilder(reversedResult).reverse().toString();
			
			//TODO NOT SURE ABOUT HOW TO STORE SYMBOLS!
			if (symbol == null) {
				stringResult += originalSymbol;
				System.out.println("APPENDING ORIGINAL!");
			}
			char[] result = stringResult.toCharArray();
			
			return result;
		}
		
}

public class RunHuffman {

	public static void main(String[] args) {

		String mode = args[0];
		// "c" stands for "compress
		if (mode.equals("c")) {
			if (args.length==3) {
				String inputFileName = args[1];
				String outputFileName = args[2];
				Encoder encoder = new Encoder();
				int [] results = encoder.encodeFile(inputFileName, 1, outputFileName);
				System.out.println("\nFile encoded. Here are the compression results:");
				System.out.println("Original length in bits: " + results[0]);
				System.out.println("Resulting length in bits: " + results[1]);
				double ratio = (double)results[1]/(double)results[0]*100;
				System.out.println("Resulting file is : " + String.format( "%.2f", ratio) + "% of the original.");
			}
			else {
				String outputFileName = args[1];
				Encoder encoder = new Encoder();
				int [] results = encoder.inputPrompt(outputFileName);
				System.out.println("\nFile encoded. Here are the compression results:");
				System.out.println("Original length in bits: " + results[0]);
				System.out.println("Resulting length in bits: " + results[1]);
				double ratio = (double)results[1]/(double)results[0]*100;
				System.out.println("Resulting file is : " + String.format( "%.2f", ratio) + "% of the original.");
			}
		}
		// Otherwise expand
		else {
			String inputFileName = args[1];
			String outputFileName = args[2];
			Decoder decodes = new Decoder();
			decodes.decode(1, inputFileName, outputFileName);
			System.out.println("File decoded. See " + outputFileName);
		}
	}

}

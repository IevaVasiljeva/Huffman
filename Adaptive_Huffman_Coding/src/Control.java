import java.util.HashMap;

public class Control {
	
	private static HashMap<Character, Integer> map = new HashMap<>();
	
	
//	public static void test() {
//		char t = 'a';
//		byte b = (byte)t;
//		System.out.println("byte: " + Integer.toHexString(b));
//
//	}
	
	
	public static void main(String[] args) {
		Encoder encode = new Encoder();
		Decoder decodes = new Decoder();
		decodes.decode();
//		test();
//		encode.inputPrompt();
	}

}

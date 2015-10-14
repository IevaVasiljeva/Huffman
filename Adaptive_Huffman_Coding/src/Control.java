import java.util.HashMap;

public class Control {
	
	private static HashMap<Character, Integer> map = new HashMap<>();
	
	
	public static void test() {
		
//		byte newOne = Byte.parseByte("10110100", 2);
		
		char t = 'H';
		byte b = (byte)t;
		String s = "H";
		String aByte = String.format("%8s", Integer.toBinaryString(s.getBytes()[0] & 0xFF)).replace(' ', '0');
		
		byte a = Byte.parseByte(aByte, 2);
		
		System.out.println("Byted a: " + aByte);
		System.out.println("Byte: " + (char)Byte.parseByte(aByte, 2));
	//	System.out.println("Charred: " + (char)Byte.parseByte(String.format("%8s", Integer.toBinaryString(newByte & 0xFF)).replace(' ', '0'), 2));
		
	}
	
	
	public static void main(String[] args) {
		Encoder encode = new Encoder();
		Decoder decodes = new Decoder();
		decodes.decode(1);
		test();
//		int[] result = encode.inputPrompt();
//		System.out.println("Start length: " + result[0] + " amnd the compressed one: " + result[1]);
	}

}

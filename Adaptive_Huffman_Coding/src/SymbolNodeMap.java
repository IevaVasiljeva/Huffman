import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

// Class for providing a mapping between symbols (represented as bytes array) and nodes
public class SymbolNodeMap extends HashMap<byte[], Node>{
	
	// ContainsKey has to be overwritten to recognise the equality of the content of the array rather than the object that the array points to
	public boolean containsKey(Object key) {
		// get all keys that the hashmap contains
		Set<byte[]> allKeys= super.keySet();
		// Iterate through them all
		Iterator<byte[]> iterator = allKeys.iterator();
		byte[] currentKey;
		while (iterator.hasNext()) {
			currentKey = iterator.next();
			// In case we want to check whether there is a mapping with null
			if (currentKey == null) {
				if (key == null) {
					return true;
				}
				continue;
			}
			// Loop through all the bytes in a key
			for (int index=0; index<((byte[])key).length; index++) {
				// As soon as one of these does not match, we know that this is not the correct key and stop checking it
				if (((byte[])key)[index] != currentKey[index]) {
					break;
				}
				// Otherwise, if all the bytes match, this is the correct key
				else {
					if (index == ((byte[])key).length-1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// Similarly, get needs to be changed to return the value mapped to a byte array of the same content rather than the exact same byte array
	public Node get(Object key) {
		// get all keys that the hashmap contains
		Set<byte[]> allKeys= super.keySet();
		// Iterate through them all to find a byte array of the same content
		Iterator<byte[]> iterator = allKeys.iterator();
		byte[] currentKey;
		while (iterator.hasNext()) {
			currentKey = iterator.next();
			// In case we want to get the element mapped to null
			if (currentKey == null) {
				if (key == null) {
					return super.get(null);
				}
				continue;
			}
			// Loop through all the bytes in a key
			for (int index=0; index<((byte[])key).length; index++) {
				// As soon as one of these does not match, we know that this is not the correct key and stop checking it
				if (((byte[])key)[index] != currentKey[index]) {
					break;
				}
				// Otherwise, if all the bytes match, this is the correct key, we get the Node mapped ot it and return this node
				else {
					if (index == ((byte[])key).length-1) {
						return super.get(currentKey);
					}
				}
			}
		}
		return null;
	}
}

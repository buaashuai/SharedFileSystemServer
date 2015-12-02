package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class SDBMHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long hash = 0;
		int tmp = 0;
		for (int i = 0; i < key.length(); i++) {
			tmp = key.charAt(i);
			// equivalent to: hash = 65599*hash + (*str++);
			hash = tmp + (hash << 6) + (hash << 16) - hash;
		}
		return hash;
	}

}

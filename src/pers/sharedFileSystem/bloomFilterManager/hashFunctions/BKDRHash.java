package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class BKDRHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long seed = 131;
		long hash = 0;
		int tmp = 0;
		for (int i = 0; i < key.length(); i++) {
			tmp = key.charAt(i);
			hash = hash * seed + tmp;
		}
		return hash;
	}
}

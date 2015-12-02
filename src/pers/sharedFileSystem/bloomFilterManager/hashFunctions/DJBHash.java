package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class DJBHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long hash = 0;
		int tmp = 0;
		for (int i = 0; i < key.length(); i++) {
			tmp = key.charAt(i);
			hash += (hash << 5) + tmp;
		}
		return hash;
	}

}

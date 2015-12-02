package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class RSHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long hash = 0;
		long b = 378551;
		long a = 63689;
		int tmp = 0;
		for (int i = 0; i < key.length(); i++) {
			tmp = key.charAt(i);
			hash = hash * a + tmp;
			a *= b;
		}
		return hash;
	}

}

package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class APHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		// 0x7fffffffffffffff long的最大值
		long hash = 0;
		int tmp = 0;
		for (int i = 0; i < key.length(); i++) {
			tmp = key.charAt(i);
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ tmp ^ (hash >> 3));
			} else {
				hash ^= (~((hash << 11) ^ tmp ^ (hash >> 5)));
			}
		}
		return hash;
	}

}

package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class TianlHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long urlHashValue = 0;
		int ilength = key.length();
		int i;
		char ucChar;
		int ucChar2;
		if (ilength <= 0) {
			return 0;
		}
		if (ilength <= 256) {
			urlHashValue = 16777216 * (ilength - 1);
		} else {
			urlHashValue = 42781900080L;
		}
		if (ilength <= 96) {
			for (i = 1; i <= ilength; i++) {
				ucChar = key.charAt(i - 1);
				if (ucChar <= 'Z' && ucChar >= 'A') {
					ucChar2 = ucChar + 32;
					ucChar = (char) ucChar2;
				}
				urlHashValue += (3 * i * ucChar * ucChar + 5 * i * ucChar + 7
						* i + 11 * ucChar) % 1677216;
			}
		} else {
			for (i = 1; i <= 96; i++) {
				ucChar = key.charAt(i + ilength - 96 - 1);
				if (ucChar <= 'Z' && ucChar >= 'A') {
					ucChar2 = ucChar + 32;
					ucChar = (char) ucChar2;
				}
				urlHashValue += (3 * i * ucChar * ucChar + 5 * i * ucChar + 7
						* i + 11 * ucChar) % 1677216;
			}
		}
		return urlHashValue;
	}

}

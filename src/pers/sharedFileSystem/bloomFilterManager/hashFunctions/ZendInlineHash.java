package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class ZendInlineHash extends HashFunction {

	@Override
	public long getHashCode(String key) {
		// TODO Auto-generated method stub
		long nKeyLength = key.length();// 字符串索引的长度，此处的索引是否==字符串长度？？？？
		long hash = 5381;
		int tmp = 0;
		int i = 0;
		/* variant with the hash unrolled eight times */
		for (; nKeyLength >= 8; nKeyLength -= 8) { // 这种step=8的方式是为何？
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++); // 比直接*33要快
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
			tmp = key.charAt(i++);
			hash = ((hash << 5) + hash) + tmp;
		}
		if (i >= key.length())
			tmp = 0;
		else
			tmp = key.charAt(i);
		switch ((int) nKeyLength) {
		case 7:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */// 此处是将剩余的字符hash
		case 6:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */
		case 5:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */
		case 4:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */
		case 3:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */
		case 2:
			hash = ((hash << 5) + hash) + tmp; /* fallthrough... */
		case 1:
			hash = ((hash << 5) + hash) + tmp;
			break;
		case 0:
			break;
		}
		return hash; // 返回hash值
	}

}

package pers.sharedFileSystem.test;

import java.io.IOException;
import java.math.BigDecimal;

public class HashTest {

	// 将long类型的数值转无符号数
	public static final BigDecimal readUnsignedLong(long value)
			throws IOException {
		if (value >= 0)
			return new BigDecimal(value);
		long lowValue = value & 0x7fffffffffffffffL;
		return BigDecimal.valueOf(lowValue)
				.add(BigDecimal.valueOf(Long.MAX_VALUE))
				.add(BigDecimal.valueOf(1));
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println(Long.MAX_VALUE + "\n");
		BigDecimal m = BigDecimal.valueOf(2000000000);// 20亿
		String str = "as1dfgasdgasdgasdgasdgsdg";
		int num = 1;
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "MurmurHash")
		// + readUnsignedLong(MurmurHash.hash64(str))
		// .divideAndRemainder(m)[1]);// 两个BigDecimal相除,求余数
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "RabinHash64")
		// + readUnsignedLong(
		// Rabin64Hash.DEFAULT_HASH_FUNCTION.hash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "BKDRHash")
		// + readUnsignedLong(BKDRHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "APHash")
		// + readUnsignedLong(HashFunctions.APHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "DJBHash")
		// + readUnsignedLong(HashFunctions.DJBHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "JSHash")
		// + readUnsignedLong(HashFunctions.JSHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "RSHash")
		// + readUnsignedLong(HashFunctions.RSHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "SDBMHash")
		// + readUnsignedLong(HashFunctions.SDBMHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "TianlHash")
		// + readUnsignedLong(HashFunctions.TianlHash(str))
		// .divideAndRemainder(m)[1]);
		// System.out.println((num++)
		// + ": "
		// + String.format("%-30s", "zend_inline_hash_func")
		// + readUnsignedLong(HashFunctions.zend_inline_hash_func(str))
		// .divideAndRemainder(m)[1]);
	}
}

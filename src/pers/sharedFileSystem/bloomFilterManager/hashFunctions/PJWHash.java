package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class PJWHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        long BitsInUnsignedInt = 61;
        long ThreeQuarters = (BitsInUnsignedInt * 3) / 4;
        long OneEighth = BitsInUnsignedInt / 8;
        long HighBits = 0xFFFFFFFF << (BitsInUnsignedInt - OneEighth);
        long hash = 0;
        long test = 0;

        for (int i = 0; i < key.length(); i++) {
            hash = (hash << OneEighth) + key.charAt(i);

            if ((test = hash & HighBits) != 0) {
                hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
            }
        }

        return (hash & 0x7FFFFFFF);
    }
}

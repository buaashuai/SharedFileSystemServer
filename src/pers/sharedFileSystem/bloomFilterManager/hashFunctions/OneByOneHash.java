package pers.sharedFileSystem.bloomFilterManager.hashFunctions;


public class OneByOneHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        int i;
        long hash;
        for (hash = 0, i = 0; i < key.length(); ++i) {
            hash += key.charAt(i);
            hash += (hash << 10);
            hash ^= (hash >> 6);
        }
        hash += (hash << 3);
        hash ^= (hash >> 11);
        hash += (hash << 15);
        // return (hash & M_MASK);
        return hash;
    }
}

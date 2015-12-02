package pers.sharedFileSystem.bloomFilterManager.hashFunctions;


public class BernsteinHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        long hash = 0;
        int i;
        for (i = 0; i < key.length(); ++i)
            hash = 49999 * hash + key.charAt(i);
        return hash;
    }
}

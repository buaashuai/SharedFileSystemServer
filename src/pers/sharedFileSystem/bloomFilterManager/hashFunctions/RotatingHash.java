package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class RotatingHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        int i;
        long prime=999999999091L,hash;
        for (hash = key.length(), i = 0; i < key.length(); ++i)
            hash = (hash << 4) ^ (hash >> 28) ^ key.charAt(i);
        return (hash % prime);
    }
}

package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class ADDHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        long hash;
        long prime=999999999091L;
        int i;
        for (hash = key.length(), i = 0; i < key.length(); i++)
            hash += key.charAt(i);
        return (hash % prime);
    }
}

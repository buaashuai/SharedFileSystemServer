package pers.sharedFileSystem.bloomFilterManager.hashFunctions;

public class DEKHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        long hash = key.length();
        for(int i = 0; i < key.length(); i++)
        {
            hash = ((hash << 5) ^ (hash >> 27)) ^ key.charAt(i);
        }
        return hash;
    }
}

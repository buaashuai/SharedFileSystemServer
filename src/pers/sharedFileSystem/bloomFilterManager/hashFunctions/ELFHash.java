package pers.sharedFileSystem.bloomFilterManager.hashFunctions;


public class ELFHash extends HashFunction{
    @Override
    public long getHashCode(String key) {
        long hash = 0;
        long x    = 0;
        for(int i = 0; i < key.length(); i++)
        {
            hash = (hash << 4) + key.charAt(i);
            if((x = hash & 0xF0000000L) != 0)
            {
                hash ^= (x >> 24);
            }
            hash &= ~x;
        }
        return hash;
    }
}

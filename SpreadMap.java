package VersionWithClockStampRangedNoise;
import java.util.Random;

public class SpreadMap {
    static long MAX_FLOW_ID = 999999999999L;
    static long MIN_FLOW_ID = 0;

    private short inputSequenceSingle = 0;
    private int countBase;

    static public long[] hashValues;
    static int NUMBER_OF_HASHES = 2;

    SpreadMap(){
        hashValues = new long[NUMBER_OF_HASHES];
        for (int i = 0; i < hashValues.length; i++) {
            hashValues[i] = new Random().nextLong();
            System.out.println(hashValues[i]);
        }
    }

    SpreadMap(int amount){
        countBase = (amount+60-1)/60;
        hashValues = new long[NUMBER_OF_HASHES];
        for (int i = 0; i < hashValues.length; i++) {
            hashValues[i] = new Random().nextLong();
        }
    }

    public Input createSingleFlow(int lineNumber){
        return new Input(
            (long)((Math.random()*(MAX_FLOW_ID-MIN_FLOW_ID+1))+MIN_FLOW_ID),
            (short)((lineNumber/countBase)+inputSequenceSingle*60)
        );
    }

    public short getInputSequenceSingle(){
        return inputSequenceSingle;
    }

    public void increaseInputSequenceSingle(){
        inputSequenceSingle++;
    }

    public int hash(long num, int entries, int hashType){
        int value = (int)(Math.abs(FNVHash1(num^hashValues[hashType]))%(long)entries);
        //For testing
        if (value < 0){
            System.out.println(num);
            System.out.println(hashValues[hashType]);
            System.out.println(FNVHash1(num^hashValues[hashType]));
            System.out.println(entries);
        }
        return value;
    }

    private static long FNVHash1(long key) {
        key = (~key) + (key << 18); // key = (key << 18) - key - 1;
        key = key ^ (key >>> 31);
        key = key * 21; // key = (key + (key << 2)) + (key << 4);
        key = key ^ (key >>> 11);
        key = key + (key << 6);
        key = key ^ (key >>> 22);
        return key;
    }
}

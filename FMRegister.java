package VersionWithClockStampRangedNoise;
public class FMRegister{
    private Timestamp[] register; 
    private int clockLength = 120;
    private int maxGeoHashValue = 11;
    FMRegister(int size){
        register = new Timestamp[size];
        for (int index = 0; index < register.length; index++) {
            register[index] = new Timestamp();
        }
    }
    public Timestamp[] getRegister() {
    	return register;
    }
    public void record(long value, Timestamp timestamp) {
        int registerValue = geometricHash(value);
        register[registerValue] = new Timestamp(timestamp);
    }
    
    public int query(int timeSequence) {
        for (int index = 0; index < register.length; index++) {
            if (!(register[index].isInWindow( timeSequence*60 + 59))){
                return index;/// !!!!!!!!!!MODIFICATION  index -> index - 1
            }
        }
        return register.length;
    }
    public int probingQuery(int timeSequence, FMRegister aa) {
        for (int index = 0; index < register.length; index++) {
            if (!(register[index].isInWindow( timeSequence*60 + 59))    && !(aa.getRegister()[index].isInWindow( timeSequence*60 + 59) ) ){
                return index;/// !!!!!!!!!!MODIFICATION  index -> index - 1
            }
        }
        return register.length;
    }
    public void clean(int timestamp) {
        for (int index = 0; index < register.length; index++) {
            if (!(register[index].isInWindow( timestamp))){
                register[index].clear();
            }
        }
    }

    private int geometricHash(long value){
        long longValue = FNVHash1(value)%(Integer.MAX_VALUE+Math.abs((long)Integer.MIN_VALUE));
        int integerValue = (int)(longValue+Integer.MIN_VALUE);
        return Math.min(Integer.numberOfLeadingZeros(integerValue), maxGeoHashValue);
    }

    private static int FNVHash1(long key) {
        key = (~key) + (key << 18); // key = (key << 18) - key - 1;
        key = key ^ (key >>> 31);
        key = key * 21; // key = (key + (key << 2)) + (key << 4);
        key = key ^ (key >>> 11);
        key = key + (key << 6);
        key = key ^ (key >>> 22);
        return (int) key;
    }
}

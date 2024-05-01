package VersionWithClockStampRangedNoise;
public class BitMap extends SpreadMap{

    static int FLOW_AMOUNT = 100000;
    static int MAP_SIZE = 10000000;
    
    public static void main(String[] args) {
        SpreadMap map = new SpreadMap(FLOW_AMOUNT);
        Timestamp[] bitMap = new Timestamp[MAP_SIZE];
        for (int index = 0; index < bitMap.length; index++) {
            bitMap[index] = new Timestamp();
        }
        int oneCount = 0;

        for (int index = 0; index < 5; index++) {
            
            for (int i = 0; i < FLOW_AMOUNT; i++) {
                Input input = map.createSingleFlow(i);
                int hashKey = map.hash(input.getIpKey(), MAP_SIZE, 0);
                bitMap[hashKey].setTimestamp(input.getTimestamp());
            }

            oneCount = 0;
            for (Timestamp bit : bitMap) {
                if (bit.getTimestamp() >= map.getInputSequenceSingle()*60)
                    oneCount++;
            }

            double zeroFraction = (1-oneCount/(double)MAP_SIZE);
            int bitMapEstimation = (int)(-MAP_SIZE*Math.log(zeroFraction));

            System.out.println("Flow "+map.getInputSequenceSingle()+":\t"+FLOW_AMOUNT+"\t"+bitMapEstimation);
            
            map.increaseInputSequenceSingle();
        }
    }
}

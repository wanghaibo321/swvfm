package VersionWithClockStampRangedNoise;

import java.io.FileNotFoundException;

public class SWVirtualBitMap extends MultiSpreadMap {

    static final int MEMORY = 1000000;
    static final int TIMESTAMP_SIZE = 7;

    static final int MAP_SIZE = MEMORY/TIMESTAMP_SIZE;
    static final int VIRTUAL_MAP_SIZE = 200000;
    static int FILE_NUMBER = 1; // Number of files to run through (min is 1)
    
    public static void main(String[] args) throws FileNotFoundException {
        MultiSpreadMap map = new MultiSpreadMap("VirtualBitMap", VIRTUAL_MAP_SIZE);
        Timestamp[] bitMap = new Timestamp[MAP_SIZE];
        for (int index = 0; index < bitMap.length; index++) {
            bitMap[index] = new Timestamp();
        }
        //map.analyzeAccuracy();
        for (int fileIndex = 1; fileIndex <= FILE_NUMBER; fileIndex++) {

            Input input = map.takeInput();

            while (input != null) {//recording each item, which is "input"
                bitMap[map.hash(input.getIpKey()^virtualHashValues
                    [map.hash(input.getDestinationIp(),VIRTUAL_MAP_SIZE, 1)],MAP_SIZE, 0)].setTimestamp(input.getTimestamp());

                input = map.takeInput();
            }

            int overallCount = 0;
            for (Timestamp bit : bitMap) {//we perform sliding window query at the end of each minute and window size is 1 minute. 
            								//(map.getInputSequence())*60 is the oldest time stamp in the sliding window. Any bit with a time stamp older will be out of the sliding window.
                if (bit.isInWindow( map.getInputSequence()*60 + 59 )) {
                    overallCount++;
                }
            }

            double zeroFractionOverall = (1-overallCount/(double)MAP_SIZE);
            int noise = (int)(-VIRTUAL_MAP_SIZE*Math.log(zeroFractionOverall));
            

            //int testCount = 0;
            String[] outputStrings = map.takeOutputString();//outputStrings are a flow and its real spread
            while (outputStrings != null){
                long IP = map.ipKeyConversion(outputStrings[0]);
                int count = 0;

                for (long hash : virtualHashValues) {
                    Timestamp bit = bitMap[map.hash(IP^hash, MAP_SIZE, 0)];
                    if (bit.isInWindow( map.getInputSequence()*60 + 59)) {
                        count++;
                    }
                }

                double zeroFraction = (1-count/(double)VIRTUAL_MAP_SIZE);
                int estimation = (int)(-VIRTUAL_MAP_SIZE*Math.log(zeroFraction));

                map.printResults(outputStrings[0]+"\t"+outputStrings[1]+"\t"+(estimation-noise));

                outputStrings = map.takeOutputString();
            }
            
            for (Timestamp bit : bitMap) {//we perform cleaning to clear outdated timestamp.
            	if (!bit.isInWindow( map.getInputSequence()*60 + 59 )) {
            		bit.clear();
            	}
            }
            map.analyzeAccuracy();
            map.increaseInputSequence(FILE_NUMBER);
        }

        map.closeScanner();

    }
}

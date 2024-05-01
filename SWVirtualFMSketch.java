package VersionWithClockStampRangedNoise;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.random.*;
public class SWVirtualFMSketch extends MultiSpreadMap {

    static final int MEMORY = 80000000;
    static final int TIMESTAMP_SIZE = 7;

    static final int VIRTUAL_MAP_SIZE = 128;
    static int FILE_NUMBER = 1; // Number of files to run through (min is 1)
    static final int REGISTER_SIZE = 12; //w
    static final int REGISTERS_NUM = MEMORY/(REGISTER_SIZE*TIMESTAMP_SIZE); //m
    static final double ALPHA = 0.77351;
    //static final int[] bin = {10, 20, 50, 80, 100, 200, 500, 800, 1000,2000,5000,8000, 10000, 100000, 200000};
    static final int[] bin = {10, 20,30,40, 50,60,70, 80, 90,100,120,150,180, 200, 300,400,500,600,700, 800, 1000,2000,5000,8000, 10000, 100000, 200000};
    static final int[] binNoise = new int[bin.length];
    
    

    public static void main(String[] args) throws FileNotFoundException {
        MultiSpreadMap map = new MultiSpreadMap("VirtualFMSketch", VIRTUAL_MAP_SIZE);
        FMRegister[] registers = new FMRegister[REGISTERS_NUM];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new FMRegister(REGISTER_SIZE);
        }
        FMRegister[] ARTregisters = new FMRegister[VIRTUAL_MAP_SIZE];
        for (int i = 0; i < ARTregisters.length; i++) {
            ARTregisters[i] = new FMRegister(REGISTER_SIZE);
        }
        Random rand = new Random();
        for (int fileIndex = 1; fileIndex <= FILE_NUMBER; fileIndex++) {
            Input input = map.takeInput();
            int n = 0;
            //Record each input
            while (input != null) {
                registers[map.hash(input.getIpKey()^virtualHashValues
                    [map.hash(input.getDestinationIp(),VIRTUAL_MAP_SIZE, 1)],
                REGISTERS_NUM, 0)].record(input.getDestinationIp(), new Timestamp(input.getTimestamp()));
                n++;
                input = map.takeInput();
            }

            
            System.out.println("finishing recording packets");
            //----------------------calcualating noise with artificial packets
        int packetN = 1000;
        int kk = packetN;
        while(kk-->=0) {
        	//System.out.println(kk);
            for (int i = 0; i<bin.length; i++) {
            	//System.out.println(i);

            	int small = 0, large = bin[i];
            	if (i != 0) small = bin[i-1];
            	int randomCard = rand.nextInt(small +1, large+1);
            	for (int j = 0; j< randomCard; j++) {
            		int registerIndex = rand.nextInt(VIRTUAL_MAP_SIZE);
            		ARTregisters[registerIndex].record(rand.nextLong(), new Timestamp(map.getInputSequence() * 60 + 59));
            		
            	}
            	int sum = 0;
            	for (int j = 0; j< VIRTUAL_MAP_SIZE; j++) {
            		int consecutivezero = registers[rand.nextInt(REGISTERS_NUM)].probingQuery(map.getInputSequence(),ARTregisters[j] );
            		sum += consecutivezero;
            	}
            	double average = Math.pow(2, sum *1.0/(double)VIRTUAL_MAP_SIZE);
            	//System.out.println(average);
                binNoise[i] += (int)((average/ALPHA)*VIRTUAL_MAP_SIZE) - randomCard;
                for (int j = 0; j < ARTregisters.length; j++) {
                    ARTregisters[j] = new FMRegister(REGISTER_SIZE);
                }
            }
        }
        for (int i = 0; i<bin.length; i++) {
        	binNoise[i] /= packetN;
        	System.out.println("noise for bin " + i + " is "+ binNoise[i]);
        }
        System.out.println("finishing calculating artifitial noise");  
            //----------------------perform real queries.
            String[] outputStrings = map.takeOutputString();

            while (outputStrings != null){
                long IP = map.ipKeyConversion(outputStrings[0]);
                int sum = 0;
                int zeroReg = 0;
                int i = 0;
                //Each in virtual hash
                for (long hash : virtualHashValues) {
                	int consecutivezero = registers[map.hash(IP^hash,REGISTERS_NUM,0)].query(map.getInputSequence());
                    sum += consecutivezero;
                }
                
                
                //newsum /=registerused;
               //if (Integer.parseInt(outputStrings[1]) > 1000) System.out.println(newsum +"\t" + registerused);
                double average = Math.pow(2, sum *1.0/(double)VIRTUAL_MAP_SIZE);
                int estimation = (int)((average/ALPHA)*VIRTUAL_MAP_SIZE);
                //if (estimation< noise) estimation = noise;
                //if (estimation <= 5.0 / 2.0 * VIRTUAL_MAP_SIZE) {			// small flows
                	
        		//	estimation = (int) (1.0 * VIRTUAL_MAP_SIZE * Math.log(1.0 * VIRTUAL_MAP_SIZE / Math.max(zeroReg, 1)));
               // }
                //---find the real bin that the queried flow belongs to 
                int binIndex = bin.length-1;
                for (int j = 0; j<bin.length; j++) {
                	if (estimation - binNoise[j] <=bin[j]) {binIndex = j; break;}
                }
                
                map.printResults(outputStrings[0]+"\t"+outputStrings[1]+"\t"+(estimation-binNoise[binIndex]) );
                

                outputStrings = map.takeOutputString();
            }
            for (FMRegister register : registers) {
                register.clean(map.getInputSequence() * 60 + 59);
            }
            map.analyzeAccuracy();
            map.increaseInputSequence(FILE_NUMBER);
        }
        map.closeScanner();
    }
}


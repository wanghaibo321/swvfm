package VersionWithClockStampRangedNoise;
//import java.util.*;

public class FMSketch extends SpreadMap{

    static int FLOW_AMOUNT = 100000;
    static int REGISTERS_NUM = 128; //m
    static int REGISTER_SIZE = 32; //w
    static double SYMBOL = 0.77351;
    
    public static void main(String[] args) {
        SpreadMap map = new SpreadMap(FLOW_AMOUNT);

        FMRegister[] registers = new FMRegister[REGISTERS_NUM];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new FMRegister(REGISTER_SIZE);
        }

        //For each input file
        for (int index = 0; index < 5; index++) {
            
            //Recording
            for (int i = 0; i < FLOW_AMOUNT; i++) {
                Input input = map.createSingleFlow(i);
                int hashKey = map.hash(input.getIpKey(), REGISTERS_NUM, 0);
                registers[hashKey].record(input.getIpKey(), new Timestamp(input.getTimestamp()));
            }

            //Query
            int sum = 0;
            for (FMRegister register : registers) {
                sum += register.query(map.getInputSequenceSingle());
            }
            double average = Math.pow(2, sum/(double)REGISTERS_NUM);
            int estimation = (int)(average/SYMBOL*REGISTERS_NUM);

            //Print
            System.out.println("Flow "+map.getInputSequenceSingle()+":\t"+FLOW_AMOUNT+"\t"+estimation);
            
            map.increaseInputSequenceSingle();
        }
    }
}

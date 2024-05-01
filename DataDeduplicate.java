package VersionWithClockStampRangedNoise;

import java.util.*;
import java.io.*;

public class DataDeduplicate{
    public static void main(String[] args){
        int FILE_START = 1; //Number of file to start on
        int FILE_END = 60; //Number of file to end on (including)

        String testExtensions = "";
        for (int fileIndex = FILE_START; fileIndex <= FILE_END; fileIndex++) {
        	String[] inputDeduplicate = new String[600000000];
            int itemDeduplicateIndex = 0;
            Map<String, Set<String>> networkInputs = new HashMap<>();
            //int test = 0;
            try {
                File input = new File("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+fileIndex+testExtensions+".txt");
                Scanner scanner = new Scanner(input);
                String inputLine[] = new String[2];

                while(scanner.hasNextLine()){
                    //System.out.print(test+" ");
                    inputLine = scanner.nextLine().split("\t",2).clone();
                    if (!networkInputs.containsKey(inputLine[1])){
                        networkInputs.put(inputLine[1], new HashSet<String>());
                    }
                    if (networkInputs.get(inputLine[1]).contains(inputLine[0])) continue;
                    networkInputs.get(inputLine[1]).add(inputLine[0]);
                    inputDeduplicate[itemDeduplicateIndex++] = inputLine[0]+"\t"+inputLine[1];
                    //test++;
                }
                scanner.close();
            } catch (Exception e) {
                System.err.println(e);
                //System.out.println(test);
            }

                

            

            try {
                FileWriter writer = new FileWriter("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+fileIndex+testExtensions+"OutputDeduplicate.txt");
                for (int i=0; i<itemDeduplicateIndex; i++) {                	
                    writer.write(inputDeduplicate[i]+"\n" );            	
            }
                writer.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        System.out.println("DONE!!----");
        return;
    }
}
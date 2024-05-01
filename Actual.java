package VersionWithClockStampRangedNoise;

import java.util.*;
import java.io.*;

public class Actual{
    public static void main(String[] args){
        int FILE_START = 1; //Number of file to start on
        int FILE_END = 60; //Number of file to end on (including)

        String testExtensions = "";
        for (int fileIndex = FILE_START; fileIndex <= FILE_END; fileIndex++) {
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
                    networkInputs.get(inputLine[1]).add(inputLine[0]);
                    //test++;
                }
                scanner.close();
            } catch (Exception e) {
                System.err.println(e);
                //System.out.println(test);
            }

            List<Result> results = new LinkedList<>();
                for (Map.Entry<String, Set<String>> entry : networkInputs.entrySet()) {
                    results.add(new Result(entry.getKey(), entry.getValue().size()));
                }

            results.sort(new Comparator<Result>() {
                public int compare(Result x, Result y){
                    return x.compareTo(y);
                }
            });

            try {
                FileWriter writer = new FileWriter("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+fileIndex+testExtensions+"OutputActual.txt");
                for (Result result : results) {
                    writer.write(result.print());
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
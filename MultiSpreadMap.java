package VersionWithClockStampRangedNoise;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class MultiSpreadMap extends SpreadMap{
    private static final int FILE_START = 23; //Number of file you want to start on

    private short inputSequence = FILE_START-1; //index for the files by the time
    //static String testExtension = "_4Mbits_ClockStampRanged_";
    static String testExtension = "_80Mbits_ClockStampRanged";

    int hashesNumber;
    
    static public long[] virtualHashValues;

    private long countBase;
    private ArrayList<Integer> bin = new ArrayList<>(Arrays.asList(10,100,1000,10000,100000,1000000000));
    //private ArrayList<Integer> bin = new ArrayList<>(Arrays.asList(1, 2, 4, 8, 16,32, 64, 128, 256,512,1024, 2048,4096,8192,1000000000));

    private long lineNumber = 0;

    private Scanner scannerInput;
    private FileWriter writer;
    private Scanner scannerOutput;
    private String fileType = "ClockStampRanged";// indicates the name of the sketches, for output file name purpose


    MultiSpreadMap(){
        super();
    }

    MultiSpreadMap(String fileType, int virtualHashSize){
        try {
            hashesNumber = virtualHashSize;
            File inputFile = new File("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+(inputSequence+1)+"OutputDeduplicate.txt");
            scannerInput = new Scanner(inputFile);

            //Inputs per second for calculations
            countBase = (fileLineCounter(inputFile)+60-1)/60;

            virtualHashValues = new long[hashesNumber];
            for (int i = 0; i < virtualHashValues.length; i++) {
                virtualHashValues[i] = (long)((Math.random()*(MAX_FLOW_ID-MIN_FLOW_ID+1))+MIN_FLOW_ID);
            }
            
            lineNumber = 0;
            this.fileType = fileType;
            File output = new File("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+(inputSequence+1)+"OutputActual.txt");
            scannerOutput = new Scanner(output);
            writer = new FileWriter("C:\\Users\\hwa281\\Dropbox (UFL)\\Eclipse-workSpace-UKOffice\\SlidingWindow\\o"+(inputSequence+1)+testExtension+"Output"+fileType+".txt");

            

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private long fileLineCounter(File inputFile){
        long count = 0;
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            while (bufferedReader.readLine() != null){
                count++;
            }
            bufferedReader.close();
        }
        catch (Exception e){
            System.err.println(e);
        }
        return count;
    }

    public Input takeInput(){
        try{
            String inputLine[] = new String[2];
            Input input;

            if(scannerInput.hasNextLine()){
                inputLine = scannerInput.nextLine().split("\t",2).clone();
                input = new Input(inputLine[1], inputLine[0], (short)(lineNumber/countBase+(inputSequence)*60));
                lineNumber++;
                return input;
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public String[] takeOutputString(){
        try {
            String inputLine[] = new String[2];
            if(scannerOutput.hasNextLine()){
                inputLine = scannerOutput.nextLine().split("\t",2).clone();
                return inputLine;
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public long ipKeyConversion(String ip){
        String[] split = ip.split("\\.",4);
        String ipKeyString = "";
        for (String piece : split) {
            if(piece.length() == 2)
                piece = "0"+piece;
            else if (piece.length() == 1)
                piece = "00"+piece;
            ipKeyString = ipKeyString+piece;
        }
        return Long.parseLong(ipKeyString);
    }

    //For multiflow SpreadMaps
    public void printResults(String fullOutputString){
        try {
            writer.write(fullOutputString+'\n');
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void increaseInputSequence(int fileLimit){
        inputSequence++;
        lineNumber = 0;
        if ((inputSequence+1-FILE_START) < fileLimit){
            try {
                closeScanner();
                File inputFile = new File("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+(inputSequence+1)+"OutputDeduplicate.txt");
                if (inputFile != null){
                    //Inputs per second for calculations
                    countBase = (fileLineCounter(inputFile)+60-1)/60;

                    scannerInput = new Scanner(inputFile);
                    File outputFile = new File("C:\\Users\\hwa281\\Dropbox (UFL)\\CAIDA\\o"+(inputSequence+1)+"OutputActual.txt");
                    scannerOutput = new Scanner(outputFile);
                    writer = new FileWriter("C:\\Users\\hwa281\\Dropbox (UFL)\\Eclipse-workSpace-UKOffice\\SlidingWindow\\o"+(inputSequence+1)+testExtension+"Output"+fileType+".txt");
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
   

public void analyzeAccuracy() throws FileNotFoundException {
	//initBin();
	//analyzeSmallAccuracy(filePath);
	System.out.println("C:\\Users\\hwa281\\Dropbox (UFL)\\Eclipse-workSpace-UKOffice\\SlidingWindow\\o"+(inputSequence+1)+testExtension+"Output"+fileType+".txt");
	String filePath = "C:\\Users\\hwa281\\Dropbox (UFL)\\Eclipse-workSpace-UKOffice\\SlidingWindow\\o"+(inputSequence+1)+testExtension+"Output"+fileType+".txt";
	int binLen = bin.size();
	
	System.out.println("bin: " + binLen);
	double[] relerr = new double[binLen];				//relative bias

	double[] bias = new double[binLen];				//relative bias
	double[] stderr = new double[binLen];			//relative standard errorAbs
	double[] abserr = new double[binLen];			//absolute error
	double[] avg = new double[binLen];				//average of the true sizes in each bin
	double[] epsilonRatio = new double [binLen];
	double totalBias = 0.0;
	double totalRelativeBias = 0.0;
	int[] num = new int[binLen];					//number of flows in each bin
	int totalNum = 0;
	double[] maxabserr = new double[binLen];			//max absolute error
	PrintWriter pw1 = new PrintWriter(new File(filePath + "_Bias.txt"));
	PrintWriter pw2 = new PrintWriter(new File(filePath + "_Standard_Error.txt"));
	PrintWriter pw3 = new PrintWriter(new File(filePath + "_abs_Error.txt"));
	PrintWriter pw4 = new PrintWriter(new File(filePath + "_total_abs_Error.txt"));
	PrintWriter pw5 = new PrintWriter(new File(filePath + "_total_relative_Error.txt"));
	PrintWriter pw6 = new PrintWriter(new File(filePath + "_relative_Error.txt"));
	PrintWriter pw7 = new PrintWriter(new File(filePath + "_max_abs_Error.txt"));
	PrintWriter pw8 = new PrintWriter(new File(filePath + "_epsilon_ratio.txt"));
	Scanner sc = new Scanner(new File(filePath));
	DecimalFormat df = new DecimalFormat("#0.0000");

	while (sc.hasNextLine()) {
		
		String entry = sc.nextLine();
		String[] strs = entry.split("\t");
		if (strs.length !=3) continue;
					
		int s = Integer.parseInt(strs[strs.length-2]);			//true value
		double shat = Double.parseDouble(strs[strs.length-1]);		//estimated value
		int binIndex = getBinIndex(s);
		//System.out.println(binIndex);
		//System.out.println(avg);
		//System.out.println(num);
		avg[binIndex] += s;
		num[binIndex]++;
		totalNum++;
		maxabserr[binIndex] = Math.max(maxabserr[binIndex] , Math.abs(shat-s));
		relerr[binIndex] +=1.0*Math.abs(shat - s) / s;
		bias[binIndex] += 1.0*(shat - s) / s;
		abserr[binIndex] += Math.abs(shat-s);
		totalBias += Math.abs(shat-s);
		totalRelativeBias +=Math.abs(shat-s)*1.0/s;
		
		stderr[binIndex] += (1.0 * shat / s - 1) *  (1.0 * shat / s - 1);
	}
	sc.close();
	
	for(int j = 0; j < binLen; j++) {
		if(num[j] != 0) {
			relerr[j] /=num[j];
			avg[j] /= num[j];
			bias[j] /= num[j];
			abserr[j] /= num[j];
			stderr[j] /= num[j];
			epsilonRatio[j] /= num[j];
			stderr[j] = Math.sqrt(stderr[j]);
			
			pw1.println(bin.get(j) + "\t" + df.format(avg[j]) + "\t" + num[j]+ "\t" +df.format(bias[j]));
			pw2.println(bin.get(j) + "\t" + avg[j] + "\t" + num[j]+ "\t" + + stderr[j]);
			pw3.println(bin.get(j) + "\t" + df.format(avg[j]) + "\t" + num[j]+ "\t" + df.format(abserr[j]));
			pw6.println(bin.get(j) + "\t" + df.format(avg[j]) + "\t" + num[j]+ "\t" + df.format(relerr[j]));
			pw7.println(bin.get(j) + "\t" + df.format(avg[j]) + "\t" + num[j]+ "\t" + df.format(maxabserr[j]));
			pw8.println(bin.get(j) + "\t" + df.format(avg[j]) + "\t" + num[j]+ "\t" + df.format(epsilonRatio[j]));

		}
	}
	pw4.println("total number " + totalNum + "\t total abs error" + totalBias/totalNum);
	pw5.println("total number " + totalNum + "\t total relative" + totalRelativeBias/totalNum);

	System.out.println(filePath + ":\t" + totalBias/totalNum);
	pw1.close();
	pw2.close();
	pw3.close();
	pw4.close();
	pw5.close();
	pw6.close();
	pw7.close();
	pw8.close();
}
	private int getBinIndex(int val) {
		int l = 0, r = bin.size()-1;
		while (l <= r) {
			int mid = l + (r - l) / 2;
			if (bin.get(mid) == val) return mid;
			if (bin.get(mid) > val) r = mid - 1;
			else l = mid + 1;
		}
		return l;
	}

    public void closeScanner(){
        try {
        scannerInput.close();
        scannerOutput.close();
        writer.flush();
        writer.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public short getInputSequence(){
        return inputSequence;
    }
}

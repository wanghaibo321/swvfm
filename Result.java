package VersionWithClockStampRangedNoise;
public class Result {
    private String destination;
    private int count;

    public Result(String destination, int count){
        this.destination = destination;
        this.count = count;
    }

    public int compareTo(Result y){
        if (count > y.count){
            return -1;
        }
        else if (count < y.count){
            return 1;
        }
        else
            return 0;
    }

    public String print(){
        return destination+"\t"+count+"\n";
    }
}

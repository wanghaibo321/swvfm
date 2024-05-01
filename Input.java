package VersionWithClockStampRangedNoise;
public class Input {
    private String sourceIp;
    private long destinationIp;
    private long ipKey;
    private Timestamp timestamp;

    //For VirtualBitMap and VirtualFMSketch
    Input(String sourceIp, String destinationIp, short timestamp){
        this.sourceIp = sourceIp;

        ipKey = ipSplice(sourceIp);
        this.destinationIp = ipSplice(destinationIp);

        this.timestamp = new Timestamp(timestamp);
    }
    
    //For BitMap and FMSketch
    Input(long ipKey, short timestamp){
        sourceIp = "";
        this.ipKey = ipKey;
        this.timestamp = new Timestamp(timestamp);
    }

    private long ipSplice(String ip){
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

    public long getIpKey() {
        return ipKey;
    }
    
    public long getDestinationIp() {
        return destinationIp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp.setTimestamp(timestamp);
    }
    public int getTimestamp() {
        return timestamp.getTimestamp();
    }

    //For Testing
    public String printWithIP(){
        return sourceIp+"\t"+ipKey+"\t"+timestamp.getTimestamp()+"\n";
    }
    public String print(){
        return ipKey+"\t"+timestamp.getTimestamp()+"\n";
    }
}

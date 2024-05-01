package VersionWithClockStampRangedNoise;


public class Timestamp {
    private int timestamp;
    private int clockLength = 120;

    Timestamp(){
        timestamp = -1;
    }
    
    Timestamp(int timestamp){
        this.timestamp = timestamp % clockLength;
    }
    Timestamp(Timestamp timestamp){
        this.timestamp = timestamp.getTimestamp();
    }
    
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp % clockLength;
    }
    public int getTimestamp() {
        return timestamp;
    }
    public void clear() {
    	timestamp = -1;
    }

	public boolean isInWindow(int currentTime) {
		currentTime %= clockLength;
		if (timestamp ==-1) return false;
    	if (currentTime < clockLength / 2) {
    		if (timestamp>currentTime && timestamp<=currentTime + clockLength / 2) {
    			return false;
    		}
    		else
    			return true;
    	}
    	else {
    		if (timestamp>currentTime-clockLength && timestamp<=currentTime) {
    			return true;
    		}
    		else
    			return false;
    	}
    }
}
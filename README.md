# swvfm
The code for SWVFM for sub-stream cardinality estimation in sliding window.

The main java file for SWVFM is SWVirtualFMsketch.java,  which extends from Multi-SpreadMap.java

Multi-SpreadMap.java is responsible for the taking the input file, taking the real sub-stream cardinality, and output results. 

SWVirtualFMsketch.java use the data structure of FM sketch, named  FMRegister[] registers. The variable registeers is an array of FMRegisters, which is implemented in FMRegister.java.

FMRegister.java also uses time stamps, which is implemented in Timestamp.java, for sliding window sub-stream cardinality estimation. 

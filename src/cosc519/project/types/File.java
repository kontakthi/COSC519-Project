/**************************************************************
 * Filename: File.java                                        *
 * Class: COSC519                                             *
 * Description: Represents a file to be stored in the RAID    *
 * file system.                                               *
 * Package: cosc519.project.types                            *
 **************************************************************/ 
 
 package cosc519.project.types;
 
 import java.io.RandomAccessFile;
 
 public class File
 {
 	private String pathToFile;
    private String fileName;
    private int sequenceInFileSeries;
    private byte[] dataStream;
    private java.io.File fileObj;
    
    static int MAX_FILE_SIZE = Codes.DATA_SIZE;
    
    // Method for mirroring ONLY
    // Takes string path of file and the filename (C:/ + filename.ext)
    public File(String pathToFile, String fileName) {        
        
        this.fileObj = new java.io.File(pathToFile + fileName);        
        this.pathToFile = pathToFile;
        this.fileName = fileName;
        
        // Java has a physical file limit
        // I tested a 700MB file, and it throws an out of memory error
        // We should limit the max file size we wish to add.
        // I recommend no bigger than DATA_SIZE constant (right now, 8MB)
        
        this.dataStream = new byte[(int) fileObj.length()];
        
        this.sequenceInFileSeries = 0;
    }
    
    /* Method for striping ONLY
     *     
     *     String path to file
     *     String filename.ext
     *     The sequence in the file series (A file of 2048 bytes in a configuration of TWO USBs will have four blocks of data
     *              00...01...02...03
     *     Idealy, part 00 goes to USB0
     *             part 01 goes to USB1 and so on
     *     If we had four USBs, then 00 goes to USB0, 02...to USB2 and 03 to USB3 (first block of a new file to a USB
     *          MUST equal the sequence number in a file series
     *    
     *      startByte is the offset from within the file I should start to read
     *      len is the number of bytes I should read
     * 
     *      In our example, len = BLOCK_SIZE = 512; from offset 0, I read 512 bytes. From startByte = 512, I read 512 bytes, etc.
     * 
     */
    public File(String pathToFile, String fileName, int sequenceInFileSeries, int startByte, int len) {    
        
        this.fileObj = new java.io.File(pathToFile + fileName);        
        this.pathToFile = pathToFile;
        this.fileName = fileName;
        this.sequenceInFileSeries = sequenceInFileSeries;
        this.dataStream = new byte[(int) Codes.BLOCK_SIZE];
        
        try {
            RandomAccessFile raf = new RandomAccessFile(this.fileObj, "rw");
            
            System.out.println(this.dataStream.length);
            raf.read(this.dataStream, startByte, len);
            
            raf.close();
        } catch (Exception e) {}
    }
    
    public String getFName() {
        return this.fileName;
    }

    public String getPathToFile() {
        return this.pathToFile;
    }

    public int getSequenceInFileSeries() {
        return this.sequenceInFileSeries;
    }

    public byte[] getByteStream() {
        return this.dataStream;
    }

    // Returns the java class File object
    public java.io.File getFileObj() {
        return fileObj;
    }
 }
 
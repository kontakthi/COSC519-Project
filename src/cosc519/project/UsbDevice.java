/**************************************************************
 * Filename: UsbDevice.java                                   *
 * Class: COSC519                                             *
 * Description: Represents a usb device which can be used in  *
 * RAID configuration.                                        *
 * Package: cosc519.project                                  *
 **************************************************************/ 
 
 package cosc519.project;
 
 import java.io.RandomAccessFile;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 
 import java.util.*;
 import cosc519.project.types.Codes;
 import cosc519.project.types.File;
 import cosc519.project.types.RFS;
 
 public class UsbDevice
 {  
    private String WORKING_DIRECTORY = null;    
    private String FORMATTED_FILE_DEST = null;
    
    private byte raidID;
    private byte raid_numOfDevicesInConfig;
    private byte raidID_Seq;
    private byte raidType;
    private boolean isFormatted;
    
    /* Create USB Device
     * 
     * First param: path to USB :: Windows = "F:\\" :: Mac = "/Volumes/drivename" :: Unix = "/media/drivename"
     * 2nd param: file.ext name // right now, max is 8 characters for filename, three for extension
     *      Any changes to the filename length will NOT trickle down
     *      I recommend not changing this limitation, but perhaps providing a truncating method BLAHBL~1....BLAHBL~2...etc
     *      Or just only provide file names with 8 characters :D. Makes my life easier.
     * 
     * 3rd param: RFS config ID (00-0F artificial limitation; max "16" configurations with any number of devices each
     * 4th param: total number of participating USB devices in this configuration
     * 5th param: byte representing the sequence in the configuration (first device is 00)
     * 6th param: byte indicating Raid Type (0x01 for mirroring)
     * 
     * 
     * USBDevice class does not have setters for configuration information (seq, type, etc)
     *      You may want to add those if you want to "create" USB devices that are not formatted, but then become formatted
     * 
     *      You will then need to change the RAID information based on it being formatted
     */
    public UsbDevice(String pathToUSB, String USBFName, byte raidID, byte numOfDevicesInConfig, byte raidID_Seq, byte raidType) {
        
        try {
            this.WORKING_DIRECTORY = pathToUSB;
            this.FORMATTED_FILE_DEST = USBFName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.raidID = raidID;
        this.raid_numOfDevicesInConfig = numOfDevicesInConfig;
        this.raidID_Seq = raidID_Seq;
        this.raidType = raidType;
        this.isFormatted = new java.io.File(pathToUSB + USBFName).exists();
    }
    
    // Use to initialize a UsbDevice that is formatted or unformatted from either an existant or nonexistant rfs.bin file
    // pathToUsb = absolute path of the usb device only, not including the RFS filename
    public UsbDevice(String pathToUsb)
    {
    	// Check if file exists
    	java.io.File rfsFile = new java.io.File(pathToUsb, Codes.RFS_FILENAME);
    	
    	if(rfsFile.exists()) 
    	{
    		// Initialize UsbDevice class from filesystem file
    		try
    		{
    			this.initMetaData(pathToUsb);
    		
    			this.WORKING_DIRECTORY = pathToUsb;
    			this.FORMATTED_FILE_DEST = Codes.RFS_FILENAME;
    			this.isFormatted = true;
    		}
    		catch(Exception e)
    		{
    			System.out.println("Error initializing UsbDevice. Do not use!");
    		}
    	}
    	else
    	{
    		this.WORKING_DIRECTORY = pathToUsb;
    		this.FORMATTED_FILE_DEST = Codes.RFS_FILENAME;
    		this.isFormatted = false;
    	}
    }
    
    // Return RAID ID (label)
    public byte getRaidID() {
        return this.raidID;
    }
    
    // Return number of devices in configuration
    public byte getNumOfDevicesInConfig() {
        return this.raid_numOfDevicesInConfig;
    }
    
    // Return RAID ID Seq
    public byte getRaidID_Seq() {
        return this.raidID_Seq;
    }
    
    // Return RAID Type
    public byte getRaidType() {
        return this.raidType;
    }
    
    // Return USB format status
    public boolean getFormatStatus() {
        return this.isFormatted;
    }
    
    // Return path to USB
    public String getPathToUSB() {
        return this.WORKING_DIRECTORY;
    }
    
    // Return filename of formatted file
    public String getUSBFName() {
        return this.FORMATTED_FILE_DEST;
    }
    
    // Return RAID ID (label)
    public void setRaidID(byte b) {
        this.raidID = b;
    }

    // Return number of devices in configuration
    public void setNumOfDevicesInConfig(byte b) {
        this.raid_numOfDevicesInConfig = b;
    }

    // Return RAID ID Seq
    public void setRaidID_Seq(byte b) {
        this.raidID_Seq = b;
    }

    // Return RAID Type
    public void setRaidType(byte b) {
        this.raidType = b;
    }

    // Set the format status
    public void setFormatStatus(boolean b) {
        this.isFormatted = b;
    }
    
    
  
    // Add new file
    // Takes a File object
    public boolean addFile (File newFile) {
        
        // Boolean to check if file exists
        boolean fileExists = false;
        
        // File to create
        File file = new File(this.WORKING_DIRECTORY,this.FORMATTED_FILE_DEST);
        String fullFName = newFile.getFName();
        String fname = "";
        String ext = "";
        
        // Receives the index where '.' exists in the string
        // Separates filename from extension
        int split = fullFName.lastIndexOf(".");
        
        // Uses the split integer and substring to retrieve filename and extension
        fname = fullFName.substring(0,split);
        ext = fullFName.substring(split+1, fullFName.length());
        
        // Here we get the bytes for the filename and extension
        byte[] byteFName = fname.getBytes();
        byte[] byteExt = ext.getBytes();
        
        // byte[] data accesses the file object we want to add and retrieves its data stream
        // In mirroring, it will be large
        // In striping, ALWAYS BLOCK_SIZE in length
        byte[] data = newFile.getByteStream();
        byte[] sizeOfFile = intToByteArray((int) data.length, 3);
        
        // Create a byte array for meta data storage of File newFile
        byte[] fileMetaData = new byte[Codes.META_DATA_SIZE];
        
        // Changes the first permission byte to 0x0E
        // 0x0E means "meta data is full, corresponding data location is full"
        fileMetaData[0] = (byte) 0x0E;
        
        // Converts the integer of the sequence number of the newFile to a byte array
        // I've defined that the fileSeq for any given file is two bytes 0x0000 in length. Potentially, 0xFFFF file parts
        // 65,536 blocks across the entire configuration.
        byte[] fileSeq = intToByteArray(newFile.getSequenceInFileSeries(), 2);
        fileMetaData[1] = fileSeq[0];
        fileMetaData[2] = fileSeq[1];
        
        for(int i = 0; i < 8; i++) {
            if(i < byteFName.length)
                fileMetaData[3+i] = byteFName[i];
                fileMetaData[3+i] = (fileMetaData[3+i] != (byte) 0x00) ? fileMetaData[3+i] : 0x20;       
            if(i < byteExt.length) {
                fileMetaData[11+i] = byteExt[i];
                fileMetaData[11+i] = (fileMetaData[11+i] != (byte) 0x00) ? fileMetaData[11+i] : 0x20; 
            }
        }
                
        byte[] dataLocation = intToByteArray(Codes.STARTING_ADDRESS, 3);
        
        // byte 32
        for(int i = 14; i < 17; i++)
            fileMetaData[i] = dataLocation[i-14];
        
        for(int i = 17; i < 20; i++)
            fileMetaData[i] = sizeOfFile[i-17];
        
        // Must use ceiling because +1 will result in data whose length is 512 to
        // "use" one more block than they really need
        int blockSize = (int) Math.ceil( data.length / Codes.BLOCK_SIZE );
        byte[] blockSizeBytes = intToByteArray(blockSize, 3);
        
        for(int i = 20; i < 23; i++)
            fileMetaData[i] = blockSizeBytes[i-20];
        
        String FATFileName = "";
        FATFileName += byteArrayToASCIIString(new byte[]{fileMetaData[3],fileMetaData[4],fileMetaData[5],fileMetaData[6],fileMetaData[7],fileMetaData[8],fileMetaData[9],fileMetaData[10]});
        FATFileName += byteArrayToASCIIString(new byte[]{fileMetaData[11],fileMetaData[12],fileMetaData[13]});
        FATFileName = FATFileName.replace(" ", "");
        
        
        // Only check if file exists if it's the first in the series
        // For mirroring, you check every file entry. 
        // For striping, you check for every first block of new data being entered
        // We only check the list for USB0!
        if (newFile.getSequenceInFileSeries() == 0)
            fileExists = checkFileList(FATFileName);
        
        // Only add the file if the file does NOT exist
        if (!(fileExists))
            try {
                FileInputStream in = new FileInputStream(newFile.getFileObj());
                in.read(data);
                in.close();

                RandomAccessFile raf = new RandomAccessFile(file.getFileObj(), "rw");

                // current file has been written
                Boolean isWritten = false;

                // "permissions" of allocation block
                byte permFile;

                // store block location of next available block
                byte[] nextAvailBlockLoc = new byte[3];

                for(int i = Codes.HEADER_SIZE; i < Codes.FAT_LENGTH; i+= Codes.META_DATA_SIZE) {

                    if(isWritten) break;

                    raf.seek(i);

                    permFile = raf.readByte();

                    // indicates freshly-formatted file
                    if(permFile == (byte) 0xEE) {
                        raf.seek(i);
                        raf.write(fileMetaData);

                        // create byte array read from meta data
                        byte[] writeLocation = {fileMetaData[14], fileMetaData[15], fileMetaData[16]};
                        byte[] locationBlockSize = {fileMetaData[20], fileMetaData[21], fileMetaData[22]};

                        // store location + block size
                        nextAvailBlockLoc = intToByteArray((byteArrayToInt(locationBlockSize,3) * Codes.BLOCK_SIZE + byteArrayToInt(writeLocation,3)),3).clone();

                        // Move cursor to data location and write
                        raf.seek(byteArrayToInt(writeLocation, 3));
                        raf.write(data);
                        isWritten = true;

                    }
                    
                    // indicates allocated block is available for meta-data storage
                    else if (permFile == (byte) 0x1E) {

                        // create byte array read from meta data
                        byte[] writeLocation = nextAvailBlockLoc.clone();
                        byte[] locationBlockSize = {fileMetaData[20], fileMetaData[21], fileMetaData[22]};


                        // check nextAvailBlockLoc
                        // breaks out if next location + new file block size > total_size of file
                        // aka, pointing to a location that doesn't exist.
                        if((byteArrayToInt(nextAvailBlockLoc,3) + byteArrayToInt(locationBlockSize,3) * Codes.BLOCK_SIZE ) > Codes.TOTAL_SIZE) {
                            System.out.println();
                            System.out.println(fname + "." + ext + " cannot be written. Data is full.");
                            break;
                        }

                        // Stores the data location that's available to be written.
                        nextAvailBlockLoc = intToByteArray((byteArrayToInt(locationBlockSize,3) * Codes.BLOCK_SIZE + byteArrayToInt(writeLocation,3)),3).clone();

                        raf.seek(i);

                        
                        fileMetaData[14] = writeLocation[0]; 
                        fileMetaData[15] = writeLocation[1]; 
                        fileMetaData[16] = writeLocation[2]; 

                        raf.write(fileMetaData);
                        
                        // Move cursor to data location and write
                        raf.seek(byteArrayToInt(writeLocation, 3));
                        raf.write(data);
                        isWritten = true;                    
                    }
                    // meta data cannot be stored here
                    else if (permFile == (byte) 0x0E) {
                        raf.seek(i+14);

                        byte[] location = {raf.readByte(),raf.readByte(),raf.readByte()};
                        
                        raf.seek(i+20);
                        byte[] locationBlockSize = {raf.readByte(),raf.readByte(),raf.readByte()};
                        
                        // store location + block size
                        nextAvailBlockLoc = intToByteArray((byteArrayToInt(locationBlockSize,3) * Codes.BLOCK_SIZE + byteArrayToInt(location,3)),3).clone();
                    
                    // If the file is deleted, we can use it
                    // As of current build, we don't even need this because we de-frag every time.
                    // We should keep it just in case
                    } else if (permFile == (byte) 0xDE) {
                        
                        // create byte array read from meta data
                        raf.seek(i+14);
                        byte[] writeLocation = {raf.readByte(), raf.readByte(), raf.readByte()};

                        raf.seek(i+20);
                        byte[] prevBlockLocSize = {raf.readByte(), raf.readByte(), raf.readByte()};

                        int currentFileBlockSize = blockSize;
                        int previousFileBlockSize = byteArrayToInt(prevBlockLocSize,3);

                        // check nextAvailBlockLoc
                        // breaks out if next location + new file block size > total_size of file
                        // aka, pointing to a location that doesn't exist.
                        if(currentFileBlockSize > previousFileBlockSize) {
                            System.out.println(fname + "." + ext + " cannot be written. Data is full.");
                        } else {

                            nextAvailBlockLoc = intToByteArray(previousFileBlockSize * Codes.BLOCK_SIZE + byteArrayToInt(writeLocation,3),3).clone();

                            raf.seek(i);

                            fileMetaData[14] = writeLocation[0]; 
                            fileMetaData[15] = writeLocation[1]; 
                            fileMetaData[16] = writeLocation[2]; 

                            raf.write(fileMetaData);
                            
                            // Move cursor to data location and write
                            raf.seek(byteArrayToInt(writeLocation, 3));
                            raf.write(data);
                            isWritten = true;
                        }
                    }
                }
                raf.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        
        return fileExists;
    }
    
    // Reads the FAT block to retrieve data location
    // Deletes data location, then deletes FAT block
    public void deleteFile(String fileNameToDelete) {      
        
        // Double boolean values here
        // We will later check if the file exists now, if it does, then say that it did exist.
        // Once the file stops existing, and we know it DID exist, then break out of loop. We're guaranteed
        // to not find anymore of that file.
        boolean fileDidExist = false;
        boolean fileExists = false;
        
        // Loops through the entire FAT
        for(int j = Codes.HEADER_SIZE; j < Codes.FAT_LENGTH; j += Codes.META_DATA_SIZE) {
            
            try {                
                // Data file on USB device
                File file = new File(this.WORKING_DIRECTORY, this.FORMATTED_FILE_DEST);

                // Opens data file for access
                RandomAccessFile raf = new RandomAccessFile(file.getFileObj(), "rw");

                // Moves cursor to integer value of the byte
                raf.seek(j);

                byte[] FATBlock = new byte[Codes.META_DATA_SIZE];
                byte[] FATBlockFName = new byte[8];
                byte[] FATBlockFExt = new byte[3];
                byte[] FATBlockFLoc = new byte[3];
                byte[] FATBlockFSize = new byte[3];

                // Reads current FAT entry into FATBlock byte array
                for(int i = 0; i < Codes.META_DATA_SIZE; i++) {
                    FATBlock[i] = (byte) raf.readByte();

                    // We're deleting, so the permission byte is changed to 0xDE
                    if(i == 0)
                        FATBlock[i] = (byte) 0xDE;   
                    
                    // store bytes for file location
                    else if (i >= 3 && i <= 10)
                    // Exclusive for deleting the correct file
                        FATBlockFName[i-3] = (FATBlock[i] != (byte) 0x00) ? FATBlock[i] : 0x20;    
                    
                    // Save the extension
                    else if (i >= 11 && i <= 13)
                        FATBlockFExt[i-11] = (FATBlock[i] != (byte) 0x00) ? FATBlock[i] : 0x20; 
                    
                    // The location of the data for current FAT entry
                    else if (i >= 14 && i <= 16)
                        FATBlockFLoc[i-14] = FATBlock[i];
                    
                    // The block size of the data for current FAT entry
                    else if (i >= 20 && i <= 22)
                        FATBlockFSize[i-20] = FATBlock[i];

                    // All other blocks will be 0x00 automatically because of new byte[]
                }
                
                // before seeking, check to see if the file name is equal to what was given
                String FATFileName = "";
                FATFileName += byteArrayToASCIIString(FATBlockFName);
                FATFileName += byteArrayToASCIIString(FATBlockFExt);
                
                // Compares the file name strings
                // Only delete the file IF it exists
                // If we delete, we must defrag!
                // Once we delete, we must start at one FAT entry before our current position
                //      because we need to read the new FAT entry that replaced our deleted one.
                if(FATFileName.equalsIgnoreCase(fileNameToDelete)) {
                    
                    fileExists = true;
                    fileDidExist = true;
                    
                    raf.seek(j);
                    raf.write(FATBlock);

                    raf.seek(byteArrayToInt(FATBlockFLoc,3));

                    byte[] emptyDataBlock = new byte[byteArrayToInt(FATBlockFSize,3) * Codes.BLOCK_SIZE];

                    raf.write(emptyDataBlock);

                    raf.close();   

                    // defrag
                    deFrag();
                    
                    // Re-read same FAT entry that now has the next FAT entry
                    j -= Codes.META_DATA_SIZE;
                } else {
                    fileExists = false;
                }
                
                // Only breaks if
                // FALSE && TRUE
                // File doesn't exist && file DID exist, so we have deleted all sequential filenames in striping
                //      Mirroring has one entry per file; striping can have N FAT entries per file
                if(!fileExists && fileDidExist)
                    break;
                
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    // defragment "USB drive"
    public void deFrag() {
        // File to create
        File file = new File(this.WORKING_DIRECTORY, this.FORMATTED_FILE_DEST);
        
        // "permissions" of allocation block
        byte permFile;
        // store block location of next available block
        
        byte[] newMetaData = new byte[Codes.META_DATA_SIZE];
        byte[] emptyFATBlockSize;
        byte[] emptyFATBlock = new byte[Codes.META_DATA_SIZE];
        byte[] emptyDataLocation;
        byte[] dataLocation;
        byte[] dataLocationBlockSize;
        byte[] buffer;
        byte[] emptyBuffer;
        
          
        try {
            for(int i = Codes.HEADER_SIZE; i < Codes.FAT_LENGTH; i+= Codes.META_DATA_SIZE) {

                RandomAccessFile raf = new RandomAccessFile(file.getFileObj(), "rw"); 

                raf.seek(i);                
                permFile = raf.readByte();

                if (permFile == (byte) 0xDE) { 
                    // store emptyFatBlock
                    raf.seek(i);
                    raf.readFully(emptyFATBlock, 0, Codes.META_DATA_SIZE);

                    // stores the next FAT block that does have data
                    raf.seek(i + Codes.META_DATA_SIZE);
                    raf.readFully(newMetaData, 0, Codes.META_DATA_SIZE);

                    if(newMetaData[0] == (byte) 0x1E) {
                        raf.seek(i);
                        if(i - Codes.HEADER_SIZE == 0) {
                            byte[] startingAddressBytes = intToByteArray(Codes.STARTING_ADDRESS, 3);
                            newMetaData[0] = (byte) 0xEE;
                            newMetaData[14] = startingAddressBytes[0];
                            newMetaData[15] = startingAddressBytes[1];
                            newMetaData[16] = startingAddressBytes[2];
                        }
                        
                        raf.write(newMetaData, 0, newMetaData.length);
                        break;

                    } else if ((newMetaData[0] == (byte) 0xDE || newMetaData[0] == (byte) 0x0E)) {
                        
                        // location of data and its block size
                        dataLocation = new byte[]{newMetaData[14], newMetaData[15], newMetaData[16]};
                        dataLocationBlockSize = new byte[]{newMetaData[20], newMetaData[21], newMetaData[22]};

                        // Save the emptyFATBlock size as well
                        emptyDataLocation = new byte[]{emptyFATBlock[14], emptyFATBlock[15], emptyFATBlock[16]};
                        emptyFATBlockSize = new byte[]{emptyFATBlock[20],emptyFATBlock[21],emptyFATBlock[22]};

                        // Sets the new meta data to the deleted FAT block's location
                        // Should look like DE FAT block, but with different location and block size
                        newMetaData[14] = emptyFATBlock[14];
                        newMetaData[15] = emptyFATBlock[15];
                        newMetaData[16] = emptyFATBlock[16];

                        newMetaData[20] = dataLocationBlockSize[0];
                        newMetaData[21] = dataLocationBlockSize[1];
                        newMetaData[22] = dataLocationBlockSize[2];

                        // Sets the empty FAT block to have the data location and block size
                        emptyFATBlock[14] = dataLocation[0];
                        emptyFATBlock[15] = dataLocation[1];
                        emptyFATBlock[16] = dataLocation[2];

                        emptyFATBlock[20] = emptyFATBlockSize[0];
                        emptyFATBlock[21] = emptyFATBlockSize[1];
                        emptyFATBlock[22] = emptyFATBlockSize[2];

                        // writes the new meta data to DE block
                        raf.seek(i);
                        raf.write(newMetaData, 0, newMetaData.length);

                        // writes DE meta data to 0E block
                        raf.seek(i + Codes.META_DATA_SIZE);
                        raf.write(emptyFATBlock, 0, emptyFATBlock.length);

                        // seek to location of data
                        raf.seek(byteArrayToInt(dataLocation, 3));
                        raf.readFully(buffer = new byte[byteArrayToInt(dataLocationBlockSize,3) * Codes.BLOCK_SIZE], 0, byteArrayToInt(dataLocationBlockSize, 3) * Codes.BLOCK_SIZE);
                        System.out.println("I created a buffer that is " + ((buffer.length / Codes.BLOCK_SIZE)) + " blocks long.");
                        
                        // write empty bytes to data location
                        raf.seek(byteArrayToInt(dataLocation,3));
                        emptyBuffer = new byte[buffer.length];
                        raf.write(emptyBuffer, 0, emptyBuffer.length);
                        
                        // write empty bytes to the delete we want to delete as well
                        raf.seek(byteArrayToInt(emptyDataLocation,3));
                        emptyBuffer = new byte[byteArrayToInt(emptyFATBlockSize,3) * Codes.BLOCK_SIZE];
                        raf.write(emptyBuffer, 0, emptyBuffer.length);

                        // seek to empty FAT block's data location and empty the buffer
                        raf.seek(byteArrayToInt(new byte[]{newMetaData[14], newMetaData[15], newMetaData[16]},3));
                        raf.write(buffer, 0, buffer.length);
                    }                    
                } else if(permFile == (byte) 0x1E) {
                    //System.out.println("Finished defragging.");
                    break;
                } else if (permFile == (byte) 0xEE) {
                    //System.out.println("No need to defrag.");
                    break;
                }
            } 
        } catch (IOException e ) {
            System.err.println(e.getMessage());
        }
    }
    
    // List files for USB0
    // Returns file # to delete from other USB devices
    public String listFiles() {
        
        // Used to determine fileCounter increment based on the sequence of RAID ID vs sequence of filename
        // USB0 considers new files to have a sequence of 0
        // USBN considers new files to have a sequence of N
        int checkSeq = (int) getRaidID_Seq() & 0xff;
        
        // Used to store integer for file selection
        int fileNum = -1;
        
        // File counter starts at -1 because our first file has 0 index
        int fileCounter = -1;
        
        // If no file is chosen, this "" is returned to RFS
        String fileNameToDelete = "";
        
        try {
            // Data file
            File file = new File(this.WORKING_DIRECTORY, this.FORMATTED_FILE_DEST);
            
            // Opens data file for access
            RandomAccessFile raf = new RandomAccessFile(file.getFileObj(), "rw");
            
            byte permFile;
            byte[] fileSeq = new byte[2];
            byte[] FATBlock = new byte[Codes.META_DATA_SIZE];
            byte[] FATBlockEmpty = new byte[Codes.META_DATA_SIZE];
            byte[] FATBlockFName = new byte[8];
            byte[] FATBlockFExt = new byte[3];
            byte[] FATBlockFLoc = new byte[3];
            byte[] FATBlockFSize = new byte[3];
            
            // Stores the list of file names
            ArrayList<String> listOfFileNames = new ArrayList<String>();
            
            for(int i = Codes.HEADER_SIZE; i < Codes.FAT_LENGTH; i += Codes.META_DATA_SIZE) {              
                raf.seek(i);                
                permFile = raf.readByte();
                
                fileSeq[0] = (byte) raf.readByte();
                fileSeq[1] = (byte) raf.readByte();
                
                if(permFile == (byte) 0x0E) {
                    
                    raf.seek(i);
                    for(int j = 0; j < Codes.META_DATA_SIZE; j++) {
                        FATBlock[j] = (byte) raf.readByte();

                        // store bytes for file name
                        if(j >= 3 && j <= 10)
                            FATBlockFName[j-3] = (FATBlock[j] != (byte) 0x00) ? FATBlock[j] : 0x20;                          
                        
                        // store bytes for file ex
                        else if (j >= 11 && j <= 13)
                            FATBlockFExt[j-11] = (FATBlock[j] != (byte) 0x00) ? FATBlock[j] : 0x20;
                        
                        // store bytes for file location
                        else if (j >= 14 && j <= 16)
                            FATBlockFLoc[j-14] = FATBlock[j];
                        
                        // store bytes for file size
                        else if (j >= 20 && j <= 22)
                            FATBlockFSize[j-20] = FATBlock[j];
                    }
                    
                    // If statement compares fileSeq # to RAID ID seq #
                    //      If equal, then add the file name to the ArrayList and print, incremente file counter
                    if(byteArrayToInt(fileSeq, fileSeq.length) == checkSeq) {
                        
                        // Store filename string (without spaces)
                        String fullFileNameWOSpaces = "";
                            fullFileNameWOSpaces += byteArrayToASCIIString(FATBlockFName);
                            fullFileNameWOSpaces += byteArrayToASCIIString(FATBlockFExt);
                            listOfFileNames.add(fullFileNameWOSpaces);
                            
                        // Inc file counter 
                        fileCounter++;
                        
                        
                        System.out.println("(" + fileCounter + ")\tName: " + byteArrayToASCIIString(FATBlockFName) + "."
                                                 + byteArrayToASCIIString(FATBlockFExt) + "\tLocation: "
                                                 + byteArrayToHexString(FATBlockFLoc) + "\tSize: "
                                                 + byteArrayToInt(FATBlockFSize, 3));
                    }
                    
                    // Zeroes out the FAT Block
                    FATBlock = FATBlockEmpty;
                }
            }
            
            raf.close();
            
            if (fileCounter >= 0) {
                
                try {
                    // Input
                    Scanner in = new Scanner(System.in);
                    fileNum = -1;

                    System.out.println();
                    System.out.print("Enter the file # to delete (<= -1 to exit): ");

                    fileNum = in.nextInt();

                    if (fileNum <= -1)
                        System.out.println("No files deleted.");
                    else if(fileNum > fileCounter) {
                        System.out.println("File does not exist");
                        fileNum = -1;
                    } else if(fileNum >= 0 && fileNum <= fileCounter) {
                        // Passes the filename string based on fileCounter index selection
                        // Stores it to the variable fileNameToDelete that is then returned to RFS
                        deleteFile(fileNameToDelete = listOfFileNames.get(fileNum));
                    }

                } catch (InputMismatchException e) {
                    System.err.println(e.getMessage());
                }      
            } else {
                System.out.println("USB contains no files.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }        
        
        return fileNameToDelete;
    }
    
    public boolean checkFileList(String fileNameExists) {
        
        // Same as listFiles, but returns true if file exists
        int checkSeq = (int) getRaidID_Seq() & 0xff;
        
        boolean fileExists = false;
        
        try {
            // Data file
            File file = new File(this.WORKING_DIRECTORY, this.FORMATTED_FILE_DEST);
            
            // Opens data file for access
            RandomAccessFile raf = new RandomAccessFile(file.getFileObj(), "rw");
            
            byte permFile;
            byte[] fileSeq = new byte[2];
            byte[] FATBlock = new byte[Codes.META_DATA_SIZE];
            byte[] FATBlockEmpty = new byte[Codes.META_DATA_SIZE];
            byte[] FATBlockFName = new byte[8];
            byte[] FATBlockFExt = new byte[3];
            byte[] FATBlockFLoc = new byte[3];
            byte[] FATBlockFSize = new byte[3];
            
            // holds an integer for the only purpose of a sum
            ArrayList<String> listOfFileNames = new ArrayList<String>();
            
            for(int i = Codes.HEADER_SIZE; i < Codes.FAT_LENGTH; i += Codes.META_DATA_SIZE) {                
                raf.seek(i);                
                permFile = raf.readByte();
                
                fileSeq[0] = (byte) raf.readByte();
                fileSeq[1] = (byte) raf.readByte();
                
                if(permFile == (byte) 0x0E) {
                    
                    raf.seek(i);
                    for(int j = 0; j < Codes.META_DATA_SIZE; j++) {
                        FATBlock[j] = (byte) raf.readByte();

                        // store bytes for file name
                        if(j >= 3 && j <= 10)
                            FATBlockFName[j-3] = (FATBlock[j] != (byte) 0x00) ? FATBlock[j] : 0x20;                          
                        
                        // store bytes for file ex
                        else if (j >= 11 && j <= 13)
                            FATBlockFExt[j-11] = (FATBlock[j] != (byte) 0x00) ? FATBlock[j] : 0x20;
                        
                        // store bytes for file location
                        else if (j >= 14 && j <= 16)
                            FATBlockFLoc[j-14] = FATBlock[j];
                        
                        // store bytes for file size
                        else if (j >= 20 && j <= 22)
                            FATBlockFSize[j-20] = FATBlock[j];
                    }
                    
                    // Print list=                  
                    if(byteArrayToInt(fileSeq, fileSeq.length) == checkSeq) {
                        
                        // Store filename string (without spaces)
                        String fullFileNameWOSpaces = "";
                            fullFileNameWOSpaces += byteArrayToASCIIString(FATBlockFName);
                            fullFileNameWOSpaces += byteArrayToASCIIString(FATBlockFExt);
                            listOfFileNames.add(fullFileNameWOSpaces);
                    }
                    
                    FATBlock = FATBlockEmpty;
                }
            }
            
            raf.close();
            
            // Loops through ArrayList and determines if the file exists
            for(int k = 0; k < listOfFileNames.size(); k++)
                if(listOfFileNames.get(k).equalsIgnoreCase(fileNameExists))
                           fileExists = true;
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        // Returns true if file exists
        // Returns false if file does not exist
        return fileExists;
    }
    
    // converts integer to byte array
    
    // If you're converting data size of file, len MUST be 3
    // If you're converting block size of data, len MUST be 3
    // If you're converting data location, len MUST be 3
    // If you're converting file sequence, len MUST be 2
    public static byte[] intToByteArray(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
    
    // Converts byte array to an integer (not ASCII)
    
    // If you're converting data size of file, len MUST be 3
    // If you're converting block size of data, len MUST be 3
    // If you're converting data location, len MUST be 3
    // If you're converting file sequence, len MUST be 2
    public static int byteArrayToInt(byte[] data, int len) {
        long sum = 0;

        for (int i = 0; i < data.length; i++)
            sum = (sum << 8) + (data[i] & 0xff);

        return (int) sum;
    }
    
    // Returns byte array as Hex string for debug purposes
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            
            if (v < 16) 
                sb.append('0');

            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
    
    // Converts byte array to ASCII string using US-ASCII decoder
    public static String byteArrayToASCIIString(byte[] b){
        String s = null;
        try {
            s = new String(b, "US-ASCII");
            s = s.toString().replaceAll(" ", "");
        } catch (Exception e) {}
        
        return s;
    }
    
    // Provides a clear way to read metadata from the filesystem
    // residing on the USB device.
    private void initMetaData(String pathToUsb) throws FileNotFoundException, IOException
    {
    	java.io.File rfsFile = new java.io.File(pathToUsb, Codes.RFS_FILENAME);	
    	RandomAccessFile raf = new RandomAccessFile(rfsFile, "rw");
    	
    	// Seek the beginning of the file
    	raf.seek(0);
    	
    	// Read in entire metadata block
    	byte[] metaDataBlock = new byte[Codes.META_DATA_SIZE];
    	int retVal = raf.read(metaDataBlock);
    	
    	// Save meta data to object
    	if(retVal != -1)
    	{
    		this.raidID                    = metaDataBlock[0];
    		this.raid_numOfDevicesInConfig = metaDataBlock[1];
    		this.raidID_Seq                = metaDataBlock[2];
    		this.raidType                  = metaDataBlock[3];
    	}
    	else
    	{
    		throw new IOException("Opened file but could not retrieve metadata block.");
    	}
    	
    	raf.close();
    }
 }
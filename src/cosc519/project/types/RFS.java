/**************************************************************
 * Filename: RFS.java                                         *
 * Class: COSC519                                             *
 * Description: Implementation of the RAID Filesystem that    *
 * performs all the RAID ops on each device.                  *
 * Package: cosc519.project.types                             *
 **************************************************************/ 
 
 package cosc519.project.types;
 
 import cosc519.project.types.Codes;
 import cosc519.project.UsbDevice;
 
 import java.io.FileOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.lang.NullPointerException;
 import java.lang.IllegalAccessException;
 
 public class RFS
 {
	// Private Members
	private byte mRaidType;                   // Refer to Codes definition
	private byte mRaidId;                     // Unique per RFS, ids a grp of devices
	private byte mRaidMemberCount;            // The number of UsbDevs required
	private ArrayList<UsbDevice> mUsbDevList; // This structure may not be complete if not all usb devs are available.
	private boolean mIsComplete;              // If all usb device members are found, the RFS is complete, else incomplete
	
	// Constructors
	// Use to initialize a new RFS with a previously formatted usb device, add usb devices with the addUsbDevice() method
	public RFS(UsbDevice pUsbDev) throws NullPointerException, IllegalAccessException
	{
		if(pUsbDev == null)
		{
			throw new NullPointerException("RFS(): USB device argument is null.");
		}
		
		if(pUsbDev.getFormatStatus())
		{
			this.mRaidType        = pUsbDev.getRaidType();
			this.mRaidId          = pUsbDev.getRaidID();
			this.mRaidMemberCount = pUsbDev.getNumOfDevicesInConfig();
			this.mUsbDevList      = new ArrayList<UsbDevice>(this.mRaidMemberCount);
			this.mUsbDevList.add(pUsbDev.getRaidID_Seq(), pUsbDev);
            
            setCompleteState();
		}
		else
		{
			throw new IllegalAccessException("RFS(UsbDevice): USB device is not formatted. Cannot create RFS object.");
		}
	}
	
	// Same as constructor above but initializes with complete list.
	public RFS(ArrayList<UsbDevice> pUsbDevList) throws NullPointerException, IllegalAccessException
	{
		if(pUsbDevList == null)
		{
			throw new NullPointerException("RFS(): USB device list argument is null. WTF mate?!");
		}
		
		// Init raidid with first usbdevice
		byte raidId = pUsbDevList.get(0).getRaidID();
		
		// Iterate through usb device list to check if each is formatted
		// uncomment for release
		for(int i = 0; i < pUsbDevList.size(); ++i)
		{
			if(pUsbDevList.get(i).getFormatStatus() == false)
			{
				throw new IllegalAccessException("RFS(ArrayList<UsbDevice>): A USB device is not formatted. Cannot create RFS object.");
			}
			
			if(raidId != pUsbDevList.get(i).getRaidID())
			{
				throw new IllegalAccessException("RFS(ArrayList<UsbDevice>): A USB device's RAID ID does not match the others. Cannot create RFS object.");
			}
			else
			{
				raidId = pUsbDevList.get(i).getRaidID();
			}
		}
		
		// Passed checks, initialize the RFS object
		this.mRaidType        = pUsbDevList.get(0).getRaidType();
		this.mRaidId          = pUsbDevList.get(0).getRaidID();
		this.mRaidMemberCount = pUsbDevList.get(0).getNumOfDevicesInConfig();
		this.mUsbDevList      = pUsbDevList;

        setCompleteState();
	}
	
	
	// Use to initialize a new RFS object with a list of usb devices
	public RFS(byte pRaidType, byte pRaidId, ArrayList<UsbDevice> pUsbDevList)
	{
		
	}
	
	// Accessors - Read Only
	public byte getRaidType()
	{
		return this.mRaidType;
	}
	
	public byte getRaidId()
	{
		return this.mRaidId;
	}
	
	public byte getMemberCount()
	{
		return this.mRaidMemberCount;
	}
	
	public ArrayList<UsbDevice> getUsbDevList()
	{
		return this.mUsbDevList;
	}     
	
	// Public Methods
	public boolean isComplete()
	{
		return this.mIsComplete;
	}
        
    public void addUsbDevice(UsbDevice toBeAdded)
    {
        this.mUsbDevList.add(toBeAdded.getRaidID_Seq(), toBeAdded);
        setCompleteState();
    }
	
	public ArrayList<File> getListOfFiles()
	{
		return null;
	}
	
	public void write(String path)
	{
		// Open new java.io.File object
		java.io.File tempFile = new java.io.File(path);
		
		// Iterate through usbdevices and stripe if striping otherwise mirroring
		if(this.mRaidType == Codes.TYPE_RAID_0_STRIPPING)
		{
			int startByte = 0;
			for(int i = 0; i < (tempFile.length() / Codes.BLOCK_SIZE); i++)
			{
				System.out.println("startbyte =" + startByte);
				System.out.println("filesize = " + Long.toString(tempFile.length()));
				System.out.println("blks = " + Long.toString(tempFile.length() / Codes.BLOCK_SIZE)); 
				System.out.println("mod = " + Integer.toString(i % this.mUsbDevList.size()));
				System.out.println("raidsequence = " + Byte.toString(this.mUsbDevList.get(i % this.mUsbDevList.size()).getRaidID_Seq()));
				
				File f = null;
				
				this.mUsbDevList.get(i % this.mUsbDevList.size()).addFile(
					f = new File(tempFile.getParent(), tempFile.getName(), i, startByte, Codes.BLOCK_SIZE) 
				);
				
				
				
				System.out.println("offset = " + Integer.toString(startByte));
				startByte += Codes.BLOCK_SIZE;
			}
		}
		else
		{
			for(int i = 0; i < mUsbDevList.size(); ++i)
			{
				this.mUsbDevList.get(i).addFile(
					new File(tempFile.getParent(), tempFile.getName())
				);
			}
		}
	}
	
	//public File read(String filename)
	//{
	//	return null;
	//}
	
	public void delete(String filename)
	{
        for(int i = 0; i < mUsbDevList.size(); ++i)
        {
            //array of usb devices
            //device(i) call deleteFile(filename)
            this.mUsbDevList.get(i).deleteFile(filename);
        }
	}
	
	public static void formatFile (UsbDevice device) 
	{

        // create byte array
        // 18 bytes for ID/TYPE/SIBLINGS
        // 22 * 32768 for FAT
        // 2097152 bytes for data
        byte[] blankFile = new byte[Codes.TOTAL_SIZE];
        
        blankFile[0] = device.getRaidID(); // USB Device ID (00...0F)
        blankFile[1] = device.getNumOfDevicesInConfig(); // 00 - FF potential
        blankFile[2] = device.getRaidID_Seq(); // sequence in RAID Config
        blankFile[3] = device.getRaidType(); // Raid Type (00 or 01)

        // Block address where data begins
        // Right now, grabs the byte conversion method from the USBDevice class
        byte[] startingAddress = UsbDevice.intToByteArray(Codes.STARTING_ADDRESS, 3);

        // EE byte is for the first file in FAT to indicate no files present
        // Sets first byte of each meta-data block to 1E
        // 1E is defined as empty file.
        // It is possible to have just one file use all blocks of data
        // but it is necessary to cycle through until we reach a meta-data block
        // whose first byte is 1E, then we know to stop the search because there are 
        // no more files, but NOT necessarily no more space.
        // 
        // Available space is determined by the last file's placement and its size.
        for(int i = Codes.HEADER_SIZE; i < Codes.FAT_LENGTH; i += Codes.META_DATA_SIZE) {
            if(i == Codes.HEADER_SIZE) {
                blankFile[i] = (byte) 0xEE;
                
                // Sets the 14-16th bytes to the starting address
                blankFile[i+14] = startingAddress[0];
                blankFile[i+15] = startingAddress[1];
                blankFile[i+16] = startingAddress[2];
                
            // If we aren't in the first FAT entry, then only create a FAT entry
            // that has 0x1E permission byte
            } else blankFile[i] = (byte) 0x1E;
        }

        try {

          // Create an output stream to the file.
          System.out.println("Creating RFS file in " + device.getPathToUSB() + "/" + device.getUSBFName());
          FileOutputStream file_output = new FileOutputStream (new java.io.File(device.getPathToUSB() + "/" + device.getUSBFName()));

          // Wrap the FileOutputStream with a DataOutputStream
          DataOutputStream data_out = new DataOutputStream (file_output);

          // Write the data
          for (int i=0; i < blankFile.length; i++) {
              data_out.writeByte(blankFile[i]);
          }

          // Close file 
          file_output.close();
          
          // Set USB device to formatted (true)
          device.setFormatStatus(true);
        }
        catch (IOException e) {
           System.out.println ("Exception = " + e );
        }
    }
    
    // Private Methods
    private void setCompleteState()
    {
        if (this.mUsbDevList.size() == this.mRaidMemberCount)
            this.mIsComplete = true;
        else // Need to check if size of usbdev list might be greater than the intended member count
            this.mIsComplete = false;
    }   
 }
/**************************************************************
 * Filename: RFS.java                                         *
 * Class: COSC519                                             *
 * Description: Implementation of the RAID Filesystem object. *
 * that resides on each usb device.                           *
 * Package: cosc519.project.types                             *
 **************************************************************/ 
 
 package cosc519.project.types;
 
 import cosc519.project.UsbDevice;
 import java.util.ArrayList;
 
 public class RFS
 {
 	public class FileEntry // May not need this, up to coders.
 	{
 		private int    mOffset;
 		private char[] mName;
 		private int    mType;
 	};
 	
	// Private Members
	private int mRaidType; // Refer to Codes definition
	private int mRaidId; // Unique per RFS, ids a grp of devices
	private int mRaidMemberCount; // The number of UsbDevs required
	//private int[] mRaidMembers; // will be allocated to size of Codes.MAX_MEMBERS - not needed
	//private ArrayList<FileEntry> mEntries; // may not need this, up to coder
	private ArrayList<UsbDevice> mUsbDevs; // This structure may not be complete if not all usb devs are present.
	
	// Public Methods
	public ArrayList<File> getListOfFiles()
	{
		return null;
	}
	
	public void write(File file)
	{
	
	}
	
	public File read(String filename)
	{
		return null;
	}
	
	public void delete(String filename)
	{
	
	}
 }
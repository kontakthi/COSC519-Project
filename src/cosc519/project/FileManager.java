/**************************************************************
 * Filename: FileManager.java                                 *
 * Class: COSC519                                             *
 * Description: Implementation of the FileManager object.     *
 * Package: cosc519.project                                   *
 **************************************************************/ 
 
 package cosc519.project;
 
 import cosc519.project.types.Codes;
 import cosc519.project.types.File;
 import cosc519.project.types.RFS;
 import cosc519.project.UsbDevice;
 import cosc519.project.RaidManager;
 import java.util.ArrayList;
 
 public class FileManager
 {
	// Private Members
	private RFS mActiveRfs;
	private ArrayList<UsbDevice> mUsbDevList;
	private ArrayList<RFS> mRfsList;
	
	public FileManager()
	{
		this.refreshAvailableUsbDev();
		this.refreshAvailableRfs();
		this.mActiveRfs = null;
	}
	
	//
	// File Operations
 	//////
 	
 	// Saves a file to a given RAID volume.
 	// Utilizes the active RFS object to write a file to
 	// the RAID volume.
 	public int saveFile(File pFileObj)
 	{
 		int result = Codes.RESULT_SUCCESS;
 	
 		return result;
 	}
 	
 	// Removes a file from a given RAID volume.
 	// Utilizes the active RFS object to remove a file from
 	// the RAID volume.
 	public int removeFile(File pFileObj)
 	{
 		int result = Codes.RESULT_SUCCESS;
 	
 		return result;
 	}
 	
 	// Retrieves a file from a given RAID config and saves onto
 	// host computer
 	public int retrieveFile(String file)
 	{
 		int result = Codes.RESULT_SUCCESS;
 	
 		return result;
 	}
 	
 	// Retrieves all files on a given RFS
 	public ArrayList<File> retrieveAllFiles()
 	{
 		return this.mActiveRfs.getListOfFiles();
 	}
 	
 	//
 	// Initialization
 	//////
 	public void setRfsContext(RFS pTargetRfs)
 	{
 		this.mActiveRfs = pTargetRfs;
 	}
 	
 	public void createRaidConfig(ArrayList<UsbDevice> pUsbDevList, byte pLabel, byte pType)
 	{
 		RFS temp = RaidManager.createRaidConfig(pUsbDevList, pLabel, pType);
 		
 		// Refresh RFS configs
 		// Refresh USB devices
 		this.refreshAvailableUsbDev();
		this.refreshAvailableRfs();
 	}
 	
 	//
 	// Status Retrieval
 	//////
 	public boolean hasRfsContext()
 	{
 		if(this.mActiveRfs == null)
 		{
 			return false;
 		}
 		else
 		{
 			return true;
 		}
 	}
 	
 	public RFS getActiveRfs()
 	{
 		return this.mActiveRfs;
 	}
 	
 	public ArrayList<RFS> getAvailableRfs()
 	{
 		return mRfsList;
 	}
 	
 	public ArrayList<UsbDevice> getAvailableUsbDev()
 	{
 		return mUsbDevList;
 	}
 	
 	public void refreshAvailableRfs()
 	{
 		if(this.mUsbDevList == null)
 		{
 			System.out.println("No USB devices were found. List is not initialized.");
 		}
 		else
 		{
 			this.mRfsList = RaidManager.getRfsList(this.mUsbDevList);
 		}
 	}
 	
 	public void refreshAvailableUsbDev()
 	{
		this.mUsbDevList = RaidManager.getStorageDevices();
 	}
 }
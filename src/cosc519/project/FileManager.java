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
 import cosc519.project.RaidManagerSingleton;
 import java.util.ArrayList;
 
 public class FileManager
 {
	// Private Members
	private RFS mActiveRfs;
	private RaidManager mRaidMgr;
	private ArrayList<UsbDevice> mUsbDevList;
	private ArrayList<RFS> mRfsList;
	
	public FileManager()
	{
		this.mRaidMgr = RaidManagerSingleton.getInstance();
		this.refreshAvailableUsbDev();
		this.refreshAvailableRfs();
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
 	
 	public void createRaidConfig(ArrayList<UsbDevice> pUsbDevList, String pLabel, int pType)
 	{
 		RFS temp = new RFS();
 		temp = this.mRaidMgr.createRaidConfig(pUsbDevList, pLabel, pType);
 		
 		// Refresh RFS configs
 		// Refresh USB devices
 	}
 	
 	//
 	// Status Retrieval
 	//////
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
 		this.mRfsList = this.mRaidMgr.getRfsList(this.mUsbDevList);
 	}
 	
 	public void refreshAvailableUsbDev()
 	{
		this.mUsbDevList = this.mRaidMgr.getStorageDevices();
 	}
 }
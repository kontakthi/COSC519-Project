/**************************************************************
 * Filename: File.java                                        *
 * Class: COSC519                                             *
 * Description: Represents a file to be stored in the RAID    *
 * file system.                                               *
 * Package: cosc.519.project.types                            *
 **************************************************************/ 
 
 package cosc519.project.types;
 
 public class File
 {
 	// Private Members
 	private String mName;
 	private String mPath;
 	private Byte[] mData;
 	
 	// Constructors
 	public File(String pPath) // Only create file obj for display purposes
 	{
 		// Save off file name
 		
 		// Save off path
 		this.mPath = pPath;
 		
 		// Set data to null for now
 		this.mData = null;
 	}	
 	
 	public File(String pPath, Boolean pSaveFlag) // Create file with binary data
 	{
 		// Save off file name
 		
 		// Save off path
 		this.mPath = pPath;
 		
 		// Save data locally if being saved to raid
 		if(pSaveFlag == true)
 		{
 			
 		}
 	}
 	
 	// Public Members
 	public String getFileName() { return this.mName; }	
 	public String getFilePath() { return this.mPath; }
 }
 
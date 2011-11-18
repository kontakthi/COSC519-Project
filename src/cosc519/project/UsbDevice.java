/**************************************************************
 * Filename: UsbDevice.java                                   *
 * Class: COSC519                                             *
 * Description: Represents a usb device which can be used in  *
 * RAID configuration.                                        *
 * Package: cosc519.project                                  *
 **************************************************************/ 
 
 package cosc519.project;
 
 import cosc519.project.types.File;
 import cosc519.project.types.RFS;
 
 public class UsbDevice
 {
 	// Private Members
 	private String mName;
 	private String mVolPath;
 	
 	// Constructors
 	public UsbDevice(String pName, String pVolPath)
 	{
 		this.mName = pName;
 		this.mVolPath = pVolPath;
 	}
 	
 	// Public Members
 	public String getName() { return this.mName; }
 	public String getVolPath() { return this.mVolPath; }
 	
 	public void writeFile(File pFile)
 	{
 	
 	}
 	
 	public File readFile(String filename)
 	{
 		return null;
 	}
 	
 	public void deleteFile(String filename)
 	{
 	
 	}
 	
 	/*
 	public RFS getRfsFat()
 	{
 		return null;
 	}
 	
 	public void formatDevice(RFS pRfs)
 	{
 	
 	}
 	*/
 }
/**************************************************************
 * Filename: Codes.java                                       *
 * Class: COSC519                                             *
 * Description: Stores the possible error codes and other     *
 * constants in the scope of this project.                    *
 * Package: cosc519.project.types                             *
 **************************************************************/ 
 
 package cosc519.project.types;
 
 public class Codes
 {
 	// Result Error Codes
 	public static final int RESULT_SUCCESS = 0;
 	public static final int RESULT_FAILURE = 1;
 	public static final int RESULT_QUIT    = 2;
 	public static final int RESULT_UNKNOWN = 3;
 	
 	// RAID Status Strings
 	public static final String RAID_STATUS_COMPLETE   = "Complete";
 	public static final String RAID_STATUS_INCOMPLETE = "Incomplete";
 	
 	// USB Status String
 	public static final String USB_STATUS_FORMATTED   = "Formatted";
 	public static final String USB_STATUS_UNFORMATTED = "Unformatted";
 	
 	// RAID Types
 	public static final int TYPE_RAID_0_STRIPPING = 0;
 	public static final int TYPE_RAID_1_MIRRORING = 1;
 	
 	// Project Constants
 	//public static final int MAX_MEMBERS = 4; // don't require this atm, uncomment if needed
 	
 	// OS Specific Paths
 	public static final String PATH_MAC = "/Volumes";
 	public static final String PATH_LINUX = "/media";
 }
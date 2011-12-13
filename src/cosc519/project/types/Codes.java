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
 	
 	// RAID Status
 	public static final String RAID_STATUS_COMPLETE   = "Complete";
 	public static final String RAID_STATUS_INCOMPLETE = "Incomplete";
 	public static final boolean RFS_STATUS_COMPLETE   = true;
 	public static final boolean RFS_STATUS_INCOMPLETE = false;
 	
 	// USB Status
 	public static final String  USB_STATUS_FORMATTED   = "Formatted";
 	public static final String  USB_STATUS_UNFORMATTED = "Unformatted";
 	public static final boolean USB_IS_FORMATTED       = true;
 	public static final boolean USB_IS_NOT_FORMATTED   = false;
 	
 	// RAID Types
 	public static final byte   TYPE_RAID_0_STRIPPING = 0;
 	public static final byte   TYPE_RAID_1_MIRRORING = 1;
 	public static final String TYPE_STRING_RAID_0    = "Stripping";
 	public static final String TYPE_STRING_RAID_1    = "Mirroring";
 	
 	// OS Specific Paths
 	public static final String PATH_MAC = "/Volumes";
 	public static final String PATH_LINUX = "/media";
 	public static final String RFS_FILENAME = "rfs.bin";
 	public static final String MAC_FILE_EXCLUSION_1 = PATH_MAC + "/Macintosh HD";
 	
 	// Size Definitions
 	public static final int RFS_DEVICE_MIN = 2;
 	public static final int DATA_SIZE = 8388608;  // in bytes
	public static final int BLOCK_SIZE = 512; // in bytes
	public static final int META_DATA_SIZE = 23; // in bytes
	public static final int HEADER_SIZE = 4;
	public static final int FAT_LENGTH = (DATA_SIZE/BLOCK_SIZE)*META_DATA_SIZE; // in bytes
	public static final int STARTING_ADDRESS = HEADER_SIZE + FAT_LENGTH;
	public static final int TOTAL_SIZE = HEADER_SIZE + FAT_LENGTH + DATA_SIZE;
	
	// Utility Methods
	public static String getRaidTypeString(byte type)
	{
		switch(type)
		{
			case TYPE_RAID_0_STRIPPING:
				return TYPE_STRING_RAID_0;
				
			case TYPE_RAID_1_MIRRORING:
				return TYPE_STRING_RAID_1;
				
			default:
				return "Unknown Type";
		}
	}
	
	public static String getRfsStatusString(boolean state)
	{
		if(state == RFS_STATUS_COMPLETE)
			return RAID_STATUS_COMPLETE;
		else
			return RAID_STATUS_INCOMPLETE;
	}
 }
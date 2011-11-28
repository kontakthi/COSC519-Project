/**************************************************************
 * Filename: Main.java                                        *
 * Class: COSC519                                             *
 * Description: The main entry point of the RAID file manager *
 * that utilizes the RAID API developed for this project.     *
 * Package: main application                                  *
 **************************************************************/ 
 
 import cosc519.project.FileManager;
 import cosc519.project.FileManagerSingleton;
 import cosc519.project.UsbDevice;
 import cosc519.project.types.Codes;
 import cosc519.project.types.RFS;
 
 import java.lang.NullPointerException;
 import java.util.ArrayList; 
 import java.io.Console;
 import java.util.Random;
 
 public class Main
 {
 	// Input Options
 	private static final String MAIN_MENU_QUIT   = "q";
 	private static final String MAIN_MENU_USB    = "u";
 	private static final String MAIN_MENU_RFS    = "i";
 	private static final String MAIN_MENU_CREATE = "c";
 	private static final String MAIN_MENU_FILE   = "f";
 	
 	private static final String SUB_MENU_QUIT    = "m";
 	
 	private static final String PROMPT_SUFFIX = "?> ";
 	
 	// Private global class members
 	private static FileManager gblFileMgr = null;
 	
 	// If time permits, handle cmd line args
 	public static void handleCmdLineArgs(String[] args)
 	{
 		
 	}
 	
 	public static ArrayList<UsbDevice> testUsbDevList(int objCount)
 	{
 		ArrayList<UsbDevice> testObjects = new ArrayList<UsbDevice>();
 		String pathToUSB = "/testpath/";
 		String newPath;
 		String USBFName = "/usbfs.bin";
 		byte raidID = 0;
 		byte numOfDevicesInConfig = 0;
 		byte raidID_Seq = 0; 
 		byte raidType = 0;
 		
 		Random rng = new Random();
 		
 		numOfDevicesInConfig = (byte)objCount;
 		raidID = (byte)rng.nextInt();
 		for(int i = 0; i < objCount; ++i)
 		{
 			newPath = pathToUSB + Integer.toString(rng.nextInt()); 
 			raidID_Seq = (byte)i;
 			raidType = (byte)(Math.abs(rng.nextInt()) % 2);
 			
 			testObjects.add(new UsbDevice(newPath, USBFName, raidID, numOfDevicesInConfig, raidID_Seq, raidType));
 		}
 		
 		return testObjects;
 	}
 	
 	public static ArrayList<RFS> testRfsList(int objCount)
 	{
 		ArrayList<RFS> testObjects = new ArrayList<RFS>();
 		ArrayList<UsbDevice> usbDevList;
 		
 		String pathToUSB = "/testpath/";
 		String newPath;
 		String USBFName  = "/usbfs.bin";
 		byte raidID = 0;
 		byte numOfDevicesInConfig = 0;
 		byte raidID_Seq = 0; 
 		byte raidType = 0;
 		
 		Random rng = new Random();
 		
 		for(int i = 0; i < objCount; ++i)
 		{
 			newPath = pathToUSB + Integer.toString(rng.nextInt()); 
 			raidID = (byte)rng.nextInt();
 			numOfDevicesInConfig = (byte)rng.nextInt();
 			raidID_Seq = (byte)rng.nextInt();
 			raidType = (byte)(Math.abs(rng.nextInt()) % 2);
 			
 			usbDevList = testUsbDevList(3);
 			try
 			{
 				testObjects.add(new RFS(usbDevList));
 			}
 			catch(Exception e)
 			{
 				System.out.println(e.toString());
 			}
 		}
 		
 		return testObjects;
 	}
 	
 	public static void displayHeader()
 	{
 		System.out.println("*********************************************");
 		System.out.println("***********                       ***********");
 		System.out.println("************* RAID File Manager *************");
 		System.out.println("***********                       ***********");
 		System.out.println("*********************************************");
 	}
 	
 	public static void displayMenu()
 	{
 		System.out.println("Please Make A Selection...");
 		System.out.println("> List (U)sb Devices");
 		System.out.println("> List (I)nitialized RAID File Systems[RFS]");
 		System.out.println("> (C)reate New RAID File System");
 		
 		if(gblFileMgr.hasRfsContext())
 		{
 			System.out.println("> Perform (F)ile Operations on RFS [ID: " + String.format("0x%02X", gblFileMgr.getActiveRfs().getRaidId()) + "]");
 		}
 		
 		System.out.println("> (Q)uit");
 	}
 	
 	public static String repeat(String pStr, int pTimes)
 	{
 		StringBuilder retString = new StringBuilder();
   		
   		for(int i = 0; i < pTimes; ++i) 
   			retString.append(pStr);
   			
   		return retString.toString();
 	}
 	
 	public static void displayUsbDevices(ArrayList<UsbDevice> pUsbDevList) throws NullPointerException
 	{
 		if(pUsbDevList == null)
 		{
 			throw new NullPointerException("displayUsbDevices(): the list is null");
 		}
 		
 		Integer i = 0;
 		
 		// Setup table header, labels should have an even number of characters for the sake of simple display
 		int tblWidth       = 98;
 		String lblTitle    = "USB  Devices";
 		String lblIndex    = "  index#  ";
 		String lblRaidId   = "   ID   ";
 		String lblRaidSeq  = "  Sequence  ";
 		String lblRaidType = "    Type    ";
 		String lblPath     = "  Path";
 		int remWidth       = tblWidth - lblIndex.length() - lblRaidId.length() - lblRaidSeq.length() - lblRaidType.length() - lblPath.length(); // Remaining table width
 		lblPath += repeat(" ", tblWidth - remWidth - 2);
 		
 		
 		System.out.format(" " + repeat("_", tblWidth) + " %n");
 		System.out.format("|" + repeat(" ", tblWidth) + "|%n");
 		System.out.format("|" + repeat(" ", (tblWidth - lblTitle.length()) / 2) + lblTitle + repeat(" ", (tblWidth - lblTitle.length()) / 2) + "|%n");
 		System.out.format("|" + repeat("_", tblWidth) + "|%n");
 		System.out.format("|" + repeat(" ", lblIndex.length())    +
 		                  "|" + repeat(" ", lblRaidId.length())   +
 		                  "|" + repeat(" ", lblRaidSeq.length())  +
 		                  "|" + repeat(" ", lblRaidType.length()) +
 		                  "|" + repeat(" ", lblPath.length())     + "|%n");
 		System.out.format("|" +
 						  lblIndex    + "|" + 
 						  lblRaidId   + "|" +
 						  lblRaidSeq  + "|" +
 						  lblRaidType + "|" +
 						  lblPath     + "|%n");
 		System.out.format("|" + repeat("-", lblIndex.length())    +
 		                  "|" + repeat("-", lblRaidId.length())   +
 		                  "|" + repeat("-", lblRaidSeq.length())  +
 		                  "|" + repeat("-", lblRaidType.length()) +
 		                  "|" + repeat("-", lblPath.length())     + "|%n");
 		
 		for(UsbDevice device: pUsbDevList)
 		{
 			System.out.format("|%" + Integer.toString(lblIndex.length()) + "d", i);
 			System.out.format("|%" + Integer.toString(lblRaidId.length()) + "X", device.getRaidID());
 			System.out.format("|%" + Integer.toString(lblRaidSeq.length()) + "d", device.getRaidID_Seq());
 			System.out.format("|"  + repeat(" ", lblRaidType.length() - Codes.getRaidTypeString(device.getRaidType()).length()) + Codes.getRaidTypeString(device.getRaidType()));
 			System.out.format("|"  + device.getPathToUSB() + device.getUSBFName() + repeat(" ", lblPath.length() - (device.getPathToUSB() + device.getUSBFName()).length()) + "|%n");
 			
 			++i;
 		}
 		
 		System.out.format("-" + repeat("-", tblWidth) + "-%n");
 	}
 	
 	public static void displayRfs(ArrayList<RFS> pRfsList) throws NullPointerException
 	{
 		if(pRfsList == null)
 		{
 			throw new NullPointerException("displayRfs(): the list is null");
 		}	
 		
 		Integer i = 0;
 		
 		for(RFS rfs: pRfsList)
 		{
 			System.out.println("RFS > "             + i.toString());
 			System.out.println("RAID Id:        "   + String.format("0x%02X", rfs.getRaidId()));
 			System.out.println("RAID Type:      "   + Codes.getRaidTypeString(rfs.getRaidType()));
 			System.out.println("RFS State:      "   + Codes.getRfsStatusString(rfs.isComplete()));
 			System.out.println("Num of Members: "   + Byte.toString(rfs.getMemberCount()));
 			
 			// List usb devices belonging to this RFS
 			ArrayList<UsbDevice> usbDevList = rfs.getUsbDevList();
			displayUsbDevices(usbDevList);
 			
 			++i;
 		}
 	}
 	
 	public static void handleListUsbDevices()
 	{
 		// Generate Test Data, comment out for release, replace with retrieveUsbDeviceList function
 		ArrayList<UsbDevice> list = testUsbDevList(5);
 	
 		// Display available usb devices
 		displayUsbDevices(list);
 	}
 	
 	public static void handleListRfs()
 	{
 		// Generate Test Data, comment out for release, replace with retrieveRfsList function
 		ArrayList<RFS> list = testRfsList(3);
 		
 		// Display available RFS
 		displayRfs(list);
 		
 		// Choose a target RFS
 		System.out.println("Please choose a valid RFS index or type 'm' to return to main menu...");
 		String input;
 		while(true)
 		{
 			input = promptForInput(PROMPT_SUFFIX);
 			
 			// Normalize input
 			input = input.trim();
 			input = input.toLowerCase();
 			
 			if(input.compareTo(SUB_MENU_QUIT) == 0)
 			{
 				// We are quitting, break loop
 				break;
 			}
 			else
 			{
 				// We have input, attempt to specify target RFS
 				try
 				{
 					Integer rfsIndex = Integer.parseInt(input);
 					
 					if((rfsIndex + 1) > list.size() || rfsIndex < 0)
 					{
 						System.out.println("The index provided is out of range. Try again.");
 					}
 					else
 					{
 						gblFileMgr.setRfsContext(list.get(rfsIndex));
 						System.out.println("Active RFS has been set to RFS ID " + String.format("0x%02X", list.get(rfsIndex).getRaidId()) + "\n");
 						break;
 					}
 				}
 				catch(Exception e) // Probably a NumberFormatException
 				{
 					System.out.println("The index provided is invalid. Try again.");
 				}		
 			}
 		}
 	}
 	
 	public static String promptForInput(String pSuffix)
 	{
 		String input; 		
 		Console console = System.console();
 		
 		while(true)
 		{
 			// Output prompt if provided
 			if(pSuffix != null)
 			{
 				System.out.print(pSuffix);
 			}
 			
 			// Read input
 			input = console.readLine();
 			
 			// Check for valid input
 			if(input.compareTo("") != 0)
 			{
 				break;
 			}
 		}
 		
 		return input;	
 	}
 	
 	public static int handleInput(String pInput)
 	{
 		int result = Codes.RESULT_FAILURE;
 		
 		// Normalize string for comparisons
 		pInput = pInput.trim();
 		pInput = pInput.toLowerCase();
 		
 		// Determine action from input
 		if(pInput.compareTo(MAIN_MENU_QUIT) == 0)
 		{
 			result = Codes.RESULT_QUIT;
 		}
 		else if(pInput.compareTo(MAIN_MENU_USB) == 0)
 		{
 			handleListUsbDevices();
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else if(pInput.compareTo(MAIN_MENU_RFS) == 0)
 		{
 			handleListRfs();
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else if(pInput.compareTo(MAIN_MENU_CREATE) == 0)
 		{
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else if(pInput.compareTo(MAIN_MENU_FILE) == 0 && gblFileMgr.hasRfsContext())
 		{
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else
 		{
 			result = Codes.RESULT_UNKNOWN;
 		}
 		
 		return result;
 	}
 	
 	public static void main(String[] args)
 	{
 		String input;
 		String suffix = PROMPT_SUFFIX;
 		int    result = Codes.RESULT_SUCCESS;
 		
 		// Initialize file manager
 		gblFileMgr = FileManagerSingleton.getInstance();
 		
 		// Display header at beginning
 		displayHeader();
 		
 		// Enter control loop
 		while(true)
 		{
 			// Display menu
 			if(result != Codes.RESULT_UNKNOWN)
 			{
 				displayMenu();
 			}
 			else
 			{
 				System.out.println("Invalid input!");
 			}
 			
 			// Prompt for input		
 			input = promptForInput(suffix);
 			
 			// Handle input
 			result = handleInput(input);
 			
 			// Check for quit
 			if(result == Codes.RESULT_QUIT)
 			{
 				System.out.println("Good Bye");
 				break;	
 			}
		}
 	}
 }
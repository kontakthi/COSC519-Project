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
 
 import java.lang.NullPointerException;
 import java.util.ArrayList; 
 import java.io.Console;
 import java.util.Random;
 
 public class Main
 {
 	// Input Options
 	private static final String MAIN_MENU_QUIT   = "q";
 	private static final String MAIN_MENU_USB    = "u";
 	private static final String MAIN_MENU_RAID   = "i";
 	private static final String MAIN_MENU_CREATE = "c";
 	
 	private static final String PROMPT_SUFFIX = "?> ";
 	
 	// Private global class members
 	private static FileManager gblFileMgr = null;
 	
 	// If time permits, handle cmd line args
 	public static void handleCmdLineArgs(String[] args)
 	{
 		
 	}
 	
 	public static ArrayList<UsbDevice> testList(int objCount)
 	{
 		ArrayList<UsbDevice> testObjects = new ArrayList<UsbDevice>();
 		String pathToUSB = "/testpath/";
 		String USBFName = "usbfs.bin";
 		byte raidID = 0;
 		byte numOfDevicesInConfig = 0;
 		byte raidID_Seq = 0; 
 		byte raidType = 0;
 		
 		Random rng = new Random();
 		
 		for(int i = 0; i < objCount; ++i)
 		{
 			raidID = (byte)rng.nextInt();
 			
 			testObjects.add(new UsbDevice(pathToUSB, USBFName, raidID, numOfDevicesInConfig, raidID_Seq, raidType));
 		}
 		
 		return testObjects;
 	}
 	
 	public static void displayHeader()
 	{
 		System.out.println("*************************************");
 		System.out.println("*********                   *********");
 		System.out.println("********* RAID File Manager *********");
 		System.out.println("*********                   *********");
 		System.out.println("*************************************");
 	}
 	
 	public static void displayMenu()
 	{
 		System.out.println("Please Make A Selection...");
 		System.out.println("> List (U)sb Devices");
 		System.out.println("> List (I)nitialized RAID File Systems[RFS]");
 		System.out.println("> (C)reate New RAID File System");
 		System.out.println("> (Q)uit");
 	}
 	
 	public static void displayUsbDevices(ArrayList<UsbDevice> pUsbDevList) throws NullPointerException
 	{
 		if(pUsbDevList == null)
 		{
 			throw new NullPointerException("displayUsbDevices(): the list is null");
 		}
 		
 		for(UsbDevice device: pUsbDevList)
 		{
 			System.out.println("Usb Device #");
 			System.out.println("Path: ");
 			System.out.println("RAID Id: ");
 			System.out.println("RAID Sequence Id: ");
 			
 			System.out.println("");
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
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else if(pInput.compareTo(MAIN_MENU_RAID) == 0)
 		{
 			
 			result = Codes.RESULT_SUCCESS;
 		}
 		else if(pInput.compareTo(MAIN_MENU_CREATE) == 0)
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
 				break;	
 			}
		}
 	}
 }
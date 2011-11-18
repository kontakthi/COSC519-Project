/**************************************************************
 * Filename: FileManagerSingleton.java                        *
 * Class: COSC519                                             *
 * Description: Keeps track of a single instance of the       *
 * FileManager class.                                         *
 * Package: cosc.519.project                                  *
 **************************************************************/ 
 
 package cosc519.project;
 
 public class FileManagerSingleton
 {
 	// Private Members
 	private static FileManager mFileMgr = null;
 	
 	// Singleton Retrieval
 	public static FileManager getInstance()
 	{
 		if(mFileMgr == null)
 		{
 			mFileMgr = new FileManager();	
 		}
 		
 		return mFileMgr;
 	}
 		
 }
/**************************************************************
 * Filename: RaidManagerSingleton.java                        *
 * Class: COSC519                                             *
 * Description: Keeps track of a single instance of the       *
 * RaidManager class.                                         *
 * Package: cosc.519.project                                  *
 **************************************************************/ 
 
 package cosc519.project;
 
 public class RaidManagerSingleton
 {
 	// Private Members
 	private static RaidManager mRaidMgr = null;
 	
 	// Singleton Retrieval
 	public static RaidManager getInstance()
 	{
 		if(mRaidMgr == null)
 		{
 			mRaidMgr = new RaidManager();	
 		}
 		
 		return mRaidMgr;
 	}
 		
 }
/**************************************************************
 * Filename: RaidManager.java                                 *
 * Class: COSC519                                             *
 * Description: Responsible for performing all RAID related   *
 * functionality.                                             *
 * Package: cosc519.project                                   *
 **************************************************************/ 
 
 package cosc519.project;
 
 import cosc519.project.types.RFS;
 import java.util.ArrayList;
 
 public class RaidManager
 {	
 	// Private Members
 	
	// Public Methods
	public RaidManager()
	{
	
	}
	
	public static ArrayList<RFS> getRfsList(ArrayList<UsbDevice> pUsbDevList)
 	{
            ArrayList<RFS> tempRfs = new ArrayList<RFS>();
            for (int i = 0; i < pUsbDevList.size(); i++) {
                if (pUsbDevList.get(i).getFormatStatus() == true) {
                    boolean inRfsList = false;
                    int existingRfs = 0;
                    for (int j = 0; j < tempRfs.size(); j++) {
                        if (tempRfs.get(j).getRaidId() != pUsbDevList.get(i).getRaidID()) {
                            inRfsList = false;
                        } else {
                            inRfsList = true;
                            existingRfs = j;
                            break;
                        }
                    }
                    if (inRfsList == true)
                        tempRfs.get(existingRfs).addUsbDevice(pUsbDevList.get(i));
                    else if (inRfsList == false) {
                        try{
                            tempRfs.add(new RFS(pUsbDevList.get(i)));
                        }
                        catch (NullPointerException e) {
                        }
                        catch (IllegalAccessException e){
                        }
                    }
                }
            }         
//              else
//                  System.err.println("USB Device with Raid ID: " + pUsbDevList.get(i).getRaidID() + " not formatted");
            return tempRfs;
        }

	public ArrayList<UsbDevice> getStorageDevices()
	{
		return null;
	}
	
	public RFS createRaidConfig(ArrayList<UsbDevice> pUsbDevList, String pLabel, int pType)
 	{
 		return null;
 	}
 }
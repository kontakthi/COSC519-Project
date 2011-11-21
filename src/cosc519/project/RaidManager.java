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
	
	public ArrayList<RFS> getRfsList(ArrayList<UsbDevice> pUsbDevList)
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
                        tempRfs.get(existingRfs).getUsbDevList().add(pUsbDevList.get(i));
                    else if (inRfsList == false) {
                        try{
                            tempRfs.add(new RFS(pUsbDevList.get(i)));
                        }
                        catch (NullPointerException e) {
                        } 
                    }
                }         
//              else
//                  System.err.println("USB Device with Raid ID: " + pUsbDevList.get(i).getRaidID() + " not formatted");
            }
        
            for (int i = 0; i < tempRfs.size(); i++) {
                int j;
                for (j = 0; j < tempRfs.get(i).getUsbDevList().size(); j++);
                if (tempRfs.get(i).getRaidMemberCount() == j) {
                    tempRfs.get(i).setCompleteState(Codes.RAID_STATUS_COMPLETE);
                } else {
                    tempRfs.get(i).setCompleteState(Codes.RAID_STATUS_INCOMPLETE);
                }
            }
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
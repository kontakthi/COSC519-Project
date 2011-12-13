/**************************************************************
 * Filename: RaidManager.java                                 *
 * Class: COSC519                                             *
 * Description: Responsible for performing all RAID related   *
 * functionality.                                             *
 * Package: cosc519.project                                   *
 **************************************************************/ 
 
 package cosc519.project;
 
 import cosc519.project.types.RFS;
 import cosc519.project.types.Codes;
 import cosc519.project.UsbDevice;
 import java.util.ArrayList;
 import java.io.File;
 
 public class RaidManager
 {		
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

	public static ArrayList<UsbDevice> getStorageDevices()
	{
		ArrayList<UsbDevice> devList     = new ArrayList<UsbDevice>();
		File                 volumesPath = new File(Codes.PATH_MAC);
		File[]               files       = volumesPath.listFiles();
		
		for(File file : files)
		{
			if(file.toString().compareTo(Codes.MAC_FILE_EXCLUSION_1) == 0)
			{
				continue; // Skip hard drive
			}
			else
			{
				devList.add(new UsbDevice(file.toString())); // Add usb drive
			}
		}
		
		return devList;
	}
	
	public static RFS createRaidConfig(ArrayList<UsbDevice> pUsbDevList, byte pLabel, byte pType)
 	{
            RFS newRfs = null;
            ArrayList<UsbDevice> tempUsb = new ArrayList<UsbDevice>();
            for (int i = 0; i < pUsbDevList.size(); i++){
                tempUsb.add(i, new UsbDevice(pUsbDevList.get(i).getPathToUSB(), "RFS", pLabel, 
                (byte) pUsbDevList.size(), (byte) i, pType));
                newRfs.formatFile(tempUsb.get(i));
            }
            try{
                newRfs = new RFS(tempUsb);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            catch (IllegalAccessException e){
                e.printStackTrace();
            }                
            return newRfs;
 	}
 }
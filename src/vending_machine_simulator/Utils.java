package vending_machine_simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

import entity.InfoPromotion;

public class Utils {
	
	public static InfoPromotion readFile (String path){
		InfoPromotion info = new InfoPromotion();
        try{
        	FileInputStream fis = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader bfr = new BufferedReader(reader);
            String line = bfr.readLine();
            while(line != null){
                String []array = line.split(",");
                if(array.length == 2){
                	InfoPromotion infoPromte = new InfoPromotion(
                            Integer.parseInt(array[0]),array[1]);
                	info = infoPromte;

                }
                line = bfr.readLine();
            }
            System.out.println("Đọc file từ nguồn " + path + " thành công !");
            fis.close();
            reader.close();
            bfr.close();
        }
        catch(IOException e){
        	e.printStackTrace();
        }
        return info;
    }  
	
	public static void ghiFile (String path, InfoPromotion info){
		File file = new File(path + "InfoPromotion.txt");
	     try {
	    	 if(!file.exists()) {
	        		file.createNewFile();
	        	}
	    	  FileOutputStream fos = new FileOutputStream(file);
		      OutputStreamWriter output = new OutputStreamWriter(fos);
		      output.write(info.getPrevLimitedBudget()+","+info.getDate()+"\n");
		      System.out.println("Ghi file vào nguồn "+ path +" thành công !");
		      output.close();
	    }
	    catch (Exception e) {
	      e.getStackTrace();
	    }
	 }
	
	public static boolean getPromotion(double winRate) {
		if(winRate == 0.1) {
			return new Random().nextInt(10) == 1;
		}
		if(winRate == 0.5) {
			return new Random().nextInt(10) <= 4; 
		}
		return false;
	}
}

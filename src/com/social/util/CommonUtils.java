package com.social.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.servlet.http.Part;


public class CommonUtils {
	
	public static void byteToFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//
                dir.mkdirs();  
            }  
            file = new File(filePath+"/"+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
	
	
	public static byte[] getImgByte(Part part) throws IOException {

		InputStream inputStream = part.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(b)) > 0) {
			baos.write(b, 0, len);
		}
		b = baos.toByteArray();
		return b;
	}
	
	public static double getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
		final double EARTH_RADIUS = 6378.137;//
        Double lat1 = Double.parseDouble(lat1Str);
        Double lng1 = Double.parseDouble(lng1Str);
        Double lat2 = Double.parseDouble(lat2Str);
        Double lng2 = Double.parseDouble(lng2Str);
          
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(lng2);
        double distance = 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2.0), 2.0)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2.0), 2.0)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000.0) / 10000.0;
        //System.out.println("CommonUtils->distance = " + distance);
        DecimalFormat df = new DecimalFormat("0.0");
        
        String distanceStr = distance+"";
        //System.out.println("CommonUtils->distance_str = " + distance);
        //distanceStr = distanceStr.substring(0, distanceStr.indexOf("."));
          
        return distance;
    }
	
	private static double rad(double d) { 
        return d * Math.PI / 180.0; 
    }
	

}

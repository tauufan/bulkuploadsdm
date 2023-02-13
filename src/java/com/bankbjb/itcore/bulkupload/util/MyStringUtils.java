/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankbjb.itcore.bulkupload.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import org.apache.log4j.Logger;

/**
 *
 * @author c994
 */
public class MyStringUtils {
    
    private static final Logger logger = Logger.getLogger(MyStringUtils.class);
    
    private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    }
    
    public static String md5Hash(String str) {
        String out = str;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes(), 0, str.length());
            out = convertToHex(md5.digest());
        } catch (Exception e) {
            logger.error("md5Hash error: " + e.getMessage());
        }
        
        return out;
    }
    
    public static String leftString(String input, int len) {
        return input.substring(0, input.length() <= len ? input.length() : len);
    }
    
}

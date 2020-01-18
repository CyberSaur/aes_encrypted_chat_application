/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_application;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
/**
 *
 * @author Suneth
 */
public class AESencryption{
    
    private static SecretKeySpec secretKey;
    private static byte[] key;

    //method to set the key
    public static void setKey(String myKey){
        try{
            MessageDigest sha = null;
            try{
                    key = myKey.getBytes("UTF-8");
                    sha = MessageDigest.getInstance("SHA-1");
                    key = sha.digest(key);
                    key = Arrays.copyOf(key, 16);
                    secretKey = new SecretKeySpec(key, "AES");
               }catch(NoSuchAlgorithmException e){
                    e.printStackTrace();
               }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
               }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Unable to send the message","Error",JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occured: " + ex.toString());
        }
    }

    //encryption method
    public static String encrypt(String strToEncrypt, String secret) throws Exception{
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }
    
    //decryption method
    public static String decrypt(String strToDecrypt, String secret) throws Exception{
        setKey(secret);
	Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	cipher.init(Cipher.DECRYPT_MODE, secretKey);
	return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
}

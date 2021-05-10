package com.ilibellus.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.ilibellus.helpers.LogDelegate;


public class Security {


    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            LogDelegate.w("Something is gone wrong calculating MD5", e);
        }
        return "";
    }


    public static String encrypt(String value, String password) {
        String encrypedValue = "";
        try {
            DESKeySpec keySpec = new DESKeySpec(password.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            return encrypedValue;
		} catch (InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException | BadPaddingException |
				IllegalBlockSizeException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			LogDelegate.w("Something is gone wrong encrypting", e);
        }
        return encrypedValue;
    }


    public static String decrypt(String value, String password) {
        String decryptedValue;
        try {
            DESKeySpec keySpec = new DESKeySpec(password.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            decryptedValue = new String(decrypedValueBytes);
		} catch (InvalidKeyException | UnsupportedEncodingException | InvalidKeySpecException |
				NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException
				e) {
			LogDelegate.e("Error decrypting");
            return value;
            // try-catch ensure compatibility with old masked (without encryption) values
        } catch (IllegalArgumentException e) {
            LogDelegate.e("Error decrypting: old notes were not encrypted but just masked to users");
            return value;
        }
        return decryptedValue;
    }


}

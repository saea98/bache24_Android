package com.cmi.bache24.security;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by omar on 3/21/16.
 */
public class Security {

    private static Cipher cipher;
    private static SecretKeySpec key;
    private static AlgorithmParameterSpec spec;

    public static final String KEY = "rCjpnthk29QdVNFC";

    private static void setKey () {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(KEY.getBytes("UTF-8"));
            byte[] keyBytes = new byte[32];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            key = new SecretKeySpec(keyBytes, "AES");
            spec = getIV();
        } catch (Exception ex) {

        }
    }

    public static AlgorithmParameterSpec getIV()
    {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public static String encrypt(String plainText)
    {
        try {
            setKey();
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");

            return encryptedText;

        } catch (Exception ex) {
        }

        return null;
    }

    public static String decrypt(String cryptedText)
    {
        try {
            setKey();
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(bytes);
            String decryptedText = new String(decrypted, "UTF-8");

            return decryptedText;

        } catch (Exception ex) {

        }

        return null;
    }
}

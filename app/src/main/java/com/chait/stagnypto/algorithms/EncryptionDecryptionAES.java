package com.chait.stagnypto.algorithms;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionDecryptionAES {
    private static Cipher cipher;
    private static KeyGenerator keyGenerator;

    public EncryptionDecryptionAES() throws Exception {
        init();
    }

    private static void init() throws Exception {
        keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        cipher = Cipher.getInstance("AES");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] encrypt(String plainText)
            throws Exception {
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        // get base64 encoded version of the key
        String encodedKey = encoder.encodeToString(secretKey.getEncoded());
        String[] encryptionResult = {encryptedText,encodedKey};
        return encryptionResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String encryptedText, String secretKey)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        // decode the base64 encoded string
        byte[] decodedKey = decoder.decode(secretKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, originalKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}
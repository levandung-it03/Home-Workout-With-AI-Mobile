package com.restproject.mobile.utils;

import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.exception.ApplicationException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class CryptoService {
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = new byte[16]; // 16 bytes of zeros

    public static String encrypt(String raw) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            byte[] keyBytes = Base64.decode(BuildConfig.SECRET_CRYPTO_KEY, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] encrypted = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new ApplicationException("Weird Encoded from Crypto");
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            byte[] keyBytes = Base64.decode(BuildConfig.SECRET_CRYPTO_KEY, Base64.DEFAULT);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] decoded = Base64.decode(encryptedText, Base64.DEFAULT);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ApplicationException("Weird Encoded from Crypto");
        }
    }
}

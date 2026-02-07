package com.cs.web.spring.helper;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GoogleAuthenticator {
    private static final int SECRET_KEY_LENGTH = 10;
    private static final int TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;
    private static final String CRYPTO = "HmacSHA1";
    private static final Base32 BASE_32 = new Base32();

    public static String generateSecretKey() {
        byte[] buffer = new byte[SECRET_KEY_LENGTH];
        new java.security.SecureRandom().nextBytes(buffer);
        return BASE_32.encodeToString(buffer);
    }

    public static String generateCode(String secretKey) {
        return generateCode(secretKey, System.currentTimeMillis() / 1000 / TIME_STEP);
    }

    public static boolean verifyCode(String secretKey, String code, int timeWindow) {
        long time = System.currentTimeMillis() / 1000 / TIME_STEP;
        for (int i = -timeWindow; i <= timeWindow; i++) {
            String tempCode = generateCode(secretKey, time + i);
            if (tempCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    private static String generateCode(String secretKey, long time) {
        byte[] key = BASE_32.decode(secretKey);
        byte[] data = ByteBuffer.allocate(8).putLong(time).array();
        
        try {
            Mac mac = Mac.getInstance(CRYPTO);
            mac.init(new SecretKeySpec(key, CRYPTO));
            byte[] hash = mac.doFinal(data);
            
            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }
            
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= Math.pow(10, CODE_DIGITS);
            
            return String.format("%0" + CODE_DIGITS + "d", truncatedHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating code", e);
        }
    }

    public static String getOtpAuthUrl(String secretKey, String account, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, account, secretKey, issuer);
    }
}
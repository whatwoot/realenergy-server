package com.cs.sp.util;



import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * google身份验证器，java服务端实现
 */
public class GoogleAuthCode {
    private static final int CODE_DIGITS = 6;
    private static final int TIME_STEP_SECONDS = 30;

    /**
     * Generate a six digit TOTP code given a secret key and a timestamp.
     *
     * @param secret the shared secret key
     * @param time   the timestamp in seconds
     * @return the six-digit TOTP code
     */
    public static String generateCode(String secret, long time) throws InvalidKeyException {
        byte[] keyBytes = new Base32().decode(secret);
        byte[] data = new byte[8];
        long value = time / TIME_STEP_SECONDS;
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        Mac mac = null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
            mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xf;
            int truncatedHash = 0;
            for (int i = 0; i < 4; i++) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xff);
            }
            truncatedHash &= 0x7fffffff;
            truncatedHash %= Math.pow(10, CODE_DIGITS);
            return String.format("%06d", truncatedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        String secret = "JBSWY3DPEHPK3PXP";
        System.out.println(generateCode(secret, System.currentTimeMillis()/1000));
    }
}

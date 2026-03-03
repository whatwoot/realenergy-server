package com.cs.energy.tron.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES加密工具类
 * 运算模式：ECB
 * 填充模式：PKCS7
 * 输出格式：Base64 UrlSafe
 */
public class AesUtil {

    /**
     * AES加密
     *
     * @param data      待加密数据
     * @param secretKey 密钥
     * @return 加密后的Base64字符串
     */
    public static String encrypt(String data, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), 0, secretKey.getBytes().length, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    /**
     * AES解密
     *
     * @param data      待解密数据
     * @param secretKey 密钥
     * @return 解密后的字符串
     */
    public static String decrypt(String data, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), 0, secretKey.getBytes().length, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedData = Base64.getUrlDecoder().decode(data);
        byte[] decrypted = cipher.doFinal(decodedData);
        return new String(decrypted);
    }
}

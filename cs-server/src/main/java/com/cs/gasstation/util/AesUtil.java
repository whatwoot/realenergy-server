package com.cs.gasstation.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
     * AES/ECB/PKCS7Padding 加密
     * * @param data 待加密字符串
     * @param key  密钥 (16, 24, 32位)
     * @return Base64 UrlSafe 编码后的密文
     */
    public static String aesEncrypt(String data, String key) throws Exception {
        // 1. 构造密钥规范
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // 2. 指定算法/模式/填充
        // 注意：JDK原生支持 PKCS5Padding，在 AES 下它与 PKCS7 逻辑一致
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        // 3. 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 4. 执行加密
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 5. 使用 Base64 UrlSafe 编码 (去掉末尾等号，将 + / 替换为 - _)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
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

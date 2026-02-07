package com.cs.web.spring.helper.aeshelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author sb
 * @date 2024/2/26 03:54
 */

public class AesHelper {

    private static final Logger log = LoggerFactory.getLogger(AesHelper.class);

    private String secretKey;
    private Charset charset = Charset.defaultCharset();

    public AesHelper(String secretKey) {
        this.secretKey = secretKey;
    }

    public AesHelper(String secretKey, Charset charset) {
        this.secretKey = secretKey;
        if(charset != null){
            this.charset = charset;
        }
    }

    /**
     * 使用参数中的密钥加密
     *
     * @param sSrc 明文
     * @return 密文
     */
    public String encrypt(String sSrc) {
        try {
            byte[] raw = secretKey.getBytes(charset);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(charset));
            // 此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
            log.error("AesHelper Encrypt", e);
        } catch (InvalidKeyException e) {
            log.info("AesHelper InvalidKey", e);
        } catch (IllegalBlockSizeException e) {
            log.info("AesHelper IllegalBlockSize", e);
        }
        return null;
    }

    /**
     * 使用参数中的密钥解密
     *
     * @param sSrc 明文
     * @return 明文
     */
    public String decrypt(String sSrc) {
        try {
            byte[] raw = secretKey.getBytes(charset);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 先用base64解密
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, charset);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
            log.error("AesHelper Encrypt", e);
        } catch (InvalidKeyException e) {
            log.info("AesHelper InvalidKey", e);
        } catch (IllegalBlockSizeException e) {
            log.info("AesHelper IllegalBlockSize", e);
        }
        return null;
    }
}

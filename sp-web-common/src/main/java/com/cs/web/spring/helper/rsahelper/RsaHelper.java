package com.cs.web.spring.helper.rsahelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @author sb
 * @date 2024/8/30 02:41
 */
@Slf4j
public class RsaHelper {

    public static final String GEN_AUTO = "auto";
    public static final String GEN_FILE = "file";
    public static final String CLASS_PATH = "classpath:";

    public static final String ALGOR = "RSA";
    private KeyPair keyPair;
    private RsaProperties rsaProperties;
    private Charset charset;

    public RsaHelper(RsaProperties rsaProperties) {
        this.rsaProperties = rsaProperties;
        this.init();
    }

    public void init() {
        this.charset = StringUtils.hasText(rsaProperties.getCharset()) ?
                Charset.forName(rsaProperties.getCharset()) : StandardCharsets.UTF_8;
        if (GEN_AUTO.equals(rsaProperties.getGenType())) {
            this.keyPair = gen();
        }
        if (GEN_FILE.equals(rsaProperties.getGenType())) {
            // 暂时只支持pkcs8格式，因为不用导入额外的加密包。
            // pkcs1支持得导加密包
            String key;
            if(rsaProperties.getFilePath().startsWith(CLASS_PATH)){
                key = loadKeyFromClasspath(rsaProperties.getFilePath().substring(CLASS_PATH.length()));
            }else{
                key = loadKeyFromSystem(rsaProperties.getFilePath());
            }
            this.keyPair = loadFromPkcs8(key);
        }
    }

    public static KeyPair gen() {
        return gen(4096);
    }

    public static KeyPair gen(Integer size) {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGOR);
            keyPairGenerator.initialize(size, new SecureRandom());
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.warn("Not Support RSA", e);
        }
        return null;
    }

    public static String loadKeyFromClasspath(String classPath){
        try {
            return loadFileFromClasspath(classPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadKeyFromSystem(String filePah){
        try {
            return new String(Files.readAllBytes(new File(filePah).toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair loadFromPkcs8(String key) {
        KeyFactory keyFactory = null;
        try {
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            keyFactory = KeyFactory.getInstance(ALGOR);
            // 获得私钥
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
            // 使用私钥生成公钥
            RSAPrivateCrtKeySpec privateCrtKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
            PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(privateCrtKeySpec.getModulus(),
                    privateCrtKeySpec.getPublicExponent()));
            return new KeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn("RSA init error", e);
        }
        return null;
    }

    private static String loadFileFromClasspath(String classPath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(classPath);
        try (InputStream inputStream = classPathResource.getInputStream();
             ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }

            return result.toString("utf-8");
        }
    }


    public String encrypt(String input) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGOR);
            cipher.init(Cipher.ENCRYPT_MODE, this.keyPair.getPublic());
            byte[] bytes = cipher.doFinal(input.getBytes(charset));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
            log.warn("RSA init error", e);
        } catch (IllegalBlockSizeException | IllegalArgumentException e) {
            log.warn("RSA param error", e);
        }
        return null;
    }

    public String decrypt(String base64) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGOR);
            cipher.init(Cipher.DECRYPT_MODE, this.keyPair.getPrivate());
            byte[] decode = Base64.getDecoder().decode(base64);
            byte[] bytes = cipher.doFinal(decode);
            return new String(bytes, charset);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            log.warn("RSA init error", e);
        } catch (IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
            log.warn("RSA param error", e);
        }
        return null;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String publicKey() {
        return Base64.getEncoder().encodeToString(this.keyPair.getPublic().getEncoded());
    }
}

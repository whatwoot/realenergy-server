package com.cs.energy.member.server.util;

import com.cs.energy.evm.api.util.EthSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.StringJoiner;

import static com.cs.sp.common.WebAssert.expect;

/**
 * @author fiona
 * @date 2024/12/10 02:32
 */
@Slf4j
public class LoginUtil {

    public static final Long PAYLOAD_TTL = 30 * 60L;
    public static final Integer PAYLOAD_BYTE_LEN = 32;
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String TOKEN_DATA = "WebAppData";
    public static final String HASH_KEY = "hash";
    public static final String PARAM_SPLITER = "&";
    public static final String PARAM_JOINER = "=";
    public static final String PARAM_DELIMITER = "\n";

    public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Long checkBind(String decrypt) {
        try {
            String[] arr = decrypt.split(",");
            long time = Long.parseLong(arr[1]);
            if (time < System.currentTimeMillis()) {
                return null;
            }
            return Long.parseLong(arr[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static void checkSign(String addr, Long unixTimestamp, String nonce, String msg, String secret, String sign) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Login:");
        sj.add(addr);
        sj.add("Time:");
        sj.add(unixTimestamp.toString());
        sj.add("Nonce:");
        sj.add(nonce);
        sj.add(msg);
        expect(checkPayload(nonce, unixTimestamp, secret), "chk.invalid.payload");
        boolean signValid = EthSignUtil.isSignatureValid(addr, sign, sj.toString());
        if (!signValid) {
            log.info("CheckSign failed {},{},{},{},{}.{}=>{}", sj.toString(), addr, unixTimestamp, nonce, msg, sign);
        }
        // 时间误差
        expect(signValid, "chk.invalid.sign");
    }




    public static String genPayload(String token) {
        return genPayload(token, PAYLOAD_TTL);
    }

    public static String genPayload(String botToken, Long expire) {
        // 生成8个随机字节
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBits = new byte[8];
        secureRandom.nextBytes(randomBits);

        // 获取当前时间并计算过期时间
        long currentTime = System.currentTimeMillis() / 1000;
        long expirationTime = currentTime + expire;

        // 将过期时间转换为8字节的Big Endian格式
        ByteBuffer expirationTimeBuffer = ByteBuffer.allocate(8);
        expirationTimeBuffer.putLong(expirationTime);

        // 拼接randomBits和expirationTime
        byte[] payload = new byte[16];
        System.arraycopy(randomBits, 0, payload, 0, 8);
        System.arraycopy(expirationTimeBuffer.array(), 0, payload, 8, 8);

        byte[] signature = encode(botToken, payload);

        // 将payload和signature拼接
        // 16字节的payload + 32字节的signature
        byte[] finalPayload = new byte[48];
        System.arraycopy(payload, 0, finalPayload, 0, 16);
        System.arraycopy(signature, 0, finalPayload, 16, 32);

        // 提取finalPayload的前32字节并转换为十六进制字符串
        byte[] payloadHexBytes = Arrays.copyOfRange(finalPayload, 0, 32);
        return Hex.encodeHexString(payloadHexBytes);
    }

    public static boolean checkPayload(String payloadStr, Long unixTimestamp, String secret) {
        byte[] payload = null;
        try {
            payload = Hex.decodeHex(payloadStr);
        } catch (DecoderException e) {
            log.warn("CheckPayload failed", e);
            return false;
        }
        if (payload.length != PAYLOAD_BYTE_LEN) {
            return false;
        }

        byte[] payloadSignatureBytes = encode(secret, Arrays.copyOfRange(payload, 0, 16));
        boolean signatureValid = Arrays.equals(
                Arrays.copyOfRange(payload, 16, 32),
                Arrays.copyOfRange(payloadSignatureBytes, 0, 16)
        );
        if (!signatureValid) {
            return false;
        }
        long now = System.currentTimeMillis() / 1000;
        ByteBuffer expireBuffer = ByteBuffer.wrap(Arrays.copyOfRange(payload, 8, 16));
        if (now > expireBuffer.getLong()) {
            return false;
        }
        if (now > unixTimestamp + PAYLOAD_TTL) {
            return false;
        }
        return true;
    }


    public static String getHash(String token, String str) {
        byte[] newKeySpec = encode(TOKEN_DATA, token);
        return encodeHex(newKeySpec, str);
    }

    private static String encodeHex(byte[] newKeySpec, String str) {
        byte[] encode = encode(newKeySpec, str);
        return Hex.encodeHexString(encode);
    }

    private static byte[] encode(String secret, String data) {
        return encode(secret.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] encode(String secret, byte[] data) {
        return encode(secret.getBytes(StandardCharsets.UTF_8), data);
    }

    private static byte[] encode(byte[] secret, String data) {
        return encode(secret, data.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] encode(byte[] secret, byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secret, HMAC_SHA256);
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("encode failed", e);
        }
        return null;
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.warn("decode failed", e);
        }
        return null;
    }
}

package com.cs.sp.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cs.sp.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author sb
 * @date 2023/8/9 17:39
 */
@Slf4j
public class StringUtil {

    public static final String EMPTY_STRING = "";

    public static JSONObject safeParseJSON(String json) {
        try {
            return JSONUtil.parseObj(json);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    public static Optional<JSONObject> safeParseJSONNullable(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(json);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return Optional.of(jsonObject);
    }

    public static String generateRandomId() {
        return String.format("-%s%s", System.currentTimeMillis(), RandomStringUtils.randomNumeric(4));
    }

    public static String sceneNick(String wallet) {
        if (!StringUtils.hasText(wallet)) {
            return wallet;
        }
        String first = wallet.substring(0, 4);
        String last = wallet.substring(wallet.length() - 4);
        return String.format("%s**%s", first, last);
    }

    public static String senseWallet(String wallet) {
        if (!StringUtils.hasText(wallet)) {
            return wallet;
        }
        String first = wallet.substring(0, 6);
        String last = wallet.substring(wallet.length() - 4);
        return String.format("%s**%s", first, last);
    }

    public static String senseCnName(String name) {
        if(name.length() <= 1) {
            return name;
        }else if(name.length() == 2) {
            return name.substring(0, 1) + "*";
        }else{
            return name.substring(0, 1) + "*" + name.substring(name.length() - 1);
        }
    }

    public static String senseName(String name) {
        String[] split = name.split("\\s+");
        StringJoiner joiner = new StringJoiner(" ");
        for (String s : split) {
            joiner.add(senseCnName(s));
        }
        return joiner.toString();
    }

    public static String senseMail(String mail) {
        if (!StringUtils.hasText(mail)) {
            return mail;
        }
        return mail.replaceAll("^(.{2}).*(.{2})@.*(.{2})$", "$1**$2@**$3");
    }

    public static String ensureLength(String str, Integer length) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }
        if (str.length() > length) {
            str = str.substring(0, length);
        }

        return str;
    }

    public static String ensureLength(String str, Integer length, char padChar) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }
        if (str.length() > length) {
            str = str.substring(0, length);
        }
        if (str.length() < length) {
            StringBuilder builder = new StringBuilder(str);
            while (builder.length() < length) {
                builder.append(padChar);
            }
            return builder.toString();
        }
        return str;
    }

    public static String fromBitString(String pos, Integer length, char padChar) {
        if (pos == null) {
            return ensureLength(Constant.EMPTY_STRING, length, padChar);
        } else {
            StringBuilder binaryString = new StringBuilder();
            byte[] bytes = pos.getBytes(StandardCharsets.ISO_8859_1);
            for (byte b : bytes) {
                binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }
            return ensureLength(binaryString.toString(), length, '0');
        }
    }

    public static String randomString() {
        return String.format("%s%s", System.currentTimeMillis(), 10000 + RandomStringUtils.randomNumeric(5));
    }
}

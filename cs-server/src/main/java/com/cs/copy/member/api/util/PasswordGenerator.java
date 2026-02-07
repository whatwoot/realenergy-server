package com.cs.copy.member.api.util;

/**
 * @authro fun
 * @date 2025/5/7 21:02
 */
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 随机密码生成工具类
 * 功能：
 * 1. 支持自定义密码长度
 * 2. 可选是否包含特殊字符
 * 3. 强制密码包含大小写字母、数字（若启用特殊字符则也会包含）
 */
public final class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = LOWERCASE.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final int length;
    private final boolean useSpecialChars;

    public PasswordGenerator(int length, boolean useSpecialChars) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }
        this.length = length;
        this.useSpecialChars = useSpecialChars;
    }

    /**
     * 生成随机密码
     */
    public String generate() {
        String allowedChars = useSpecialChars ?
                LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS :
                LOWERCASE + UPPERCASE + DIGITS;

        // 确保密码至少包含1个小写字母、1个大写字母、1个数字（如果启用特殊字符则也包含1个）
        StringBuilder password = new StringBuilder();
        password.append(randomChar(LOWERCASE));
        password.append(randomChar(UPPERCASE));
        password.append(randomChar(DIGITS));
        if (useSpecialChars) {
            password.append(randomChar(SPECIAL_CHARS));
        }

        // 填充剩余字符
        for (int i = password.length(); i < length; i++) {
            password.append(randomChar(allowedChars));
        }

        // 打乱顺序（避免前几位固定）
        return shuffle(password.toString());
    }

    /**
     * 从字符串中随机选取一个字符
     */
    private char randomChar(String charSet) {
        return charSet.charAt(RANDOM.nextInt(charSet.length()));
    }

    /**
     * 打乱字符串顺序
     */
    private String shuffle(String input) {
        List<Character> chars = input.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(chars, RANDOM);
        return chars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}

package com.cs.web.spring.helper.argon2;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author sb
 * @date 2025/2/20 19:19
 */
public class Argon2Helper {
    /**
     * 配认配置（加密时长大约500ms时间）
     */
    private int opsLimit = 2;
    private int memLimit = 10240;
    private int outputLength = 32;
    private int parallelism = 1;

    public Argon2Helper(Argon2Properties prop) {
        if(prop.getOps() != null){
            this.opsLimit = prop.getOps();
        }
        if(prop.getMemory() != null){
            this.memLimit = prop.getMemory();
        }
        if(prop.getParallel() != null){
            this.parallelism = prop.getParallel();
        }
        if(prop.getOutLength() != null){
            this.outputLength = prop.getOutLength();
        }
    }

    public String encrypt(String password, String salt) {
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(opsLimit)
                .withMemoryAsKB(memLimit)
                .withParallelism(parallelism)
                .withSalt(btoa(salt));
        Argon2BytesGenerator gen = new Argon2BytesGenerator();
        gen.init(builder.build());
        byte[] result = new byte[outputLength];
        gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
        return atob(result);
    }

    public static String randomSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return atob(salt);
    }

    public static String atob(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] btoa(String b64) {
        return Base64.getDecoder().decode(b64);
    }
}

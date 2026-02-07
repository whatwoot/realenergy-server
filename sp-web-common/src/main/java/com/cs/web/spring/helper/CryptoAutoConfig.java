package com.cs.web.spring.helper;

import com.cs.web.spring.helper.aeshelper.AesHelper;
import com.cs.web.spring.helper.aeshelper.AesProperties;
import com.cs.web.spring.helper.argon2.Argon2Helper;
import com.cs.web.spring.helper.argon2.Argon2Properties;
import com.cs.web.spring.helper.hashids.HashidsHelper;
import com.cs.web.spring.helper.hashids.HashidsProperties;
import com.cs.web.spring.helper.rsahelper.RsaHelper;
import com.cs.web.spring.helper.rsahelper.RsaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.nio.charset.Charset;

/**
 * @author sb
 * @date 2024/2/26 04:12
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({AesProperties.class, RsaProperties.class,
        HashidsProperties.class, Argon2Properties.class})
public class CryptoAutoConfig {

    public CryptoAutoConfig() {
        log.info("CryptoAutoConfig init");
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("crypt.aes.secretKey")
    public AesHelper aesHelper(AesProperties aesProperties) {
        log.info("AesHelper init");
        Charset charset = null;
        if (StringUtils.isNotBlank(aesProperties.getCharset())) {
            charset = Charset.forName(aesProperties.getCharset());
        }
        return new AesHelper(aesProperties.getSecretKey(), charset);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("crypt.rsa.genType")
    public RsaHelper rsaHelper(RsaProperties rsaProperties) {
        log.info("RsaHelper init: {}", rsaProperties.getGenType());
        return new RsaHelper(rsaProperties);
    }

    @Bean
    @Primary
    @ConditionalOnProperty("crypt.hashids.salt")
    public HashidsHelper hashidsHelper(HashidsProperties hashidsProperties) {
        log.info("HashidsHelper init");
        return new HashidsHelper(hashidsProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("crypt.argon2.outLength")
    public Argon2Helper argon2Helper(Argon2Properties argon2Properties) {
        log.info("Argon2Helper init");
        return new Argon2Helper(argon2Properties);
    }
}

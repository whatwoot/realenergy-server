package com.cs.web.spring.helper.rsahelper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2024/8/30 02:37
 */
@Data
@ConfigurationProperties(prefix = "crypt.rsa")
public class RsaProperties {
    /**
     * 生成方式： auto: 自动生成, file: 指定文件路径
     * auto：每次启动都会变动私钥
     * file: 保存密钥文件，方便任意时候使用文件解密
     */
    private String genType;
    /**
     * 如果是file类型，则说明文件类型：PKCS1 or PKCS8
     * 暂时只支持pkcs8格式，因为不用导入额外的加密包，暂时不支持加密文件加载
     * PKCS1： 直接使用 openssl genrsa -out private_pkcs1.pem 4096 生成的就是pkcs1格式的
     * PKCS8： openssl pkcs8 -topk8 -inform PEM -in private_pkcs1.pem -outform PEM -nocrypt -out private_pkcs8.pem
     */
    private String fileType;
    /**
     * 如果是file类型，指明文件classpath路径
     */
    private String filePath;
    /**
     * classpath: 会被打成包
     * system: 系统目录
     */
    private String pathType;
    private String charset;
}

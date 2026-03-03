package com.cs.energy.system.server.util;

/**
 * @authro fun
 * @date 2025/5/29 13:22
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.tika.Tika;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 文件类型检测工具（通用逻辑，不涉及业务规则）
 */
@Slf4j
public class FileTypeDetector {
    private static final Tika TIKA = new Tika();

    /**
     * 综合检测文件真实类型（优先使用Tika，失败后回退到Hex检测）
     */
    public static String detectFileType(InputStream inputStream, String originalFilename) throws IOException {
        // 1. 使用Tika检测MIME类型（最可靠）
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(1024 * 1024); // 标记支持1MB重置
        try {
            String mimeType = TIKA.detect(inputStream);
            log.info("File-type {}", mimeType);
            if (mimeType != null) {
                String ext = MimeTypeToExtMap.get(mimeType);
                if (ext != null){
                    log.info("File-type ext {}", ext);
                    return ext;
                }
            }
        } finally {
            bufferedInputStream.reset();
        }

        // 2. Hex检测（兜底逻辑）
        byte[] header = new byte[2048];
        int bytesRead = bufferedInputStream.read(header);
        if(bytesRead > -1){
            String hex = Hex.encodeHexString(Arrays.copyOfRange(header, 0, bytesRead)).toLowerCase();
            String hexExt = detectByHex(hex);
            if (hexExt != null) return hexExt;
        }

        // 3. 原始扩展名（最终兜底）
        return extractExtension(originalFilename);
    }

    /**
     * Hex检测逻辑（内部使用）
     */
    private static String detectByHex(String prefix) {
        log.info("detectByHex {}", prefix);
        if (prefix.startsWith("ffd8ff")) return ".jpg";
        if (prefix.startsWith("89504e47")) return ".png";
        if (prefix.startsWith("47494638")) return ".gif";
        if (prefix.startsWith("424d")) return ".bmp";
        // 其他Hex规则...
        return null;
    }

    /**
     * 从文件名提取扩展名
     */
    public static String extractExtension(String filename) {
        log.info("extractExtension {}", filename);
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? null : filename.substring(dotIndex).toLowerCase();
    }
}



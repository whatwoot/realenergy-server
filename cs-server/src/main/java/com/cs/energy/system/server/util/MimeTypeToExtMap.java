package com.cs.energy.system.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @authro fun
 * @date 2025/5/29 15:22
 */
public class MimeTypeToExtMap {
    public static final Map<String, String> MAP = new HashMap<String, String>() {{
        put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
        put("application/vnd.ms-excel", ".xls");
        put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
        put("application/msword", ".doc");
        put("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx");
        put("application/vnd.ms-powerpoint", ".ppt");
        put("application/vnd.wps-office.xlsx", ".xlsx");
        put("application/vnd.wps-office.docx", ".docx");
        put("image/jpeg", ".jpg");
        put("image/png", ".png");
        put("image/gif", ".gif");
        put("image/bmp", ".bmp");
        put("application/pdf", ".pdf");
        put("application/zip", ".zip");
        put("application/x-rar-compressed", ".rar");
        put("application/x-7z-compressed", ".7z");
        put("application/gzip", ".gz");
        put("text/plain", ".txt");
        put("text/csv", ".csv");
        put("application/rtf", ".rtf");
        put("audio/mpeg", ".mp3");
        put("video/mp4", ".mp4");
    }};

    public static String get(String mimeType) {
        return MAP.get(mimeType);
    }
}

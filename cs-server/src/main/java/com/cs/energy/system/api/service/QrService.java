package com.cs.energy.system.api.service;

/**
 * @authro fun
 * @date 2025/3/20 19:01
 */

public interface QrService {
    String scanQRCode(String imagePath);
    String scanCode(String imagePath);
    String scanMulti(String imagePath);
    String scanMulti2(String imagePath);
}

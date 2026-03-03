package com.cs.energy.system.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.cs.energy.system.api.service.QrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.cs.sp.common.WebAssert.throwBizException;

/**
 * @authro fun
 * @date 2025/3/20 19:02
 */
@Slf4j
@Service
public class QrServiceImpl implements QrService {
    @Override
    public String scanQRCode(String imagePath) {
        try {
            // 读取本地图片
            BufferedImage image = ImageIO.read(new File(imagePath));

            // 2. 图像预处理
            BufferedImage processedImage = preprocessImage(image);

            // 3、将图片转换为 ZXing 可处理的格式
            LuminanceSource source = new BufferedImageLuminanceSource(processedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // 4. 配置解码提示参数
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            // 创建 QR Code 专用读取器
            QRCodeReader reader = new QRCodeReader();
            try {
                // 解码图片中的二维码
                Result result = reader.decode(bitmap, hints);
                // 返回二维码内容
                return result.getText();
            } catch (NotFoundException e) {
                log.info("Qr-retry");
                // 如果失败，尝试调整图像后再次解码
                BufferedImage adjustedImage = adjustImage(processedImage);
                LuminanceSource newSource = new BufferedImageLuminanceSource(adjustedImage);
                BinaryBitmap newBitmap = new BinaryBitmap(new HybridBinarizer(newSource));

                try {
                    Result result = reader.decode(newBitmap, hints);
                    return result.getText();
                } catch (NotFoundException ex) {
                    log.info("Qr-retry2");
                    // 最后尝试使用不同的二值化方法
                    BinaryBitmap globalBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                    Result result = reader.decode(globalBitmap, hints);
                    return result.getText();
                }
            }
        } catch (IOException e) {
            log.warn("Qr-scan {}", imagePath);
            throwBizException("chk.payment.imgNotFound");
        } catch (NotFoundException | ChecksumException | FormatException e) {
            log.warn("Qr-scan {}", imagePath);
            throwBizException("chk.payment.imgInvalid");
        } catch (Exception e) {
            throwBizException("chk.payment.scanFailed");
        }
        return null;
    }

    @Override
    public String scanCode(String imagePath) {
        try {
            // 读取本地图片
            BufferedImage image = ImageIO.read(new File(imagePath));
            BufferedImage processedImage = preprocessImage(image);
            //读取指定的二维码文件
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(processedImage)));

            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
            hints.put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);  // 尝试反转图像

            MultiFormatReader formatReader = new MultiFormatReader();
            Result result = formatReader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (IOException e) {
            log.warn("Code-scan {}", imagePath);
            throwBizException("chk.payment.imgNotFound");
        } catch (NotFoundException e) {
            log.warn("Code-scan {}", imagePath);
            throwBizException("chk.payment.imgInvalid");
        } catch (Exception e) {
            throwBizException("chk.payment.scanFailed");
        }
        return null;
    }

    private static BufferedImage preprocessImage(BufferedImage original) {
        // 转换为灰度图
//        BufferedImage grayImage = new BufferedImage(
//                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        Graphics g = grayImage.getGraphics();
//        g.drawImage(original, 0, 0, null);
//        g.dispose();

        // 转换为灰度图
        BufferedImage grayImage = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayImage.getGraphics().drawImage(original, 0, 0, null);

        // 可选：增加对比度
        RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
        rescaleOp.filter(grayImage, grayImage);

        // 这里可以添加其他预处理步骤，如锐化、降噪等
        return grayImage;
    }

    private static BufferedImage adjustImage(BufferedImage image) {
        // 简单的亮度/对比度调整
        float factor = 1.2f; // 调整因子
        BufferedImage adjusted = new BufferedImage(
                image.getWidth(), image.getHeight(), image.getType());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int r = (int) (((rgb >> 16) & 0xFF) * factor);
                int g = (int) (((rgb >> 8) & 0xFF) * factor);
                int b = (int) ((rgb & 0xFF) * factor);

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                adjusted.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return adjusted;
    }

    public String scanMulti(String imagePath) {
        try {
            // 读取图像文件
            BufferedImage image = ImageIO.read(new File(imagePath));

            // 配置支持的条码格式
            List<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);

            // 设置解码参数
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            // 尝试不同预处理方法
            for (int i = 0; i < 3; i++) {
                try {
                    BufferedImage processedImage = preprocessImage(image, i);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(
                            new HybridBinarizer(new BufferedImageLuminanceSource(processedImage)));

                    MultiFormatReader reader = new MultiFormatReader();
                    Result result = reader.decode(binaryBitmap, hints);
                    log.info("Multi-scan {}", i);
                    return result.getText();
                } catch (NotFoundException e) {
                    // 尝试下一种预处理方法
                    if (i == 2) throw e; // 最后一次尝试后抛出异常
                }
            }
        }catch (IOException e){
            log.warn("Multi-scan {} not found", imagePath);
            throwBizException("chk.payment.imgNotFound");
        } catch (NotFoundException e) {
            log.warn("Multi-scan {}", imagePath);
            throwBizException("chk.payment.imgInvalid");
        } catch (Throwable e) {
            log.warn(StrUtil.format("Multi-scan {} failed", imagePath), e);
        }
        return null;
    }

    private static BufferedImage preprocessImage(BufferedImage original, int method) {
        BufferedImage processed = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // 方法0: 基本灰度化
        if (method == 0) {
            processed.getGraphics().drawImage(original, 0, 0, null);
        }
        // 方法1: 增加对比度
        else if (method == 1) {
            processed.getGraphics().drawImage(original, 0, 0, null);
            RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
            rescaleOp.filter(processed, processed);
        }
        // 方法2: 边缘增强
        else {
            processed.getGraphics().drawImage(original, 0, 0, null);
            float[] sharpenMatrix = {0, -1, 0, -1, 5, -1, 0, -1, 0};
            ConvolveOp op = new ConvolveOp(new Kernel(3, 3, sharpenMatrix));
            processed = op.filter(processed, null);
        }

        return processed;
    }

    public String scanMulti2(String imagePath) {
        try {
            // 读取图像文件
            BufferedImage image = ImageIO.read(new File(imagePath));
            if (image == null) {
                log.warn("Multi-scan: Cannot read image from path: {}", imagePath);
                throwBizException("chk.payment.imgNotFound");
            }

            // 配置支持的条码格式
            List<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);

            // 设置解码参数 - 增强提示
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE); // 重要：尝试反转图像

            // 尝试不同预处理方法和二值化器
            Result result = null;
            for (int method = 0; method < 4; method++) {
                for (int binarizerType = 0; binarizerType < 2; binarizerType++) {
                    try {
                        BufferedImage processedImage = enhancedPreprocessImage(image, method);
                        LuminanceSource source = new BufferedImageLuminanceSource(processedImage);

                        BinaryBitmap binaryBitmap;
                        if (binarizerType == 0) {
                            binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                        } else {
                            binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                        }

                        MultiFormatReader reader = new MultiFormatReader();
                        result = reader.decode(binaryBitmap, hints);
                        log.info("Multi-scan success with method {} and binarizer {}", method, binarizerType);
                        return result.getText();
                    } catch (NotFoundException e) {
                        // 继续尝试下一种组合
                        log.debug("Method {} with binarizer {} failed, trying next...", method, binarizerType);
                    }
                }
            }

            // 所有方法都失败
            log.warn("Multi-scan failed after all attempts for: {}", imagePath);
            throwBizException("chk.payment.imgInvalid");

        } catch (IOException e) {
            log.warn("Multi-scan {} not found", imagePath);
            throwBizException("chk.payment.imgNotFound");
        } catch (Throwable e) {
            log.warn(StrUtil.format("Multi-scan {} failed", imagePath), e);
            throwBizException("chk.payment.scanFailed");
        }
        return null;
    }

    /**
     * 增强的预处理方法
     */
    private static BufferedImage enhancedPreprocessImage(BufferedImage original, int method) {
        // 首先确保转换为真正的灰度图
        BufferedImage grayImage = convertToRealGrayscale(original);
        BufferedImage processed = new BufferedImage(
                grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2d = processed.createGraphics();
        g2d.drawImage(grayImage, 0, 0, null);
        g2d.dispose();

        switch (method) {
            case 0:
                // 基本处理 - 只做灰度转换
                break;

            case 1:
                // 增强对比度
                RescaleOp rescaleOp = new RescaleOp(1.3f, 10, null);
                processed = rescaleOp.filter(processed, null);
                break;

            case 2:
                // 锐化处理
                float[] sharpenMatrix = {0, -1, 0, -1, 5, -1, 0, -1, 0};
                ConvolveOp sharpenOp = new ConvolveOp(new Kernel(3, 3, sharpenMatrix));
                processed = sharpenOp.filter(processed, null);
                break;

            case 3:
                // 组合处理：对比度 + 锐化
                RescaleOp contrastOp = new RescaleOp(1.4f, 5, null);
                BufferedImage contrasted = contrastOp.filter(processed, null);
                float[] kernel = {0, -1, 0, -1, 5, -1, 0, -1, 0};
                ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel));
                processed = op.filter(contrasted, null);
                break;
        }

        return processed;
    }

    /**
     * 真正的灰度转换
     */
    private static BufferedImage convertToRealGrayscale(BufferedImage original) {
        if (original.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            return original;
        }

        BufferedImage grayImage = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // 使用标准的灰度转换公式
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int grayRGB = (gray << 16) | (gray << 8) | gray;
                grayImage.setRGB(x, y, grayRGB);
            }
        }
        return grayImage;
    }
}



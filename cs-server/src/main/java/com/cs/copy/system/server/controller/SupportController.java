package com.cs.copy.system.server.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.system.api.constant.ConfigKey;
import com.cs.copy.system.api.enums.SpFileServiceName;
import com.cs.copy.system.api.enums.SpImageServiceName;
import com.cs.copy.system.api.request.ImageUploadRequest;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.copy.system.server.util.FileTypeDetector;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.spring.helper.rsahelper.RsaHelper;
import com.cs.web.spring.web.IgnoreResBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "系统支撑接口")
@RestController
@Slf4j
@RequestMapping("/api/sp")
public class SupportController {

    @Autowired
    private Environment env;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RsaHelper rsaHelper;
    @Autowired
    private ConfigService configService;

    @Operation(summary = "健康")
    @GetMapping("/health")
    @IgnoreResBody
    public void health() {
    }

    @Operation(summary = "系统时间")
    @GetMapping("/serverTime")
    public Long serverTime() {
        return System.currentTimeMillis();
    }

    @Operation(summary = "公共配置")
    @GetMapping("/config")
    public JSONObject config() {
        return configService.getShowJsonByCategory(ConfigKey.CATE_CONFIG);
    }

    @Operation(summary = "RSA公钥")
    @GetMapping("/rsa/public")
    public String rsaPublic() {
        return rsaHelper.publicKey();
    }

    @Operation(summary = "RSA加密")
    @GetMapping("/rsa/encrypt")
    public String encrypt(@RequestParam String input) {
        return rsaHelper.encrypt(input);
    }

    @Operation(summary = "base64图片上传。")
    @PostMapping("/image/upload")
    public String imageUpload(@Valid @RequestBody ImageUploadRequest req) {
        SpImageServiceName serviceEnum = SpImageServiceName.of(req.getServiceName());
        expectNotNull(serviceEnum, "chk.sp.serviceInvalid");
        JwtUser jwtUser = JwtUserHolder.get();
        hasPermission(jwtUser != null);
        Pair<String, String> pair = checkAllowFileType(serviceEnum, req.getF());
        String ext = pair.getKey();
        expectNotNull(ext, "chk.sp.imageInvalid");
        byte[] imgBits = Base64.getDecoder().decode(pair.getValue());
        expect(imgBits.length <= Gkey.MAX_IMAGE_SIZE, "chk.sp.reachMaxLimit");
        String fileName = String.format("/ugc/img/%s%s", UUID.randomUUID().toString().replaceAll("-", ""), ext);
        Path path = Paths.get(appProperties.getUploadPath() + fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, imgBits);
        } catch (IOException e) {
            log.error("Upload-img failed: dir create failed");
            throwBizException("chk.sp.uploadFile");
        }
        return fileName;
    }

    private Pair<String, String> checkAllowFileType(SpImageServiceName serviceEnum, String source) {
        int idx = source.indexOf(",");
        if (source.startsWith(Gkey.BASE64_IMG) && idx > -1) {
            source = source.substring(idx + 1);
        }
        switch (serviceEnum) {
            case IMAGE_AVATAR:
                // 检查 Base64 字符串长度
                expect(source.length() <= Gkey.MAX_IMAGE_SIZE * 4 / 3, "chk.sp.reachMaxLimit");
                // 最长是11位
                String fileTypeBits = source.substring(0, 12);
                String ext = null;
                for (Map.Entry<String, String> entry : Gkey.IMG_MAP.entrySet()) {
                    if (fileTypeBits.startsWith(entry.getKey())) {
                        ext = entry.getValue();
                        break;
                    }
                }
                return Pair.of(ext, source);
            default:
                break;
        }
        return Pair.of(null, source);
    }

    @Operation(summary = "原始文件上传。")
    @PostMapping("/file/upload")
    public String fileUpload(@RequestParam String serviceName,
                             @RequestParam("f") MultipartFile[] files) throws IOException {
        SpFileServiceName fileService = SpFileServiceName.of(serviceName);
        expectNotNull(fileService, "chk.sp.serviceInvalid");

        JwtUser jwtUser = JwtUserHolder.get();
        hasPermission(jwtUser != null);

        // 2. 检测文件真实类型（通用逻辑）

        expect(files.length > 0, "chk.common.required", "f");
        expect(files.length < 10, "chk.file.uploadLimit", Gkey.UPLOAD_LIMIT);

        List<String> fileNames = new ArrayList<>();
        String fileName;
        String realExt;
        for (MultipartFile file : files) {
             realExt = FileTypeDetector.detectFileType(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
            // 3. 校验业务权限（业务逻辑）
            expect(getAllowedExtensions(fileService).contains(realExt), "chk.sp.fileTypeInvalid");

            fileName = generateFileName(realExt);
            saveToDisk(file.getInputStream(), fileName);
            fileNames.add(fileName);
        }

        return StringUtils.join(fileNames, ",");
    }

    private static Set<String> getAllowedExtensions(SpFileServiceName service) {
        switch (service) {
            case MERCHANT_LICENSE:
            case MERCHANT_PAYMENT:
            case MERCHANT_PHOTO:
            case MERCHANT_PAY:
                return new HashSet<>(Arrays.asList(".jpg", ".png", ".bmp", ".gif")); // 只允许图片
            case FORM_APPLY:
                return new HashSet<>(Arrays.asList(
                        ".jpg", ".png", ".bmp", ".gif",
                        ".xls", ".xlsx",
                        ".doc", ".docx",
                        ".ppt", ".pptx",
                        ".gzip", ".7z",
                        ".cvs", ".txt", ".rtf",
                        ".pdf"
                )); // 只允许图片
            default:
                return Collections.emptySet();
        }
    }

    private String generateFileName(String ext) {
        return "/ugc/f/" + UUID.randomUUID().toString().replace("-","") + ext;
    }

    private void saveToDisk(InputStream stream, String fileName) throws IOException {
        Path path = Paths.get(appProperties.getUploadPath() + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
    }
}


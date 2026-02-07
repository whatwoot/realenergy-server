package com.cs.oksdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @authro fun
 * @date 2025/12/1 00:22
 */
@Data
public class CopyMember implements Serializable {
    private String id;
    private String nickname;
    private String start;
    @Schema(description = "本金")
    private String principalAmount;
    @Schema(description = "余额")
    private String balance;
    @Schema(description = "跟单状态。")
    private String status;
    private String apikey;
    private String secret;
    private String passphrase;
}
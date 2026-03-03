package com.cs.energy.member.api.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 20:24
 */
@Data
public class TgUserDTO extends BaseDTO {
    private Long id;
    @JSONField(alternateNames = "is_bot")
    private Boolean bot;
    @JSONField(alternateNames = "first_name")
    private String firstName;
    @JSONField(alternateNames = "last_name")
    private String lastName;
    private String username;
    @JSONField(alternateNames = "language_code")
    private String languageCode;
    @JSONField(alternateNames = "is_premium")
    private Boolean premium;
    @JSONField(alternateNames = "added_to_attachment_menu")
    private Boolean addedToAttachmentMenu;
    @JSONField(alternateNames = "allows_write_to_pm")
    private Boolean allowsWriteToPm;
    @JSONField(alternateNames = "photo_url")
    private String photoUrl;
}

package com.cs.web.jwt;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.serializer.PwdSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 暂时没想到怎么才能不修改该类实现兼容所有场景的jwtUser实现jwtHolder
 *
 * @author sb
 * @date 2023/5/30 19:55
 */
@Data
public class JwtUser implements Serializable {
    private static final long serialVersionUID = -5007373145518882820L;

    @Schema(description = "UID")
    private Long id;
    @Schema(description = "货币")
    private String currency;
    @Schema(description = "登录账户")
    private Login account;
    @Schema(description = "登录端。ios=ios,android=安卓,h5=h5,nexa=三方nexa")
    private String terminal;

    @Schema(description = "过期时间。反序列化时使用，获取jwt的token的有效期，存入到该字段")
    @JSONField(serialize = false)
    @JsonIgnore
    private Date expired;

    @Data
    public static class Login {
        @Schema(description = "类型。1=邮箱,2=手机,3=钱包,9=PIN码")
        private Byte type;
        @Schema(description = "账户")
        private String account;
        @Schema(description = "密码。1=存在,0=不存在")
        @JsonSerialize(using = PwdSerializer.class, nullsUsing = PwdSerializer.class)
        private String secret;
    }
}

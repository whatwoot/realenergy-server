package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import com.cs.web.validator.annotation.AllowString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "用户信息更新")
public class MemberEditRequest extends BaseRequest {

    @Schema(description = "头像")
    private String photoUrl;
    @Schema(description = "国家代码")
    private Integer countryCode;
    @Schema(description = "国家名称")
    private String country;

    @Schema(description = "姓")
    private String familyName;
    @Schema(description = "名")
    private String givenName;
    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "手机区号")
    private Integer mobileCode;
    private String mobile;

    @Schema(description = "性别。1=男,0=女")
    private Byte gender;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "生日")
    private Integer birthday;

    @Schema(description = "货币")
    @AllowString(message = "chk.common.invalid", vals={"CNY","THB","USD","KRW","VND"})
    private String currency;
}

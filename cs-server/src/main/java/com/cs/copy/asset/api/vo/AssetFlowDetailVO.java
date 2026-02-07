package com.cs.copy.asset.api.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import com.cs.sp.serializer.fastjson2.DecimalFast2Serializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/9/29 07:28
 */
@Data
@Schema(description = "资产详情")
public class AssetFlowDetailVO extends BaseVO {
    private Long id;
    @Schema(description = "账户，默认为0。0=默认账号，1=跟单保证金账户")
    private Byte type;
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "场景")
    private String scene;
    @Schema(description = "账期")
    private Integer day;
    @Schema(description = "余额")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal balance;
    @Schema(description = "变动前余额")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal beginBalance;

    @Schema(description = "业务关联id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long relateId;
    @Schema(description = "业务关联说明")
    private String relateMemo;

    @Schema(description = "备注")
    private String memo;
    @Schema(description = "创建时间")
    private Long createAt;
}

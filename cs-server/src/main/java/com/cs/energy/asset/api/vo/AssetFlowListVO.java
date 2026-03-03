package com.cs.energy.asset.api.vo;

import com.alibaba.fastjson2.annotation.JSONField;
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
@Schema(description = "资产列表")
public class AssetFlowListVO extends BaseVO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "账户，默认为0。0=默认账号，1=跟单保证金账户")
    private Byte type;
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "场景")
    private String scene;
    @Schema(description = "账期")
    private Integer ymd;
    @Schema(description = "余额")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal balance;
    @Schema(description = "变动前余额")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal beginBalance;
    @Schema(description = "冻结")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal frozen;
    @Schema(description = "变动前冻结")
    @JsonSerialize(using = MoneySerializer.class)
    @JSONField(serializeUsing = DecimalFast2Serializer.class)
    private BigDecimal beginFrozen;

    @Schema(description = "业务关联id")
    private Long relateId;
    @Schema(description = "业务关联说明")
    private String relateMemo;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "扩展参数")
    private String extParams;

    @Schema(description = "创建时间")
    private Long createAt;
    @Schema(description = "退款状态。1=是,0=否")
    private Byte refunded;
    private Long refundAt;
}

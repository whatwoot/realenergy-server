package com.cs.energy.asset.api.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import com.cs.sp.serializer.fastjson2.DecimalFast2Serializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fiona
 * @date 2024/9/29 07:28
 */
@Data
@Schema(description = "兑换推送")
public class AssetExchangeVO extends BaseVO {
    private Long uid;
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
    @Schema(description = "备注")
    private String memo;
    @Schema(description = "创建时间")
    private Date createTime;
}

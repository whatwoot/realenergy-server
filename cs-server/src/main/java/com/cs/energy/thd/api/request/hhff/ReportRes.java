package com.cs.energy.thd.api.request.hhff;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.sp.serializer.fastjson2.DecimalFast2Serializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @authro fun
 * @date 2025/4/1 22:41
 */
@Data
public class ReportRes extends BaseRes {
    private String date;
    private BigDecimal amount;
    private Long count;
    private Integer offset;
    private Integer limit;
    private List<Data> data;

    @lombok.Data
    public static class Data {
        @JSONField(name = "distribute_code")
        private Long distributeCode;
        private Long id;
        @JSONField(serializeUsing = DecimalFast2Serializer.class)
        private BigDecimal amount;
        private String status;
        private String notify;
        @JSONField(name = "created_at")
        private Date createdAt;
    }
}

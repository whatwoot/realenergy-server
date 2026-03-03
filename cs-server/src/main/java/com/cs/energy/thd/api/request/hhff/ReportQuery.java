package com.cs.energy.thd.api.request.hhff;

import lombok.Data;

/**
 * @authro fun
 * @date 2025/4/1 14:56
 */
@Data
public class ReportQuery extends BasePay {
    private String date;
    private Integer offset;
    private Integer limit;
}

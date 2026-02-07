package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 01:18
 */
@Data
public class SetAccountLevelRes extends BaseOkxRes<List<SetAccountLevelRes.Data>> {

    @lombok.Data
    public static class Data {
        @Schema(description = "当前账户类型")
        private String curAcctLv;
        @Schema(description = "切换后的账户类型")
        private String acctLv;
        @Schema(description = "用户预设置的全仓合约仓位杠杆倍数")
        private String lever;
    }
}

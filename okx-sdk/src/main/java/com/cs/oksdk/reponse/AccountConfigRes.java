package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 17:26
 */
@Data
public class AccountConfigRes extends BaseOkxRes<List<AccountConfigRes.Data>> {

    @lombok.Data
    public static class Data {
        @Schema(description = "账户模式")
        private String acctLv;
        private String acctStpMode;
        @Schema(description = "是否自动借币")
        private Boolean autoLoan;
        private String ctIsoMode;
        private Boolean enableSpotBorrow;
        private String greeksType;
        @Schema(description = "手续费类型")
        private String feeType;
        private String ip;
        private String type;
        private String kycLv;
        @Schema(description = "API key的备注名")
        private String label;
        @Schema(description = "用户等级")
        private String level;
        private String levelTmp;
        private String liquidationGear;
        private String mainUid;
        private String mgnIsoMode;
        private String opAuth;
        private String perm;
        @Schema(description = "持仓方式")
        private String posMode;
        @Schema(description = "用户角色")
        private String roleType;
        private String spotBorrowAutoRepay;
        private String spotOffsetType;
        private String spotRoleType;
        private String spotTraderInsts;
        private String stgyType;
        private String traderInsts;
        private String uid;
        private String settleCcy;
        private String settleCcyList;
    }
}

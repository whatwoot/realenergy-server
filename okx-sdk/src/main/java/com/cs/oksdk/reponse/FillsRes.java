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
public class FillsRes extends BaseOkxRes<List<FillsRes.Data>> {

    /**
     * "alias": "",
     * "auctionEndTime": "",
     * "baseCcy": "",
     * "category": "1",
     * "contTdSwTime": "",
     * "ctMult": "1",
     * "ctType": "linear",
     * "ctVal": "0.1",
     * "ctValCcy": "ETH",
     * "expTime": "",
     * "futureSettlement": false,
     * "groupId": "4",
     * "instFamily": "ETH-USDT",
     * "instId": "ETH-USDT-SWAP",
     * "instIdCode": 10461,
     * "instType": "SWAP",
     * "lever": "100",
     * "listTime": "1573557408000",
     * "lotSz": "0.01",
     * "maxIcebergSz": "100000000.0000000000000000",
     * "maxLmtAmt": "20000000",
     * "maxLmtSz": "100000000",
     * "maxMktAmt": "",
     * "maxMktSz": "20000",
     * "maxPlatOILmt": "",
     * "maxStopSz": "20000",
     * "maxTriggerSz": "100000000.0000000000000000",
     * "maxTwapSz": "100000000.0000000000000000",
     * "minSz": "0.01",
     * "openType": "",
     * "optType": "",
     * "posLmtAmt": "250000",
     * "posLmtPct": "30",
     * "preMktSwTime": "",
     * "quoteCcy": "",
     * "ruleType": "normal",
     * "settleCcy": "USDT",
     * "state": "live",
     * "stk": "",
     * "tickSz": "0.01",
     * "tradeQuoteCcyList": [],
     * "uly": "ETH-USDT"
     */
    @lombok.Data
    public static class Data {
        private String alias;
        private String auctionEndTime;
        @Schema(description = "交易货币币种")
        private String baseCcy;
        private String category;
        private String contTdSwTime;
        @Schema(description = "合约乘数")
        private String ctMult;
        @Schema(description = "合约类型.linear：正向合约,inverse：反向合约")
        private String ctType;
        @Schema(description = "合约面值")
        private String ctVal;
        @Schema(description = "合约面值计价币种")
        private String ctValCcy;
        private String expTime;
        private String futureSettlement;
        private String groupId;
        private String instFamily;
        @Schema(description = "产品id")
        private String instId;
        @Schema(description = "产品id码")
        private String instIdCode;
        @Schema(description = "产品类型")
        private String instType;
        @Schema(description = "最大杠杆倍数")
        private String lever;
        private String listTime;
        @Schema(description = "下单数量精度")
        private String lotSz;
        private String maxIcebergSz;
        private String maxLmtAmt;
        private String maxLmtSz;
        private String maxMktAmt;
        private String maxMktSz;
        private String maxPlatOILmt;
        private String maxStopSz;
        private String maxTriggerSz;
        private String maxTwapSz;
        @Schema(description = "最小下单数量")
        private String minSz;
        private String openType;
        private String optType;
        private String posLmtAmt;
        private String posLmtPct;
        private String preMktSwTime;
        private String quoteCcy;
        private String ruleType;
        private String settleCcy;
        @Schema(description = "产品状态.live：交易中，suspend：暂停中，preopen：预上线，test：测试中")
        private String state;
        private String stk;
        @Schema(description = "下单价格精度")
        private String tickSz;
        private String tradeQuoteCcyList;
        private String uly;
    }
}

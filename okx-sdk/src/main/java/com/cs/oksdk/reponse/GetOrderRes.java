package com.cs.oksdk.reponse;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/12/1 00:34
 */
@Data
public class GetOrderRes extends BaseOkxRes<List<GetOrderRes.Data>> {
    /**
     * "accFillSz": "0.1",
     * "algoClOrdId": "",
     * "algoId": "",
     * "attachAlgoClOrdId": "",
     * "attachAlgoOrds": [],
     * "avgPx": "3046.58",
     * "cTime": "1764520382611",
     * "cancelSource": "",
     * "cancelSourceReason": "",
     * "category": "normal",
     * "ccy": "USDT",
     * "clOrdId": "",
     * "fee": "-0.0152329",
     * "feeCcy": "USDT",
     * "fillPx": "3046.58",
     * "fillSz": "0.1",
     * "fillTime": "1764520382612",
     * "instId": "ETH-USDT-SWAP",
     * "instType": "SWAP",
     * "isTpLimit": "false",
     * "lever": "10",
     * "linkedAlgoOrd": {
     * "algoId": ""
     * },
     * "ordId": "3087611140324925440",
     * "ordType": "market",
     * "pnl": "-0.0042",
     * "posSide": "long",
     * "px": "",
     * "pxType": "",
     * "pxUsd": "",
     * "pxVol": "",
     * "quickMgnType": "",
     * "rebate": "0",
     * "rebateCcy": "USDT",
     * "reduceOnly": "true",
     * "side": "sell",
     * "slOrdPx": "",
     * "slTriggerPx": "",
     * "slTriggerPxType": "",
     * "source": "",
     * "state": "filled",
     * "stpId": "",
     * "stpMode": "cancel_maker",
     * "sz": "0.1",
     * "tag": "",
     * "tdMode": "isolated",
     * "tgtCcy": "",
     * "tpOrdPx": "",
     * "tpTriggerPx": "",
     * "tpTriggerPxType": "",
     * "tradeId": "1881495776",
     * "tradeQuoteCcy": "",
     * "uTime": "1764520382613"
     */
    @lombok.Data
    public static class Data {
        private String accFillSz;
        private String algoClOrdId;
        private String algoId;
        private String attachAlgoClOrdId;
        private String attachAlgoOrds;
        private String avgPx;
        @JSONField(name = "cTime")
        private Long cTime;
        private String cancelSource;
        private String cancelSourceReason;
        private String category;
        private String ccy;
        private String clOrdId;
        private String fee;
        private String feeCcy;
        private String fillPx;
        private String fillSz;
        private String fillTime;
        private String instId;
        private String instType;
        private Boolean isTpLimit;
        private String lever;
        private String ordId;
        private String ordType;
        private String pnl;
        private String posSide;
        private String px;
        private String pxType;
        private String pxUsd;
        private String pxVol;
        private String quickMgnType;
        private String rebate;
        private String rebateCcy;
        private String reduceOnly;
        private String side;
        private String slOrdPx;
        private String slTriggerPx;
        private String slTriggerPxType;
        private String source;
        private String state;
        private String stpId;
        private String stpMode;
        private String sz;
        private String tag;
        private String tdMode;
        private String tgtCcy;
        private String tpOrdPx;
        private String tpTriggerPx;
        private String tpTriggerPxType;
        private String tradeId;
        private String tradeQuoteCcy;
        @JSONField(name = "uTime")
        private Long uTime;
    }
}

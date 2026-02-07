package com.cs.oksdk.reponse;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 23:19
 */
@Data
public class PositionsRes extends BaseOkxRes<List<PositionsRes.Data>> {
    /**
     * {"adl":"1","availPos":"1","avgPx":"3011","baseBal":"","baseBorrowed":"","baseInterest":"","bePx":"3014.0125062531265","bizRefId":"","bizRefType":"","cTime":"1764434109835","ccy":"USDT","clSpotInUseAmt":"","closeOrderAlgo":[],"deltaBS":"","deltaPA":"","fee":"-0.15055","fundingFee":"0","gammaBS":"","gammaPA":"","hedgedPos":"","idxPx":"3001.2646","imr":"","instId":"ETH-USDT-SWAP","instType":"SWAP","interest":"","last":"3011","lever":"10","liab":"","liabCcy":"","liqPenalty":"0","liqPx":"2722.159673530889","margin":"30.11","markPx":"3010.54","maxSpotInUseAmt":"","mgnMode":"isolated","mgnRatio":"22.191662920568692","mmr":"1.204216","nonSettleAvgPx":"","notionalUsd":"301.11722134000007","optVal":"","pendingCloseOrdLiabVal":"","pnl":"0","pos":"1","posCcy":"","posId":"3084716306329329664","posSide":"long","quoteBal":"","quoteBorrowed":"","quoteInterest":"","realizedPnl":"-0.15055","settledPnl":"","spotInUseAmt":"","spotInUseCcy":"","thetaBS":"","thetaPA":"","tradeId":"1878602795","uTime":"1764434109835","upl":"-0.0460000000000036","uplLastPx":"0","uplRatio":"-0.0015277316506146","uplRatioLastPx":"0","usdPx":"1.00021","vegaBS":"","vegaPA":""}
     */
    @lombok.Data
    public static class Data {
        private String adl;
        private String availPos;
        private String avgPx;
        private String baseBal;
        private String baseBorrowed;
        private String baseInterest;
        private String bePx;
        private String bizRefId;
        private String bizRefType;
        private String ccy;
        private String clSpotInUseAmt;
        private String deltaBS;
        private String deltaPA;
        private String fee;
        private String fundingFee;
        private String gammaBS;
        private String gammaPA;
        private String hedgedPos;
        private String idxPx;
        private String imr;
        private String instId;
        private String instType;
        private String interest;
        private String last;
        private String lever;
        private String liab;
        private String liabCcy;
        private String liqPenalty;
        private String liqPx;
        private String margin;
        private String markPx;
        private String maxSpotInUseAmt;
        private String mgnMode;
        private String mgnRatio;
        private String mmr;
        private String nonSettleAvgPx;
        private String notionalUsd;
        private String optVal;
        private String pendingCloseOrdLiabVal;
        private String pnl;
        private String pos;
        private String posCcy;
        private String posId;
        private String posSide;
        private String quoteBal;
        private String quoteBorrowed;
        private String quoteInterest;
        private String realizedPnl;
        private String settledPnl;
        private String spotInUseAmt;
        private String spotInUseCcy;
        private String thetaBS;
        private String thetaPA;
        private String tradeId;
        private String upl;
        private String uplLastPx;
        private String uplRatio;
        private String uplRatioLastPx;
        private String usdPx;
        private String vegaBS;
        private String vegaPA;
        @JSONField(name = "cTime")
        private Long cTime;
        @JSONField(name = "uTime")
        private Long uTime;
        private List<CloseOrderAlgo> closeOrderAlgo;
    }

    @lombok.Data
    public static class CloseOrderAlgo {
        private String algoId;
        private String slTriggerPx;
        private String slTriggerPxType;
        private String tpTriggerPx;
        private String tpTriggerPxType;
        private String closeFraction;
    }
}

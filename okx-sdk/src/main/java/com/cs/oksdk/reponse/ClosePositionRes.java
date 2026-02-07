package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/12/26 15:04
 */
@Data
public class ClosePositionRes extends BaseOkxRes<List<ClosePositionRes.Data>> {
    /**
     * {"clOrdId":"","instId":"ETH-USDT-SWAP","posSide":"long","tag":""}
     */
    @lombok.Data
    public static class Data {
        private String clOrdId;
        private String instId;
        private String posSide;
        private String tag;
    }
}

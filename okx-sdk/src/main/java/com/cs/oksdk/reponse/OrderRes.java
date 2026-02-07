package com.cs.oksdk.reponse;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.oksdk.reponse.base.BaseOkxRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 18:02
 */
@Data
public class OrderRes extends BaseOkxRes<List<OrderRes.Data>> {
    /**
     * {"clOrdId":"","ordId":"3083928922020995072","sCode":"0","sMsg":"Order placed","tag":"","ts":"1764410643954"}
     */
    @lombok.Data
    public static class Data {
        private String sCode;
        private String sMsg;
        private String clOrdId;
        private String ordId;
        private String tag;
        private Long ts;

        @JsonIgnore
        @JSONField(serialize = false)
        public boolean isOk(){
            return OK.equals(sCode);
        }
    }
}

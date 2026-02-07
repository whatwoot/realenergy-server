package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 17:26
 */
@Data
public class AssetBillsRes extends BaseOkxRes<List<AssetBillsRes.Data>> {

    /**
     * billId	String	账单 ID
     * ccy	String	账户余额币种
     * clientId	String	转账或提币的客户自定义ID
     * balChg	String	账户层面的余额变动数量
     * bal	String	账户层面的余额数量
     * type	String	账单类型
     * notes	String	备注
     * ts
     */
    @lombok.Data
    public static class Data {
        private String billId;
        private String ccy;
        private String clientId;
        private String balChg;
        private String bal;
        private String type;
        private String notes;
        private String ts;
    }
}

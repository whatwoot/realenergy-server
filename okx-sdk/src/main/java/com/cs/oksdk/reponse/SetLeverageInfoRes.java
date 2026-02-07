package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 01:18
 */
@Data
public class SetLeverageInfoRes extends BaseOkxRes<List<SetLeverageInfoRes.Data>> {

    @lombok.Data
    public static class Data {
        private String ccy;
        private String instId;
        private String lever;
        private String mgnMode;
        private String posSide;
    }
}

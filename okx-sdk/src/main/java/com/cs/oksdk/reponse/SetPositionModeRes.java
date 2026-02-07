package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 01:18
 */
public class SetPositionModeRes extends BaseOkxRes<List<SetPositionModeRes.Data>> {

    @lombok.Data
    public static class Data {
        private String posMode;
    }
}

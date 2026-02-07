package com.cs.copy.asset.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/3/3 02:16
 */
@Getter
public enum AssetSceneEnum {
    /**
     * 每个大类预留3位数
     * 每个小类预留3位数
     */
    SYS_RECHARGE("01", "0101", "系統贈送"),
    CORRECTION("01", "0102", "系統扣除"),

    WITHDRAW("01", "0103", "提现"),
    RECHARGE("01", "0104", "充值"),

    WITHDRAW_WECHAT("01", "0105", "微信提现"),
    WITHDRAW_ALIPAY("01", "0106", "支付宝提现"),
    WITHDRAW_UNIONPAY("01", "0107", "银行卡提现"),
    WITHDRAW_UNION("01", "0108", "聚合码提现"),

    COPYER("02", "0210", "跟单"),
    COPYER_PROFIT("02", "0211", "手续费"),
    COPYER_COMPENSATE("02", "0212", "补偿"),

    INVITE_RANK("05", "0501", "邀请池奖励"),
    RANDOM_POOL("05", "0502", "爆击池奖励"),

    TRANSFER_OUT("06", "0602", "转出"),
    TRANSFER_IN("06", "0603", "转入"),

    FUND_INTEREST("08", "0801", "余额宝利息")
    ;

    private String category;
    private String code;
    private String msg;

    AssetSceneEnum(String category, String code, String msg) {
        this.category = category;
        this.code = code;
        this.msg = msg;
    }

    public static AssetSceneEnum of(String code){
        for(AssetSceneEnum value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public boolean eq(String code){
        return this.getCode().equals(code);
    }
}

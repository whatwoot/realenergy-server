package com.cs.copy.system.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/10/9 22:03
 */
@Getter
public enum SseNameEnum {
    /**
     *
     */
    ROOM_CREATE("0101", "房间创建"),
    ROOM_ENTER("0102", "进入房间"),
    ROOM_EXIT("0103", "离开房间"),

    NEW_LOTTER("0201", "新一期游戏开始"),
    ADD_LOTTERY("0202", "新玩家竞猜"),
    COUNT_LOTTERY("0203", "游戏倒计时开始"),
    START_LOTTERY("0204", "游戏开始"),
    CONFIRMING_LOTTERY("0205","游戏开奖中"),
    END_LOTTERY("0206","游戏已开奖并结束"),
    CANCEL_LOTTERY("0207","取消竞猜"),

    ADD_ASSET("0301","后台充值到账"),
    EXCHANGE_CONFIRMED("0302", "USDT充值到账"),

    EXCHANGE_DIRECT_PRIZE("0402", "充值直推奖励"),

    WITHDRAW_ADD("0502","申请提现"),
    WITHDRAW_FINISH("0503","提现确认完成"),

    BLOCK_NOW("0601", "当前区块"),

    LV_CHANGE("0701", "用户等级变动"),
    USER_PHOTO_UPDATE("0702", "用户头像更新");
    ;

    private String code;
    private String msg;

    SseNameEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static SseNameEnum of(String code){
        for(SseNameEnum value: values()){
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

package com.cs.copy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/5/3 00:34
 */
@Getter
public enum NoticeTypeEnum {

    NOTICE("1", "通知"),
    ANNOUNCE("2", "公告"),
    NEWS("3", "新闻"),
    FLESH_NEWS("4", "快讯"),
    ;

   private String code;
   private String msg;

   NoticeTypeEnum(String code, String msg){
       this.code = code;
       this.msg = msg;
   }

   public static NoticeTypeEnum of(String code){
       for(NoticeTypeEnum value: values()){
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

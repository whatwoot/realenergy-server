package com.cs.energy.system.api.enums;

import com.cs.energy.system.api.dto.GlobalConfigDTO;
import lombok.Getter;

/**
 * @author fiona
 * @date 2024/9/30 02:50
 */
@Getter
public enum CacheKeyEnum {
    /**
     * 添加到此处的缓存，会每次重启时更新
     */
    CONFIG("config", true, GlobalConfigDTO.class),
    QUEUE("queue", false, null),
    ;

    private String code;
    private Boolean toObj;
    private Class clazz;

    CacheKeyEnum(String code, Boolean obj, Class clazz) {
        this.code = code;
        this.toObj = obj;
        this.clazz = clazz;
    }

    public static CacheKeyEnum of(String code){
        for(CacheKeyEnum value: values()){
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

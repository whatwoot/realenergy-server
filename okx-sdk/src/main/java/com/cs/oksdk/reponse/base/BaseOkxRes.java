package com.cs.oksdk.reponse.base;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @authro fun
 * @date 2025/11/29 01:18
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseOkxRes<T> implements Serializable {
    private static final long serialVersionUID = -4813812548904246344L;

    public static final String OK = "0";
    private String code;
    private String msg;
    private T data;

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isOk(){
        return OK.equals(code);
    }
}

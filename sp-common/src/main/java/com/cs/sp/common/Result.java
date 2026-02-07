package com.cs.sp.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author jamie
 * @date 2022/3/31 03:00
 */
@Getter
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -6743823770156819809L;

    public static final String OK_RES = "0";
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Result() {
        this.code = OK_RES;
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result ok() {
        return new Result();
    }

    @JsonIgnore
    public boolean isOk() {
        return OK_RES.equals(this.code);
    }

    /**
     * 用于处理有消息提示的正常返回
     *
     * @param msg
     * @return
     */
    public static Result ok(String msg) {
        Result r = ok();
        r.msg = msg;
        return r;
    }


    /**
     * 用于处理有消息的异常返回
     *
     * @param code
     * @param msg
     * @return
     */
    public static Result fail(String code, String msg) {
        return new Result(code, msg);
    }

    /**
     * 大部分情况使用
     * 用于处理无消息，但是有数据的返回
     *
     * @param data
     * @return
     */
    public Result data(T data) {
        this.data = data;
        return this;
    }
}

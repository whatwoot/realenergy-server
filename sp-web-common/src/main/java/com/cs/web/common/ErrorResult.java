package com.cs.web.common;

import com.cs.sp.common.base.BaseVO;

/**
 * 已国际化处理后的错误提示
 *
 * @author sb
 * @date 2022/3/31 02:59
 */
public class ErrorResult extends BaseVO {

    private Integer status;
    private String code;
    private String msg;

    public ErrorResult() {
    }

    public ErrorResult(Integer status, String code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public ErrorResult(String code, String msg) {
        this.status = 0;
        this.code = code;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

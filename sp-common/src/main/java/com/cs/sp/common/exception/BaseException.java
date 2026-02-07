package com.cs.sp.common.exception;

/**
 * 基础，用于适配不同场景
 * @author sb
 * @date 2018/9/7 02:10
 */
public abstract class BaseException extends RuntimeException {

    /**
     * http状态码
     * 适配不同的需求，
     * 单机服务，异常直接对用户，可以返回200
     * 微服务里的异常响应，如果要fast-fail，则需要返回非200的状态码
     *
     */
    protected Integer statusCode;
    /**
     * 错误编码，同时是i18n的key
     * eg:
     * chk 开头表示校验失败
     * {
     * required 结尾表示必填项未填、
     * invalid 结尾表示数据错误、
     * exception 结尾则表示出现异常
     * }
     * thd 开头表示第三方服务错误、
     * sys 表示当前系统错误，
     *
     */
    protected String code;
    /**
     * i18n的占位参数，可为空
     * eg:
     * code为chk.age.min.invalid=年龄需要大于{0}岁
     * args是一个业务的动态参数，如18
     */
    protected Object[] args;

    public BaseException(Integer statusCode, String code, Object... args) {
        super(code);
        this.statusCode = statusCode;
        this.code = code;
        this.args = args;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

}

package com.cs.energy.system.api.base;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseLog {
    private Long id;
    private Long uid;
    private String ua;
    private String fin;
    private Date createTime;
    private String ip;
    private Byte status;
    private String errCode;
    private String errMsg;
}
package com.cs.oksdk.constant;

/**
 * @authro fun
 * @date 2025/12/6 20:05
 */
public interface CopyCacheKey {
    String appName = "copybot";
    /**
     * 存储所有跟单用户的id
     */
    String USER_ALL = appName +":users:all";
    /**
     *
     */
    String USER_APIS_ALL = appName +":users:apis";
    String USER_APIKEYS = appName +":users:keys";
    String USER_START = appName +":user:start";
    String USER_START_OF_TRADER = appName +":user:start:";
    String USER = appName +":user:";
    String USER_CONFIG = appName +":user:config:";
    String USER_POS = appName +":user:pos:";
    String TRADER_POS = appName +":trader:pos:";
    String USER_ID = appName +":users:id";
    String NODE_LOAD_BALANCE = appName +":node:lb";
    String POS_TIMELINE_OF = appName +":pos:timeline:";
    String PRICE = appName +":price:";
    String ID_OKX_ORDER = appName + ":okx_order";
}

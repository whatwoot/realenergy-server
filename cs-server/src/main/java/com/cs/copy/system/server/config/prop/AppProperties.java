package com.cs.copy.system.server.config.prop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fiona
 * @date 2025/2/20 18:43
 */
@Data
@ConfigurationProperties("app.config")
public class AppProperties {
    /**
     * 主代理
     * 对于无法多任务并行的队列，只会在主agent执行
     */
    private Boolean agent;
    /**
     * 所有任务开关
     */
    private Byte taskFlag;
    /**
     * 销售池奖励数量
     */
    private Integer inviteRankPrizeNum;

    private String uploadPath;
    private String gaIssuer;

    private String usdtCa;
    private String welcomeStr;
    /**
     * 创世起始用户id
     */
    private Integer getGenesisStart;
    /**
     * 创世用户数量
     */
    private Integer genesisNum;
    /**
     * 排行榜人数
     */
    private Integer inviteRankNum;
    /**
     * 代币精度
     */
    private Integer tokenDecimal;
    /**
     * 价格精度
     */
    private Integer priceDecimal;
    private Byte regCanWithdraw;
    private Byte autoSellGccFlag;


    private String qrDomain;
    private Byte checkQrCode;
    private Integer callPayApi;
    private String paytool;
    private String payNotifyUrl;
    private String allowPayTypes;

    /**
     * okx订单最后平仓时间
     */
    private Long okxLastCloseTime;
    /**
     * okx订单碰拉取仓位等信息异常时的睡眠时间
     */
    private Long okxSleepTime;
    /**
     * okx默认分页限制
     */
    private Integer okxApiPageLimit = 100;
    /**
     * 平仓后，到获取position-his的延迟时间
     */
    private Integer okxPosHisDelayTime = 2000;

    private Integer okxSettleConcurrencyNum = 10;
    /**
     * 余额刷新的频率
     */
    private Long okxBalanceRefreshLimit = 60 * 1000L;
    /**
     * 接口跳过api检查
     */
    private Boolean skipApiCheck;
    /**
     * 邀请奖池结算周期
     */
    private Integer invitePoolSettleDay;
    /**
     * 代币LP地址
     */
    private String tokenLpAddr;
    // 代币方向
    private Integer tokenDirection;
    // 均价时长
    private Integer tokenTwapMinute;
    // 最长gap时长
    private Integer tokenTwapGap;
    // 使用旧版跟单
    private Boolean useLegendaryCopyOfTrader;
    @Schema(description = "代理商码")
    private String agentCode;

}

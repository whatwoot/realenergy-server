package com.cs.energy.system.api.dto;

import com.cs.sp.common.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/12/2 17:27
 */
@Data
public class GlobalConfigDTO extends BaseDTO {

    /**
     * 基础静态产出
     */
    private BigDecimal dailyStaticRate;

    private BigDecimal dailyStaticGainRate;
    /**
     * 基础静态产出
     */
    private BigDecimal dailyStaticMaxRate;
    /**
     * 直推
     */
    private BigDecimal directInviteRate;
    /**
     * 间推
     */
    private BigDecimal secondInviteRate;
    /**
     * 奖池比例
     */
    private BigDecimal poolRate;
    /**
     * 理财回购比例
     */
    private BigDecimal investBuyBackRate;

    /**
     * 小区业绩
     */
    private BigDecimal smallRate;
    /**
     * 最小小区业绩
     */
    private BigDecimal minSmallRate;
    /**
     * 单次减少率
     */
    private BigDecimal reduceRate;
    /**
     * 单次减少开关
     */
    private Integer reduceFlag;
    /**
     *
     */
    private Integer reduceLv;
    /**
     * 赔率
     */
    private BigDecimal oddsRate;
    private String oddsRates;
    /**
     * 商家奖励比例
     */
    private BigDecimal merchantRewardRate;
    /**
     * 签名人
     */
    private String signer;

    /**
     * 奖励轮次
     */
    private Integer prizeRound;
    /**
     * 多少人出局
     */
    private Integer roundOutNum;

    /**
     *
     */
    private Integer investFlag;

    /**
     * 理财nft比例
     */
    private BigDecimal investNftRate;
    /**
     * 理财分红比例
     */
    private BigDecimal investBonusRate;
    /**
     * 理财代币比例
     */
    private BigDecimal investTokenRate;

    /**
     * 公排公开
     */
    private Integer globalRanked;

    /**
     * 收款方式数量
     */
    private Integer paymentLimit;

    /**
     * 提现审核起始金额
     */
    private BigDecimal withdrawAuditAmount;

    /**
     * 动态扫码解析的域名
     * 微信,支付宝,自家商户
     */
    private String supportWxDomains;
    private String supportAlipayDomains;
    private String supportMerchantDomains;
    /**
     * 中转商户码域名
     */
    private String transitMerchantDomain;
    /**
     * 中转邀请域名
     */
    private String transitInviteDomain;

    /**
     * 自有商家奖励比例
     */
    private BigDecimal merchantPrizeRate;

    /**
     * 需要锁定公排提现的用户
     */
    private String lockPublicUids;

    /**
     * 6in1out
     */
    private String queueStrategy;

    /**
     * 提现回购比例
     */
    private BigDecimal withdrawBuyBackRate;

    /**
     * 精选商户到账比例
     */
    private BigDecimal curatedArriveRate;
    /**
     * 消费回购比例
     */
    private BigDecimal payBuyBackRate;
    /**
     * 发邮件的中转页
     * 否则使用跳转页
     * auto: 使用域名适应
     */
    private String regForwardUrl;
    /**
     * 可换绑开关
     */
    private Integer canChangeNimWallet;
    /**
     * 用户提现开关
     */
    private Integer withdrawFlag;
    /**
     * 商户提现开关
     */
    private Integer merchantWithdrawFlag;

    /**
     * 邮箱策略
     */
    private String mailStrategy;
    /**
     * 邮箱策略服务商
     */
    private String mailSupplier;

    /**
     * 返利比例
     */
    private BigDecimal rebateRate;
    /**
     * 做单开关
     */
    private Integer makeOrderFlag;
    /**
     * 余额宝确认时间
     */
    private Integer fundConfirmDays;
    /**
     * 余额宝日化
     */
    private BigDecimal fundRate;
    /**
     * 7日年化收益率
     */
    private BigDecimal fund7DaysAnnualizedReturnRate;

    /**
     * 提现节点分成比例
     */
    private BigDecimal withdrwGenesisRate;
    private BigDecimal withdrwPoolRate;

    /**
     * 铸币创世总比例
     */
    private BigDecimal mintGenesisRate;
    private BigDecimal mintRate;
    private BigDecimal mintBurnRate;
    private BigDecimal nftBonusRate;
    private BigDecimal inviteRankPrizeRate;
    private BigDecimal nftSoldReduceRate;

    private String rechargeCas;
    private BigDecimal autoSellGccRate;

    /**
     * 开放绑定的apikey平台
     */
    private String allowApikeyPlatforms;
    /**
     * 跟单开放开关全局开关
     */
    private Integer copyOpenFlag;
    @Schema(description = "间推业绩比例")
    private BigDecimal indirectParentPerformanceRate;
}

package com.cs.oksdk.reponse;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/12/1 00:34
 */
@Data
public class GetOrderHisRes extends BaseOkxRes<List<GetOrderHisRes.Data>> {
    /**
     instType	String	产品类型
     instId	String	产品ID
     tgtCcy	String	币币市价单委托数量sz的单位
     base_ccy: 交易货币 ；quote_ccy：计价货币
     仅适用于币币市价订单
     默认买单为quote_ccy，卖单为base_ccy
     ccy	String	保证金币种，适用于逐仓杠杆及合约模式下的全仓杠杆订单以及交割、永续和期权合约订单。
     ordId	String	订单ID
     clOrdId	String	客户自定义订单ID
     tag	String	订单标签
     px	String	委托价格，对于期权，以币(如BTC, ETH)为单位
     pxUsd	String	期权价格，以USD为单位
     仅适用于期权，其他业务线返回空字符串""
     pxVol	String	期权订单的隐含波动率
     仅适用于期权，其他业务线返回空字符串""
     pxType	String	期权的价格类型
     px：代表按价格下单，单位为币 (请求参数 px 的数值单位是BTC或ETH)
     pxVol：代表按pxVol下单
     pxUsd：代表按照pxUsd下单，单位为USD (请求参数px 的数值单位是USD)
     sz	String	委托数量
     ordType	String	订单类型
     market：市价单
     limit：限价单
     post_only：只做maker单
     fok：全部成交或立即取消
     ioc：立即成交并取消剩余
     optimal_limit_ioc：市价委托立即成交并取消剩余（仅适用交割、永续）
     mmp：做市商保护(仅适用于组合保证金账户模式下的期权订单)
     mmp_and_post_only：做市商保护且只做maker单(仅适用于组合保证金账户模式下的期权订单)
     op_fok：期权简选（全部成交或立即取消）
     elp：流动性增强计划订单
     side	String	订单方向
     posSide	String	持仓方向
     tdMode	String	交易模式
     accFillSz	String	累计成交数量
     fillPx	String	最新成交价格，如果成交数量为0，该字段为""
     tradeId	String	最新成交ID
     fillSz	String	最新成交数量
     fillTime	String	最新成交时间
     avgPx	String	成交均价，如果成交数量为0，该字段也为""
     state	String	订单状态
     canceled：撤单成功
     filled：完全成交
     mmp_canceled：做市商保护机制导致的自动撤单
     lever	String	杠杆倍数，0.01到125之间的数值，仅适用于 币币杠杆/交割/永续
     attachAlgoClOrdId	String	下单附带止盈止损时，客户自定义的策略订单ID
     tpTriggerPx	String	止盈触发价
     tpTriggerPxType	String	止盈触发价类型
     last：最新价格
     index：指数价格
     mark：标记价格
     tpOrdPx	String	止盈委托价
     slTriggerPx	String	止损触发价
     slTriggerPxType	String	止损触发价类型
     last：最新价格
     index：指数价格
     mark：标记价格
     slOrdPx	String	止损委托价
     attachAlgoOrds	Array of objects	下单附带止盈止损信息
     > attachAlgoId	String	附带止盈止损的订单ID，改单时，可用来标识该笔附带止盈止损订单。下止盈止损委托单时，该值不会传给 algoId
     > attachAlgoClOrdId	String	下单附带止盈止损时，客户自定义的策略订单ID
     > tpOrdKind	String	止盈订单类型
     condition: 条件单
     limit: 限价单
     > tpTriggerPx	String	止盈触发价
     > tpTriggerRatio	String	止盈触发比例，0.3 代表 30%
     仅适用于交割/永续合约
     > tpTriggerPxType	String	止盈触发价类型
     last：最新价格
     index：指数价格
     mark：标记价格
     > tpOrdPx	String	止盈委托价
     > slTriggerPx	String	止损触发价
     > slTriggerRatio	String	止损触发比例，0.3 代表 30%
     仅适用于交割/永续合约
     > slTriggerPxType	String	止损触发价类型
     last：最新价格
     index：指数价格
     mark：标记价格
     > slOrdPx	String	止损委托价
     > sz	String	张数。仅适用于“多笔止盈”的止盈订单
     > amendPxOnTriggerType	String	是否启用开仓价止损，仅适用于分批止盈的止损订单
     0：不开启，默认值
     1：开启
     > failCode	String	委托失败的错误码，默认为"",
     委托失败时有值，如 51020
     > failReason	String	委托失败的原因，默认为""
     委托失败时有值
     linkedAlgoOrd	Object	止损订单信息，仅适用于包含限价止盈单的双向止盈止损订单，触发后生成的普通订单
     > algoId	String	策略订单唯一标识
     stpId	String	自成交保护ID
     如果自成交保护不适用则返回""（已弃用）
     stpMode	String	自成交保护模式
     feeCcy	String	手续费币种
     对于币币和杠杆的挂单卖单，表示计价币种；其他情况下，表示收取手续费的币种。
     fee	String	手续费金额
     对于币币和杠杆（除挂单卖单外）：平台收取的累计手续费，始终为负数。
     对于币币和杠杆的挂单卖单、交割、永续和期权：累计手续费和返佣（币币和杠杆挂单卖单始终以计价币种计算）。
     rebateCcy	String	返佣币种
     对于币币和杠杆的挂单卖单，表示交易币种；其他情况下，表示支付返佣的币种。
     rebate	String	返佣金额，仅适用于币币和杠杆
     对于挂单卖单：以交易币种为单位的累计手续费和返佣金额。
     其他情况下，表示挂单返佣金额，始终为正数，如无返佣则返回""。
     source	String	订单来源
     6：计划委托策略触发后的生成的普通单
     7：止盈止损策略触发后的生成的普通单
     13：策略委托单触发后的生成的普通单
     25：移动止盈止损策略触发后的生成的普通单
     34: 追逐限价委托生成的普通单
     pnl	String	收益(不包括手续费)
     适用于有成交的平仓订单，其他情况均为0
     category	String	订单种类
     normal：普通委托
     twap：TWAP自动换币
     adl：ADL自动减仓
     full_liquidation：强制平仓
     partial_liquidation：强制减仓
     delivery：交割
     ddh：对冲减仓类型订单
     auto_conversion：抵押借币自动还币订单
     reduceOnly	String	是否只减仓，true 或 false
     cancelSource	String	订单取消来源的原因枚举值代码
     cancelSourceReason	String	订单取消来源的对应具体原因
     algoClOrdId	String	客户自定义策略订单ID。策略订单触发，且策略单有algoClOrdId时有值，否则为"",
     algoId	String	策略委托单ID，策略订单触发时有值，否则为""
     isTpLimit	String	是否为限价止盈，true 或 false.
     uTime	String	订单状态更新时间，Unix时间戳的毫秒数格式，如 1597026383085
     cTime	String	订单创建时间，Unix时间戳的毫秒数格式，如 1597026383085
     */
    @lombok.Data
    public static class Data {
        private String accFillSz;
        private String algoClOrdId;
        private String algoId;
        private String attachAlgoClOrdId;
        private String attachAlgoOrds;
        private String avgPx;
        @JSONField(name = "cTime")
        private String cTime;
        private String cancelSource;
        private String cancelSourceReason;
        private String category;
        private String ccy;
        private String clOrdId;
        private String fee;
        private String feeCcy;
        private String fillPx;
        private String fillSz;
        private String fillTime;
        private String instId;
        private String instType;
        private Boolean isTpLimit;
        private String lever;
        private String ordId;
        private String ordType;
        private String pnl;
        private String posSide;
        private String px;
        private String pxType;
        private String pxUsd;
        private String pxVol;
        private String quickMgnType;
        private String rebate;
        private String rebateCcy;
        private String reduceOnly;
        private String side;
        private String slOrdPx;
        private String slTriggerPx;
        private String slTriggerPxType;
        private String source;
        private String state;
        private String stpId;
        private String stpMode;
        private String sz;
        private String tag;
        private String tdMode;
        private String tgtCcy;
        private String tpOrdPx;
        private String tpTriggerPx;
        private String tpTriggerPxType;
        private String tradeId;
        private String tradeQuoteCcy;
        @JSONField(name = "uTime")
        private String uTime;
    }
}

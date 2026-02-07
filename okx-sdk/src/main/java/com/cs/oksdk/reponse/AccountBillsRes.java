package com.cs.oksdk.reponse;

import com.cs.oksdk.reponse.base.BaseOkxRes;
import lombok.Data;

import java.util.List;

/**
 * @authro fun
 * @date 2025/11/29 17:26
 */
@Data
public class AccountBillsRes extends BaseOkxRes<List<AccountBillsRes.Data>> {

    /**
     * instType	String	产品类型
     * billId	String	账单ID
     * type	String	账单类型
     * subType	String	账单子类型
     * ts	String	余额更新完成的时间，Unix时间戳的毫秒数格式，如 1597026383085
     * balChg	String	账户层面的余额变动数量
     * posBalChg	String	仓位层面的余额变动数量
     * bal	String	账户层面的余额数量
     * posBal	String	仓位层面的余额数量
     * sz	String	数量
     * 对于交割、永续以及期权，为成交或者持仓的数量，单位为张，总为正数。
     * 其他情况下，单位为账户余额币种（ccy）。
     * px	String	价格，与 subType 相关
     * 为成交价格时有
     * 1：买入 2：卖出 3：开多 4：开空 5：平多 6：平空 204：大宗交易买 205：大宗交易卖 206：大宗交易开多 207：大宗交易开空 208：大宗交易平多 209：大宗交易平空 114：自动换币买入 115：自动换币卖出
     * 为强平价格时有
     * 100：强减平多 101：强减平空 102：强减买入 103：强减卖出 104：强平平多 105：强平平空 106：强平买入 107：强平卖出 16：强制还币 17：强制借币还息 110：强平换币转入 111：强平换币转出
     * 为交割价格时有
     * 112：交割平多 113：交割平空
     * 为行权价格时有
     * 170：到期行权 171：到期被行权 172：到期作废
     * 为标记价格时有
     * 173：资金费支出 174：资金费收入
     * ccy	String	账户余额币种
     * pnl	String	收益
     * fee	String	手续费
     * 正数代表平台返佣 ，负数代表平台扣除
     * 手续费规则
     * earnAmt	String	自动赚币数量
     * 仅适用于type 381
     * earnApr	String	自动赚币实际年利率
     * 仅适用于type 381
     * mgnMode	String	保证金模式
     * isolated：逐仓
     * cross：全仓
     * cash：非保证金
     * 如果账单不是由交易产生的，该字段返回 ""
     * instId	String	产品ID，如 BTC-USDT
     * ordId	String	订单ID
     * 当type为2/5/9时，返回相应订单id
     * 无订单时，该字段返回 ""
     * execType	String	流动性方向
     * T：taker
     * M：maker
     * from	String	转出账户
     * 6：资金账户
     * 18：交易账户
     * 仅适用于资金划转，不是资金划转时，返回 ""
     * to	String	转入账户
     * 6：资金账户
     * 18：交易账户
     * 仅适用于资金划转，不是资金划转时，返回 ""
     * notes	String	备注
     * interest	String	利息
     * tag	String	订单标签
     * 字母（区分大小写）与数字的组合，可以是纯字母、纯数字，且长度在1-16位之间。
     * fillTime	String	最新成交时间
     * tradeId	String	最新成交ID
     * clOrdId	String	客户自定义订单ID
     * fillIdxPx	String	交易执行时的指数价格 d
     * 对于交叉现货币对，返回 baseCcy-USDT 的指数价格。 例如 LTC-ETH，该字段返回 LTC-USDT 的指数价格。
     * fillMarkPx	String	成交时的标记价格，仅适用于 交割/永续/期权
     * fillPxVol	String	成交时的隐含波动率，仅适用于 期权，其他业务线返回空字符串""
     * fillPxUsd	String	成交时的期权价格，以USD为单位，仅适用于期权，其他业务线返回空字符串""
     * fillMarkVol	String	成交时的标记波动率，仅适用于期权，其他业务线返回空字符串""
     * fillFwdPx
     */
    @lombok.Data
    public static class Data {
        private String instType;
        private String billId;
        private String type;
        private String subType;
        private String ts;
        private String balChg;
        private String posBalChg;
        private String bal;
        private String posBal;
        private String sz;
        private String px;
        private String ccy;
        private String pnl;
        private String fee;
        private String earnAmt;
        private String earnApr;
        private String mgnMode;
        private String isolated;
        private String cross;
        private String cash;
        private String instId;
        private String ordId;
        private String execType;
        private String from;
        private String to;
        private String notes;
        private String interest;
        private String tag;
        private String fillTime;
        private String tradeId;
        private String clOrdId;
        private String fillIdxPx;
        private String fillMarkPx;
        private String fillPxVol;
        private String fillPxUsd;
        private String fillMarkVol;
        private String fillFwdPx;
    }
}

package com.cs.energy.global.constants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fiona
 * @date 2024/9/29 23:30
 */
public interface Gkey {
    Integer NEW_REG_TASK_ID = 1;
    String USDT = "USDT";
    String MIN = "NIMT";
    String BNB = "BNB";
    String SYMBOL_ALL = "*";
    Integer CNY_DECIMALS = 2;

    /**
     * 同级烧伤比率
     */
    BigDecimal SAME_LV_RATE = BigDecimal.valueOf(0.5);
    Integer BABY = 0;
    Long ADMIN = 0L;
    /**
     * 根据区块浏览器里其他钱包单纯部署合约的费用统计
     */
    BigDecimal INIT_TON =BigDecimal.valueOf(0.0052);
    /**
     * 父级的层级，0表示自己
     */
    Integer LV_N_0 = 0;
    Long DAY_HOURS = 24L;
    Double DAY_HOURS_D = 24D;
    /**
     * 奖励最高发放级别
     */
    Integer TEAM_LEVEL = 10;
    Integer DIRECT_PRIZE_LEVEL = 10;
    /**
     *
     */
    Long SECOND_MILLISECOND = 1000L;
    Long DAY_MILLISECOND = 24 * 60 * 60 * 1000L;
    Long HOUR_MILLISECOND =  60 * 60 * 1000L;
    Long LOGIN_TOLERANCE_TIME =  20 * 1000L;
    Long MINUTE_MILLISECOND =  60 * 1000L;
    Long HALF_MINUTE_MILLISECOND =  30 * 1000L;
    Long HALF_HOUR_MILLISECOND =  30 * 60 * 1000L;
    Long HALF_HOUR_MINUTE = 30L;
    Long YEAR_2099 =  4070880000000L;
    Long MINUTE_59 =  3540000L;
    Long MINUTE_60 =  3600000L;
    Long FIVE_MINUTE_MILLISECOND =  5 * 60 * 1000L;
    Long PRICE_TIME_WINDOW = FIVE_MINUTE_MILLISECOND * 6; // 30分钟价格时间窗口,用于计算TWAP,即用30分钟到价格平均值
    /**
     * 参考ton4j的demo等待时间
     */
    Integer NORMAL_TX_SECOND = 30;
    Integer TON_WAIT_IN_SECOND = 40;
    Integer TON_DEPLOY_IN_SECOND = 60;
    Long TON_WAIT = TON_WAIT_IN_SECOND * 1000L;
    Long SEND_ERROR_CODE = -99L;
    Long DEPLOY_FAIL = -98L;

    Long TEN_SCEONDS = 10 * 1000L;
    Long TEN = 10L;
    Long HUNDRED = 100L;
    Long TEN_LITTLE_SCEONDS = 9500L;
    Long PER_BLOCK_TIME = 3L;
    String DEAD = "0x000000000000000000000000000000000000dead";
    String TOKEN = "CF";
    String TOKEN_FROM = "0x0000000000000000000000000000000000000008";
    String TOKEN_PAIR = "0xCAaF3c41a40103a23Eeaa4BbA468AF3cF5b0e0D8";//bsc-usdt:ARK
//    String TOKEN_PAIR = "0x20Fc836685eb8dE74706eC894C0Dd2CFb4b93e65"//bsctest
    String TOKEN_0 = "0x55d398326f99059ff775485246999027b3197955";//usdt
    String TOKEN_1 = "0xCae117ca6Bc8A341D2E7207F30E180f0e5618B9D";//ark
    /**
     * 开发者用户
     */
    Long U_DEV = 2L;
    /**
     * 加LP用户
     */
    Long U_LP = 3L;
    /**
     * 盲盒奖池用户
     */
    Long U_BLIND_POOL = 4L;
    Long EVM_CONFIRM = 45000L;
    Long EVM_TX_WAIT = 18000L;
    Long EVM_TX_MIN_WAIT = 15000L;
    Long EVM_COLLECT_WAIT = 20000L;
    Long COLLECT_AGAIN_WAIT = 30000L;
    Long HALL = 0L;
    Integer REG_MAIL_TIME = 30;
    Integer MAIL_FAIL_MAX_LIMIT = 3;
    Integer MAIL_REG_FAIL_MAX_LIMIT = 5;
    Integer PWD_MIN_LENGTH = 8;
    Integer PIN_LENGTH = 6;
    String BASE64_IMG = "data:image";
    Map<String, String> IMG_MAP = new HashMap(){{
        put("iVBORw0KGgo", ".png");
        put("/9j/", ".jpg");
        put("R0lGODlh", ".gif");
        put("R0lGODdh", ".gif");
    }};
    long MAX_IMAGE_SIZE = 4 * 1024 * 1024;

    String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    String WEB2 = "web2";
    Long POOL_UID  = 2L;
    Long MERCHANT_UID = 3L;
    // 公排抽5%派给奖励的用户
    Long POOL_PRIZE_UID = 6L;
    // 提现回购账户
    Long WITHDRAW_BUY_BACK_UID = 7L;
    //
    Long PAY_BUY_BACK_UID = 8L;

    Integer BONUS_DECIMALS = 6;
    Integer RATE_DECIMALS = 4;
    BigDecimal SKIP_AMOUNT = BigDecimal.valueOf(0.0001);
    BigDecimal MIN_DYNAMIC_AMOUNT = BigDecimal.valueOf(0.000001);
    BigDecimal DYNAMIC_SKIP_AMOUNT = BigDecimal.valueOf(0.00000001);
    BigDecimal A1500 = BigDecimal.valueOf(1500);
    BigDecimal DOUBLE_BIG = BigDecimal.valueOf(2);
    Long RESEND_TTL = 1740L;

    String ALIPAY_QR = "https://qr.alipay.com";
    String ALIPAY_QR_UPPER = "HTTPS://QR.ALIPAY.COM";
    String ALIPAY_TIME_RANDOM = "?";
    String WECHAT_QR = "wxp://";
    String WECHAT_QR_SCREEN = "wxp://f2f6";
    String WECHAT_BUSI_QR = "https://payapp.wechatpay.cn";
    String WECHAT_BUSI2_QR = "https://payapp.weixin.qq.com";
    String UNION_QR = "https://";
    String CNY = "CNY";
    Integer FAIL_COUNT = 3;
    Integer NO_RETRY_COUNT = 1;
    Long AUTO_WITHDRAW_QUEUE_DELAY = 15 * 1000L;
    Byte PAY_TYPE_BSC = 3;
    String MERCHANT = "merchant";
    String UID = "uid";
    Integer CHINA = 86;
    Long PUBLIC_MERCHANT = 1L;

    String INCOME_TOTAL = "total";
    String INCOME_YESTERDAY ="yest";
    String IN6_OUT1 ="in6out1";
    String IN6_OUT1_TOLERABLE ="in6out1tolerable";
    String IN_N_OUT1_TOLERABLE ="inNout1tolerable";
    String HHFF_OPERATOR_CHANNEL = "operator";
    String AUTO_CHANNEL = "auto";
    String AUTO_FORWARD_URL = "auto";
    Integer UPLOAD_LIMIT = 9;
    String AT = "@";
    String UGC_STR = "/ugc";
    BigDecimal ONE_YEAR_BIG = BigDecimal.valueOf(365) ;
    BigDecimal FUND_MIN = BigDecimal.valueOf(0.01);
    BigDecimal MONTH_DAY = BigDecimal.valueOf(30);
    Integer MONTY_DAY_INT = 30;
    int TOKEN_DECIMALS = 10;
    Integer USDT_DECIMALS = 18;

    // 押金比例
    BigDecimal MARGIN_RATE = BigDecimal.valueOf(0.1);
    boolean IS_MATIAN = true;

    String TRADER_TAG = "cs";

    BigDecimal SETTLE_RATE = BigDecimal.valueOf(0.1);

    String OKX_KEY_PERM_READ_ONLY = "read_only";
    String OKX_KEY_PERM_TRADE = "trade";
    String OKX_KEY_PERM_WITHDRAW = "withdraw";
    String DEF_OKX = "欧易";

    String EMPTY_LIST = "[]";
    // 6表示7天一轮回
    Integer INVITE_POOL_PERIOD = 6;
    String OKX_CA_ACC_LEVEL = "2";

    Double MILLISECOND_TO_HOUR = 3600000.0;
    String MILLISECOND_TO_HOUR_DECIMAL = "%.2f H";
}

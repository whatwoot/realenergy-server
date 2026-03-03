package com.cs.energy.global.constants;

/**
 * @author fiona
 * @date 2024/9/29 06:50
 */
public interface CacheKey {
    String APP_PREFIX = "cs";
    String CONFIG_CACHE = APP_PREFIX + ":CfgCache:";
    String EAGLE_ALL = APP_PREFIX + ":eagle:all";
    String EXCHANGE_ALL = APP_PREFIX + ":exchange:all";
    String MAX_LV = APP_PREFIX + ":MaxLv";
    String MIN_WITHDRAW_LV = APP_PREFIX + ":MinWithdrawLv";
    String EXP_DAY = APP_PREFIX + ":ExpDay";
    String WITHDRAW_QUEUE = APP_PREFIX + ":queue:withdraw:";
    String DEPOSITS = APP_PREFIX + ":deposits";
    String DEPOSITS_START_TIME = "startTime";
    String DEPOSITS_START = "start";
    String DELAY_STATIC_INDEX = APP_PREFIX + ":static:scan";
    String RECHARGE_ADDRS = APP_PREFIX + ":recharge:addrs";
    String CHAIN_ADDR_TYPE = APP_PREFIX + ":addrs";
    String SCAN_MULTI = APP_PREFIX + ":a_deposits:scan";
    String SCAN_EVM = APP_PREFIX + ":a_evm:scan";
    String SCAN_EVM_NFT = APP_PREFIX + ":a_evm:scanNft";
    String SCAN_EVM_LATEST_KEY =   "latestNum";
    String SCAN_EVM_BLOCKNUM_KEY =   "blockNum";

    String INVITE_LEVEL_ALL = APP_PREFIX + ":level:invite";
    String TEAM_LEVEL_ALL = APP_PREFIX + ":level:team";

    String LEVEL_POOL_ALL = APP_PREFIX + ":level:pool";
    String ROOM_TYPE = APP_PREFIX + ":room:types";
    String PRICE_MAP = APP_PREFIX + ":symbols:price";

    String GAME_STAUS = APP_PREFIX + ":a:status";
    String ROOM_POS_KEY = "ag10n1:roomPos:";

    String ALL_ROOM = APP_PREFIX + ":rooms";
    String ROOM_UID_SET = APP_PREFIX + ":room:u:";
    String UID_ROOM_SET = APP_PREFIX + ":u:room:";

    String OMS_ALL = APP_PREFIX + ":oms:all";
    String OMS_PROVIDER_ALL = APP_PREFIX + ":oms:provider";
    String APP_ALL = APP_PREFIX + ":app:list";
    String REG_MAIL_LOCK = APP_PREFIX + ":mail:reg:";
    String MAIL_LOGIN_LOCK = APP_PREFIX + ":mail:login:";
    String GEN_WALLET_LOCK = APP_PREFIX + ":wallet:gen:";
    String MAIL_RESET_PWD_LOCK = APP_PREFIX + ":mail:resetPwd:";
    String MAIL_RESET_PIN_LOCK = APP_PREFIX + ":mail:resetPin:";
    String MAIL_SET_OTP_LOCK = APP_PREFIX + ":mail:setOtp:";
    String SET_PIN_LOCK = APP_PREFIX + ":member:pin:";
    String MERCHANT_APPLY_LOCK = APP_PREFIX + ":merchant:apply:";

    String SYMBOL_PRICE_KEY = APP_PREFIX + ":symbolPrice:";


    String INVEST_QUEUE = APP_PREFIX + ":invest:queue";
    String OMS_FLOW_ID = "oms:id";
    String MERCHANT_PAYMENT_MAP = APP_PREFIX + ":merchant:payment";

    String MERCHANT_GEO = APP_PREFIX + ":merchant:geo";

    String PAY_FLOW_ID = APP_PREFIX + ":payFlow:id";
    String THD_PAY_LOCK = APP_PREFIX + ":thd:pay:lock:";

    String QUEUE_POOL_REWARD = APP_PREFIX + ":queue:pool:reward";
    String QUEUE_POOL2 = APP_PREFIX + ":queue:pool2";
    String QUEUE_POOL2_KEY_QUEUE = "queue";
    String QUEUE_POOL2_KEY_QUEUE_FROM_ASSET = "queueFromAsset";
    String QUEUE_POOL2_KEY_REWARD = "reward";
    String QUEUE_POOL2_KEY_ADJUST = "adjust";
    String QUEUE_POOL2_KEY_POOL_FIX = "pool_fix";

    String CONFIG_CATE_CACHE = APP_PREFIX + ":config:category:";
    String CONFIG_CATE_KEY_CACHE = APP_PREFIX + ":config:cate_key:";
    String CONFIG_KEY_CACHE = APP_PREFIX + ":config:key:";


    String SYMBOL_WITHDRAW = APP_PREFIX + ":withdraw:symbols:";

    String MEMBER_INCODE = APP_PREFIX + ":income:";

    String USER_WITHDRAW = APP_PREFIX + ":withdraw:user:";

    String INVEST_POOL_INVEST = APP_PREFIX + ":investPool:%s:invest";
    String INVEST_POOL_INVITE = APP_PREFIX + ":investPool:%s:invite";
    String INVEST_MARK_FOR_DAILY_REPORT = APP_PREFIX + ":invest:report:daily";
    String APP_RELEASE = APP_PREFIX + ":app:%s";
    String ANDROID = "android";
    String IOS = "ios";
    String POOL_RATE_V2 = APP_PREFIX + ":pool:rate2";
    String STREAM_MSG_ID = APP_PREFIX + ":stream:id:msg";

    String THD_ORDER_NOTIFY = APP_PREFIX + ":thd:order:timeout:";
    
    String NIM_PUSH_FLAG = APP_PREFIX + ":nim:push";
    String SYMBOL_ALL = APP_PREFIX + ":symbol:all";
    String MERCHANT_WITHDRAW_LOCK = APP_PREFIX + ":mer_withdraw:lock:%s:%s";
    String MAIL_PROVIDER_CACHE = APP_PREFIX + ":mail:providers";
    String FUND_LOCK = APP_PREFIX + ":fund:lock:";
    String FUND_BONUS_LOCK = APP_PREFIX + ":fund:bonus:lock";

    String OF_INVITE_LIMIT = APP_PREFIX + ":limit:ofInvite";
    String INVITE_RANK_DAILY = APP_PREFIX + ":inviteRank:daily:%s:%s";
    String INVEST_LOCK  = APP_PREFIX + ":invest:lock:";
    String LEVEL_NUM_KEY = APP_PREFIX + ":levels:num";

    String INVITE_RANK_LIST = APP_PREFIX + ":rank:invite:";
    String TOKEN_LOCK = APP_PREFIX + ":lock:token";

    String TOKEN_TRADE_VOL = APP_PREFIX + ":trade:h24";
    String TOKEN_TRADE_KLINE = APP_PREFIX + ":trade:kline";
    String TOKEN_PRICE_HIS = APP_PREFIX + ":trade:priceHis";
    String INVITE_RANK_POOL = APP_PREFIX + ":inviteRank:pool";

    String GENESSIS_UIDS = APP_PREFIX + ":genesis:uids" ;
    String SMALL_TOTAL = APP_PREFIX + ":genesis:smallTotal";

}

package com.cs.oksdk;

/**
 * @authro fun
 * @date 2025/11/27 16:47
 */
public interface OkxV5 {
    String API_STATUS = "/api/v5/system/status";
    String API_BALANCE = "/api/v5/account/balance";
    String API_ORDER = "/api/v5/trade/order";
    String API_ORDER_HIS = "/api/v5/trade/orders-history";
    String API_ORDER_HIS_ARC = "/api/v5/trade/orders-history-archive";
    String API_CLOSE_POSITION = "/api/v5/trade/close-position";
    String API_POSITIONS = "/api/v5/account/positions";
    String API_POSITIONS_HIS = "/api/v5/account/positions-history";
    String API_INSTRUMENTS = "/api/v5/public/instruments";
    String API_LEVERAGE_INFO = "/api/v5/account/leverage-info";
    String API_SET_LEVERAGE = "/api/v5/account/set-leverage";
    String API_SET_POSITION_MODE = "/api/v5/account/set-position-mode";
    String API_SET_ACCOUNT_LEVEL = "/api/v5/account/set-account-level";
    String API_ACCOUNT_CONFIG = "/api/v5/account/config";
    String API_FILLS = "/api/v5/trade/fills";
    String API_ASSET_BILLS = "/api/v5/asset/bills";
    String API_ASSET_BILLS_HIS = "/api/v5/asset/bills-history";
    String API_ACCOUNT_BILLS = "/api/v5/account/bills";
}

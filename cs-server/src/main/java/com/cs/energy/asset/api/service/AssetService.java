package com.cs.energy.asset.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.asset.api.entity.Asset;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.asset.api.request.ExchangeRequest;
import com.cs.energy.evm.api.entity.ChainWork;

import javax.validation.Valid;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
public interface AssetService extends IService<Asset> {
    AssetFlow updateAsset(AssetFlow assetFlow);

    AssetFlow updateAsset(AssetFlow assetFlow, Asset asset);

    AssetFlow updateRefund(AssetFlow assetFlow);

    AssetFlow updateRefundOptional(AssetFlow assetFlow);

    void addDeposit(ChainWork chainWork);

    void addDepositByCa(ChainWork chainWork);

    void initPoolAsset();

    void updateExchange(@Valid ExchangeRequest assetFlow);

    int updateChange(Asset asset);

    void updateSnapFund(Integer ymd);

    int updateGenessisWithWight(AssetFlow assetFlow);

    public Asset getAssetBalance(Long uid, String symbol,byte type);

}

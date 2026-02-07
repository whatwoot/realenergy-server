package com.cs.copy.chain.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.chain.api.entity.ChainAddress;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-11-23
 */
public interface ChainAddressService extends IService<ChainAddress> {

    List<ChainAddress> listRechargeAndCached(Byte type, String chain, boolean force);

    List<ChainAddress> listRechargeAndCached(Byte type,String chain);

    Map<String, ChainAddress> listAsMap(Byte type, String chain);

    void markNeedRefresh(ChainAddress addr);

    void addNewWallet(Member member);

    ChainAddress genWallet() ;
}

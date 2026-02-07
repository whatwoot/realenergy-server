package com.cs.copy.evm.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.evm.api.entity.Symbol;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-24
 */
public interface SymbolService extends IService<Symbol> {

    List<Symbol> listAll();
    List<Symbol> listAll(boolean force);

    /**
     * mapKey: orgId + ":" chain + ":" + symbol
     * @return
     */
    Map<String, Symbol> listAsTypeChainSymolMap();
}

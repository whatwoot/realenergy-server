package com.cs.copy.evm.server.controller;

import com.cs.copy.asset.api.enums.AssetTypeEnum;
import com.cs.copy.evm.api.entity.Symbol;
import com.cs.copy.evm.api.enums.SymbolTypeEnum;
import com.cs.copy.evm.api.request.SymbolDetailRequest;
import com.cs.copy.evm.api.request.SymbolListRequest;
import com.cs.copy.evm.api.service.SymbolService;
import com.cs.copy.evm.api.vo.SymbolListVO;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-12-24
 */
@Tag(name = "资产")
@RestController
@RequestMapping("/api/symbol")
public class SymbolController {

    @Autowired
    private SymbolService symbolService;

    @Operation(summary = "代币列表")
    @GetMapping("/list")
    public List<SymbolListVO> list(SymbolListRequest req) {
        List<Symbol> symbols = symbolService.listAll();
        List<Symbol> collect = symbols.stream()
                .filter(s -> {
                    boolean show = YesNoByteEnum.YES.eq(s.getShowed());
                    boolean symbol = !StringUtils.hasText(req.getSymbol()) || req.getSymbol().equals(s.getSymbol());
                    boolean baseCoin = !StringUtils.hasText(req.getBaseCoin()) || req.getBaseCoin().equals(s.getBaseCoin());
                    boolean type = req.getType() == null || s.getType().equals(req.getType());
                    boolean chain = req.getChain() == null || s.getChain().equals(req.getChain());
                    return show && symbol && type && chain && baseCoin;
                }).collect(Collectors.toList());
        return BeanCopior.mapList(collect, SymbolListVO.class);
    }

    @Operation(summary = "代币详情")
    @GetMapping("/detail")
    public SymbolListVO detail(SymbolDetailRequest req) {
        List<Symbol> symbols = symbolService.listAll();
        Symbol symbol = null;

        if (req.getId() != null) {
            // 从缓存中按ID查找
            symbol = symbols.stream()
                    .filter(s -> req.getId().equals(s.getId()))
                    .findFirst()
                    .orElse(null);
        } else if (StringUtils.hasText(req.getSymbol())) {
            // 从缓存中按Symbol和条件查找
            Stream<Symbol> symbolStream = symbols.stream()
                    .filter(s -> req.getSymbol().equals(s.getSymbol()));

            if (req.getType() != null) {
                symbolStream = symbolStream.filter(s -> s.getType().equals(req.getType()));
            }

            if (StringUtils.hasText(req.getChain())) {
                symbolStream = symbolStream.filter(s -> req.getChain().equals(s.getChain()));
            }

            // 按权重排序并获取第一个
            symbol = symbolStream
                    .sorted(Comparator.comparing(Symbol::getWeight).reversed())
                    .findFirst()
                    .orElse(null);
        }

        return BeanCopior.map(symbol, SymbolListVO.class, dest -> {

        });
    }

}

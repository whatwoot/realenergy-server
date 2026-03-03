package com.cs.energy.asset.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.asset.api.request.WithdrawListRequest;
import com.cs.energy.asset.api.request.WithdrawRequest;
import com.cs.energy.asset.api.service.WithdrawFlowService;
import com.cs.energy.asset.api.vo.WithdrawFlowVO;
import com.cs.energy.system.api.annotation.MaintainCheck;
import com.cs.energy.system.server.controller.base.BaseUnionTestController;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 提现流水 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-10-03
 */
@Slf4j
@Tag(name = "资产")
@RestController
@RequestMapping("/api/withdrawFlow")
public class WithdrawFlowController extends BaseUnionTestController {

    @Autowired
    private WithdrawFlowService withdrawFlowService;

    @Operation(summary = "提现")
    @PostMapping("/withdraw")
    @LoginRequired
    @MaintainCheck
    public WithdrawFlowVO withdraw(@Valid @RequestBody WithdrawRequest req) {
        JwtUser jwtUser = JwtUserHolder.get();
        WithdrawFlow query = BeanCopior.map(req, WithdrawFlow.class);
        query.setUid(jwtUser.getId());
        WithdrawFlow withdrawFlow = withdrawFlowService.add(query);
        return BeanCopior.map(withdrawFlow, WithdrawFlowVO.class);
    }

    @Operation(summary = "提现记录")
    @GetMapping("/list")
    @LoginRequired
    public Page<WithdrawFlowVO> list(WithdrawListRequest req) {
        JwtUser jwtUser = JwtUserHolder.get();
        Page<WithdrawFlow> page= new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<WithdrawFlow> lambda = new QueryWrapper<WithdrawFlow>().lambda();
        lambda.eq(WithdrawFlow::getUid, jwtUser.getId());
        if(StringUtils.hasText(req.getSymbol())){
            lambda.eq(WithdrawFlow::getSymbol, req.getSymbol());
        }
        if(req.getStatus() != null){
            lambda.eq(WithdrawFlow::getStatus, req.getStatus());
        }

        if(req.getStartDay() != null){
            lambda.ge(WithdrawFlow::getYmd, req.getStartDay());
        }

        if(req.getEndDay() != null){
            lambda.le(WithdrawFlow::getYmd, req.getEndDay());
        }

        lambda.orderByDesc(WithdrawFlow::getId);
        Page<WithdrawFlow> pageList = withdrawFlowService.page(page, lambda);
        return BeanCopior.mapPage(pageList, WithdrawFlowVO.class);
    }
}

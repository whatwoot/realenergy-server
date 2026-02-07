package com.cs.copy.asset.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.copy.asset.api.entity.AssetFlow;
import com.cs.copy.asset.api.request.AssetFlowDetailRequest;
import com.cs.copy.asset.api.request.AssetFlowListRequest;
import com.cs.copy.asset.api.service.AssetFlowService;
import com.cs.copy.asset.api.vo.AssetFlowDetailVO;
import com.cs.copy.asset.api.vo.AssetFlowListVO;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.cs.sp.common.WebAssert.expectNotNull;
import static com.cs.sp.common.WebAssert.hasPermission;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Tag(name = "资产")
@RestController
@RequestMapping("/api/assetFlow")
public class AssetFlowController {

    @Autowired
    private AssetFlowService assetFlowService;

    @Operation(summary = "资产流水详情")
    @GetMapping("/detail")
    @LoginRequired
    public AssetFlowDetailVO detail(@Valid AssetFlowDetailRequest req) {
        AssetFlow flow = assetFlowService.getById(req.getId());
        expectNotNull(flow, "chk.assetFlow.idIncorrect");
        hasPermission(flow.getUid().equals(JwtUserHolder.get().getId()));
        return BeanCopior.map(flow, AssetFlowDetailVO.class);
    }

    @Operation(summary = "用户资产流水")
    @GetMapping("/list")
    @LoginRequired
    public Page<AssetFlowListVO> list(@Valid AssetFlowListRequest req) {
        JwtUser jwtUser = JwtUserHolder.get();
        Page<AssetFlow> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<AssetFlow> lambda = Wrappers.lambdaQuery(AssetFlow.class);
        lambda.eq(AssetFlow::getUid, jwtUser.getId());
        if (req.getShowed() != null) {
            lambda.eq(AssetFlow::getShowed, req.getShowed());
        } else {
            lambda.eq(AssetFlow::getShowed, YesNoByteEnum.YES.getCode());
        }
        if (req.getType() != null) {
            lambda.eq(AssetFlow::getType, req.getType());
        }

        if (StringUtils.hasText(req.getSymbol())) {
            lambda.eq(AssetFlow::getSymbol, req.getSymbol());
        }

        if (StringUtils.hasText(req.getScene())) {
            lambda.eq(AssetFlow::getScene, req.getScene());
        } else {
            String[] scenes = null;
            if (StringUtils.hasText(req.getScenes())) {
                scenes = req.getScenes().split(",");
                lambda.in(AssetFlow::getScene, scenes);
            }
        }

        lambda.orderByDesc(AssetFlow::getId);
        Page<AssetFlow> pageList = assetFlowService.page(page, lambda);
        return BeanCopior.mapPage(pageList, AssetFlowListVO.class);
    }
}

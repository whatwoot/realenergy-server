package com.cs.energy.system.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.system.api.entity.Banner;
import com.cs.energy.system.api.service.BannerService;
import com.cs.energy.system.api.vo.BannerVO;
import com.cs.sp.common.BeanCopior;
import com.cs.sp.enums.YesNoByteEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2025-02-18
 */
@Tag(name = "系统支撑接口")
@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Operation(summary = "轮播图列表")
    @GetMapping("/list")
    public List<BannerVO> list(@RequestParam String pos) {
        List<Banner> list = bannerService.list(new QueryWrapper<Banner>().lambda()
                .eq(Banner::getPos, pos)
                .eq(Banner::getStatus, YesNoByteEnum.YES.getCode())
                .orderByDesc(Banner::getWeight)
        );
        return BeanCopior.mapList(list, BannerVO.class);
    }

}

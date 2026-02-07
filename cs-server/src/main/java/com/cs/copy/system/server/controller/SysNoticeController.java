package com.cs.copy.system.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.copy.system.api.entity.Notice;
import com.cs.copy.system.api.enums.NoticeTypeEnum;
import com.cs.copy.system.api.request.NoticeCusorRequest;
import com.cs.copy.system.api.request.NoticeListRequest;
import com.cs.copy.system.api.service.NoticeService;
import com.cs.copy.system.api.vo.NoticeDetailVO;
import com.cs.copy.system.api.vo.NoticeListVO;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
 * @since 2024-02-08
 */
@Tag(name = "公告")
@RestController
@RequestMapping("/api/notice")
public class SysNoticeController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "资讯列表（游标分页）noticeType:2=公告,3=新闻,4=快讯")
    @GetMapping("/cursorlist")
    public List<NoticeListVO> cursorList(NoticeCusorRequest req) {

        LambdaQueryWrapper<Notice> lambda = new QueryWrapper<Notice>().lambda();
        if (StringUtils.hasText(req.getNoticeType())) {
            lambda.eq(Notice::getNoticeType, req.getNoticeType());
        }else if(StringUtils.hasText(req.getNoticeTypes())) {
            String[] noticeTypes = req.getNoticeTypes().split(",");
            lambda.in(Notice::getNoticeType, noticeTypes);
        }
        // 如果从最新往旧的查，得倒序
        if(req.getFromId() != null){
            lambda.lt(Notice::getNoticeId, req.getFromId());
        }else if(req.getToId() != null){
            // 如果从旧往新的拉（场景：从之前的最新一条，往更新后的数据拉取）
            lambda.gt(Notice::getNoticeId, req.getToId());
        }
        // 排序：默认时间倒序，!=1表示正序
        if(req.getSort() != null && !YesNoByteEnum.YES.eq(req.getSort())){
            lambda.orderByAsc(Notice::getNoticeId);
        }else{
            lambda.orderByDesc(Notice::getNoticeId);
        }

        lambda.last("limit " + req.getPageSize());
        List<Notice> list = noticeService.list(lambda);
        return BeanCopior.mapList(list, NoticeListVO.class);
    }

    @Operation(summary = "资讯列表。noticeType:2=公告,3=新闻,4=快讯")
    // 接口下线
//    @GetMapping("/list")
    public Page<NoticeListVO> list(NoticeListRequest req) {
        Page<Notice> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<Notice> lambda = new QueryWrapper<Notice>().lambda();
        if (StringUtils.hasText(req.getNoticeType())) {
            lambda.eq(Notice::getNoticeType, req.getNoticeType());
        }else{
            lambda.eq(Notice::getNoticeType, NoticeTypeEnum.ANNOUNCE.getCode());
        }
        lambda.orderByDesc(Notice::getNoticeId);
        Page<Notice> pageList = noticeService.page(page, lambda);
        return BeanCopior.mapPage(pageList, NoticeListVO.class);
    }

    @Operation(summary = "资讯详情。noticeType:2=公告,3=新闻,4=快讯")
    @GetMapping("/detail")
    public NoticeDetailVO detail(@RequestParam Integer id) {
        Notice notice = noticeService.getById(id);
        return BeanCopior.map(notice, NoticeDetailVO.class);
    }
}

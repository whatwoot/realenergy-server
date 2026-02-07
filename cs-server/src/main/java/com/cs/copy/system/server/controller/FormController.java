package com.cs.copy.system.server.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.copy.system.api.entity.Form;
import com.cs.copy.system.api.request.FormListRequest;
import com.cs.copy.system.api.request.GroupApplyRequest;
import com.cs.copy.system.api.service.FormService;
import com.cs.copy.system.api.vo.GroupApplyListVO;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

import static com.cs.sp.common.WebAssert.hasPermission;


@Tag(name = "系统支撑接口")
@RestController
@RequestMapping("/api/form")
public class FormController {

    @Autowired
    private FormService formService;

    @Operation(summary = "提交团队申请")
    @PostMapping("/groupApply")
    public boolean apply(@Valid @RequestBody GroupApplyRequest req) {
        Form form = new Form();
        JwtUser jwtUser = JwtUserHolder.get();
        if (jwtUser != null) {
            form.setUid(jwtUser.getId());
        }
        form.setType((byte) 1);
        form.setDeleted(0);
        form.setEntityClass(req.getClass().getName());
        form.setJson(JSON.toJSONString(req));
        form.setCreateTime(new Date());
        return formService.save(form);
    }

    /**
     * 只能查自己的，非登录用户登记的不给所有人查
     *
     * @param req
     * @return
     */
    @Operation(summary = "团队申请列表")
    @GetMapping("/groupApplyList")
    @LoginRequired
    public Page<?> list(FormListRequest req) {
        JwtUser jwtUser = JwtUserHolder.get();
        Page<Form> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<Form> lambda = new QueryWrapper<Form>().lambda();
        lambda.eq(Form::getType, 1);
        lambda.eq(Form::getUid, jwtUser.getId());
        lambda.orderByDesc(Form::getId);
        Page<Form> pageList = formService.page(page, lambda);
        return BeanCopior.mapPage(pageList, GroupApplyListVO.class, groupApplyListVO -> {
            try {
                Class<?> aClass = Class.forName(groupApplyListVO.getEntityClass());
                Object source = JSONObject.parseObject(groupApplyListVO.getJson(), aClass);
                BeanUtils.copyProperties(source, groupApplyListVO);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 只能查自己的，非登录用户登记的不给所有人查
     *
     * @param id
     * @return
     */
    @Operation(summary = "团队申请详情")
    @GetMapping("/groupApplyDetail")
    @LoginRequired
    public GroupApplyListVO detail(@RequestParam Integer id) {
        JwtUser jwtUser = JwtUserHolder.get();
        Form form = formService.getById(id);
        // 防越权
        hasPermission(jwtUser.getId().equals(form.getUid()));
        return BeanCopior.map(form, GroupApplyListVO.class, groupApplyListVO -> {
            try {
                Class<?> aClass = Class.forName(groupApplyListVO.getEntityClass());
                Object source = JSONObject.parseObject(groupApplyListVO.getJson(), aClass);
                BeanUtils.copyProperties(source, groupApplyListVO);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

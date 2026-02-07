package com.cs.copy.member.server.controller;

import com.cs.copy.member.api.entity.InviteLevel;
import com.cs.copy.member.api.service.InviteLevelService;
import com.cs.copy.member.api.vo.InviteLevelListVO;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-12-11
 */
@Tag(name = "用户")
@RestController
@RequestMapping("/api/inviteLevel")
public class InviteLevelController {

    @Autowired
    private InviteLevelService inviteLevelService;

    @Operation(summary = "邀请级别列表")
    @GetMapping("/list")
    public List<InviteLevelListVO> list() {
        List<InviteLevel> inviteLevels = inviteLevelService.listAll();
        return BeanCopior.mapList(inviteLevels, InviteLevelListVO.class);
    }

    @Operation(summary = "最大级别")
    @GetMapping("/max")
    public InviteLevelListVO max() {
        List<InviteLevel> inviteLevels = inviteLevelService.listAll();
        if (inviteLevels.isEmpty()) {
            return null;
        }
        return BeanCopior.map(inviteLevels.get(inviteLevels.size() - 1), InviteLevelListVO.class);
    }
}

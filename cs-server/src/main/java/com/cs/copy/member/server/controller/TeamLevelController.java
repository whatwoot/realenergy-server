package com.cs.copy.member.server.controller;

import com.cs.copy.member.api.entity.InviteLevel;
import com.cs.copy.member.api.entity.TeamLevel;
import com.cs.copy.member.api.service.TeamLevelService;
import com.cs.copy.member.api.vo.InviteLevelListVO;
import com.cs.copy.member.api.vo.TeamlevelListVO;
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
@RequestMapping("/api/teamLevel")
public class TeamLevelController {

    @Autowired
    private TeamLevelService teamLevelService;

    @Operation(summary = "团队级别列表")
    @GetMapping("/list")
    public List<TeamlevelListVO> list() {
        List<TeamLevel> teamLevels = teamLevelService.listAll();
        return BeanCopior.mapList(teamLevels, TeamlevelListVO.class);
    }
}

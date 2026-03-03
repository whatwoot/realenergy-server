package com.cs.energy.member.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @authro fun
 * @date 2025/10/10 17:11
 */
@AllArgsConstructor
@Getter
public class LevelChgEvent {
    private transient Object source;
    private Long uid;
    private Integer levelId;
    private Integer newLevelId;
}
